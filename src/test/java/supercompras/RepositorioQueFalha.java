package supercompras;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

// Por padrão falha como a RN-12 exige: PersistenciaException com SQLException de causa.
// O JDBC ainda não faz isso: engole a SQLException com printStackTrace.
class RepositorioQueFalha implements PedidoRepositorio {

    static final String MENSAGEM_FALHA = "falha simulada ao salvar pedido";

    // recebe o número da chamada (a partir de 1) e o pedido
    private final BiPredicate<Integer, Pedido> deveFalhar;
    private final RepositorioEmMemoria sucessos = new RepositorioEmMemoria();
    private final List<String> tentativas = new ArrayList<>();
    // null = PersistenciaException padrão
    private final Supplier<? extends RuntimeException> fabricaDeFalha;
    private final List<RuntimeException> falhasLancadas = new ArrayList<>();

    private RepositorioQueFalha(BiPredicate<Integer, Pedido> deveFalhar) {
        this(deveFalhar, null);
    }

    private RepositorioQueFalha(BiPredicate<Integer, Pedido> deveFalhar,
            Supplier<? extends RuntimeException> fabricaDeFalha) {
        this.deveFalhar = deveFalhar;
        this.fabricaDeFalha = fabricaDeFalha;
    }

    // Chame antes de qualquer salvar: o histórico não é copiado para o novo dublê.
    RepositorioQueFalha lancando(Supplier<? extends RuntimeException> fabrica) {
        return new RepositorioQueFalha(deveFalhar, Objects.requireNonNull(fabrica, "fabrica"));
    }

    static RepositorioQueFalha falhandoParaCompradores(String... compradorIds) {
        Set<String> alvos = new HashSet<>(Arrays.asList(compradorIds));
        return new RepositorioQueFalha((chamada, pedido) -> alvos.contains(pedido.getCompradorId()));
    }

    // 1 = primeira chamada a salvar
    static RepositorioQueFalha falhandoNasChamadas(Integer... numerosDaChamada) {
        Set<Integer> alvos = new HashSet<>(Arrays.asList(numerosDaChamada));
        return new RepositorioQueFalha((chamada, pedido) -> alvos.contains(chamada));
    }

    static RepositorioQueFalha falhandoSempre() {
        return new RepositorioQueFalha((chamada, pedido) -> true);
    }

    @Override
    public synchronized void salvar(Pedido pedido) {
        Objects.requireNonNull(pedido, "pedido");
        tentativas.add(pedido.getCompradorId());
        int numeroDaChamada = tentativas.size();
        if (deveFalhar.test(numeroDaChamada, pedido)) {
            RuntimeException falha = criarFalha(numeroDaChamada);
            falhasLancadas.add(falha);
            throw falha;
        }
        sucessos.salvar(pedido);
    }

    private RuntimeException criarFalha(int numeroDaChamada) {
        if (fabricaDeFalha == null) {
            return new PersistenciaException(MENSAGEM_FALHA,
                    new SQLException("falha simulada na chamada " + numeroDaChamada));
        }
        RuntimeException falha = Objects.requireNonNull(fabricaDeFalha.get(), "a fábrica de falha devolveu null");
        // AssertionError é Error: atravessa o catch do sistema e derruba o teste com a mensagem certa.
        for (RuntimeException anterior : falhasLancadas) {
            if (anterior == falha) {
                throw new AssertionError("a fábrica de lancando(...) deve criar uma exceção nova a cada chamada");
            }
        }
        return falha;
    }

    // só enxerga os pedidos salvos com sucesso
    @Override
    public synchronized List<Pedido> buscarPorComprador(String compradorId) {
        return sucessos.buscarPorComprador(compradorId);
    }

    // inclui as chamadas que falharam
    synchronized List<String> getTentativas() {
        // List.copyOf não aceita null, e o legado pode salvar pedido com compradorId nulo
        return Collections.unmodifiableList(new ArrayList<>(tentativas));
    }

    synchronized List<Pedido> getSalvos() {
        return sucessos.getSalvos();
    }

    synchronized List<String> getCompradoresSalvos() {
        return sucessos.getCompradoresNaOrdemSalva();
    }

    synchronized List<RuntimeException> getFalhasLancadas() {
        return List.copyOf(falhasLancadas);
    }
}
