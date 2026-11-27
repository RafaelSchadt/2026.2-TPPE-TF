package supercompras;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static supercompras.PedidosDeTeste.pedidoDe;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

public class RepositorioQueFalhaTest {

    @Test
    public void falhaSoParaOsCompradoresConfiguradosEContinuaRegistrando() {
        RepositorioQueFalha repositorio = RepositorioQueFalha.falhandoParaCompradores("lud");

        repositorio.salvar(pedidoDe("amanda"));
        salvarEsperandoFalha(repositorio, "lud");
        repositorio.salvar(pedidoDe("rafael"));

        assertEquals(List.of("amanda", "lud", "rafael"), repositorio.getTentativas());
        assertEquals(List.of("amanda", "rafael"), repositorio.getCompradoresSalvos());
        assertEquals(1, repositorio.getFalhasLancadas().size());
    }

    @Test
    public void falhaNaNesimaChamada() {
        RepositorioQueFalha repositorio = RepositorioQueFalha.falhandoNasChamadas(2, 3);

        repositorio.salvar(pedidoDe("amanda"));
        salvarEsperandoFalha(repositorio, "lud");
        salvarEsperandoFalha(repositorio, "rafael");
        repositorio.salvar(pedidoDe("davi"));

        assertEquals(List.of("amanda", "lud", "rafael", "davi"), repositorio.getTentativas());
        assertEquals(List.of("amanda", "davi"), repositorio.getCompradoresSalvos());
    }

    @Test
    public void falhandoSempreNaoSalvaNada() {
        RepositorioQueFalha repositorio = RepositorioQueFalha.falhandoSempre();

        salvarEsperandoFalha(repositorio, "amanda");
        salvarEsperandoFalha(repositorio, "lud");

        assertTrue(repositorio.getSalvos().isEmpty());
        assertTrue(repositorio.buscarPorComprador("amanda").isEmpty());
        assertEquals(2, repositorio.getFalhasLancadas().size());
    }

    @Test
    public void excecaoLancadaTemSqlExceptionComoCausaEEhAMesmaRegistrada() {
        RepositorioQueFalha repositorio = RepositorioQueFalha.falhandoSempre();

        PersistenciaException lancada = salvarEsperandoFalha(repositorio, "amanda");

        assertTrue(lancada.getCause() instanceof SQLException);
        assertSame(lancada, repositorio.getFalhasLancadas().get(0));
    }

    @Test
    public void buscarPorCompradorSoVeOsSalvosComSucesso() {
        RepositorioQueFalha repositorio = RepositorioQueFalha.falhandoParaCompradores("lud");
        repositorio.salvar(pedidoDe("amanda"));
        salvarEsperandoFalha(repositorio, "lud");

        assertEquals(1, repositorio.buscarPorComprador("amanda").size());
        assertTrue(repositorio.buscarPorComprador("lud").isEmpty());
    }

    @Test
    public void lancandoTrocaOTipoDaExcecaoMantendoOAlvo() {
        IllegalStateException bug = new IllegalStateException("bug no repositório");
        RepositorioQueFalha repositorio =
                RepositorioQueFalha.falhandoParaCompradores("lud").lancando(() -> bug);

        repositorio.salvar(pedidoDe("amanda"));
        try {
            repositorio.salvar(pedidoDe("lud"));
            fail("deveria ter falhado ao salvar o pedido de lud");
        } catch (IllegalStateException lancada) {
            assertSame(bug, lancada);
        }

        assertEquals(List.of("amanda", "lud"), repositorio.getTentativas());
        assertEquals(List.of("amanda"), repositorio.getCompradoresSalvos());
        assertEquals(List.of(bug), repositorio.getFalhasLancadas());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void listaDeTentativasNaoPermiteAlterarODuble() {
        RepositorioQueFalha repositorio = RepositorioQueFalha.falhandoSempre();
        salvarEsperandoFalha(repositorio, "amanda");

        repositorio.getTentativas().clear();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void listaDeFalhasNaoPermiteAlterarODuble() {
        RepositorioQueFalha repositorio = RepositorioQueFalha.falhandoSempre();
        salvarEsperandoFalha(repositorio, "amanda");

        repositorio.getFalhasLancadas().clear();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void listaDeSalvosNaoPermiteAlterarODuble() {
        RepositorioQueFalha repositorio = RepositorioQueFalha.falhandoNasChamadas(2);
        repositorio.salvar(pedidoDe("amanda"));

        repositorio.getCompradoresSalvos().clear();
    }

    private static PersistenciaException salvarEsperandoFalha(RepositorioQueFalha repositorio,
            String compradorId) {
        try {
            repositorio.salvar(pedidoDe(compradorId));
        } catch (PersistenciaException esperada) {
            return esperada;
        }
        fail("deveria ter falhado ao salvar o pedido de " + compradorId);
        return null;
    }
}
