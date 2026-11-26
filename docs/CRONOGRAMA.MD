#  <div align="center"> Cronograma do Projeto </div>

## 1. Datas das Entregas

<div align="center">

<table>
  <thead>
    <tr>
      <th colspan="2">Fase</th>
      <th colspan="7">Etapa</th>
    </tr>
    <tr>
      <th>ID</th>
      <th>Nome</th>
      <th>ID</th>
      <th>Nome</th>
      <th>Responsável</th>
      <th>Início</th>
      <th>Entrega</th>
      <th>Tipo</th>
      <th>Roda em paralelo com</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>1</td>
      <td>Fundação</td>
      <td>1.1</td>
      <td>Build</td>
      <td>Othavio</td>
      <td>25/11</td>
      <td>26/11</td>
      <td>-</td>
      <td>-</td>
    </tr>
    <tr>
      <td>1</td>
      <td>Fundação</td>
      <td>1.2</td>
      <td>Dublês de Teste</td>
      <td>Ludmila + Amanda</td>
      <td>25/11</td>
      <td>26/11</td>
      <td>Paralela</td>
      <td>1.3, 1.4</td>
    </tr>
    <tr>
      <td>1</td>
      <td>Fundação</td>
      <td>1.3</td>
      <td>Testes de Caracterização</td>
      <td>Todos</td>
      <td>25/11</td>
      <td>26/11</td>
      <td>Paralela</td>
      <td>1.2, 1.4</td>
    </tr>
    <tr>
      <td>1</td>
      <td>Fundação</td>
      <td>1.4</td>
      <td>Code Smells</td>
      <td>Todos</td>
      <td>25/11</td>
      <td>26/11</td>
      <td>Paralela</td>
      <td>1.2, 1.3</td>
    </tr>
    <tr>
      <td>1</td>
      <td>Fundação</td>
      <td>1.5</td>
      <td>Refatoração Base</td>
      <td>Othavio + Yan</td>
      <td>26/11</td>
      <td>27/11</td>
      <td>Paralela</td>
      <td>2.1, 2.2, 2.3</td>
    </tr>
    <tr>
      <td>2</td>
      <td>Fronteira e Infra</td>
      <td>2.1</td>
      <td>Logging seguro</td>
      <td>Ludmila</td>
      <td>26/11</td>
      <td>26/11</td>
      <td>Paralela</td>
      <td>1.5, 2.2</td>
    </tr>
    <tr>
      <td>2</td>
      <td>Fronteira e Infra</td>
      <td>2.2</td>
      <td>Validação de comprador</td>
      <td>Yan</td>
      <td>26/11</td>
      <td>26/11</td>
      <td>Paralela</td>
      <td>1.5, 2.1</td>
    </tr>
    <tr>
      <td>2</td>
      <td>Fronteira e Infra</td>
      <td>2.3</td>
      <td>Validação de lista</td>
      <td>Yan + Ludmila</td>
      <td>26/11</td>
      <td>27/11</td>
      <td>Sequencial (2.1 e 2.2)</td>
      <td>1.5, 4.1, 5.1</td>
    </tr>
    <tr>
      <td>3</td>
      <td>Core de Cotação</td>
      <td>3.1</td>
      <td>Lote</td>
      <td>Rafael</td>
      <td>27/11</td>
      <td>27/11</td>
      <td>Sequencial (2.3)</td>
      <td>4.1, 5.1</td>
    </tr>
    <tr>
      <td>3</td>
      <td>Core de Cotação</td>
      <td>3.2</td>
      <td>Cotação nos mercados</td>
      <td>Rafael</td>
      <td>27/11</td>
      <td>28/11</td>
      <td>Sequencial (3.1)</td>
      <td>4.1, 5.1</td>
    </tr>
    <tr>
      <td>3</td>
      <td>Core de Cotação</td>
      <td>3.3</td>
      <td>Total e desempate</td>
      <td>Davi</td>
      <td>28/11</td>
      <td>28/11</td>
      <td>Sequencial (3.2)</td>
      <td>4.1, 5.1</td>
    </tr>
    <tr>
      <td>4</td>
      <td>Compra e Rateio</td>
      <td>4.1</td>
      <td>Fracionamento</td>
      <td>Amanda</td>
      <td>26/11</td>
      <td>28/11</td>
      <td>Paralela (começa com dados fictícios)</td>
      <td>2.3, 3.x, 5.1</td>
    </tr>
    <tr>
      <td>4</td>
      <td>Compra e Rateio</td>
      <td>4.2</td>
      <td>Compra e fail-over</td>
      <td>Davi</td>
      <td>28/11</td>
      <td>29/11</td>
      <td>Sequencial (3.3)</td>
      <td>5.1</td>
    </tr>
    <tr>
      <td>4</td>
      <td>Compra e Rateio</td>
      <td>4.3</td>
      <td>Formato do resultado</td>
      <td>Amanda + Davi</td>
      <td>29/11</td>
      <td>30/11</td>
      <td>Sequencial (4.1 e 4.2)</td>
      <td>5.1</td>
    </tr>
    <tr>
      <td>5</td>
      <td>Persistência</td>
      <td>5.1</td>
      <td>Repositório JDBC</td>
      <td>Rodrigo</td>
      <td>26/11</td>
      <td>30/11</td>
      <td>Paralela (começa com <code>Pedido</code> provisório)</td>
      <td>2.3, 3.x, 4.x</td>
    </tr>
    <tr>
      <td>5</td>
      <td>Persistência</td>
      <td>5.2</td>
      <td>Encerramento da rodada</td>
      <td>Rodrigo + Othavio</td>
      <td>30/11</td>
      <td>1/12</td>
      <td>Sequencial (4.3 e 5.1)</td>
      <td>6.1</td>
    </tr>
    <tr>
      <td>6</td>
      <td>Fechamento</td>
      <td>6.1</td>
      <td>Ataque e histórico</td>
      <td>Todos</td>
      <td>30/11</td>
      <td>1/12</td>
      <td><strong>Geral</strong> (todos juntos)</td>
      <td>5.2</td>
    </tr>
    <tr>
      <td>6</td>
      <td>Fechamento</td>
      <td>6.2</td>
      <td>Relatório</td>
      <td>Todos</td>
      <td>25/11</td>
      <td>1/12</td>
      <td><strong>Geral</strong> (todos, o tempo todo)</td>
      <td>Todas</td>
    </tr>
  </tbody>
</table>

<div align="center"> Tabela 1 </div>

</div>

## 2. Resumo


<div align="center">

<table>
  <thead>
    <tr>
      <th>Pessoa</th>
      <th>Etapas</th>
      <th>Responsabilidade extra</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Othavio</td>
      <td>1.1, 1.5, 5.2, 6.1</td>
      <td>Build e revisão geral</td>
    </tr>
    <tr>
      <td>Yan</td>
      <td>1.5, 2.2, 2.3, 6.1</td>
      <td>-</td>
    </tr>
    <tr>
      <td>Ludmila</td>
      <td>1.2, 2.1, 2.3, 6.1, 6.2</td>
      <td>Dublês de mercados falsos/hostis, consolidação do <code>RELATORIO.md</code></td>
    </tr>
    <tr>
      <td>Rafael</td>
      <td>3.1, 3.2, 6.1</td>
      <td>Revisão geral</td>
    </tr>
    <tr>
      <td>Davi</td>
      <td>3.3, 4.2, 4.3, 6.1</td>
      <td>-</td>
    </tr>
    <tr>
      <td>Amanda</td>
      <td>1.2, 4.1, 4.3, 6.1, 6.2</td>
      <td>Dublês de repositório (em memória e com falha), consolidação do <code>RELATORIO.md</code></td>
    </tr>
    <tr>
      <td>Rodrigo</td>
      <td>5.1, 5.2, 6.1</td>
      <td>-</td>
    </tr>
  </tbody>
</table>

<div align="center"> Tabela 2 </div>

</div>

## 3. Descrição das Etapas

<div align="center">

<table>
  <thead>
    <tr>
      <th>ID</th>
      <th>Nome</th>
      <th>Descrição</th>
      <th>Dependência</th>
      <th>Observação</th>
      <th>Responsável</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>1.1</td>
      <td>Build</td>
      <td>Setup do build com <code>-ea</code> (enable assertions) e criação do <code>ContratoApiTest</code> (contrato da API que todas as etapas devem respeitar)</td>
      <td>-</td>
      <td>Base para todo o projeto. Sem o build com <code>-ea</code> e o contrato, as demais etapas não têm como validar nada</td>
      <td><strong>Othavio</strong></td>
    </tr>
    <tr>
      <td>1.2</td>
      <td>Dublês de Teste</td>
      <td>Criação dos dublês de teste: <strong>mercados falsos/hostis</strong> + <strong>repositório em memória</strong> e <strong>repositório que falha</strong></td>
      <td>1.1</td>
      <td>Ludmila: mercados falsos/hostis. Amanda: repositório em memória e repositório que falha. Base para testar RN-01 a RN-12 sem depender de H2 real ou mercados externos</td>
      <td><strong>Ludmila</strong> (mercados falsos/hostis) + <strong>Amanda</strong> (repositório em memória e repositório que falha)</td>
    </tr>
    <tr>
      <td>1.3</td>
      <td>Testes de Caracterização</td>
      <td>Testes que capturam o comportamento <strong>atual</strong> do sistema legado antes de qualquer refatoração (devem ficar verdes na 1.5)</td>
      <td>1.1, 1.2</td>
      <td>Protegem a refatoração base (1.5); sem eles, não há como garantir que o comportamento original foi preservado</td>
      <td><strong>Todos</strong></td>
    </tr>
    <tr>
      <td>1.4</td>
      <td>Code Smells</td>
      <td>Levantamento e listagem dos <em>code smells</em> do código legado (estado estático, métodos longos, <code>double</code> em vez de <code>BigDecimal</code>, etc.)</td>
      <td>1.1</td>
      <td>Serve de roteiro para a refatoração base (1.5) e alimenta o <code>RELATORIO.md</code> (6.2)</td>
      <td><strong>Todos</strong></td>
    </tr>
    <tr>
      <td>1.5</td>
      <td>Refatoração Base</td>
      <td>Remover estado estático, quebrar métodos longos, migrar <code>double</code> para <code>BigDecimal</code> (com testes de caracterização verdes)</td>
      <td>1.1, 1.2, 1.3, 1.4</td>
      <td>Deixa o TDD viável para RN-01 a RN-12</td>
      <td><strong>Othavio</strong> + <strong>Yan</strong></td>
    </tr>
    <tr>
      <td>2.1</td>
      <td>Logging seguro</td>
      <td>RN-11 (Logging): utilitário sobre <code>java.util.logging</code> sem entrada bruta e sem quebra de linha (<em>log injection</em>)</td>
      <td>1.1</td>
      <td>Deixa RN-02 e RN-04 viáveis</td>
      <td><strong>Ludmila</strong></td>
    </tr>
    <tr>
      <td>2.2</td>
      <td>Validação de comprador</td>
      <td>RN-01: validação de <code>compradorId</code> (<code>[a-z0-9][a-z0-9_.-]{2,29}</code> no texto inteiro)</td>
      <td>1.1</td>
      <td>Permite RN-02 e RN-11 (<code>buscarPorComprador</code>)</td>
      <td><strong>Yan</strong></td>
    </tr>
    <tr>
      <td>2.3</td>
      <td>Validação de lista</td>
      <td>RN-02: SKU, quantidade 1–999, 1–20 itens, 1 lista por rodada, atomicidade ("sem rastro"), cópia defensiva, exceção genérica + log <code>WARNING</code></td>
      <td>2.1, 2.2</td>
      <td>Deixa RN-03 viável</td>
      <td><strong>Yan</strong> + <strong>Ludmila</strong></td>
    </tr>
    <tr>
      <td>3.1</td>
      <td>Lote</td>
      <td>RN-03: consolidação do lote (soma por SKU) como mapa imutável</td>
      <td>2.3</td>
      <td>Deixa RN-04 e RN-08 viáveis</td>
      <td><strong>Rafael</strong></td>
    </tr>
    <tr>
      <td>3.2</td>
      <td>Cotação nos mercados</td>
      <td>RN-04: desqualificação, nunca capturar <code>Error</code>, preço ≥ 0,01, frete ≥ 0, cópia imediata dos preços, log em 1 linha</td>
      <td>2.1, 3.1</td>
      <td>Deixa RN-05 e RN-08 viáveis</td>
      <td><strong>Rafael</strong></td>
    </tr>
    <tr>
      <td>3.3</td>
      <td>Total e desempate</td>
      <td>RN-05 + RN-06: total por mercado (<code>HALF_EVEN</code> linha a linha + frete) e desempate por menor <code>nome()</code> (<code>compareTo</code>)</td>
      <td>1.5, 3.2</td>
      <td>Deixa RN-07 e RN-08 viáveis</td>
      <td><strong>Davi</strong></td>
    </tr>
    <tr>
      <td>4.1</td>
      <td>Fracionamento</td>
      <td>RN-07: método do maior resto (SKUs por quantidade, frete por subtotal, empate por menor <code>compradorId</code>, soma exata)</td>
      <td>3.3</td>
      <td>Deixa RN-09 viável. Matemática pura: pode começar antes com dados fictícios</td>
      <td><strong>Amanda</strong></td>
    </tr>
    <tr>
      <td>4.2</td>
      <td>Compra e fail-over</td>
      <td>RN-08: ordem RN-05/06, entradas imutáveis, para na 1ª compra válida, nunca repete mercado, mantém listas pendentes se falhar</td>
      <td>3.1, 3.2, 3.3</td>
      <td>Deixa RN-09 e RN-10 viáveis</td>
      <td><strong>Davi</strong></td>
    </tr>
    <tr>
      <td>4.3</td>
      <td>Formato do resultado</td>
      <td>RN-09 + RN-12 (saída): escala 2 em todo <code>BigDecimal</code>, <code>id = protocolo + "-" + compradorId</code>, pedidos ordenados, coleções imutáveis</td>
      <td>4.1, 4.2</td>
      <td>Deixa RN-10 e integração JDBC viáveis</td>
      <td><strong>Amanda</strong> + <strong>Davi</strong></td>
    </tr>
    <tr>
      <td>5.1</td>
      <td>Repositório JDBC</td>
      <td>RN-11 (SQL) + RN-12 (<code>PedidoRepositorioJdbc</code>): esquema no construtor, consultas parametrizadas, dados literais, transação com restauração de <em>autocommit</em>, <code>try-with-resources</code>, nunca fechar a conexão, validação em <code>buscarPorComprador</code></td>
      <td>2.2, 4.3</td>
      <td>Integração final com H2 real. Pode começar cedo com um <code>Pedido</code> provisório</td>
      <td><strong>Rodrigo</strong></td>
    </tr>
    <tr>
      <td>5.2</td>
      <td>Encerramento da rodada</td>
      <td>RN-10: <code>IllegalStateException</code> se vazia, salvar um a um em ordem crescente sem parar na 1ª falha, <code>PersistenciaException</code> com protocolo + mercado + causa + suprimidas</td>
      <td>4.2, 4.3, 5.1</td>
      <td>Testes de ponta a ponta</td>
      <td><strong>Rodrigo</strong> + <strong>Othavio</strong> (par)</td>
    </tr>
    <tr>
      <td>6.1</td>
      <td>Ataque e histórico</td>
      <td>Testes de ataque (concorrência/mutação, mercado hostil, SQL/log injection) e revisão do <code>git log</code> (TDD visível)</td>
      <td>Todas as etapas da Fase 5</td>
      <td>Necessário para a entrega final</td>
      <td><strong>Todos</strong> (ataque cruzado: cada um ataca a etapa de outra pessoa)</td>
    </tr>
    <tr>
      <td>6.2</td>
      <td>Relatório</td>
      <td>Preenchimento e fechamento do <code>RELATORIO.md</code> (modelo <code>RELATORIO-modelo.md</code>): para cada refatoração e técnica defensiva, onde foi aplicada, por quê, qual teste a protege e o hash do commit</td>
      <td>6.1</td>
      <td>O relatório deve ser atualizado a cada etapa concluída, não só no final. Serve de roteiro para a defesa, mas não substitui o conhecimento individual</td>
      <td><strong>Amanda</strong> e <strong>Ludmila</strong> (consolidam) + <strong>todos</strong> (cada um preenche a própria entrada)</td>
    </tr>
  </tbody>
</table>

<div align="center"> Tabela 3 </div>

</div>