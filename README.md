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
adapter/out        → repositório em memória
```

## Testes

| Classe de teste | O que valida |
|---|---|
| `StatusPagamentoTest` | transições permitidas/proibidas e estados terminais |
| `PagamentoTest` | validações de criação e transição de status |
| `PagamentoServiceTest` | casos de uso com o repositório simulado (Mockito) |

Rodar os testes:

```bash
./mvnw test        # ou: mvn test
```

Rodar a aplicação:

```bash
mvn spring-boot:run
```
