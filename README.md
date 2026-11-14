# Sistema de Pagamentos (estilo PIX)

Projeto da disciplina **Teste de Software** (UNICAP), por Thallis Henrique Ferreira.

API REST simples de pagamentos em **Java 17 + Spring Boot**, organizada em **arquitetura hexagonal**
(domínio, portas e adaptadores), com testes unitários em **JUnit 5** e **Mockito**.

## Casos de uso

| ID | Caso de uso | Endpoint |
|---|---|---|
| UC-01 | Solicitar pagamento | `POST /api/pagamentos` (header `Idempotency-Key`) |
| UC-02 | Processar pagamento | `POST /api/pagamentos/{id}/processar` |
| — | Consultar pagamento | `GET /api/pagamentos/{id}` |

Regras principais:

- valor deve ser positivo; remetente e destinatário são obrigatórios e diferentes;
- a mesma `Idempotency-Key` devolve o pagamento original (não duplica);
- status segue a máquina de estados `SOLICITADO → AUTORIZADO → CAPTURADO`, ou `→ FALHOU`;
- pagamentos acima de R$ 5.000,00 falham no processamento;
- pagamentos finalizados (`CAPTURADO` ou `FALHOU`) não são reprocessados.

## Estrutura

```
domain/model       → Pagamento, StatusPagamento, exceções (Java puro)
domain/port        → interfaces de entrada (casos de uso) e saída (repositório)
application        → PagamentoService (orquestra os casos de uso)
adapter/in/web     → controller REST e tratamento de erros
adapter/out        → repositórios: PostgreSQL (padrão) e em memória (perfil "memoria")
db/migration       → scripts Flyway: criação da tabela e carga inicial de dados
```

## Testes

| Classe de teste | O que valida |
|---|---|
| `StatusPagamentoTest` | transições permitidas/proibidas e estados terminais |
| `PagamentoTest` | validações de criação e transição de status |
| `PagamentoServiceTest` | casos de uso com o repositório simulado (Mockito: mocks e stubs) |
| `PagamentoServiceComStubTest` | casos de uso com um stub escrito à mão (`PagamentoRepositoryStub`) |
| `PagamentoControllerTest` | endpoints REST com `MockMvc` e `@MockBean` |

Rodar os testes unitários (não precisam de banco de dados):

```bash
./mvnw test
```

## Como rodar a aplicação

1. Subir o PostgreSQL com Docker:

```bash
docker compose up -d
```

2. Rodar a aplicação:

```bash
./mvnw spring-boot:run
```

Ao iniciar, o **Flyway** cria a tabela `pagamentos` e carrega 5 pagamentos de exemplo
(um em cada status), definidos em `src/main/resources/db/migration`.

Para rodar **sem Docker**, com os dados só em memória:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=memoria
```

## Teste de sistema (manual)

Com a aplicação rodando, acesse **http://localhost:8080/swagger** para testar os endpoints pelo navegador.

As evidências desses testes (prints do Swagger, ligados aos casos de teste CT-01 a CT-09) estão na pasta [`evidencias`](evidencias/README.md).

Pagamentos de exemplo da carga inicial:

| ID | Valor | Status | Sugestão de teste |
|---|---|---|---|
| `11111111-1111-1111-1111-111111111111` | 150,00 | SOLICITADO | processar → CAPTURADO |
| `22222222-2222-2222-2222-222222222222` | 4.999,90 | SOLICITADO | processar → CAPTURADO |
| `33333333-3333-3333-3333-333333333333` | 7.500,00 | SOLICITADO | processar → FALHOU (acima do limite) |
| `44444444-4444-4444-4444-444444444444` | 89,90 | CAPTURADO | processar → continua CAPTURADO |
| `55555555-5555-5555-5555-555555555555` | 6.200,00 | FALHOU | consultar |
