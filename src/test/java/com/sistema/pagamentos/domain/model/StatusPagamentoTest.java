package com.sistema.pagamentos.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StatusPagamento - máquina de estados")
class StatusPagamentoTest {

    @Test
    @DisplayName("SOLICITADO pode ir para AUTORIZADO")
    void solicitadoPodeIrParaAutorizado() {
        assertTrue(StatusPagamento.SOLICITADO.podeTransicionarPara(StatusPagamento.AUTORIZADO));
    }

    @Test
    @DisplayName("SOLICITADO pode ir para FALHOU")
    void solicitadoPodeIrParaFalhou() {
        assertTrue(StatusPagamento.SOLICITADO.podeTransicionarPara(StatusPagamento.FALHOU));
    }

    @Test
    @DisplayName("SOLICITADO não pode pular direto para CAPTURADO")
    void solicitadoNaoPodePularParaCapturado() {
        assertFalse(StatusPagamento.SOLICITADO.podeTransicionarPara(StatusPagamento.CAPTURADO));
    }

    @Test
    @DisplayName("AUTORIZADO pode ir para CAPTURADO ou FALHOU")
    void autorizadoPodeIrParaCapturadoOuFalhou() {
        assertTrue(StatusPagamento.AUTORIZADO.podeTransicionarPara(StatusPagamento.CAPTURADO));
        assertTrue(StatusPagamento.AUTORIZADO.podeTransicionarPara(StatusPagamento.FALHOU));
    }

    @Test
    @DisplayName("AUTORIZADO não pode voltar para SOLICITADO")
    void autorizadoNaoPodeVoltar() {
        assertFalse(StatusPagamento.AUTORIZADO.podeTransicionarPara(StatusPagamento.SOLICITADO));
    }

    @ParameterizedTest
    @EnumSource(StatusPagamento.class)
    @DisplayName("CAPTURADO não pode ir para nenhum outro status")
    void capturadoNaoTransiciona(StatusPagamento destino) {
        assertFalse(StatusPagamento.CAPTURADO.podeTransicionarPara(destino));
    }

    @ParameterizedTest
    @EnumSource(StatusPagamento.class)
    @DisplayName("FALHOU não pode ir para nenhum outro status")
    void falhouNaoTransiciona(StatusPagamento destino) {
        assertFalse(StatusPagamento.FALHOU.podeTransicionarPara(destino));
    }

    @Test
    @DisplayName("Apenas CAPTURADO e FALHOU são terminais")
    void estadosTerminais() {
        assertTrue(StatusPagamento.CAPTURADO.isTerminal());
        assertTrue(StatusPagamento.FALHOU.isTerminal());
        assertFalse(StatusPagamento.SOLICITADO.isTerminal());
        assertFalse(StatusPagamento.AUTORIZADO.isTerminal());
    }
}
