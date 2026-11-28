package supercompras;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Compras coletivas de supermercado.
 */
public class SistemaCompras {
    public List<Mercado> mercados;
    public PedidoRepositorio repo;
    public Map<String, Map<String, Integer>> listas = new LinkedHashMap<>();
    static int contador = 0;

    public SistemaCompras(List<Mercado> mercados, PedidoRepositorio repo) {
        this.mercados = mercados;
        this.repo = repo;
    }

    // recebe a lista de um comprador
    public void receberLista(String compradorId, Map<String, Integer> itens) {
        System.out.println("Recebendo lista de " + compradorId + " com itens " + itens);
        if (itens == null) {
            return;
        }
        listas.put(compradorId, itens);
    }

    // fecha a rodada: cota, compra e divide
    public ResultadoRodada fecharRodada() {
        Map<String, Integer> lote = new LinkedHashMap<>();
        for (Map<String, Integer> lista : listas.values()) {
            for (Map.Entry<String, Integer> item : lista.entrySet()) {
                lote.merge(item.getKey(), item.getValue(), Integer::sum);
            }
        }
        Map<String, Integer> loteImutavel = Collections.unmodifiableMap(lote);

        // Cada Object[]: [0] = Mercado, [1] = Map<String, BigDecimal> precosFiltrados, [2] = BigDecimal total
        List<Object[]> cotacoesValidas = new ArrayList<>();

        for (Mercado mercado : mercados) {
            if (mercado == null || mercado.nome() == null || mercado.nome().isBlank()) {
                continue;
            }
            try {
                Map<String, BigDecimal> cotados = mercado.cotarPrecosUnitarios(loteImutavel);
                if (cotados == null) {
                    continue;
                }

                // Copia imediata apenas dos SKUs do lote e validacao de preco >= 0.01 (RN-04 / RN-08)
                Map<String, BigDecimal> precosFiltrados = new HashMap<>();
                boolean cotacaoCompleta = true;
                BigDecimal totalMercado = BigDecimal.ZERO;

                for (Map.Entry<String, Integer> item : loteImutavel.entrySet()) {
                    String sku = item.getKey();
                    BigDecimal precoUnit = cotados.get(sku);
                    if (precoUnit == null || precoUnit.compareTo(new BigDecimal("0.01")) < 0) {
                        cotacaoCompleta = false;
                        break;
                    }
                    precosFiltrados.put(sku, precoUnit);

                    BigDecimal valorLinha = precoUnit
                            .multiply(BigDecimal.valueOf(item.getValue()))
                            .setScale(2, RoundingMode.HALF_EVEN);
                    totalMercado = totalMercado.add(valorLinha);
                }

                if (!cotacaoCompleta) {
                    continue;
                }

                BigDecimal frete = mercado.frete(loteImutavel);
                if (frete == null || frete.compareTo(BigDecimal.ZERO) < 0) {
                    continue;
                }

                totalMercado = totalMercado.add(frete.setScale(2, RoundingMode.HALF_EVEN));
                cotacoesValidas.add(new Object[]{
                        mercado,
                        Collections.unmodifiableMap(precosFiltrados),
                        totalMercado
                });
            } catch (MercadoIndisponivelException | RuntimeException e) {
                // Desqualifica mercado que falhou; Error nao e capturado e propaga
            }
        }

        if (cotacoesValidas.isEmpty()) {
            throw new SemCotacaoValidaException("Nenhum mercado apresentou cotacao valida para o lote.");
        }

        // Ordena da menor para a maior cotacao (RN-05 + RN-06)
        cotacoesValidas.sort((a, b) -> {
            BigDecimal totalA = (BigDecimal) a[2];
            BigDecimal totalB = (BigDecimal) b[2];
            int cmpTotal = totalA.compareTo(totalB);
            if (cmpTotal != 0) {
                return cmpTotal;
            }
            return ((Mercado) a[0]).nome().compareTo(((Mercado) b[0]).nome());
        });

        // RN-08: percorre em ordem, para na 1a compra valida, nunca repete mercado
        Mercado mercadoVencedor = null;
        Map<String, BigDecimal> precosVencedor = null;
        BigDecimal menorTotal = null;
        String protocolo = null;

        for (Object[] candidata : cotacoesValidas) {
            Mercado mercado = (Mercado) candidata[0];
            @SuppressWarnings("unchecked")
            Map<String, BigDecimal> precosImutaveis = (Map<String, BigDecimal>) candidata[1];

            try {
                String resp = mercado.comprar(loteImutavel, precosImutaveis);
                if (resp != null && !resp.isBlank()) {
                    mercadoVencedor = mercado;
                    precosVencedor = precosImutaveis;
                    menorTotal = (BigDecimal) candidata[2];
                    protocolo = resp;
                    break;
                }
            } catch (MercadoIndisponivelException | RuntimeException e) {
                // Falha na compra: tenta o proximo mercado da lista ordenada; Error propaga
            }
        }

        if (protocolo == null) {
            throw new CompraNaoRealizadaException("Nenhum mercado aceitou a compra do lote.");
        }

        ResultadoRodada r = new ResultadoRodada();
        r.mercadoVencedor = mercadoVencedor.nome();
        r.protocolo = protocolo;

        BigDecimal freteLote = BigDecimal.ZERO;
        try {
            freteLote = mercadoVencedor.frete(loteImutavel);
        } catch (Exception e) {
        }

        // Divide entre os compradores (sera substituido nas etapas 4.1 e 4.3)
        for (String c : listas.keySet()) {
            Map<String, Integer> l = listas.get(c);
            Pedido p = new Pedido();
            p.setId(protocolo + "-" + c);
            p.setCompradorId(c);
            p.setMercado(mercadoVencedor.nome());
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

        r.totalLote = menorTotal;
        listas.clear();
        return r;
    }
}
