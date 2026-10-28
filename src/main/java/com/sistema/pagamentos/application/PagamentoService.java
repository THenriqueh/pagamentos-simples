package com.sistema.pagamentos.application;

import com.sistema.pagamentos.domain.model.Pagamento;
import com.sistema.pagamentos.domain.model.PagamentoNaoEncontradoException;
import com.sistema.pagamentos.domain.model.StatusPagamento;
import com.sistema.pagamentos.domain.port.in.PagamentoUseCase;
import com.sistema.pagamentos.domain.port.out.PagamentoRepositoryPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Orquestra os casos de uso. A regra de negócio da entidade fica em Pagamento.
 */
@Service
public class PagamentoService implements PagamentoUseCase {

    /** Valor máximo autorizado automaticamente por transação. */
    public static final BigDecimal LIMITE_POR_TRANSACAO = new BigDecimal("5000.00");

    private final PagamentoRepositoryPort repository;

    public PagamentoService(PagamentoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Pagamento solicitar(BigDecimal valor, String remetente,
                               String destinatario, UUID chaveIdempotencia) {
        // Idempotência: mesma chave devolve o pagamento já existente
        var existente = repository.buscarPorChaveIdempotencia(chaveIdempotencia);
        if (existente.isPresent()) {
            return existente.get();
        }
        var pagamento = Pagamento.criar(valor, remetente, destinatario, chaveIdempotencia);
        return repository.salvar(pagamento);
    }

    @Override
    public Pagamento processar(UUID id) {
        var pagamento = consultar(id);

        // Pagamento já finalizado não é processado de novo
        if (pagamento.isTerminal()) {
            return pagamento;
        }

        if (pagamento.getValor().compareTo(LIMITE_POR_TRANSACAO) > 0) {
            pagamento.transicionarPara(StatusPagamento.FALHOU);
        } else {
            pagamento.transicionarPara(StatusPagamento.AUTORIZADO);
            pagamento.transicionarPara(StatusPagamento.CAPTURADO);
        }
        return repository.salvar(pagamento);
    }

    @Override
    public Pagamento consultar(UUID id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new PagamentoNaoEncontradoException(id));
    }
}
