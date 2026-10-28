package com.sistema.pagamentos.adapter.out.persistence;

import com.sistema.pagamentos.domain.model.Pagamento;
import com.sistema.pagamentos.domain.port.out.PagamentoRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adapter de saída: guarda os pagamentos em memória.
 * Pode ser trocado por um banco de dados sem alterar o domínio.
 */
@Repository
public class PagamentoRepositoryEmMemoria implements PagamentoRepositoryPort {

    private final Map<UUID, Pagamento> pagamentos = new ConcurrentHashMap<>();

    @Override
    public Pagamento salvar(Pagamento pagamento) {
        pagamentos.put(pagamento.getId(), pagamento);
        return pagamento;
    }

    @Override
    public Optional<Pagamento> buscarPorId(UUID id) {
        return Optional.ofNullable(pagamentos.get(id));
    }

    @Override
    public Optional<Pagamento> buscarPorChaveIdempotencia(UUID chaveIdempotencia) {
        return pagamentos.values().stream()
                .filter(p -> p.getChaveIdempotencia().equals(chaveIdempotencia))
                .findFirst();
    }
}
