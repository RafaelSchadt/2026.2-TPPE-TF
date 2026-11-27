package supercompras;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * DUBLÊ DE TESTE (mercado bem-comportado e configurável)
 *
 * Só serve para testar sem rede, e os mercados que se comportam mal (exceções,
 * preços inválidos, Error etc.) ficam em dublês separados.
 *
 * Além de responder, ele registra o que recebeu, para os testes verificarem
 * quantas vezes foi chamado e com quais argumentos
 *
 */
public class MercadoFalso implements Mercado {
    private final String nome;
    private final BigDecimal precoUnico;
    private final Map<String, BigDecimal> precosPorSku;
    private final BigDecimal frete;
    private String protocolo;
    private int chamadasCotar;
    private int chamadasFrete;
    private int chamadasComprar;
    private Map<String, Integer> ultimoLoteCotado;
    private Map<String, Integer> ultimoLoteComprado;
    private Map<String, BigDecimal> ultimosPrecosDaCompra;

    private MercadoFalso(String nome, BigDecimal precoUnico, Map<String, BigDecimal> precosPorSku, BigDecimal frete) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("MercadoFalso precisa de nome válido");
        }
        this.nome = nome;
        this.precoUnico = precoUnico;
        this.precosPorSku = precosPorSku == null ? null : new HashMap<>(precosPorSku);
        this.frete = frete;
        this.protocolo = "PROTO-" + nome;
    }

    /** Todo SKU pedido custa o mesmo preço unitário */
    public static MercadoFalso precoUnico(String nome, String preco, String frete) {
        return new MercadoFalso(nome, new BigDecimal(preco), null, new BigDecimal(frete));
    }

    /** Só tem os SKUs do mapa. qualquer outro SKU é "não temos o item" */
    public static MercadoFalso comPrecos(String nome, Map<String, BigDecimal> precos, String frete) {
        return new MercadoFalso(nome, null, precos, new BigDecimal(frete));
    }

    /** Define o protocolo devolvido por comprar(). Padrão: "PROTO-" + nome */
    public MercadoFalso comProtocolo(String protocolo) {
        this.protocolo = protocolo;
        return this;
    }

    @Override
    public String nome() {
        return nome;
    }

    @Override
    public Map<String, BigDecimal> cotarPrecosUnitarios(Map<String, Integer> itens) {
        chamadasCotar++;
        ultimoLoteCotado = itens;
        Map<String, BigDecimal> precos = new HashMap<>();// sempre um mapa novo
        for (String sku : itens.keySet()) {
            if (precoUnico != null) {
                precos.put(sku, precoUnico);
            } else if (precosPorSku.containsKey(sku)) {
                precos.put(sku, precosPorSku.get(sku));
            }
        }
        return precos;
    }

    @Override
    public BigDecimal frete(Map<String, Integer> itens) {
        chamadasFrete++;
        return frete;
    }

    @Override
    public String comprar(Map<String, Integer> itens, Map<String, BigDecimal> precosUnitarios) {
        chamadasComprar++;
        ultimoLoteComprado = itens;
        ultimosPrecosDaCompra = precosUnitarios;
        return protocolo;
    }

    public int getChamadasCotar() { return chamadasCotar; }
    public int getChamadasFrete() { return chamadasFrete; }
    public int getChamadasComprar() { return chamadasComprar; }

    /** Referência exata recebida - permite testar se o lote chegou imutável */
    public Map<String, Integer> getUltimoLoteCotado() { return ultimoLoteCotado; }
    public Map<String, Integer> getUltimoLoteComprado() { return ultimoLoteComprado; }
    public Map<String, BigDecimal> getUltimosPrecosDaCompra() { return ultimosPrecosDaCompra; }
}