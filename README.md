# Universidade de Brasília – UnB <br>Faculdade de Ciências e Tecnologias em Engenharia – FCTE <br> Técnicas de Programação para Plataformas Emergentes – FGA0242 <br> Semestre 2026.2 <br> <br> <div align="center">Trabalho Final - Equipe Malcheirosos</div>

## 1. Resumo

Este repositório contém o desenvolvimento do Trabalho Final da disciplina Técnicas de Programação para Plataformas Emergentes (FGA0242), ofertada pela Universidade de Brasília. O projeto tem como objetivo aplicar conceitos de desenvolvimento de software, testes automatizados, refatoração e boas práticas de programação. O relatório completo do projeto está disponível em: [Relatório](docs/Relatorio.md).

## 2. Integrantes do Grupo

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

<div align="center"> Tabela 1  </div>

</div>

## 3. Tecnologias Utilizadas

<div align="center">
    
| Tecnologia | Descrição |
|:---:|---|
| **Java 17** | Linguagem e plataforma de desenvolvimento utilizada na implementação das funcionalidades e regras de negócio do sistema. |
| **JUnit 4.13.2** | Framework utilizado para a criação e execução de testes automatizados, apoiando a aplicação da prática de desenvolvimento orientado a testes (TDD). |
| **JDK 17** | Kit de desenvolvimento Java que fornece ferramentas necessárias para compilar, executar e depurar a aplicação. |
| **H2 2.3.232** | Sistema gerenciador de banco de dados relacional utilizado para persistência e manipulação dos dados da aplicação. |

<div align="center"> Tabela 2 </div>

</div>


## 4. Como Executar o Projeto

### 4.1. Instalação do Java

Para instalar o JDK no Ubuntu ou WSL, execute:

```
sudo apt update
sudo apt install -y openjdk-17-jdk

java -version
javac -version
```

### 4.2. Instalação das Dependências

Baixe as bibliotecas necessárias nos links abaixo:

<div align="center">

| Biblioteca    | Versão  | Download                                                                                                     |
| ------------- | ------- | ------------------------------------------------------------------------------------------------------------ |
| H2 Database   | 2.3.232 | [h2-2.3.232.jar](https://repo1.maven.org/maven2/com/h2database/h2/2.3.232/h2-2.3.232.jar)                    |
| JUnit         | 4.13.2  | [junit-4.13.2.jar](https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar)                       |
| Hamcrest Core | 1.3     | [hamcrest-core-1.3.jar](https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar) |

<div align="center"> Tabela 3 </div>

</div>

Após o download, coloque os três arquivos `.jar` no diretório `libs/` do projeto.

### 4.3. Configuração das Permissões

No terminal, na raiz do projeto, conceda permissão de execução aos scripts de compilação e testes:

```
chmod +x build.sh test.sh
```

### 4.4. Compilação

Para compilar a aplicação, execute:

```
./build.sh
```

### 4.5. Execução dos Testes

Após a compilação, execute os testes com:

```
./test.sh
```

