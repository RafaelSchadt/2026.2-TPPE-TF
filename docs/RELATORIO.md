# <div align="center"> Relatório - Equipe Malcheirosos</div>


## 1. Integrantes do Grupo

<div align="center">
    
| Membro                              | Matrícula | GitHub                                               |
| :---------------------------------- | :-------: | :--------------------------------------------------- |
| Amanda Cruz Lima                    | 231027023 | [@mandicrz](https://github.com/mandicrz)             |
| Davi Marques do Egito Coelho        | 231030421 | [@daviegito](https://github.com/daviegito)           |
| Ludmila Aysha Oliveira Nune         | 231026750 | [@ludmilaaysha](https://github.com/ludmilaaysha)     |
| Othavio Araujo Bolzan               | 231039150 | [@bolzanMGB](https://github.com/bolzanMGB)           |
| Rafael Welz Schadt                  | 231011800 | [@RafaelSchadt](https://github.com/RafaelSchadt)     |
| Rodrigo Ferreira do Amaral          | 231011810 | [@rodrigoFAmaral](https://github.com/rodrigoFAmaral) |
| Yan Matheus Santa Brigida de Aguiar | 231038303 | [@Yanmatheus0812](https://github.com/Yanmatheus0812) |

<div align="center"> Tabela 1 </div>

</div>

## 1. Visão geral da arquitetura

_Em até 10 linhas: antes (o legado) e depois (suas classes e responsabilidades). Um diagrama simples é bem-vindo._

## 2. Code smells e refatorações

<div align="center">

| #   | Smell encontrado (nome do catálogo) | Onde estava | Refatoração candidata (nome) | Teste que protegerá | Commit |
| --- | ----------------------------------- | ----------- | --------------------------- | ------------------ | ------ |
| SM-01 | Método longo (`Long Method`) | `SistemaCompras.fecharRodada()` (`src/main/java/supercompras/SistemaCompras.java:31-118`) | Extrair métodos/coordenadores para lote, cotação, compra, fracionamento e persistência (`Extract Method`); preservar um método-orquestrador curto | Pendente: testes de caracterização e testes das RN-03 a RN-10 | Pendente |
| SM-02 | Estado global (`Global State`) | `contador` estático em `SistemaCompras` (`src/main/java/supercompras/SistemaCompras.java:14`) | Remover o estado estático e tornar o estado da rodada responsabilidade de instâncias (`Replace Global State with Class State`) | Pendente: testes de caracterização da rodada | Pendente |
| SM-03 | Campos públicos (`Public Fields`) | `mercados`, `repo`, `listas` em `SistemaCompras` (`src/main/java/supercompras/SistemaCompras.java:11-14`) | Encapsular campos com `private` e exposição controlada (`Encapsulate Field`) | Pendente: comportamento da instância e mutação externa | Pendente |
| SM-04 | Classe de dados (`Data Class`) | `Pedido` (`src/main/java/supercompras/Pedido.java:7-35`) e `ResultadoRodada` (`src/main/java/supercompras/ResultadoRodada.java:7-15`) | Tornar os objetos imutáveis e construir o estado em construtores/fábricas (`Encapsulate Record`, `Introduce Parameter Object`) | Pendente: imutabilidade das coleções e objetos devolvidos | Pendente |
| SM-05 | Exposição de representação interna (`Inappropriate Intimacy` / `Exposure of Internals`) | Getters e mapas mutáveis de `Pedido`, `ResultadoRodada` e listas internas de `SistemaCompras` | Devolver cópias ou coleções imutáveis e proteger o estado interno (`Encapsulate Collection`, cópia defensiva) | Pendente: ataques de mutação das coleções | Pendente |
| SM-06 | Número em ponto flutuante para dinheiro (`Primitive Obsession`) | Uso de `double` e `BigDecimal.valueOf(double)` em `SistemaCompras` (`src/main/java/supercompras/SistemaCompras.java:47-50,59,88,98-108`) | Representar todo valor monetário com `BigDecimal`, centavos inteiros quando conveniente e `HALF_EVEN` (`Replace Primitive with Object` / `Introduce Monetary Value`) | Pendente: RN-05, RN-07 e RN-09 | Pendente |
| SM-07 | Literais mágicos (`Magic Numbers`) | Limite `999999999` em `SistemaCompras` (`src/main/java/supercompras/SistemaCompras.java:47`) e arredondamentos aproximados em centavos (`:108-115`) | Extrair constantes ou tipos auxiliares com nomes do domínio (`Replace Magic Number with Symbolic Constant`) | Pendente: RN-05 e RN-09 | Pendente |
| SM-08 | Responsabilidades misturadas (`Large Class` / `God Object`) | `SistemaCompras` valida entradas, agrega lotes, cita mercados, calcula valores, cria pedidos e persiste dados (`src/main/java/supercompras/SistemaCompras.java:10-118`) | Extrair classes responsáveis por lote, cotação, fracionamento e encerramento (`Extract Class`) | Pendente: caracterização antes da extração | Pendente |
| SM-09 | Captura excessiva de erros (`Catch Throwable`) | `catch (Throwable t)` em `SistemaCompras` (`src/main/java/supercompras/SistemaCompras.java:63-66`) | Capturar somente `MercadoIndisponivelException` e `RuntimeException`; permitir a propagação de `Error` (`Replace Exception with Test`) | Pendente: mercado hostil com `Error` | Pendente |
| SM-10 | Exceções engolidas (`Ignored Error`) | Tratamentos vazios ou incompletos em compra/frete e persistência (`SistemaCompras.java:77-88`; `PedidoRepositorioJdbc.java:22-23,37-38,64-65`) | Propagar exceções com contexto ou tratar no nível adequado (`Replace Error-Code with Exception`, exceção com causa) | Pendente: compra falha e falhas SQL | Pendente |
| SM-11 | Logging inseguro/indevido (`Logging` incorreto) | `System.out.println` em `SistemaCompras.java:23,64,78,86`; `printStackTrace()` em `SistemaCompras.java:78` e `PedidoRepositorioJdbc.java:23,38,65` | Usar `java.util.logging`, mensagens genéricas e sanitizadas, sem dados brutos (`Introduce Safe Logging`) | Pendente: RN-02 e RN-11 | Pendente |
| SM-12 | Injeção de SQL (`SQL Injection`) | Concatenação de dados em SQL em `PedidoRepositorioJdbc` (`src/main/java/supercompras/PedidoRepositorioJdbc.java:30-35,46,57`) | Usar `PreparedStatement` com parâmetros e consultas explícitas (`Replace SQL by Parameterized Query`) | Pendente: mercado/comprador com aspas e comandos SQL | Pendente |
| SM-13 | Vazamento de recursos (`Resource Leak`) | `Statement`, `PreparedStatement` e `ResultSet` não são fechados corretamente em `PedidoRepositorioJdbc` | Fechar todos os recursos em `try-with-resources`, inclusive em caminhos de falha | Pendente: falhas JDBC e verificação de recursos | Pendente |
| SM-14 | Tratamento de falhas de banco incompleto (`Incomplete Database Transaction`) | `PedidoRepositorioJdbc.salvar()` não restaura o `autocommit`, não usa transação explícita e engole SQLExceptions (`src/main/java/supercompras/PedidoRepositorioJdbc.java:27-39`) | Usar transação, `try-with-resources`, restauração do `autocommit` e `PersistenciaException` com causa (`Extract Method`, `Encapsulate Transaction`) | Pendente: RN-12 | Pendente |

<div align="center"> Tabela 2 </div>

</div>

## 3. TDD: regra por regra

<div align="center">

| Regra | Teste(s) que a especificam | Commit vermelho (`test:`) | Commit verde (`feat:`/`fix:`) | Refatoração posterior |
| ----- | -------------------------- | --------------------------- | --------------------------------- | ----------------------- |
| RN-01 |                            |                             |                                   |                         |
| RN-02 |                            |                             |                                   |                         |
| RN-03 |                            |                             |                                   |                         |
| RN-04 |                            |                             |                                   |                         |
| RN-05 |                            |                             |                                   |                         |
| RN-06 |                            |                             |                                   |                         |
| RN-07 |                            |                             |                                   |                         |
| RN-08 |                            |                             |                                   |                         |
| RN-09 |                            |                             |                                   |                         |
| RN-10 |                            |                             |                                   |                         |
| RN-11 |                            |                             |                                   |                         |
| RN-12 |                            |                             |                                   |                         |

<div align="center"> Tabela 3 </div>

</div>

## 4. Defesas: técnica, lugar, ataque e teste

<div align="center">

| Técnica defensiva               | Onde foi aplicada | Problema que evita | Teste que ataca a defesa | Custo / efeito colateral |
| ------------------------------- | ----------------- | ------------------ | ------------------------ | ------------------------ |
| Barricada                       |                   |                    |                          |                          |
| Fail fast / pré-condição        |                   |                    |                          |                          |
| Asserção                        |                   |                    |                          |                          |
| Exceção com contexto            |                   |                    |                          |                          |
| Cópia defensiva / imutabilidade |                   |                    |                          |                          |
| `try-with-resources`            |                   |                    |                          |                          |
| Consulta parametrizada          |                   |                    |                          |                          |
| Log seguro                      |                   |                    |                          |                          |

<div align="center"> Tabela 4 </div>

</div>

## 5. Onde NÃO aplicamos defesa (e por quê)

_Defesa em excesso também é defeito. Cite pelo menos três lugares onde o grupo decidiu **não** validar ou **não** capturar algo, com a justificativa._

## 6. Decisões difíceis e divergências do grupo

_Pelo menos duas decisões em que houve discordância, as alternativas e o critério usado._

## 7. Limitações conhecidas

_O que o sistema ainda não protege. Honestidade vale ponto na defesa._

## 8. Quem fez o quê

<div align="center">

| Integrante | Principais contribuições (commits, testes, partes do código) |
| ---------- | --------------------------------------------------------------- |
| [Rafael Schadt](https://github.com/RafaelSchadt) |  Criação do repositório.                                                       |
| [Othavio Bolzan](https://github.com/bolzanMGB)   |    Clonagem do repositório template; Criação da readme.                                                       |
| [Amanda Lima](https://github.com/mandicrz)       |  Dublês de repositório (etapa 1.2): `RepositorioEmMemoria`, `RepositorioQueFalha`, `PedidosDeTeste` e os testes `RepositorioEmMemoriaTest` e `RepositorioQueFalhaTest` |
| [Ludmila Nune](https://github.com/ludmilaaysha)  |  Dublês de mercado (etapa 1.2): `MercadoFalso` (mercado bem-comportado e configurável) e `MercadoHostil` (injeta falhas) e os testes `MercadoFalsoTest` e `MercadoHostilTest` |

<div align="center"> Tabela 5 </div>

</div>
