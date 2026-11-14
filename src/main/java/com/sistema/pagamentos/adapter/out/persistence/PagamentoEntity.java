package com.sistema.pagamentos.adapter.out.persistence;

import com.sistema.pagamentos.domain.model.Pagamento;
import com.sistema.pagamentos.domain.model.StatusPagamento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representação do pagamento na tabela "pagamentos".
 * Fica no adapter para o domínio não depender do JPA.
 */
@Entity
@Table(name = "pagamentos")
public class PagamentoEntity {

    @Id
    private UUID id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, length = 140)
    private String remetente;

    @Column(nullable = false, length = 140)
    private String destinatario;

    @Column(name = "chave_idempotencia", nullable = false, unique = true)
    private UUID chaveIdempotencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPagamento status;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    protected PagamentoEntity() {
        // exigido pelo JPA
    }

    public static PagamentoEntity de(Pagamento p) {
        var e = new PagamentoEntity();
        e.id = p.getId();
        e.valor = p.getValor();
        e.remetente = p.getRemetente();
        e.destinatario = p.getDestinatario();
        e.chaveIdempotencia = p.getChaveIdempotencia();
        e.status = p.getStatus();
        e.criadoEm = p.getCriadoEm();
        e.atualizadoEm = p.getAtualizadoEm();
        return e;
    }

    public Pagamento paraDominio() {
        return Pagamento.reconstituir(id, valor, remetente, destinatario,
                chaveIdempotencia, status, criadoEm, atualizadoEm);
    }
}
