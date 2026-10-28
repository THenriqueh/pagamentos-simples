package com.sistema.pagamentos.domain.port.out;

import com.sistema.pagamentos.domain.model.Pagamento;

import java.util.Optional;
import java.util.UUID;

/**
 * Port de saída: o domínio não sabe onde os pagamentos são guardados.
 */
public interface PagamentoRepositoryPort {

    Pagamento salvar(Pagamento pagamento);

    Optional<Pagamento> buscarPorId(UUID id);

    Optional<Pagamento> buscarPorChaveIdempotencia(UUID chaveIdempotencia);
}
