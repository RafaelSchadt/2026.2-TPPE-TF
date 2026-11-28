package supercompras;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PedidoRepositorioJdbcCaracterizacaoTest {

    private Connection conexao;
    private PedidoRepositorioJdbc repositorio;

    @Before
    public void configurar() throws SQLException {
        conexao = DriverManager.getConnection("jdbc:h2:mem:carac_" + UUID.randomUUID());
        repositorio = new PedidoRepositorioJdbc(conexao);
    }

    @After
    public void encerrar() throws SQLException {
        if (conexao != null && !conexao.isClosed()) {
            conexao.close();
        }
    }

    @Test
    public void construtor_criaEsquemaENaoFechaConexao() throws SQLException {
        assertFalse(conexao.isClosed());
        assertTrue(repositorio.buscarPorComprador("inexistente").isEmpty());
    }

    @Test
    public void salvarEBuscarPorComprador_persisteERecuperaPedidoComItens() {
        Pedido pedido = PedidosDeTeste.pedidoCom(
                "ana",
                Map.of("LEI-0001", 2, "ARR-0002", 1),
                Map.of("LEI-0001", new BigDecimal("6.00"), "ARR-0002", new BigDecimal("4.00")));

        repositorio.salvar(pedido);

        List<Pedido> encontrados = repositorio.buscarPorComprador("ana");
        assertEquals(1, encontrados.size());
        Pedido salvo = encontrados.get(0);
        assertEquals(pedido.getId(), salvo.getId());
        assertEquals("ana", salvo.getCompradorId());
        assertEquals(pedido.getMercado(), salvo.getMercado());
        assertEquals(pedido.getProtocolo(), salvo.getProtocolo());
        assertEquals(0, pedido.getSubtotal().compareTo(salvo.getSubtotal()));
        assertEquals(0, pedido.getFrete().compareTo(salvo.getFrete()));
        assertEquals(0, pedido.getTotal().compareTo(salvo.getTotal()));
        assertEquals(Map.of("LEI-0001", 2, "ARR-0002", 1), salvo.getItens());
        assertEquals(0, new BigDecimal("6.00").compareTo(salvo.getValoresPorSku().get("LEI-0001")));
        assertEquals(0, new BigDecimal("4.00").compareTo(salvo.getValoresPorSku().get("ARR-0002")));
    }

    @Test
    public void buscarPorComprador_retornaApenasPedidosDoCompradorInformado() {
        repositorio.salvar(PedidosDeTeste.pedidoDe("ana"));
        repositorio.salvar(PedidosDeTeste.pedidoDe("bia"));

        List<Pedido> encontrados = repositorio.buscarPorComprador("ana");

        assertEquals(1, encontrados.size());
        assertEquals("ana", encontrados.get(0).getCompradorId());
    }

    @Test
    public void buscarPorComprador_vulneravelASqlInjectionRetornaPedidosDeOutros() {
        repositorio.salvar(PedidosDeTeste.pedidoDe("ana"));
        repositorio.salvar(PedidosDeTeste.pedidoDe("bia"));

        List<Pedido> vazados = repositorio.buscarPorComprador("' OR '1'='1");

        assertEquals(2, vazados.size());
    }

    @Test
    public void salvar_engoleSqlExceptionQuandoIdDuplicado() {
        Pedido pedido = PedidosDeTeste.pedidoDe("ana");

        repositorio.salvar(pedido);
        repositorio.salvar(pedido);

        assertEquals(1, repositorio.buscarPorComprador("ana").size());
    }

    @Test
    public void salvar_engoleSqlExceptionQuandoMercadoContemAspasSimples() {
        Pedido pedido = PedidosDeTeste.pedidoDe("ana");
        pedido.setMercado("Mercado D'Agua");

        repositorio.salvar(pedido);

        assertTrue(repositorio.buscarPorComprador("ana").isEmpty());
    }

    @Test
    public void salvar_naoUsaTransacaoEDeixaPedidoSemItensQuandoItemFalha() {
        Pedido pedido = PedidosDeTeste.pedidoCom(
                "ana",
                Map.of("SKU'ERR", 1),
                Map.of("SKU'ERR", new BigDecimal("10.00")));

        repositorio.salvar(pedido);

        List<Pedido> encontrados = repositorio.buscarPorComprador("ana");
        assertEquals(1, encontrados.size());
        assertTrue(encontrados.get(0).getItens().isEmpty());
    }

    @Test
    public void buscarPorComprador_expoeListaEColecoesDoPedidoMutaveis() {
        Pedido pedido = PedidosDeTeste.pedidoCom(
                "ana",
                Map.of("LEI-0001", 2),
                Map.of("LEI-0001", new BigDecimal("10.00")));
        repositorio.salvar(pedido);

        List<Pedido> encontrados = repositorio.buscarPorComprador("ana");
        Pedido salvo = encontrados.get(0);
        salvo.getItens().put("EXT-0001", 1);
        salvo.getValoresPorSku().put("EXT-0001", new BigDecimal("1.00"));
        encontrados.clear();

        assertEquals(Integer.valueOf(1), salvo.getItens().get("EXT-0001"));
        assertTrue(encontrados.isEmpty());
    }
}