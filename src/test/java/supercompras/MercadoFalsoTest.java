package supercompras;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MercadoFalsoTest {

    private static Map<String, Integer> lote() {
        Map<String, Integer> lote = new HashMap<>();
        lote.put("LEI-0001", 2);
        lote.put("ARR-0003", 1);
        return lote;
    }

    @Test
    public void precoUnicoCotaTodosOsSkusDoLote() {
        MercadoFalso mercado = MercadoFalso.precoUnico("A", "2.50", "1.00");
        Map<String, BigDecimal> precos = mercado.cotarPrecosUnitarios(lote());
        assertEquals(2, precos.size());
        assertEquals(new BigDecimal("2.50"), precos.get("LEI-0001"));
        assertEquals(new BigDecimal("2.50"), precos.get("ARR-0003"));
    }

    @Test
    public void comPrecosOmiteSkuQueNaoTem() {
        MercadoFalso mercado = MercadoFalso.comPrecos("A", Map.of("LEI-0001", new BigDecimal("3.10")), "0.00");
        Map<String, BigDecimal> precos = mercado.cotarPrecosUnitarios(lote());
        assertEquals(1, precos.size());
        assertEquals(new BigDecimal("3.10"), precos.get("LEI-0001"));
        assertFalse(precos.containsKey("ARR-0003"));
    }

    @Test
    public void soCotaOQueEstaNoLote() {
        Map<String, BigDecimal> catalogo = Map.of("LEI-0001", new BigDecimal("3.10"), "FEI-0009", new BigDecimal("7.00"));
        MercadoFalso mercado = MercadoFalso.comPrecos("A", catalogo, "0.00");
        Map<String, BigDecimal> precos = mercado.cotarPrecosUnitarios(lote());
        assertFalse(precos.containsKey("FEI-0009"));
    }

    @Test
    public void cadaCotacaoDevolveUmMapaNovo() {
        MercadoFalso mercado = MercadoFalso.precoUnico("A", "2.50", "1.00");
        Map<String, BigDecimal> primeiro = mercado.cotarPrecosUnitarios(lote());
        primeiro.put("LEI-0001", new BigDecimal("99.99"));
        assertEquals(new BigDecimal("2.50"), mercado.cotarPrecosUnitarios(lote()).get("LEI-0001"));
    }

    @Test
    public void comPrecosCopiaOMapaRecebido() {
        Map<String, BigDecimal> catalogo = new HashMap<>();
        catalogo.put("LEI-0001", new BigDecimal("3.10"));
        MercadoFalso mercado = MercadoFalso.comPrecos("A", catalogo, "0.00");
        catalogo.put("LEI-0001", new BigDecimal("99.99"));
        assertEquals(new BigDecimal("3.10"), mercado.cotarPrecosUnitarios(lote()).get("LEI-0001"));
    }

    @Test
    public void freteDevolveOValorConfigurado() {
        MercadoFalso mercado = MercadoFalso.precoUnico("A", "2.50", "4.75");
        assertEquals(new BigDecimal("4.75"), mercado.frete(lote()));
    }

    @Test
    public void protocoloPadraoUsaONomeDoMercado() {
        MercadoFalso mercado = MercadoFalso.precoUnico("Mercado A", "2.50", "1.00");
        assertEquals("PROTO-Mercado A", mercado.comprar(lote(), Map.of()));
    }

    @Test
    public void comProtocoloTrocaOProtocoloDevolvido() {
        MercadoFalso mercado = MercadoFalso.precoUnico("A", "2.50", "1.00").comProtocolo("ABC-123");
        assertEquals("ABC-123", mercado.comprar(lote(), Map.of()));
    }

    @Test
    public void nomeDevolveOConfigurado() {
        assertEquals("Mercado A", MercadoFalso.precoUnico("Mercado A", "2.50", "1.00").nome());
    }

    @Test
    public void registraQuantidadeDeChamadas() {
        MercadoFalso mercado = MercadoFalso.precoUnico("A", "2.50", "1.00");
        mercado.cotarPrecosUnitarios(lote());
        mercado.cotarPrecosUnitarios(lote());
        mercado.frete(lote());
        mercado.comprar(lote(), Map.of());
        assertEquals(2, mercado.getChamadasCotar());
        assertEquals(1, mercado.getChamadasFrete());
        assertEquals(1, mercado.getChamadasComprar());
    }

    @Test
    public void guardaAsReferenciasExatasRecebidas() {
        MercadoFalso mercado = MercadoFalso.precoUnico("A", "2.50", "1.00");
        Map<String, Integer> loteCotado = lote();
        Map<String, Integer> loteComprado = lote();
        Map<String, BigDecimal> precos = new HashMap<>();
        mercado.cotarPrecosUnitarios(loteCotado);
        mercado.comprar(loteComprado, precos);
        assertSame(loteCotado, mercado.getUltimoLoteCotado());
        assertSame(loteComprado, mercado.getUltimoLoteComprado());
        assertSame(precos, mercado.getUltimosPrecosDaCompra());
    }

    @Test
    public void semChamadasOContadorComecaEmZero() {
        MercadoFalso mercado = MercadoFalso.precoUnico("A", "2.50", "1.00");
        assertEquals(0, mercado.getChamadasCotar());
        assertEquals(0, mercado.getChamadasFrete());
        assertEquals(0, mercado.getChamadasComprar());
        assertNull(mercado.getUltimoLoteCotado());
    }

    @Test
    public void nomeNuloOuEmBrancoNaoCriaOMercado() {
        assertThrows(IllegalArgumentException.class, () -> MercadoFalso.precoUnico(null, "2.50", "1.00"));
        assertThrows(IllegalArgumentException.class, () -> MercadoFalso.precoUnico("   ", "2.50", "1.00"));
    }
}