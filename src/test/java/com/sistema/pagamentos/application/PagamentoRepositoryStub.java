package com.sistema.pagamentos.application;

import com.sistema.pagamentos.domain.model.Pagamento;
import com.sistema.pagamentos.domain.port.out.PagamentoRepositoryPort;

import java.util.Optional;
import java.util.UUID;

public class PagamentoRepositoryStub implements PagamentoRepositoryPort {

    private final Pagamento pagamentoFixo;

    public PagamentoRepositoryStub(Pagamento pagamentoFixo) {
        this.pagamentoFixo = pagamentoFixo;
    }

    @Override
    public Pagamento salvar(Pagamento pagamento) {

        return pagamento;
    }

    @Override
    public Optional<Pagamento> buscarPorId(UUID id) {

        return Optional.ofNullable(pagamentoFixo);
    }

    @Override
    public Optional<Pagamento> buscarPorChaveIdempotencia(UUID chaveIdempotencia) {

        return Optional.empty();
    }

}
