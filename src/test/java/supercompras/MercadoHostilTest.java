package supercompras;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import supercompras.MercadoHostil.Etapa;
import supercompras.MercadoHostil.Falha;

public class MercadoHostilTest {

    private static Map<String, Integer> lote() {
        Map<String, Integer> lote = new HashMap<>();
        lote.put("LEI-0001", 2);
        lote.put("ARR-0003", 1);
        return lote;
    }

    @Test
    public void comportadoDevolveCotacaoFreteEProtocoloValidos() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A");

        Map<String, BigDecimal> precos = mercado.cotarPrecosUnitarios(lote());

        assertEquals("A", mercado.nome());
        assertEquals(new BigDecimal("2.50"), precos.get("LEI-0001"));
        assertEquals(new BigDecimal("2.50"), precos.get("ARR-0003"));
        assertEquals(new BigDecimal("1.00"), mercado.frete(lote()));
        assertEquals("PROTO-HOSTIL", mercado.comprar(lote(), precos));
    }

    @Test
    public void indisponivelLancaAExcecaoChecada() {
        MercadoHostil mercado = MercadoHostil.comportado("A").falhandoEm(Etapa.COTAR, Falha.INDISPONIVEL);
        assertThrows(MercadoIndisponivelException.class, () -> mercado.cotarPrecosUnitarios(lote()));
    }

    @Test
    public void runtimeLancaRuntimeException() {
        MercadoHostil mercado = MercadoHostil.comportado("A").falhandoEm(Etapa.FRETE, Falha.RUNTIME);
        assertThrows(RuntimeException.class, () -> mercado.frete(lote()));
    }

    @Test
    public void errorLancaOErroSimulado() {
        MercadoHostil mercado = MercadoHostil.comportado("A").falhandoEm(Etapa.COMPRAR, Falha.ERROR);
        assertThrows(MercadoHostil.ErroSimulado.class, () -> mercado.comprar(lote(), Map.of()));
    }

    @Test
    public void falhaSoNaEtapaConfigurada() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A").falhandoEm(Etapa.FRETE, Falha.INDISPONIVEL);
        assertEquals(2, mercado.cotarPrecosUnitarios(lote()).size());
        assertThrows(MercadoIndisponivelException.class, () -> mercado.frete(lote()));
        assertEquals("PROTO-HOSTIL", mercado.comprar(lote(), Map.of()));
    }

    @Test
    public void nomeNuloOuEmBrancoEhDevolvidoComoFoiConfigurado() {
        assertNull(MercadoHostil.comportado("A").comNome(null).nome());
        assertEquals("   ", MercadoHostil.comportado("A").comNome("   ").nome());
    }

    @Test
    public void devolvendoPrecosNulosCotaNull() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A").devolvendoPrecosNulos();
        assertNull(mercado.cotarPrecosUnitarios(lote()));
    }

    @Test
    public void semOSkuOmiteSoEsseSku() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A").semOSku("ARR-0003");
        Map<String, BigDecimal> precos = mercado.cotarPrecosUnitarios(lote());
        assertTrue(precos.containsKey("LEI-0001"));
        assertFalse(precos.containsKey("ARR-0003"));
    }

    @Test
    public void precoDoSkuPodeSerNulo() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A").comPrecoDoSku("LEI-0001", null);
        Map<String, BigDecimal> precos = mercado.cotarPrecosUnitarios(lote());
        assertTrue(precos.containsKey("LEI-0001"));
        assertNull(precos.get("LEI-0001"));
    }

    @Test
    public void precoDoSkuPodeSerZeroOuNegativo() throws Exception {
        MercadoHostil zero = MercadoHostil.comportado("A").comPrecoDoSku("LEI-0001", new BigDecimal("0.00"));
        MercadoHostil negativo = MercadoHostil.comportado("A").comPrecoDoSku("LEI-0001", new BigDecimal("-1.00"));
        assertEquals(new BigDecimal("0.00"), zero.cotarPrecosUnitarios(lote()).get("LEI-0001"));
        assertEquals(new BigDecimal("-1.00"), negativo.cotarPrecosUnitarios(lote()).get("LEI-0001"));
    }

    @Test
    public void precoForcadoNaoMudaOsOutrosSkus() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A").comPrecoDoSku("LEI-0001", null);
        assertEquals(new BigDecimal("2.50"), mercado.cotarPrecosUnitarios(lote()).get("ARR-0003"));
    }

    @Test
    public void freteConfiguradoPodeSerNuloNegativoOuZero() throws Exception {
        assertNull(MercadoHostil.comportado("A").comFrete(null).frete(lote()));
        assertEquals(new BigDecimal("-0.01"), MercadoHostil.comportado("A").comFrete(new BigDecimal("-0.01")).frete(lote()));
        assertEquals(new BigDecimal("0.00"), MercadoHostil.comportado("A").comFrete(new BigDecimal("0.00")).frete(lote()));
    }

    @Test
    public void protocoloConfiguradoPodeSerNuloOuEmBranco() throws Exception {
        assertNull(MercadoHostil.comportado("A").comProtocolo(null).comprar(lote(), Map.of()));
        assertEquals("  ", MercadoHostil.comportado("A").comProtocolo("  ").comprar(lote(), Map.of()));
    }

    @Test
    public void adulterandoPrecosDevolvidosMudaOMapaQuandoOFreteEhChamado() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A").adulterandoPrecosDevolvidos();
        Map<String, BigDecimal> precos = mercado.cotarPrecosUnitarios(lote());
        mercado.frete(lote());
        assertEquals(new BigDecimal("0.01"), precos.get("LEI-0001"));
        assertEquals(new BigDecimal("0.01"), precos.get("ARR-0003"));
    }

    @Test
    public void adulterandoPrecosDevolvidosMudaOMapaQuandoACompraEhChamada() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A").adulterandoPrecosDevolvidos();
        Map<String, BigDecimal> precos = mercado.cotarPrecosUnitarios(lote());
        mercado.comprar(lote(), Map.of());
        assertEquals(new BigDecimal("0.01"), precos.get("LEI-0001"));
    }

    @Test
    public void semAdulterarOMapaDevolvidoNaoMuda() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A");
        Map<String, BigDecimal> precos = mercado.cotarPrecosUnitarios(lote());
        mercado.frete(lote());
        mercado.comprar(lote(), Map.of());
        assertEquals(new BigDecimal("2.50"), precos.get("LEI-0001"));
    }

    @Test
    public void tentandoAlterarEntradasConsegueQuandoOLoteEhMutavel() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A").tentandoAlterarEntradas();
        Map<String, Integer> lote = lote();
        mercado.frete(lote);
        assertTrue(mercado.conseguiuAlterarLote());
        assertEquals(lote(), lote);
    }

    @Test
    public void tentandoAlterarEntradasNaoConsegueQuandoOLoteEhImutavel() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A").tentandoAlterarEntradas();
        mercado.frete(Map.copyOf(lote()));
        assertFalse(mercado.conseguiuAlterarLote());
    }

    @Test
    public void tentandoAlterarEntradasConsegueQuandoOsPrecosSaoMutaveis() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A").tentandoAlterarEntradas();
        Map<String, BigDecimal> precos = new HashMap<>();
        precos.put("LEI-0001", new BigDecimal("2.50"));
        mercado.comprar(Map.copyOf(lote()), precos);
        assertTrue(mercado.conseguiuAlterarPrecos());
        assertEquals(1, precos.size());
    }

    @Test
    public void tentandoAlterarEntradasNaoConsegueQuandoOsPrecosSaoImutaveis() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A").tentandoAlterarEntradas();
        mercado.comprar(Map.copyOf(lote()), Map.of("LEI-0001", new BigDecimal("2.50")));
        assertFalse(mercado.conseguiuAlterarLote());
        assertFalse(mercado.conseguiuAlterarPrecos());
    }

    @Test
    public void semConfigurarNadaNaoTentaAlterarAsEntradas() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A");
        mercado.frete(lote());
        mercado.comprar(lote(), new HashMap<>());
        assertFalse(mercado.conseguiuAlterarLote());
        assertFalse(mercado.conseguiuAlterarPrecos());
    }

    @Test
    public void tentarAlterarOLoteNaoContaminaACotacao() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A").tentandoAlterarEntradas();
        Map<String, BigDecimal> precos = mercado.cotarPrecosUnitarios(lote());
        assertEquals(2, precos.size());
    }

    @Test
    public void registraChamadasEReferenciasRecebidas() throws Exception {
        MercadoHostil mercado = MercadoHostil.comportado("A");
        Map<String, Integer> loteCotado = lote();
        Map<String, Integer> loteComprado = lote();
        Map<String, BigDecimal> precos = new HashMap<>();
        mercado.cotarPrecosUnitarios(loteCotado);
        mercado.frete(lote());
        mercado.frete(lote());
        mercado.comprar(loteComprado, precos);
        assertEquals(1, mercado.getChamadasCotar());
        assertEquals(2, mercado.getChamadasFrete());
        assertEquals(1, mercado.getChamadasComprar());
        assertSame(loteCotado, mercado.getUltimoLoteCotado());
        assertSame(loteComprado, mercado.getUltimoLoteComprado());
        assertSame(precos, mercado.getUltimosPrecosDaCompra());
    }

    @Test
    public void contaAChamadaMesmoQuandoElaFalha() {
        MercadoHostil mercado = MercadoHostil.comportado("A").falhandoEm(Etapa.COMPRAR, Falha.RUNTIME);
        assertThrows(RuntimeException.class, () -> mercado.comprar(lote(), Map.of()));
        assertEquals(1, mercado.getChamadasComprar());
    }
}