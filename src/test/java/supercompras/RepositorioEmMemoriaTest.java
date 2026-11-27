package supercompras;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static supercompras.PedidosDeTeste.adulterar;
import static supercompras.PedidosDeTeste.pedidoCom;
import static supercompras.PedidosDeTeste.pedidoDe;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class RepositorioEmMemoriaTest {

    @Test
    public void guardaPedidosNaOrdemDeSalvar() {
        RepositorioEmMemoria repositorio = new RepositorioEmMemoria();
        repositorio.salvar(pedidoDe("amanda"));
        repositorio.salvar(pedidoDe("lud"));
        repositorio.salvar(pedidoDe("yan"));

        assertEquals(List.of("amanda", "lud", "yan"), repositorio.getCompradoresNaOrdemSalva());
        assertEquals(3, repositorio.quantidadeSalva());
    }

    @Test
    public void buscarPorCompradorDevolveSoOsPedidosDele() {
        RepositorioEmMemoria repositorio = new RepositorioEmMemoria();
        Pedido daLud = pedidoDe("lud");
        repositorio.salvar(daLud);
        repositorio.salvar(pedidoDe("yan"));

        List<Pedido> encontrados = repositorio.buscarPorComprador("lud");

        assertEquals(1, encontrados.size());
        Pedido encontrado = encontrados.get(0);
        assertEquals(daLud.getId(), encontrado.getId());
        assertEquals(daLud.getCompradorId(), encontrado.getCompradorId());
        assertEquals(daLud.getMercado(), encontrado.getMercado());
        assertEquals(daLud.getProtocolo(), encontrado.getProtocolo());
        assertEquals(daLud.getItens(), encontrado.getItens());
        assertEquals(daLud.getValoresPorSku(), encontrado.getValoresPorSku());
        assertEquals(daLud.getSubtotal(), encontrado.getSubtotal());
        assertEquals(daLud.getFrete(), encontrado.getFrete());
        assertEquals(daLud.getTotal(), encontrado.getTotal());
    }

    @Test
    public void alterarPedidoDepoisDeSalvarNaoMudaOGuardado() {
        RepositorioEmMemoria repositorio = new RepositorioEmMemoria();
        Map<String, Integer> itensDoComprador = new HashMap<>(Map.of("LEI-0001", 2));
        Map<String, BigDecimal> valoresDoComprador = new HashMap<>(Map.of("LEI-0001", new BigDecimal("10.00")));
        Pedido pedido = pedidoCom("lud", itensDoComprador, valoresDoComprador);
        repositorio.salvar(pedido);

        itensDoComprador.put("LEI-0001", 999);
        valoresDoComprador.put("LEI-0001", new BigDecimal("0.01"));
        adulterar(pedido);

        Pedido guardado = repositorio.buscarPorComprador("lud").get(0);
        assertEquals(Map.of("LEI-0001", 2), guardado.getItens());
        assertEquals(Map.of("LEI-0001", new BigDecimal("10.00")), guardado.getValoresPorSku());
        assertEquals(new BigDecimal("11.00"), guardado.getTotal());
        assertEquals("Mercado Teste", guardado.getMercado());
    }

    @Test
    public void alterarPedidoDevolvidoNaoMudaOGuardado() {
        RepositorioEmMemoria repositorio = new RepositorioEmMemoria();
        repositorio.salvar(pedidoDe("lud"));

        Pedido devolvido = repositorio.buscarPorComprador("lud").get(0);
        assertNotSame(devolvido, repositorio.buscarPorComprador("lud").get(0));
        adulterar(devolvido);
        adulterar(repositorio.getSalvos().get(0));

        Pedido releitura = repositorio.buscarPorComprador("lud").get(0);
        assertEquals(new BigDecimal("11.00"), releitura.getTotal());
        assertTrue(releitura.getItens().isEmpty());
        assertEquals("Mercado Teste", releitura.getMercado());
    }

    @Test
    public void buscarCompradorSemPedidosDevolveListaVazia() {
        RepositorioEmMemoria repositorio = new RepositorioEmMemoria();
        repositorio.salvar(pedidoDe("lud"));

        assertTrue(repositorio.buscarPorComprador("rafael").isEmpty());
        assertTrue(repositorio.buscarPorComprador(null).isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void listaDeSalvosNaoPermiteAlterarODuble() {
        RepositorioEmMemoria repositorio = new RepositorioEmMemoria();
        repositorio.salvar(pedidoDe("lud"));

        repositorio.getSalvos().clear();
    }

    @Test(expected = NullPointerException.class)
    public void salvarNuloEhErroDeUsoDoDuble() {
        new RepositorioEmMemoria().salvar(null);
    }
}
