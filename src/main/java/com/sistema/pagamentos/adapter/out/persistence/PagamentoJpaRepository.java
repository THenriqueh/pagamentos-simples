package com.sistema.pagamentos.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PagamentoJpaRepository extends JpaRepository<PagamentoEntity, UUID> {

    Optional<PagamentoEntity> findByChaveIdempotencia(UUID chaveIdempotencia);
}
