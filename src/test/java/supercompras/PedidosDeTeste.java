package supercompras;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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

    static Pedido pedidoCom(String compradorId, Map<String, Integer> itens, Map<String, BigDecimal> valoresPorSku) {
        Pedido pedido = pedidoDe(compradorId);
        pedido.setItens(itens);
        pedido.setValoresPorSku(valoresPorSku);
        return pedido;
    }

    // Quando o Pedido ficar imutável, este método e os testes que o usam deixam de fazer sentido.
    static void adulterar(Pedido pedido) {
        pedido.setMercado("Adulterado");
        pedido.setTotal(null);
        pedido.getItens().put("ADU-0001", 1);
    }

    // Só os mapas precisam de cópia nova; String e BigDecimal não mudam.
    static Pedido copiaDe(Pedido original) {
        Pedido copia = new Pedido();
        copia.setId(original.getId());
        copia.setCompradorId(original.getCompradorId());
        copia.setMercado(original.getMercado());
        copia.setProtocolo(original.getProtocolo());
        copia.setItens(original.getItens() == null ? null : new HashMap<>(original.getItens()));
        copia.setValoresPorSku(original.getValoresPorSku() == null ? null : new HashMap<>(original.getValoresPorSku()));
        copia.setSubtotal(original.getSubtotal());
        copia.setFrete(original.getFrete());
        copia.setTotal(original.getTotal());
        return copia;
    }
}
