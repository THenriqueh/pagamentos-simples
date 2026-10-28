package com.sistema.pagamentos.domain.model;

/**
 * Máquina de estados do pagamento.
 *
 * Fluxo válido:
 *   SOLICITADO → AUTORIZADO → CAPTURADO
 *   SOLICITADO → FALHOU
 *   AUTORIZADO → FALHOU
 *
 * CAPTURADO e FALHOU são estados terminais.
 */
public enum StatusPagamento {

    SOLICITADO,
    AUTORIZADO,
    CAPTURADO,
    FALHOU;

    public boolean podeTransicionarPara(StatusPagamento destino) {
        return switch (this) {
            case SOLICITADO -> destino == AUTORIZADO || destino == FALHOU;
            case AUTORIZADO -> destino == CAPTURADO || destino == FALHOU;
            case CAPTURADO, FALHOU -> false;
        };
    }

    public boolean isTerminal() {
        return this == CAPTURADO || this == FALHOU;
    }
}
