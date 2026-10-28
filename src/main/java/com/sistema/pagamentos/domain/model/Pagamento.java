package com.sistema.pagamentos.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade central do domínio. Java puro, sem dependência de framework.
 * As regras de negócio ficam aqui.
 */
public class Pagamento {

    private final UUID id;
    private final BigDecimal valor;
    private final String remetente;
    private final String destinatario;
    private final UUID chaveIdempotencia;
    private final LocalDateTime criadoEm;
    private StatusPagamento status;
    private LocalDateTime atualizadoEm;

    private Pagamento(UUID id, BigDecimal valor, String remetente, String destinatario,
                      UUID chaveIdempotencia, StatusPagamento status, LocalDateTime criadoEm) {
        this.id = id;
        this.valor = valor;
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.chaveIdempotencia = chaveIdempotencia;
        this.status = status;
        this.criadoEm = criadoEm;
        this.atualizadoEm = criadoEm;
    }

    /**
     * Cria um novo pagamento. Status inicial é sempre SOLICITADO.
     */
    public static Pagamento criar(BigDecimal valor, String remetente,
                                  String destinatario, UUID chaveIdempotencia) {
        validarValor(valor);
        validarPartes(remetente, destinatario);
        if (chaveIdempotencia == null) {
            throw new IllegalArgumentException("Chave de idempotência é obrigatória");
        }
        return new Pagamento(UUID.randomUUID(), valor, remetente, destinatario,
                chaveIdempotencia, StatusPagamento.SOLICITADO, LocalDateTime.now());
    }

    /**
     * Muda o status respeitando a máquina de estados.
     */
    public void transicionarPara(StatusPagamento novoStatus) {
        if (!status.podeTransicionarPara(novoStatus)) {
            throw new TransicaoStatusInvalidaException(
                    "Transição inválida: %s → %s".formatted(status, novoStatus));
        }
        this.status = novoStatus;
        this.atualizadoEm = LocalDateTime.now();
    }

    public boolean isTerminal() {
        return status.isTerminal();
    }

    private static void validarValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do pagamento deve ser positivo");
        }
    }

    private static void validarPartes(String remetente, String destinatario) {
        if (remetente == null || remetente.isBlank()) {
            throw new IllegalArgumentException("Remetente é obrigatório");
        }
        if (destinatario == null || destinatario.isBlank()) {
            throw new IllegalArgumentException("Destinatário é obrigatório");
        }
        if (remetente.equals(destinatario)) {
            throw new IllegalArgumentException("Remetente e destinatário não podem ser iguais");
        }
    }

    public UUID getId()                    { return id; }
    public BigDecimal getValor()           { return valor; }
    public String getRemetente()           { return remetente; }
    public String getDestinatario()        { return destinatario; }
    public UUID getChaveIdempotencia()     { return chaveIdempotencia; }
    public StatusPagamento getStatus()     { return status; }
    public LocalDateTime getCriadoEm()     { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
