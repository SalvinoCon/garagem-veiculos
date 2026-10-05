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

### Veículos
| Operação | Rota |
|---|---|
| Listar | GET /veiculos |
| Cadastrar | GET e POST /veiculos/novo |
| Editar | GET e POST /veiculos/{id}/editar |
| Excluir | POST /veiculos/{id}/excluir (com confirmação) |

## Regra de negócio: conflito de reservas
Um veículo não pode ter duas reservas com períodos que se sobrepõem. Há conflito quando:

```
novaInicio <= existenteFim  E  novaFim >= existenteInicio
```

A verificação fica **fora do Controller**, no método `existeConflito(veiculoId, inicio, fim, idIgnorado)`
do `IReservaRepository`. Ao editar, a reserva ignora o próprio Id. O status "Disponível / Reservado"
de cada veículo é calculado a partir das reservas na data de hoje.

## Ferramentas de IA usadas
- Claude (Anthropic)

## Prints

### Entrega 1 — Pessoas
![Listagem de Pessoas](docs/Captura%20de%20tela%202026-09-28%20200232.png)

### Entrega 2 — Reservas
Página de Reservas:

![Página de Reservas](docs/Captura%20de%20tela%202026-10-05%20190314.png)

Tentativa de reserva em conflito sendo bloqueada:

![Reserva em conflito bloqueada](docs/Captura%20de%20tela%202026-10-05%20190417.png)
