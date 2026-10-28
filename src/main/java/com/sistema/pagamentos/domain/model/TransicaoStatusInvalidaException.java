package com.sistema.pagamentos.domain.model;

public class TransicaoStatusInvalidaException extends RuntimeException {

    public TransicaoStatusInvalidaException(String message) {
        super(message);
    }
}
