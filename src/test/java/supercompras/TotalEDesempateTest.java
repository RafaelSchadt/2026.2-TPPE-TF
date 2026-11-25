package supercompras;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class TotalEDesempateTest {

    @Test
    public void deveCalcularTotalComArredondamentoHalfEvenLinhaALinhaEFrete() {
        // Precos >= 0.01 conforme RN-04, forcando empates de metade (.xx5)
        // ARR-0001: 1.005 * 1 = 1.005 -> HALF_EVEN = 1.00
        // FEI-0002: 1.025 * 1 = 1.025 -> HALF_EVEN = 1.02
        // Frete: 10.005 -> HALF_EVEN = 10.00
        // Total esperado: 1.00 + 1.02 + 10.00 = 12.02
        Map<String, BigDecimal> precos = Map.of(
                "ARR-0001", new BigDecimal("1.005"),
                "FEI-0002", new BigDecimal("1.025")
        );
        Mercado mercado = new MercadoStub("MercadoAlfa", precos, new BigDecimal("10.005"), "PROT-01");
        SistemaCompras sistema = new SistemaCompras(List.of(mercado), new RepositorioStub());

        sistema.receberLista("ana", Map.of("ARR-0001", 1, "FEI-0002", 1));
        ResultadoRodada resultado = sistema.fecharRodada();

        assertEquals(new BigDecimal("12.02"), resultado.getTotalLote());
    }

    @Test
    public void deveArredondarPorSkuAntesDeSomarENaoSobreASomaBruta() {
        // Se somasse antes: (0.025 * 1) + (0.025 * 1) = 0.050 -> 0.05
        // Linha a linha (RN-05): 0.025 -> 0.02 e 0.025 -> 0.02 => soma = 0.04
        Map<String, BigDecimal> precos = Map.of(
                "ARR-0001", new BigDecimal("0.025"),
                "FEI-0002", new BigDecimal("0.025")
        );
        Mercado mercado = new MercadoStub("MercadoZeroFrete", precos, BigDecimal.ZERO, "PROT-02");
        SistemaCompras sistema = new SistemaCompras(List.of(mercado), new RepositorioStub());

        sistema.receberLista("ana", Map.of("ARR-0001", 1, "FEI-0002", 1));
        ResultadoRodada resultado = sistema.fecharRodada();

        assertEquals(new BigDecimal("0.04"), resultado.getTotalLote());
    }

    @Test
    public void deveEscolherMercadoComMenorTotalIndependenteDaOrdem() {
        Mercado caro = new MercadoStub("MercadoA", Map.of("LEI-0001", new BigDecimal("10.00")), new BigDecimal("5.00"), "P1");
        Mercado barato = new MercadoStub("MercadoZ", Map.of("LEI-0001", new BigDecimal("8.00")), new BigDecimal("2.00"), "P2");
        Mercado medio = new MercadoStub("MercadoM", Map.of("LEI-0001", new BigDecimal("9.00")), new BigDecimal("3.00"), "P3");

        SistemaCompras sistema = new SistemaCompras(Arrays.asList(caro, barato, medio), new RepositorioStub());
        sistema.receberLista("ana", Map.of("LEI-0001", 1));

        ResultadoRodada resultado = sistema.fecharRodada();
        assertEquals("MercadoZ", resultado.getMercadoVencedor());
        assertEquals(new BigDecimal("10.00"), resultado.getTotalLote());
    }

    @Test
    public void deveDesempatarPorMenorNomeCompareToAposArredondamento() {
        // Ambos arredondam para 10.00 (10.004 -> 10.00 e 10.001 -> 10.00)
        // Na tabela ASCII, "MercadoA" < "MercadoB"
        Mercado mercadoB = new MercadoStub("MercadoB", Map.of("LEI-0001", new BigDecimal("10.001")), BigDecimal.ZERO, "PB");
        Mercado mercadoA = new MercadoStub("MercadoA", Map.of("LEI-0001", new BigDecimal("10.004")), BigDecimal.ZERO, "PA");

        SistemaCompras sistema = new SistemaCompras(Arrays.asList(mercadoB, mercadoA), new RepositorioStub());
        sistema.receberLista("ana", Map.of("LEI-0001", 1));

        ResultadoRodada resultado = sistema.fecharRodada();
        assertEquals("MercadoA", resultado.getMercadoVencedor());
    }

    // --- Dublês locais mínimos ---
    private static class MercadoStub implements Mercado {
        private final String nome;
        private final Map<String, BigDecimal> precos;
        private final BigDecimal frete;
        private final String protocolo;

        MercadoStub(String nome, Map<String, BigDecimal> precos, BigDecimal frete, String protocolo) {
            this.nome = nome;
            this.precos = new HashMap<>(precos);
            this.frete = frete;
            this.protocolo = protocolo;
        }

        @Override public String nome() { return nome; }
        @Override public Map<String, BigDecimal> cotarPrecosUnitarios(Map<String, Integer> itens) { return new HashMap<>(precos); }
        @Override public BigDecimal frete(Map<String, Integer> itens) { return frete; }
        @Override public String comprar(Map<String, Integer> itens, Map<String, BigDecimal> precosUnitarios) { return protocolo; }
    }

    private static class RepositorioStub implements PedidoRepositorio {
        private final List<Pedido> salvos = new ArrayList<>();
        @Override public void salvar(Pedido pedido) { salvos.add(pedido); }
        @Override public List<Pedido> buscarPorComprador(String compradorId) { return Collections.unmodifiableList(salvos); }
    }
}
