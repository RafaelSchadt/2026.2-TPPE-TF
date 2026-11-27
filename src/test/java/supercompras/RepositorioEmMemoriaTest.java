package supercompras;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static supercompras.PedidosDeTeste.pedidoDe;

import java.util.List;

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
        assertSame(daLud, encontrados.get(0));
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
