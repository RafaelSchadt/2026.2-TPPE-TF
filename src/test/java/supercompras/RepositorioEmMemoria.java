package supercompras;

import static supercompras.PedidosDeTeste.copiaDe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// Guarda cópias dos pedidos, igual ao banco faz. Se guardasse o próprio objeto, um teste
// passaria mesmo com o sistema alterando o pedido depois de salvo.
class RepositorioEmMemoria implements PedidoRepositorio {

    private final List<Pedido> salvos = new ArrayList<>();

    @Override
    public synchronized void salvar(Pedido pedido) {
        Objects.requireNonNull(pedido, "pedido");
        salvos.add(copiaDe(pedido));
    }

    @Override
    public synchronized List<Pedido> buscarPorComprador(String compradorId) {
        List<Pedido> encontrados = new ArrayList<>();
        for (Pedido pedido : salvos) {
            if (Objects.equals(pedido.getCompradorId(), compradorId)) {
                encontrados.add(copiaDe(pedido));
            }
        }
        return List.copyOf(encontrados);
    }

    synchronized List<Pedido> getSalvos() {
        List<Pedido> copias = new ArrayList<>();
        for (Pedido pedido : salvos) {
            copias.add(copiaDe(pedido));
        }
        return List.copyOf(copias);
    }

    synchronized List<String> getCompradoresNaOrdemSalva() {
        List<String> ids = new ArrayList<>();
        for (Pedido pedido : salvos) {
            ids.add(pedido.getCompradorId());
        }
        // List.copyOf não aceita null, e o legado pode salvar pedido com compradorId nulo
        return Collections.unmodifiableList(ids);
    }

    synchronized int quantidadeSalva() {
        return salvos.size();
    }
}
