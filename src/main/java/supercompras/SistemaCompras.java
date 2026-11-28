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
        // Agrupamento do lote (base do legado; sera substituido pela etapa 3.1)
        Map<String, Integer> lote = new LinkedHashMap<>();
        for (Map<String, Integer> lista : listas.values()) {
            for (Map.Entry<String, Integer> item : lista.entrySet()) {
                lote.merge(item.getKey(), item.getValue(), Integer::sum);
            }
        }
        Map<String, Integer> loteImutavel = Collections.unmodifiableMap(lote);

        // RN-05 e RN-06: calculo HALF_EVEN linha a linha + frete e desempate por nome()
        Mercado mercadoVencedor = null;
        BigDecimal menorTotal = null;
        Map<String, BigDecimal> precosVencedor = null;

        for (Mercado mercado : mercados) {
            try {
            Map<String, BigDecimal> cotados = mercado.cotarPrecosUnitarios(loteImutavel);
            BigDecimal frete = mercado.frete(loteImutavel);

            BigDecimal totalMercado = BigDecimal.ZERO;
            Map<String, BigDecimal> precosCopiados = new HashMap<>();

            for (Map.Entry<String, Integer> item : loteImutavel.entrySet()) {
                String sku = item.getKey();
                BigDecimal precoUnit = cotados.get(sku);
                precosCopiados.put(sku, precoUnit);

                BigDecimal valorLinha = precoUnit
                        .multiply(BigDecimal.valueOf(item.getValue()))
                        .setScale(2, RoundingMode.HALF_EVEN);
                totalMercado = totalMercado.add(valorLinha);
            }
            totalMercado = totalMercado.add(frete.setScale(2, RoundingMode.HALF_EVEN));

            boolean ehMelhor = (menorTotal == null)
                    || (totalMercado.compareTo(menorTotal) < 0)
                    || (totalMercado.compareTo(menorTotal) == 0
                        && mercado.nome().compareTo(mercadoVencedor.nome()) < 0);

            if (ehMelhor) {
                mercadoVencedor = mercado;
                menorTotal = totalMercado;
                precosVencedor = precosCopiados;
            }
        } catch (MercadoIndisponivelException e) {
            continue;
            }
        }

        // Compra (sera substituida pelo fail-over na etapa 4.2)
        String protocolo = null;
        try {
            protocolo = mercadoVencedor.comprar(loteImutavel, precosVencedor);
        } catch (Exception e) {
            e.printStackTrace();
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
