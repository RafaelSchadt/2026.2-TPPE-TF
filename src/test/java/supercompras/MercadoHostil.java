package supercompras;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Mercado de teste que começa válido (preço 2.50, frete 1.00) e falha só no que for configurado.
public class MercadoHostil implements Mercado {

    public enum Etapa { COTAR, FRETE, COMPRAR }

    public enum Falha { INDISPONIVEL, RUNTIME, ERROR }

    // Error de verdade, para o teste conferir que ele propagou
    public static class ErroSimulado extends Error {
        public ErroSimulado(String mensagem) {
            super(mensagem);
        }
    }

    private static final BigDecimal PRECO_PADRAO = new BigDecimal("2.50");
    private static final BigDecimal FRETE_PADRAO = new BigDecimal("1.00");
    private static final BigDecimal MENOR_PRECO = new BigDecimal("0.01");
    private static final String SKU_EXTRA = "HOS-9999";

    private String nome;
    private BigDecimal frete = FRETE_PADRAO;
    private String protocolo = "PROTO-HOSTIL";
    private boolean precosNulos;
    private boolean adulteraPrecos;
    private boolean tentaAlterarEntradas;
    private final Set<String> skusOmitidos = new HashSet<>();
    private final Map<String, BigDecimal> precosForcados = new HashMap<>(); // aceita valor null
    private final Map<Etapa, Falha> falhas = new EnumMap<>(Etapa.class);

    private int chamadasCotar;
    private int chamadasFrete;
    private int chamadasComprar;
    private Map<String, Integer> ultimoLoteCotado;
    private Map<String, Integer> ultimoLoteComprado;
    private Map<String, BigDecimal> ultimosPrecosDaCompra;
    private Map<String, BigDecimal> ultimoMapaDevolvido;
    private boolean conseguiuAlterarLote;
    private boolean conseguiuAlterarPrecos;

    private MercadoHostil(String nome) {
        this.nome = nome;
    }

    public static MercadoHostil comportado(String nome) {
        return new MercadoHostil(nome);
    }

    public MercadoHostil falhandoEm(Etapa etapa, Falha falha) {
        falhas.put(etapa, falha);
        return this;
    }

    public MercadoHostil comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public MercadoHostil devolvendoPrecosNulos() {
        precosNulos = true;
        return this;
    }

    public MercadoHostil semOSku(String sku) {
        skusOmitidos.add(sku);
        return this;
    }

    public MercadoHostil comPrecoDoSku(String sku, BigDecimal preco) {
        precosForcados.put(sku, preco);
        return this;
    }

    public MercadoHostil comFrete(BigDecimal frete) {
        this.frete = frete;
        return this;
    }

    public MercadoHostil comProtocolo(String protocolo) {
        this.protocolo = protocolo;
        return this;
    }

    // muda os preços já devolvidos para 0.01 quando frete() ou comprar() são chamados
    public MercadoHostil adulterandoPrecosDevolvidos() {
        adulteraPrecos = true;
        return this;
    }

    // tenta dar put no lote e nos preços recebidos; se conseguir, registra
    public MercadoHostil tentandoAlterarEntradas() {
        tentaAlterarEntradas = true;
        return this;
    }

    @Override
    public String nome() {
        return nome;
    }

    @Override
    public Map<String, BigDecimal> cotarPrecosUnitarios(Map<String, Integer> itens)
            throws MercadoIndisponivelException {
        chamadasCotar++;
        ultimoLoteCotado = itens;
        Set<String> skus = new HashSet<>(itens.keySet());
        tentarAlterarLote(itens);
        falharSeConfigurado(Etapa.COTAR);
        if (precosNulos) {
            return null;
        }
        Map<String, BigDecimal> precos = new HashMap<>();
        for (String sku : skus) {
            if (skusOmitidos.contains(sku)) {
                continue;
            }
            precos.put(sku, precosForcados.containsKey(sku) ? precosForcados.get(sku) : PRECO_PADRAO);
        }
        ultimoMapaDevolvido = precos;
        return precos;
    }

    @Override
    public BigDecimal frete(Map<String, Integer> itens) throws MercadoIndisponivelException {
        chamadasFrete++;
        adulterarPrecosDevolvidos();
        tentarAlterarLote(itens);
        falharSeConfigurado(Etapa.FRETE);
        return frete;
    }

    @Override
    public String comprar(Map<String, Integer> itens, Map<String, BigDecimal> precosUnitarios)
            throws MercadoIndisponivelException {
        chamadasComprar++;
        ultimoLoteComprado = itens;
        ultimosPrecosDaCompra = precosUnitarios;
        adulterarPrecosDevolvidos();
        tentarAlterarLote(itens);
        tentarAlterarPrecos(precosUnitarios);
        falharSeConfigurado(Etapa.COMPRAR);
        return protocolo;
    }

    private void falharSeConfigurado(Etapa etapa) throws MercadoIndisponivelException {
        Falha falha = falhas.get(etapa);
        if (falha == null) {
            return;
        }
        String mensagem = "falha simulada em " + etapa;
        switch (falha) {
            case INDISPONIVEL:
                throw new MercadoIndisponivelException(mensagem);
            case RUNTIME:
                throw new IllegalStateException(mensagem);
            case ERROR:
                throw new ErroSimulado(mensagem);
            default:
                throw new IllegalStateException("falha desconhecida: " + falha);
        }
    }

    private void adulterarPrecosDevolvidos() {
        if (adulteraPrecos && ultimoMapaDevolvido != null) {
            ultimoMapaDevolvido.replaceAll((sku, preco) -> MENOR_PRECO);
        }
    }

    private void tentarAlterarLote(Map<String, Integer> lote) {
        if (!tentaAlterarEntradas) {
            return;
        }
        try {
            lote.put(SKU_EXTRA, 1);
            lote.remove(SKU_EXTRA);
            conseguiuAlterarLote = true;
        } catch (UnsupportedOperationException e) {
            // imutável, como deveria
        }
    }

    private void tentarAlterarPrecos(Map<String, BigDecimal> precos) {
        if (!tentaAlterarEntradas) {
            return;
        }
        try {
            precos.put(SKU_EXTRA, MENOR_PRECO);
            precos.remove(SKU_EXTRA);
            conseguiuAlterarPrecos = true;
        } catch (UnsupportedOperationException e) {
            // imutável, como deveria
        }
    }

    public int getChamadasCotar() { return chamadasCotar; }
    public int getChamadasFrete() { return chamadasFrete; }
    public int getChamadasComprar() { return chamadasComprar; }

    public Map<String, Integer> getUltimoLoteCotado() { return ultimoLoteCotado; }
    public Map<String, Integer> getUltimoLoteComprado() { return ultimoLoteComprado; }
    public Map<String, BigDecimal> getUltimosPrecosDaCompra() { return ultimosPrecosDaCompra; }

    public boolean conseguiuAlterarLote() { return conseguiuAlterarLote; }
    public boolean conseguiuAlterarPrecos() { return conseguiuAlterarPrecos; }
}