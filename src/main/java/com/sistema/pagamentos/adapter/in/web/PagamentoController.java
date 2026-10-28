package com.sistema.pagamentos.adapter.in.web;

import com.sistema.pagamentos.domain.model.Pagamento;
import com.sistema.pagamentos.domain.port.in.PagamentoUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Adapter de entrada HTTP. Só traduz requisições para os casos de uso.
 */
@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoUseCase useCase;

    public PagamentoController(PagamentoUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<PagamentoResponse> solicitar(
            @RequestHeader("Idempotency-Key") UUID chaveIdempotencia,
            @Valid @RequestBody SolicitarPagamentoRequest request) {
        var pagamento = useCase.solicitar(request.valor(), request.remetente(),
                request.destinatario(), chaveIdempotencia);
        return ResponseEntity.status(HttpStatus.CREATED).body(PagamentoResponse.de(pagamento));
    }

    @PostMapping("/{id}/processar")
    public PagamentoResponse processar(@PathVariable UUID id) {
        return PagamentoResponse.de(useCase.processar(id));
    }

    @GetMapping("/{id}")
    public PagamentoResponse consultar(@PathVariable UUID id) {
        return PagamentoResponse.de(useCase.consultar(id));
    }

    public record SolicitarPagamentoRequest(
            @NotNull BigDecimal valor,
            @NotBlank String remetente,
            @NotBlank String destinatario) {
    }

    public record PagamentoResponse(UUID id, BigDecimal valor, String remetente,
                                    String destinatario, String status,
                                    LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        static PagamentoResponse de(Pagamento p) {
            return new PagamentoResponse(p.getId(), p.getValor(), p.getRemetente(),
                    p.getDestinatario(), p.getStatus().name(), p.getCriadoEm(), p.getAtualizadoEm());
        }
    }
}
