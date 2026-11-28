package supercompras;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class SistemaComprasCaracterizacaoTest {

    private static void assertValorIgual(String esperado, BigDecimal atual) {
        assertNotNull(atual);
        assertEquals(0, new BigDecimal(esperado).compareTo(atual));
    }

    @Test
    public void fecharRodada_consolidaLoteEscolheMaisBaratoESalvaPedidos() {
        MercadoFalso caro = MercadoFalso.precoUnico("Caro", "20.00", "0.00");
        MercadoFalso barato = MercadoFalso.precoUnico("Barato", "10.00", "4.00");
        RepositorioEmMemoria repo = new RepositorioEmMemoria();
        SistemaCompras sistema = new SistemaCompras(List.of(caro, barato), repo);

        sistema.receberLista("ana", Map.of("LEI-0001", 2));
        sistema.receberLista("bia", Map.of("LEI-0001", 3, "ARR-0002", 1));

        ResultadoRodada resultado = sistema.fecharRodada();

        assertEquals("Barato", resultado.getMercadoVencedor());
        assertEquals("PROTO-Barato", resultado.getProtocolo());
        assertValorIgual("64.00", resultado.getTotalLote());
        assertEquals(Map.of("LEI-0001", 5, "ARR-0002", 1), barato.getUltimoLoteCotado());
        assertEquals(1, barato.getChamadasComprar());
        assertEquals(0, caro.getChamadasComprar());
        assertEquals(1, barato.getChamadasFrete());
        assertEquals(0, caro.getChamadasFrete());

        assertEquals(2, resultado.getPedidos().size());
        Pedido pedidoAna = resultado.getPedidos().get(0);
        assertEquals("PROTO-Barato-ana", pedidoAna.getId());
        assertEquals("ana", pedidoAna.getCompradorId());
        assertValorIgual("20.00", pedidoAna.getSubtotal());
        assertValorIgual("2.00", pedidoAna.getFrete());
        assertValorIgual("22.00", pedidoAna.getTotal());

        Pedido pedidoBia = resultado.getPedidos().get(1);
        assertEquals("PROTO-Barato-bia", pedidoBia.getId());
        assertEquals("bia", pedidoBia.getCompradorId());
        assertValorIgual("40.00", pedidoBia.getSubtotal());
        assertValorIgual("2.00", pedidoBia.getFrete());
        assertValorIgual("42.00", pedidoBia.getTotal());

        assertEquals(2, repo.quantidadeSalva());
        assertEquals(List.of("ana", "bia"), repo.getCompradoresNaOrdemSalva());
    }

    @Test
    public void fecharRodada_limpaListasECompraLoteVazioQuandoNaoHaListasPendentes() {
        MercadoFalso mercado = MercadoFalso.precoUnico("Mercado", "10.00", "5.00");
        RepositorioEmMemoria repo = new RepositorioEmMemoria();
        SistemaCompras sistema = new SistemaCompras(List.of(mercado), repo);

        sistema.receberLista("ana", Map.of("LEI-0001", 1));
        sistema.fecharRodada();

        ResultadoRodada segundaRodada = sistema.fecharRodada();

        assertTrue(segundaRodada.getPedidos().isEmpty());
        assertValorIgual("0.00", segundaRodada.getTotalLote());
        assertTrue(mercado.getUltimoLoteComprado().isEmpty());
        assertEquals(1, repo.quantidadeSalva());
    }

    @Test
    public void receberLista_ignoraSilenciosamenteMapaNulo() {
        MercadoFalso mercado = MercadoFalso.precoUnico("Mercado", "10.00", "0.00");
        RepositorioEmMemoria repo = new RepositorioEmMemoria();
        SistemaCompras sistema = new SistemaCompras(List.of(mercado), repo);

        sistema.receberLista("ana", null);
        sistema.receberLista("bia", Map.of("LEI-0001", 1));

        ResultadoRodada resultado = sistema.fecharRodada();

        assertEquals(1, resultado.getPedidos().size());
        assertEquals("bia", resultado.getPedidos().get(0).getCompradorId());
    }

    @Test
    public void receberLista_aceitaEntradaForaDoPadraoESobrescreveCompradorRepetido() {
        MercadoFalso mercado = MercadoFalso.precoUnico("Mercado", "10.00", "0.00");
        RepositorioEmMemoria repo = new RepositorioEmMemoria();
        SistemaCompras sistema = new SistemaCompras(List.of(mercado), repo);

        sistema.receberLista("ANA MAIUSCULA!", Map.of("sku-invalido", 5));
        sistema.receberLista("ANA MAIUSCULA!", Map.of("sku-invalido", 2));

        ResultadoRodada resultado = sistema.fecharRodada();

        assertEquals(1, resultado.getPedidos().size());
        Pedido pedido = resultado.getPedidos().get(0);
        assertEquals("ANA MAIUSCULA!", pedido.getCompradorId());
        assertEquals(Map.of("sku-invalido", 2), pedido.getItens());
        assertValorIgual("20.00", pedido.getTotal());
    }

    @Test
    public void receberLista_naoFazCopiaDefensivaDoMapaRecebido() {
        MercadoFalso mercado = MercadoFalso.precoUnico("Mercado", "10.00", "0.00");
        RepositorioEmMemoria repo = new RepositorioEmMemoria();
        SistemaCompras sistema = new SistemaCompras(List.of(mercado), repo);

        Map<String, Integer> itens = new HashMap<>();
        itens.put("LEI-0001", 1);
        sistema.receberLista("ana", itens);
        itens.put("LEI-0001", 4);

        ResultadoRodada resultado = sistema.fecharRodada();

        assertEquals(Integer.valueOf(4), mercado.getUltimoLoteCotado().get("LEI-0001"));
        assertEquals(Integer.valueOf(4), resultado.getPedidos().get(0).getItens().get("LEI-0001"));
    }

    @Test
    public void fecharRodada_ignoraFreteAoEscolherMercadoVencedor() {
        MercadoFalso freteCaro = MercadoFalso.precoUnico("FreteCaro", "10.00", "50.00");
        MercadoFalso freteGratis = MercadoFalso.precoUnico("FreteGratis", "11.00", "0.00");
        RepositorioEmMemoria repo = new RepositorioEmMemoria();
        SistemaCompras sistema = new SistemaCompras(List.of(freteCaro, freteGratis), repo);

        sistema.receberLista("ana", Map.of("LEI-0001", 1));

        ResultadoRodada resultado = sistema.fecharRodada();

        assertEquals("FreteCaro", resultado.getMercadoVencedor());
        assertValorIgual("60.00", resultado.getTotalLote());
    }

    @Test
    public void fecharRodada_desempataPelaOrdemDaListaDeMercadosENaoPeloNome() {
        MercadoFalso zeta = MercadoFalso.precoUnico("Zeta", "10.00", "0.00");
        MercadoFalso alfa = MercadoFalso.precoUnico("Alfa", "10.00", "0.00");
        RepositorioEmMemoria repo = new RepositorioEmMemoria();
        SistemaCompras sistema = new SistemaCompras(List.of(zeta, alfa), repo);

        sistema.receberLista("ana", Map.of("LEI-0001", 1));

        ResultadoRodada resultado = sistema.fecharRodada();

        assertEquals("Zeta", resultado.getMercadoVencedor());
    }

    @Test
    public void fecharRodada_divideFreteIgualmenteEMantemOrdemDeInsercao() {
        MercadoFalso mercado = MercadoFalso.precoUnico("Mercado", "10.00", "10.00");
        RepositorioEmMemoria repo = new RepositorioEmMemoria();
        SistemaCompras sistema = new SistemaCompras(List.of(mercado), repo);

        sistema.receberLista("carlos", Map.of("LEI-0001", 9));
        sistema.receberLista("ana", Map.of("LEI-0001", 1));

        ResultadoRodada resultado = sistema.fecharRodada();

        assertEquals("carlos", resultado.getPedidos().get(0).getCompradorId());
        assertEquals("ana", resultado.getPedidos().get(1).getCompradorId());
        assertValorIgual("5.00", resultado.getPedidos().get(0).getFrete());
        assertValorIgual("5.00", resultado.getPedidos().get(1).getFrete());
        assertValorIgual("95.00", resultado.getPedidos().get(0).getTotal());
        assertValorIgual("15.00", resultado.getPedidos().get(1).getTotal());
    }

    @Test
    public void fecharRodada_engoleExcecaoEErrorDuranteCotacao() {
        MercadoHostil comError = MercadoHostil.comportado("ComError")
                .falhandoEm(MercadoHostil.Etapa.COTAR, MercadoHostil.Falha.ERROR);
        MercadoHostil indisponivel = MercadoHostil.comportado("Indisponivel")
                .falhandoEm(MercadoHostil.Etapa.COTAR, MercadoHostil.Falha.INDISPONIVEL);
        MercadoFalso valido = MercadoFalso.precoUnico("Valido", "5.00", "1.00");
        RepositorioEmMemoria repo = new RepositorioEmMemoria();
        SistemaCompras sistema = new SistemaCompras(List.of(comError, indisponivel, valido), repo);

        sistema.receberLista("ana", Map.of("LEI-0001", 1));

        ResultadoRodada resultado = sistema.fecharRodada();

        assertEquals("Valido", resultado.getMercadoVencedor());
        assertEquals(1, comError.getChamadasCotar());
        assertEquals(1, indisponivel.getChamadasCotar());
    }

    @Test
    public void fecharRodada_naoFazFailOverQuandoCompraFalhaEUsaProtocoloNulo() {
        MercadoHostil falhaCompra = MercadoHostil.comportado("FalhaCompra")
                .comPrecoDoSku("LEI-0001", new BigDecimal("1.00"))
                .falhandoEm(MercadoHostil.Etapa.COMPRAR, MercadoHostil.Falha.INDISPONIVEL);
        MercadoFalso reserva = MercadoFalso.precoUnico("Reserva", "5.00", "1.00");
        RepositorioEmMemoria repo = new RepositorioEmMemoria();
        SistemaCompras sistema = new SistemaCompras(List.of(falhaCompra, reserva), repo);

        sistema.receberLista("ana", Map.of("LEI-0001", 1));

        ResultadoRodada resultado = sistema.fecharRodada();

        assertEquals("FalhaCompra", resultado.getMercadoVencedor());
        assertNull(resultado.getProtocolo());
        assertEquals("null-ana", resultado.getPedidos().get(0).getId());
        assertEquals(0, reserva.getChamadasComprar());
    }

    @Test
    public void fecharRodada_permiteMercadoHostilAlterarEntradasEAdulterarPrecos() {
        MercadoHostil hostil = MercadoHostil.comportado("Hostil")
                .tentandoAlterarEntradas()
                .adulterandoPrecosDevolvidos();
        RepositorioEmMemoria repo = new RepositorioEmMemoria();
        SistemaCompras sistema = new SistemaCompras(List.of(hostil), repo);

        sistema.receberLista("ana", Map.of("LEI-0001", 2));

        ResultadoRodada resultado = sistema.fecharRodada();

        assertTrue(hostil.conseguiuAlterarLote());
        assertTrue(hostil.conseguiuAlterarPrecos());
        assertValorIgual("0.02", resultado.getPedidos().get(0).getSubtotal());
    }

    @Test(expected = NullPointerException.class)
    public void fecharRodada_lancaNullPointerExceptionQuandoNenhumMercadoCota() {
        MercadoHostil falho = MercadoHostil.comportado("Falho")
                .falhandoEm(MercadoHostil.Etapa.COTAR, MercadoHostil.Falha.INDISPONIVEL);
        RepositorioEmMemoria repo = new RepositorioEmMemoria();
        SistemaCompras sistema = new SistemaCompras(List.of(falho), repo);

        sistema.receberLista("ana", Map.of("LEI-0001", 1));

        sistema.fecharRodada();
    }

    @Test
    public void fecharRodada_interrompeSalvamentoQuandoRepositorioFalha() {
        MercadoFalso mercado = MercadoFalso.precoUnico("Mercado", "5.00", "0.00");
        RepositorioQueFalha repo = RepositorioQueFalha.falhandoParaCompradores("bia");
        SistemaCompras sistema = new SistemaCompras(List.of(mercado), repo);

        sistema.receberLista("ana", Map.of("LEI-0001", 1));
        sistema.receberLista("bia", Map.of("LEI-0001", 1));
        sistema.receberLista("caio", Map.of("LEI-0001", 1));

        try {
            sistema.fecharRodada();
            fail("Esperava PersistenciaException");
        } catch (PersistenciaException esperado) {
            assertEquals(List.of("ana", "bia"), repo.getTentativas());
            assertEquals(List.of("ana"), repo.getCompradoresSalvos());
        }
    }

    @Test
    public void fecharRodada_mantemListasPendentesQuandoRepositorioFalhaNaPrimeiraChamada() {
        MercadoFalso mercado = MercadoFalso.precoUnico("Mercado", "5.00", "0.00");
        RepositorioQueFalha repo = RepositorioQueFalha.falhandoNasChamadas(1);
        SistemaCompras sistema = new SistemaCompras(List.of(mercado), repo);

        sistema.receberLista("ana", Map.of("LEI-0001", 1));
        sistema.receberLista("bia", Map.of("LEI-0001", 1));

        try {
            sistema.fecharRodada();
            fail("Esperava PersistenciaException");
        } catch (PersistenciaException esperado) {
            assertTrue(repo.getCompradoresSalvos().isEmpty());
        }

        ResultadoRodada segundaTentativa = sistema.fecharRodada();
        assertEquals(2, segundaTentativa.getPedidos().size());
        assertEquals(List.of("ana", "bia"), repo.getCompradoresSalvos());
    }

    @Test
    public void resultadoEPedido_expoemColecoesMutaveis() {
        MercadoFalso mercado = MercadoFalso.precoUnico("Mercado", "5.00", "0.00");
        RepositorioEmMemoria repo = new RepositorioEmMemoria();
        SistemaCompras sistema = new SistemaCompras(List.of(mercado), repo);

        sistema.receberLista("ana", new HashMap<>(Map.of("LEI-0001", 1)));
        ResultadoRodada resultado = sistema.fecharRodada();

        Pedido pedido = resultado.getPedidos().get(0);
        pedido.getItens().put("EXT-0001", 9);
        pedido.getValoresPorSku().put("EXT-0001", new BigDecimal("9.00"));
        resultado.getPedidos().clear();

        assertEquals(Integer.valueOf(9), pedido.getItens().get("EXT-0001"));
        assertTrue(resultado.getPedidos().isEmpty());
    }
}