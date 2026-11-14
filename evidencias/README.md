# Evidências de teste de sistema (Swagger)

Testes manuais feitos com a aplicação rodando (PostgreSQL via Docker), pelo Swagger em `http://localhost:8080/swagger`.

| # | Evidência | Requisição | Resultado obtido | Caso de teste |
|---|---|---|---|---|
| 01 | `01_post_criar_pagamento_201.png` | POST /api/pagamentos com dados válidos | 201, status SOLICITADO | CT-01 |
| 02 | `02_post_valor_invalido_400.png` | POST com valor 0 | 400, "Valor do pagamento deve ser positivo" | CT-02 |
| 03 | `03_post_remetente_igual_destinatario_400.png` | POST com remetente igual ao destinatário | 400, "Remetente e destinatário não podem ser iguais" | CT-03 |
| 04 | `04_post_sem_remetente_400.png` | POST sem o campo remetente | 400, "Campos obrigatórios ausentes ou inválidos" | CT-04 |
| 05 | `05_post_valor_acima_do_limite_201.png` | POST com valor 7.000,00 | 201, status SOLICITADO | — |
| 06 | `06_processar_dentro_do_limite_capturado.png` | POST /api/pagamentos/{id}/processar (150,00) | 200, status CAPTURADO | CT-06 |
| 07 | `07_processar_acima_do_limite_falhou.png` | Processar pagamento de 7.500,00 | 200, status FALHOU | CT-07 |
| 08 | `08_processar_pagamento_ja_finalizado.png` | Processar pagamento já CAPTURADO | 200, status continua CAPTURADO | CT-08 |
| 09 | `09_get_pagamento_existente_200.png` | GET /api/pagamentos/{id} existente | 200, dados do pagamento | — |
| 10 | `10_get_pagamento_inexistente_404.png` | GET /api/pagamentos/{id} inexistente | 404, "Pagamento não encontrado" | CT-09 |
| 11 | `11_post_mesma_chave_idempotencia.png` | POST repetindo a Idempotency-Key da evidência 01, com outros dados | 201, devolve o pagamento original (mesmo id, valor 250,00); nada é duplicado | CT-05 |
