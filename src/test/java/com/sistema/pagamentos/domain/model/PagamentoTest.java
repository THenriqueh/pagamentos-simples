package com.sistema.pagamentos.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pagamento - regras da entidade")
class PagamentoTest {

    private static final BigDecimal VALOR = new BigDecimal("150.00");
    private static final String REMETENTE = "joao@pix.com";
    private static final String DESTINATARIO = "maria@pix.com";

    // ─── Criação (UC-01) ──────────────────────────────────────────────────────

    @Test
    @DisplayName("CT-01: cria pagamento válido com status SOLICITADO")
    void criaPagamentoValido() {
        var chave = UUID.randomUUID();

        var pagamento = Pagamento.criar(VALOR, REMETENTE, DESTINATARIO, chave);

        assertNotNull(pagamento.getId());
        assertEquals(StatusPagamento.SOLICITADO, pagamento.getStatus());
        assertEquals(VALOR, pagamento.getValor());
        assertEquals(chave, pagamento.getChaveIdempotencia());
        assertNotNull(pagamento.getCriadoEm());
    }

    @Test
    @DisplayName("CT-02: rejeita valor zero")
    void rejeitaValorZero() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> Pagamento.criar(BigDecimal.ZERO, REMETENTE, DESTINATARIO, UUID.randomUUID()));
        assertEquals("Valor do pagamento deve ser positivo", ex.getMessage());
    }

    @Test
    @DisplayName("CT-02: rejeita valor negativo")
    void rejeitaValorNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> Pagamento.criar(new BigDecimal("-10"), REMETENTE, DESTINATARIO, UUID.randomUUID()));
    }

    @Test
    @DisplayName("CT-03: rejeita remetente igual ao destinatário")
    void rejeitaRemetenteIgualDestinatario() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> Pagamento.criar(VALOR, REMETENTE, REMETENTE, UUID.randomUUID()));
        assertEquals("Remetente e destinatário não podem ser iguais", ex.getMessage());
    }

    @Test
    @DisplayName("CT-04: rejeita remetente vazio")
    void rejeitaRemetenteVazio() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> Pagamento.criar(VALOR, "  ", DESTINATARIO, UUID.randomUUID()));
        assertEquals("Remetente é obrigatório", ex.getMessage());
    }

    @Test
    @DisplayName("Rejeita destinatário nulo")
    void rejeitaDestinatarioNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> Pagamento.criar(VALOR, REMETENTE, null, UUID.randomUUID()));
    }

    @Test
    @DisplayName("Rejeita chave de idempotência nula")
    void rejeitaChaveNula() {
        assertThrows(IllegalArgumentException.class,
                () -> Pagamento.criar(VALOR, REMETENTE, DESTINATARIO, null));
    }

    // ─── Transição de status (UC-02) ──────────────────────────────────────────

    @Test
    @DisplayName("Transição válida atualiza o status")
    void transicaoValidaAtualizaStatus() {
        var pagamento = Pagamento.criar(VALOR, REMETENTE, DESTINATARIO, UUID.randomUUID());

        pagamento.transicionarPara(StatusPagamento.AUTORIZADO);
        pagamento.transicionarPara(StatusPagamento.CAPTURADO);

        assertEquals(StatusPagamento.CAPTURADO, pagamento.getStatus());
        assertTrue(pagamento.isTerminal());
    }

    @Test
    @DisplayName("CT-10: transição inválida lança exceção e mantém o status")
    void transicaoInvalidaLancaExcecao() {
        var pagamento = Pagamento.criar(VALOR, REMETENTE, DESTINATARIO, UUID.randomUUID());
        pagamento.transicionarPara(StatusPagamento.AUTORIZADO);
        pagamento.transicionarPara(StatusPagamento.CAPTURADO);

        assertThrows(TransicaoStatusInvalidaException.class,
                () -> pagamento.transicionarPara(StatusPagamento.AUTORIZADO));
        assertEquals(StatusPagamento.CAPTURADO, pagamento.getStatus());
    }
}
