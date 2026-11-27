package supercompras;

import java.math.BigDecimal;

// Único lugar dos testes que cria Pedido: quando os setters saírem, será necessário alterar aqui.
final class PedidosDeTeste {

    private PedidosDeTeste() {
    }

    static Pedido pedidoDe(String compradorId) {
        Pedido pedido = new Pedido();
        pedido.setId("PROTO-1-" + compradorId);
        pedido.setCompradorId(compradorId);
        pedido.setMercado("Mercado Teste");
        pedido.setProtocolo("PROTO-1");
        pedido.setSubtotal(new BigDecimal("10.00"));
        pedido.setFrete(new BigDecimal("1.00"));
        pedido.setTotal(new BigDecimal("11.00"));
        return pedido;
    }
}
