package com.sistema.pagamentos.adapter.in.web;

import com.sistema.pagamentos.domain.model.PagamentoNaoEncontradoException;
import com.sistema.pagamentos.domain.model.TransicaoStatusInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Traduz exceções do domínio para respostas HTTP.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PagamentoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(PagamentoNaoEncontradoException ex) {
        return erro(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> dadosInvalidos(IllegalArgumentException ex) {
        return erro(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> camposObrigatorios(MethodArgumentNotValidException ex) {
        return erro(HttpStatus.BAD_REQUEST, "Campos obrigatórios ausentes ou inválidos");
    }

    @ExceptionHandler(TransicaoStatusInvalidaException.class)
    public ResponseEntity<Map<String, String>> transicaoInvalida(TransicaoStatusInvalidaException ex) {
        return erro(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    private ResponseEntity<Map<String, String>> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("erro", mensagem));
    }
}
