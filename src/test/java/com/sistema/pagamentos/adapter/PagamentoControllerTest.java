package com.sistema.pagamentos.adapter.in.web;

import com.sistema.pagamentos.domain.model.Pagamento;
import com.sistema.pagamentos.domain.model.PagamentoNaoEncontradoException;
import com.sistema.pagamentos.domain.port.in.PagamentoUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagamentoController.class)
class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagamentoUseCase pagamentoUseCase;

    private static final String JSON_VALIDO = """
            {"valor": 100.00, "remetente": "joao@pix.com", "destinatario": "maria@pix.com"}
            """;

    @Test
    void postValidoRetorna201() throws Exception {
        var pagamento = Pagamento.criar(new BigDecimal("100.00"), "joao@pix.com", "maria@pix.com", UUID.randomUUID());
        // STUB: o use case devolve um pagamento pronto
        when(pagamentoUseCase.solicitar(any(), any(), any(), any())).thenReturn(pagamento);

        mockMvc.perform(post("/api/pagamentos")
                        .header("Idempotency-Key", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SOLICITADO"));
    }

    @Test
    void postSemRemetenteRetorna400ENaoChamaOUseCase() throws Exception {
        var json = """
                {"valor": 100.00, "destinatario": "maria@pix.com"}
                """;

        mockMvc.perform(post("/api/pagamentos")
                        .header("Idempotency-Key", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        // MOCK: confere que o use case NUNCA foi chamado
        verify(pagamentoUseCase, never()).solicitar(any(), any(), any(), any());
    }

    @Test
    void postComValorInvalidoRetorna400() throws Exception {
        // STUB: o use case lança a exceção de regra de negócio
        when(pagamentoUseCase.solicitar(any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Valor do pagamento deve ser positivo"));

        mockMvc.perform(post("/api/pagamentos")
                        .header("Idempotency-Key", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Valor do pagamento deve ser positivo"));
    }

    @Test
    void getDePagamentoInexistenteRetorna404() throws Exception {
        var id = UUID.randomUUID();
        when(pagamentoUseCase.consultar(id)).thenThrow(new PagamentoNaoEncontradoException(id));

        mockMvc.perform(get("/api/pagamentos/{id}", id))
                .andExpect(status().isNotFound());
    }
}