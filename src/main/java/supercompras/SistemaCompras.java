package supercompras;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

// Orquestra o recebimento de listas, cotação, compra com fail-over e geração de pedidos.
public class SistemaCompras {
    private static final BigDecimal PRECO_MINIMO_VALIDO = new BigDecimal("0.01");
    private static final int ESCALA_MONETARIA = 2;

    public List<Mercado> mercados;
    public PedidoRepositorio repo;
    public Map<String, Map<String, Integer>> listas = new LinkedHashMap<>();
    static int contador = 0;

    public SistemaCompras(List<Mercado> mercados, PedidoRepositorio repo) {
        this.mercados = mercados;
        this.repo = repo;
    }

    public void receberLista(String compradorId, Map<String, Integer> itens) {
        System.out.println("Recebendo lista de " + compradorId + " com itens " + itens);
        if (itens == null) {
            return;
        }
        listas.put(compradorId, itens);
    }

    public ResultadoRodada fecharRodada() {
        Map<String, Integer> loteImutavel = agruparLoteImutavel();

        List<CotacaoAvaliada> cotacoesValidas = avaliarCotacoes(loteImutavel);
        if (cotacoesValidas.isEmpty()) {
            throw new SemCotacaoValidaException("Nenhum mercado apresentou cotacao valida para o lote.");
        }

        CompraConfirmada compra = comprarComFailOver(loteImutavel, cotacoesValidas);
        CotacaoAvaliada vencedora = compra.cotacao();

        ResultadoRodada r = new ResultadoRodada();
        r.mercadoVencedor = vencedora.mercado().nome();
        r.protocolo = compra.protocolo();
        r.totalLote = vencedora.total();

        gerarESalvarPedidosProvisorios(r, vencedora, compra.protocolo());

        listas.clear();
        return r;
    }

    private Map<String, Integer> agruparLoteImutavel() {
        Map<String, Integer> lote = new LinkedHashMap<>();
        for (Map<String, Integer> lista : listas.values()) {
            for (Map.Entry<String, Integer> item : lista.entrySet()) {
                lote.merge(item.getKey(), item.getValue(), Integer::sum);
            }
        }
        return Collections.unmodifiableMap(lote);
    }

    private List<CotacaoAvaliada> avaliarCotacoes(Map<String, Integer> loteImutavel) {
        List<CotacaoAvaliada> validas = new ArrayList<>();
        for (Mercado mercado : mercados) {
            CotacaoAvaliada avaliada = avaliarMercado(mercado, loteImutavel);
            if (avaliada != null) {
                validas.add(avaliada);
            }
        }
        Collections.sort(validas);
        return validas;
    }

    private CotacaoAvaliada avaliarMercado(Mercado mercado, Map<String, Integer> loteImutavel) {
        if (mercado == null || mercado.nome() == null || mercado.nome().isBlank()) {
            return null;
        }
        try {
            Map<String, BigDecimal> cotados = mercado.cotarPrecosUnitarios(loteImutavel);
            Map<String, BigDecimal> precosFiltrados = filtrarECopiarPrecos(loteImutavel, cotados);
            if (precosFiltrados == null) {
                return null;
            }

            BigDecimal freteBruto = mercado.frete(loteImutavel);
            if (freteBruto == null || freteBruto.compareTo(BigDecimal.ZERO) < 0) {
                return null;
            }

            BigDecimal freteArredondado = freteBruto.setScale(ESCALA_MONETARIA, RoundingMode.HALF_EVEN);
            BigDecimal total = calcularTotalMercado(loteImutavel, precosFiltrados, freteArredondado);

            return new CotacaoAvaliada(
                    mercado,
                    Collections.unmodifiableMap(precosFiltrados),
                    freteArredondado,
                    total
            );
        } catch (MercadoIndisponivelException | RuntimeException e) {
            return null;
        }
    }

    private Map<String, BigDecimal> filtrarECopiarPrecos(
            Map<String, Integer> lote, Map<String, BigDecimal> cotados) {
        if (cotados == null) {
            return null;
        }
        Map<String, BigDecimal> copia = new HashMap<>();
        for (String sku : lote.keySet()) {
            BigDecimal preco = cotados.get(sku);
            if (preco == null || preco.compareTo(PRECO_MINIMO_VALIDO) < 0) {
                return null;
            }
            copia.put(sku, preco);
        }
        return copia;
    }

    private BigDecimal calcularTotalMercado(
            Map<String, Integer> lote,
            Map<String, BigDecimal> precos,
            BigDecimal freteArredondado) {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> item : lote.entrySet()) {
            BigDecimal valorLinha = precos.get(item.getKey())
                    .multiply(BigDecimal.valueOf(item.getValue()))
                    .setScale(ESCALA_MONETARIA, RoundingMode.HALF_EVEN);
            total = total.add(valorLinha);
        }
        total = total.add(freteArredondado);
        assert total.scale() == ESCALA_MONETARIA : "Total do mercado deve ter escala 2";
        return total;
    }

    private CompraConfirmada comprarComFailOver(
            Map<String, Integer> loteImutavel, List<CotacaoAvaliada> cotacoesOrdenadas) {
        Set<Mercado> jaTentados = Collections.newSetFromMap(new IdentityHashMap<>());

        for (CotacaoAvaliada cotacao : cotacoesOrdenadas) {
            Mercado mercado = cotacao.mercado();
            if (!jaTentados.add(mercado)) {
                continue;
            }
            try {
                String protocolo = mercado.comprar(loteImutavel, cotacao.precosUnitarios());
                if (protocolo != null && !protocolo.isBlank()) {
                    return new CompraConfirmada(cotacao, protocolo);
                }
            } catch (MercadoIndisponivelException | RuntimeException e) {
                // Falha na compra: tenta o próximo mercado; Error propaga (RN 8)
            }
        }
        throw new CompraNaoRealizadaException("Nenhum mercado aceitou a compra do lote.");
    }

    private void gerarESalvarPedidosProvisorios(
            ResultadoRodada r, CotacaoAvaliada vencedora, String protocolo) {
        Map<String, BigDecimal> precosVencedor = vencedora.precosUnitarios();
        BigDecimal freteLote = vencedora.freteArredondado();

        for (String c : listas.keySet()) {
            Map<String, Integer> l = listas.get(c);
            Pedido p = new Pedido();
            p.setId(protocolo + "-" + c);
            p.setCompradorId(c);
            p.setMercado(vencedora.mercado().nome());
            p.setProtocolo(protocolo);
            p.setItens(l);
            double sub = 0;
            Map<String, BigDecimal> valores = new HashMap<>();
            for (String sku : l.keySet()) {
                double v = precosVencedor.get(sku).doubleValue() * l.get(sku);
                v = Math.round(v * 100) / 100.0;
                valores.put(sku, BigDecimal.valueOf(v));
                sub += v;
            }
            p.setValoresPorSku(valores);
            p.setSubtotal(BigDecimal.valueOf(sub));
            double f = freteLote.doubleValue() / listas.size();
            p.setFrete(BigDecimal.valueOf(Math.round(f * 100) / 100.0));
            p.setTotal(BigDecimal.valueOf(Math.round((sub + f) * 100) / 100.0));
            r.pedidos.add(p);
            repo.salvar(p);
        }
    }

    private record CotacaoAvaliada(
            Mercado mercado,
            Map<String, BigDecimal> precosUnitarios,
            BigDecimal freteArredondado,
            BigDecimal total
    ) implements Comparable<CotacaoAvaliada> {

        @Override
        public int compareTo(CotacaoAvaliada outra) {
            int cmpTotal = this.total.compareTo(outra.total);
            return (cmpTotal != 0)
                    ? cmpTotal
                    : this.mercado.nome().compareTo(outra.mercado.nome());
        }
    }

    private record CompraConfirmada(CotacaoAvaliada cotacao, String protocolo) {}
}
