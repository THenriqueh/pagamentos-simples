package com.sistema.pagamentos.application;

import com.sistema.pagamentos.domain.model.Pagamento;
import com.sistema.pagamentos.domain.model.PagamentoNaoEncontradoException;
import com.sistema.pagamentos.domain.model.StatusPagamento;
import com.sistema.pagamentos.domain.port.out.PagamentoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do service. O repositório é substituído por um mock (Mockito),
 * então nenhum banco de dados é necessário.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PagamentoService - casos de uso")
class PagamentoServiceTest {

    @Mock
    private PagamentoRepositoryPort repository;

    private PagamentoService service;

    @BeforeEach
    void setUp() {
        service = new PagamentoService(repository);
    }

    private Pagamento novoPagamento(String valor) {
        return Pagamento.criar(new BigDecimal(valor), "joao@pix.com", "maria@pix.com", UUID.randomUUID());
    }

    // ─── UC-01: Solicitar pagamento ───────────────────────────────────────────

    @Test
    @DisplayName("CT-01: solicita pagamento novo e salva com status SOLICITADO")
    void solicitaPagamentoNovo() {
        var chave = UUID.randomUUID();
        when(repository.buscarPorChaveIdempotencia(chave)).thenReturn(Optional.empty());
        when(repository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        var resultado = service.solicitar(new BigDecimal("100.00"), "joao@pix.com", "maria@pix.com", chave);

        assertEquals(StatusPagamento.SOLICITADO, resultado.getStatus());
        verify(repository).salvar(resultado);
    }

    @Test
    @DisplayName("CT-02: valor inválido não é salvo")
    void valorInvalidoNaoESalvo() {
        var chave = UUID.randomUUID();
        when(repository.buscarPorChaveIdempotencia(chave)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.solicitar(new BigDecimal("-5"), "joao@pix.com", "maria@pix.com", chave));
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("CT-05: mesma chave de idempotência devolve o pagamento original")
    void idempotenciaDevolvePagamentoOriginal() {
        var existente = novoPagamento("100.00");
        var chave = existente.getChaveIdempotencia();
        when(repository.buscarPorChaveIdempotencia(chave)).thenReturn(Optional.of(existente));

        var resultado = service.solicitar(new BigDecimal("100.00"), "joao@pix.com", "maria@pix.com", chave);

        assertSame(existente, resultado);
        verify(repository, never()).salvar(any());
    }

    // ─── UC-02: Processar pagamento ───────────────────────────────────────────

    @Test
    @DisplayName("CT-06: pagamento dentro do limite é autorizado e capturado")
    void processaPagamentoDentroDoLimite() {
        var pagamento = novoPagamento("5000.00");
        when(repository.buscarPorId(pagamento.getId())).thenReturn(Optional.of(pagamento));
        when(repository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        var resultado = service.processar(pagamento.getId());

        assertEquals(StatusPagamento.CAPTURADO, resultado.getStatus());
        verify(repository).salvar(pagamento);
    }

    @Test
    @DisplayName("CT-07: pagamento acima do limite falha")
    void processaPagamentoAcimaDoLimite() {
        var pagamento = novoPagamento("5000.01");
        when(repository.buscarPorId(pagamento.getId())).thenReturn(Optional.of(pagamento));
        when(repository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        var resultado = service.processar(pagamento.getId());

        assertEquals(StatusPagamento.FALHOU, resultado.getStatus());
    }

    @Test
    @DisplayName("CT-08: pagamento já finalizado não é processado de novo")
    void pagamentoFinalizadoNaoEReprocessado() {
        var pagamento = novoPagamento("100.00");
        pagamento.transicionarPara(StatusPagamento.AUTORIZADO);
        pagamento.transicionarPara(StatusPagamento.CAPTURADO);
        when(repository.buscarPorId(pagamento.getId())).thenReturn(Optional.of(pagamento));

        var resultado = service.processar(pagamento.getId());

        assertEquals(StatusPagamento.CAPTURADO, resultado.getStatus());
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("CT-09: processar pagamento inexistente lança exceção")
    void processarPagamentoInexistente() {
        var id = UUID.randomUUID();
        when(repository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThrows(PagamentoNaoEncontradoException.class, () -> service.processar(id));
    }

    // ─── Consulta ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Consulta devolve o pagamento existente")
    void consultaPagamentoExistente() {
        var pagamento = novoPagamento("100.00");
        when(repository.buscarPorId(pagamento.getId())).thenReturn(Optional.of(pagamento));

        assertSame(pagamento, service.consultar(pagamento.getId()));
    }
}
