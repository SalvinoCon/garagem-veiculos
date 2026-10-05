# Garagem de Veículos

Atividade Prática — Arquitetura de Software (ESW430) — UniRV

## Integrantes
- Fabricio Salvino Martins
- Henrique Antunes da Silva
- João Paulo Gomes de Oliveira Sousa

## Linguagem e framework
- Java 17 (JDK 17 ou superior)
- Spring Boot 3.5 com Spring Web + Thymeleaf
- Persistência em arquivos JSON (Jackson / ObjectMapper)

## Arquitetura
MVC + Repository + Injeção de Dependência, seguindo SOLID.

Caminho dos dados: **View → Controller → Interface → Repositório → arquivo JSON**

| Papel | Arquivo |
|---|---|
| Model | `model/Pessoa.java` |
| Contrato (interface) | `repository/IPessoaRepository.java` |
| Implementação (JSON) | `repository/PessoaRepository.java` |
| Controller | `controller/PessoaController.java` |
| View do formulário | `templates/pessoa/PessoaForm.html` |
| View da listagem | `templates/pessoa/index.html` |
| Registro da DI | `@Repository` na classe + injeção pelo construtor |

Os dados ficam em `data/pessoas.json`. Somente o `PessoaRepository` lê e grava esse arquivo.

## Como executar
Na pasta do projeto (onde está o `pom.xml`):

Windows: dê dois cliques em `executar.cmd` (na primeira vez ele baixa o Maven sozinho).

Ou, com o Maven instalado:
```
mvn spring-boot:run
```

Depois abra no navegador: http://localhost:8080/pessoas

## Rotas (Entrega 1 — Pessoas)
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

