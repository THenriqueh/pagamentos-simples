package com.sistema.pagamentos.domain.port.in;

import com.sistema.pagamentos.domain.model.Pagamento;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Port de entrada: casos de uso oferecidos pelo sistema.
 */
public interface PagamentoUseCase {

    /** UC-01: Solicitar pagamento. */
    Pagamento solicitar(BigDecimal valor, String remetente, String destinatario, UUID chaveIdempotencia);

    /** UC-02: Processar pagamento (autorizar e capturar). */
    Pagamento processar(UUID id);

    Pagamento consultar(UUID id);
}
