# Garagem de Veículos

Atividade Prática — Arquitetura de Software (ESW430) — UniRV

Sistema web para cadastrar pessoas e veículos e reservar um veículo para uma pessoa por um período.

## Integrantes
- Fabricio Salvino Martins
- Henrique Antunes da Silva
- João Paulo Gomes de Oliveira Sousa

## Linguagem e framework
- Java 17 (JDK 17 ou superior)
- Spring Boot 3.5 com Spring Web + Thymeleaf
- Persistência em arquivos JSON (Jackson / ObjectMapper)

## Como executar
Na pasta do projeto (onde está o `pom.xml`):

Windows: dê dois cliques em `executar.cmd` (na primeira vez ele baixa o Maven sozinho).

Ou, com o Maven instalado:
```
mvn spring-boot:run
```

Depois abra no navegador: http://localhost:8080/

## Arquitetura
MVC + Repository + Injeção de Dependência, seguindo SOLID.

Caminho dos dados: **View → Controller → Interface → Repositório → arquivo JSON**

| Papel | Pessoa | Veículo | Reserva |
|---|---|---|---|
| Model | `model/Pessoa.java` | `model/Veiculo.java` | `model/Reserva.java` |
| Contrato (interface) | `repository/IPessoaRepository.java` | `repository/IVeiculoRepository.java` | `repository/IReservaRepository.java` |
| Implementação (JSON) | `repository/PessoaRepository.java` | `repository/VeiculoRepository.java` | `repository/ReservaRepository.java` |
| Controller | `controller/PessoaController.java` | `controller/VeiculoController.java` | `controller/ReservaController.java` |
| View do formulário | `templates/pessoa/PessoaForm.html` | `templates/veiculo/VeiculoForm.html` | `templates/reserva/index.html` |
| View da listagem | `templates/pessoa/index.html` | `templates/veiculo/index.html` | `templates/reserva/index.html` |
| Dados | `data/pessoas.json` | `data/veiculos.json` | `data/reservas.json` |

- Registro da DI: `@Repository` em cada repositório + injeção pelo construtor do Controller.
- Somente as classes `...Repository` leem e gravam os arquivos `.json`.
- O visual das telas fica em `src/main/resources/static/css/estilo.css`.

## SOLID no projeto
| Princípio | Onde aparece |
|---|---|
| S — Responsabilidade Única | Model guarda dados e validações; Repository só lê e grava o JSON; Controller só recebe o pedido, chama o repositório e escolhe a View. |
| O — Aberto/Fechado | Para trocar o JSON por banco de dados basta criar uma nova classe que implemente a interface; Controllers e Views não mudam. |
| L — Substituição de Liskov | Qualquer classe que implemente `IPessoaRepository` (JSON, memória ou banco) funciona no `PessoaController` sem ajuste. |
| I — Segregação de Interfaces | Uma interface por entidade (`IPessoaRepository`, `IVeiculoRepository`, `IReservaRepository`), cada uma só com os métodos que usa. |
| D — Inversão de Dependência | Os Controllers dependem das interfaces, recebidas pelo construtor; quem entrega a implementação é o Spring. |

## Rotas

### Reservas (página inicial)
| Operação | Rota |
|---|---|
| Página inicial (listar reservas e status dos veículos) | GET / |
| Criar reserva | POST /reservas/novo |
| Editar período | GET e POST /reservas/{id}/editar |
| Cancelar reserva | POST /reservas/{id}/excluir (com confirmação) |

### Pessoas
| Operação | Rota |
|---|---|
| Listar | GET /pessoas |
| Cadastrar | GET e POST /pessoas/novo |
| Editar | GET e POST /pessoas/{id}/editar |
| Excluir | POST /pessoas/{id}/excluir (com confirmação) |

## Rotas (Entrega 2 — Veículos)
| Operação | Rota |
|---|---|
| Listar | GET /veiculos |
| Cadastrar | GET e POST /veiculos/novo |
| Editar | GET e POST /veiculos/{id}/editar |
| Excluir | POST /veiculos/{id}/excluir (com confirmação) |

Arquivos: `model/Veiculo.java`, `repository/IVeiculoRepository.java`, `repository/VeiculoRepository.java`,
`controller/VeiculoController.java`, `templates/veiculo/VeiculoForm.html` e `index.html`. Dados em `data/veiculos.json`.

## Reservas (página inicial)
Vincula um veículo a uma pessoa por um período. Ao abrir o sistema (`http://localhost:8080/`) o usuário cai em Reservas.

| Operação | Rota |
|---|---|
| Listar / página inicial | GET / |
| Criar | POST /reservas/novo |
| Editar período | GET e POST /reservas/{id}/editar |
| Cancelar | POST /reservas/{id}/excluir (com confirmação) |

- Veículo e pessoa são escolhidos em listas carregadas dos repositórios.
- Regra de conflito (`IReservaRepository.existeConflito`): um veículo não pode ter reservas com períodos que se sobrepõem; a data de fim não pode ser anterior à de início.
- Cada veículo aparece como Disponível ou Reservado na data de hoje, calculado a partir das reservas.
- Dados em `data/reservas.json`.

## Ferramentas de IA usadas
- Claude (Anthropic)

## Prints
Na pasta `docs/`:

| Tela | Arquivo |
|---|---|
| Página inicial (Reservas) | `docs/print-reservas.png` |
| Listagem de Pessoas | `docs/print-pessoas.png` |
| Formulário de Pessoa | `docs/print-formulario-pessoa.png` |
| Listagem de Veículos | `docs/print-veiculos.png` |
| Formulário de Veículo | `docs/print-formulario-veiculo.png` |

