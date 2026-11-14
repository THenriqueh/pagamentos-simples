package com.sistema.pagamentos.application;

import com.sistema.pagamentos.domain.model.Pagamento;
import com.sistema.pagamentos.domain.model.PagamentoNaoEncontradoException;
import com.sistema.pagamentos.domain.model.StatusPagamento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PagamentoServiceComStubTest {

    @Test
    void processaPagamentoDentroDoLimite() {
        var pagamento = Pagamento.criar(new BigDecimal("100.00"), "joao@pix.com", "maria@pix.com", UUID.randomUUID());
        var service = new PagamentoService(new PagamentoRepositoryStub(pagamento));

        var resultado = service.processar(pagamento.getId());

        assertEquals(StatusPagamento.CAPTURADO, resultado.getStatus());
    }

    @Test
    void processaPagamentoAcimaDoLimite() {
        var pagamento = Pagamento.criar(new BigDecimal("5000.01"), "joao@pix.com", "maria@pix.com", UUID.randomUUID());
        var service = new PagamentoService(new PagamentoRepositoryStub(pagamento));

        var resultado = service.processar(pagamento.getId());

        assertEquals(StatusPagamento.FALHOU, resultado.getStatus());
    }

    @Test
    void processarPagamentoInexistente() {
        var service = new PagamentoService(new PagamentoRepositoryStub(null)); // null = "não encontrado"

        assertThrows(PagamentoNaoEncontradoException.class,
                () -> service.processar(UUID.randomUUID()));
    }
}

