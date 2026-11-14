package com.sistema.pagamentos.adapter.out.persistence;

import com.sistema.pagamentos.domain.model.Pagamento;
import com.sistema.pagamentos.domain.port.out.PagamentoRepositoryPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter de saída: guarda os pagamentos no PostgreSQL.
 * É o repositório padrão; o perfil "memoria" usa o PagamentoRepositoryEmMemoria.
 */
@Repository
@Profile("!memoria")
public class PagamentoRepositoryPostgres implements PagamentoRepositoryPort {

    private final PagamentoJpaRepository jpa;

    public PagamentoRepositoryPostgres(PagamentoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Pagamento salvar(Pagamento pagamento) {
        return jpa.save(PagamentoEntity.de(pagamento)).paraDominio();
    }

    @Override
    public Optional<Pagamento> buscarPorId(UUID id) {
        return jpa.findById(id).map(PagamentoEntity::paraDominio);
    }

    @Override
    public Optional<Pagamento> buscarPorChaveIdempotencia(UUID chaveIdempotencia) {
        return jpa.findByChaveIdempotencia(chaveIdempotencia).map(PagamentoEntity::paraDominio);
    }
}
