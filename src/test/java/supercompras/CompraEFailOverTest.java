package supercompras;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class CompraEFailOverTest {

    @Test
    public void deveComprarApenasNoPrimeiroMercadoValidoENuncaChamarOsDemais() {
        AtomicInteger chamadasComprar1 = new AtomicInteger();
        AtomicInteger chamadasComprar2 = new AtomicInteger();

        Mercado m1 = new MercadoConfiguravel("Mercado1", new BigDecimal("5.00"), () -> {
            chamadasComprar1.incrementAndGet();
            return "PROT-OK";
        });
        Mercado m2 = new MercadoConfiguravel("Mercado2", new BigDecimal("9.00"), () -> {
            chamadasComprar2.incrementAndGet();
            return "PROT-2";
        });

        SistemaCompras sistema = new SistemaCompras(Arrays.asList(m2, m1), new RepositorioVazio());
        sistema.receberLista("ana", Map.of("LEI-0001", 2));

        ResultadoRodada res = sistema.fecharRodada();
        assertEquals("Mercado1", res.getMercadoVencedor());
        assertEquals("PROT-OK", res.getProtocolo());
        assertEquals(1, chamadasComprar1.get());
        assertEquals(0, chamadasComprar2.get());
    }

    @Test
    public void deveFazerFailOverQuandoCompraLancaExcecaoOuRetornaProtocoloEmBranco() {
        AtomicInteger tentativasM1 = new AtomicInteger();
        AtomicInteger tentativasM2 = new AtomicInteger();
        AtomicInteger tentativasM3 = new AtomicInteger();

        Mercado m1 = new MercadoConfiguravel("Mercado1", new BigDecimal("5.00"), () -> {
            tentativasM1.incrementAndGet();
            throw new MercadoIndisponivelException("timeout na compra");
        });
        Mercado m2 = new MercadoConfiguravel("Mercado2", new BigDecimal("6.00"), () -> {
            tentativasM2.incrementAndGet();
            return "   "; // protocolo em branco e considerado falha
        });
        Mercado m3 = new MercadoConfiguravel("Mercado3", new BigDecimal("7.00"), () -> {
            tentativasM3.incrementAndGet();
            return "PROT-M3";
        });

        SistemaCompras sistema = new SistemaCompras(Arrays.asList(m1, m2, m3), new RepositorioVazio());
        sistema.receberLista("ana", Map.of("LEI-0001", 1));

        ResultadoRodada res = sistema.fecharRodada();
        assertEquals("Mercado3", res.getMercadoVencedor());
        assertEquals("PROT-M3", res.getProtocolo());
        assertEquals(1, tentativasM1.get());
        assertEquals(1, tentativasM2.get());
        assertEquals(1, tentativasM3.get());
    }

    @Test
    public void devePassarMapasImutaveisESomenteSkusDoLoteNoComprar() {
        Mercado hostil = new Mercado() {
            @Override public String nome() { return "MercadoHostil"; }

            @Override
            public Map<String, BigDecimal> cotarPrecosUnitarios(Map<String, Integer> itens) {
                Map<String, BigDecimal> retorno = new HashMap<>();
                retorno.put("LEI-0001", new BigDecimal("4.00"));
                retorno.put("EXT-9999", new BigDecimal("99.00")); // chave extra deve ser filtrada
                return retorno;
            }

            @Override
            public BigDecimal frete(Map<String, Integer> itens) {
                return BigDecimal.ONE;
            }

            @Override
            public String comprar(Map<String, Integer> itens, Map<String, BigDecimal> precosUnitarios) {
                assertFalse("Chave extra nao pode chegar em comprar", precosUnitarios.containsKey("EXT-9999"));
                try {
                    itens.put("HAC-0001", 10);
                    fail("Mapa de itens entregue a comprar deveria ser imutavel");
                } catch (UnsupportedOperationException esperado) {
                    // ok
                }
                try {
                    precosUnitarios.put("HAC-0001", BigDecimal.ONE);
                    fail("Mapa de precos entregue a comprar deveria ser imutavel");
                } catch (UnsupportedOperationException esperado) {
                    // ok
                }
                return "PROT-SEGURO";
            }
        };

        SistemaCompras sistema = new SistemaCompras(List.of(hostil), new RepositorioVazio());
        sistema.receberLista("ana", Map.of("LEI-0001", 1));

        ResultadoRodada res = sistema.fecharRodada();
        assertEquals("PROT-SEGURO", res.getProtocolo());
    }

    @Test(expected = AssertionError.class)
    public void devePropagarErrorDaJvmSemCapturarNoFailOver() {
        Mercado comError = new MercadoConfiguravel("MercadoError", new BigDecimal("5.00"), () -> {
            throw new AssertionError("falha grave JVM");
        });
        Mercado backup = new MercadoConfiguravel("MercadoBackup", new BigDecimal("9.00"), () -> "PROT-OK");

        SistemaCompras sistema = new SistemaCompras(Arrays.asList(comError, backup), new RepositorioVazio());
        sistema.receberLista("ana", Map.of("LEI-0001", 1));
        sistema.fecharRodada();
    }

    @Test
    public void deveManterListasPendentesQuandoNenhumaCompraForAceita() {
        AtomicInteger modo = new AtomicInteger(0);
        Mercado instavel = new MercadoConfiguravel("MercadoInstavel", new BigDecimal("5.00"), () -> {
            if (modo.get() == 0) {
                throw new RuntimeException("erro interno no parceiro");
            }
            return "PROT-RECUPERADO";
        });

        SistemaCompras sistema = new SistemaCompras(List.of(instavel), new RepositorioVazio());
        sistema.receberLista("ana", Map.of("LEI-0001", 2));

        try {
            sistema.fecharRodada();
            fail("Deveria lancar CompraNaoRealizadaException");
        } catch (CompraNaoRealizadaException esperado) {
            // listas devem continuar pendentes
        }

        modo.set(1);
        ResultadoRodada segundaTentativa = sistema.fecharRodada();
        assertEquals("PROT-RECUPERADO", segundaTentativa.getProtocolo());
    }

    @Test(expected = SemCotacaoValidaException.class)
    public void deveLancarSemCotacaoValidaQuandoNenhumMercadoCotar() {
        Mercado invalido = new Mercado() {
            @Override public String nome() { return "Invalido"; }
            @Override public Map<String, BigDecimal> cotarPrecosUnitarios(Map<String, Integer> itens) { return Collections.emptyMap(); }
            @Override public BigDecimal frete(Map<String, Integer> itens) { return BigDecimal.ZERO; }
            @Override public String comprar(Map<String, Integer> itens, Map<String, BigDecimal> precosUnitarios) { return "P1"; }
        };

        SistemaCompras sistema = new SistemaCompras(List.of(invalido), new RepositorioVazio());
        sistema.receberLista("ana", Map.of("LEI-0001", 1));
        sistema.fecharRodada();
    }

    @FunctionalInterface
    private interface AcaoCompra {
        String executar() throws MercadoIndisponivelException;
    }

    private static class MercadoConfiguravel implements Mercado {
        private final String nome;
        private final BigDecimal precoUnitario;
        private final AcaoCompra acaoCompra;

        MercadoConfiguravel(String nome, BigDecimal precoUnitario, AcaoCompra acaoCompra) {
            this.nome = nome;
            this.precoUnitario = precoUnitario;
            this.acaoCompra = acaoCompra;
        }

        @Override public String nome() { return nome; }
        @Override public Map<String, BigDecimal> cotarPrecosUnitarios(Map<String, Integer> itens) { return Map.of("LEI-0001", precoUnitario); }
        @Override public BigDecimal frete(Map<String, Integer> itens) { return BigDecimal.ZERO; }
        @Override public String comprar(Map<String, Integer> itens, Map<String, BigDecimal> precosUnitarios) throws MercadoIndisponivelException {
            return acaoCompra.executar();
        }
    }

    private static class RepositorioVazio implements PedidoRepositorio {
        @Override public void salvar(Pedido pedido) {}
        @Override public List<Pedido> buscarPorComprador(String compradorId) { return List.of(); }
    }
}
