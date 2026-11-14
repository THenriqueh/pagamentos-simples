CREATE TABLE pagamentos (
    id                  UUID           PRIMARY KEY,
    valor               NUMERIC(15, 2) NOT NULL CHECK (valor > 0),
    remetente           VARCHAR(140)   NOT NULL,
    destinatario        VARCHAR(140)   NOT NULL,
    chave_idempotencia  UUID           NOT NULL UNIQUE,
    status              VARCHAR(20)    NOT NULL,
    criado_em           TIMESTAMP      NOT NULL,
    atualizado_em       TIMESTAMP      NOT NULL,
    CONSTRAINT ck_status CHECK (status IN ('SOLICITADO', 'AUTORIZADO', 'CAPTURADO', 'FALHOU')),
    CONSTRAINT ck_partes_diferentes CHECK (remetente <> destinatario)
);

CREATE INDEX idx_pagamentos_status ON pagamentos (status);
