# <div align="center"> Relatório - Equipe Malcheirosos</div>


## 1. Integrantes do Grupo

<div align="center">
    
| Membro | Matrícula | GitHub |
|:---|:---:|:---|
| Amanda Cruz Lima | 231027023 | [@mandicrz](https://github.com/mandicrz) |
| Davi Marques do Egito Coelho | 231030421 | [@daviegito](https://github.com/daviegito) |
| Ludmila Aysha Oliveira Nune | 231026750 | [@ludmilaaysha](https://github.com/ludmilaaysha) |
| Othavio Araujo Bolzan | 231039150 | [@bolzanMGB](https://github.com/bolzanMGB) |
| Rafael Welz Schadt | 231011800 | [@RafaelSchadt](https://github.com/RafaelSchadt) |
| Rodrigo Ferreira do Amaral | 231011810 | [@rodrigoFAmaral](https://github.com/rodrigoFAmaral) |
| Yan Matheus Santa Brigida de Aguiar | 231038303 | [@Yanmatheus0812](https://github.com/Yanmatheus0812) |

<div align="center"> Tabela 1 </div>

</div>

## 1. Visão geral da arquitetura

_Em até 10 linhas: antes (o legado) e depois (suas classes e responsabilidades). Um diagrama simples é bem-vindo._

## 2. Code smells e refatorações

<div align="center">

| # | Smell encontrado (nome do catálogo) | Onde estava | Refatoração aplicada (nome) | Teste que protegeu | Commit |
| - | ------------------------------------ | ----------- | ----------------------------- | ------------------ | ------ |
| 1 |                                      |             |                               |                    |        |

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
| -------------------------------- | ----------------- | ------------------ | ------------------------ | ------------------------ |
| Barricada                        |                   |                    |                          |                          |
| Fail fast / pré-condição      |                   |                    |                          |                          |
| Asserção                       |                   |                    |                          |                          |
| Exceção com contexto           |                   |                    |                          |                          |
| Cópia defensiva / imutabilidade |                   |                    |                          |                          |
| `try-with-resources`           |                   |                    |                          |                          |
| Consulta parametrizada           |                   |                    |                          |                          |
| Log seguro                       |                   |                    |                          |                          |

<div align="center"> Tabela 4 </div>

</div>

## 5. Onde NÃO aplicamos defesa (e por quê)

_Defesa em excesso também é defeito. Cite pelo menos três lugares onde o grupo decidiu **não** validar ou **não** capturar algo, com a justificativa._

* **`buscarPorComprador` dos dublês de repositório não valida o formato RN-01.** Para um id fora do formato o dublê só devolve lista vazia, o que a RN-11 permite. A validação de verdade é responsabilidade do `PedidoRepositorioJdbc` e é testada lá; repetir a regra no dublê duplicaria código de produção dentro do teste.

## 6. Decisões difíceis e divergências do grupo

_Pelo menos duas decisões em que houve discordância, as alternativas e o critério usado._

## 7. Limitações conhecidas

_O que o sistema ainda não protege. Honestidade vale ponto na defesa._

* **`RepositorioEmMemoria` guarda a própria instância do `Pedido`, não uma cópia.** Enquanto o `Pedido` do legado for mutável, se o sistema alterar um pedido depois de salvá-lo o dublê mostra o valor novo, ao contrário de um banco real. A decisão é reavaliar depois que o `Pedido` ficar imutável (refatoração base, etapa 1.5).

## 8. Quem fez o quê

<div align="center">

| Integrante | Principais contribuições (commits, testes, partes do código) |
| ---------- | --------------------------------------------------------------- |
|    [Rafael Schadt](https://github.com/RafaelSchadt)    |  Ciação do repositório. I                                                       |
|    [Othavio Bolzan](https://github.com/bolzanMGB)        |    Clonagem do repositório template; Criação da readme.       I                                                       |
|    [Amanda Lima](https://github.com/mandicrz)    |  Dublês de repositório (etapa 1.2): `RepositorioEmMemoria`, `RepositorioQueFalha`, `PedidosDeTeste` e os testes `RepositorioEmMemoriaTest` e `RepositorioQueFalhaTest` |

<div align="center"> Tabela 5 </div>

</div>
