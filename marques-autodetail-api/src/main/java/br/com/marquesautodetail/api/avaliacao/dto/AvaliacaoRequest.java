package br.com.marquesautodetail.api.avaliacao.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoRequest(
        @NotNull(message = "informe a empresa")
        Long empresaId,
        @NotNull(message = "informe o agendamento")
        Long agendamentoId,
        @NotNull(message = "informe a nota")
        @Min(value = 1, message = "a nota mínima é 1")
        @Max(value = 5, message = "a nota máxima é 5")
        Integer nota,
        @Size(max = 1000, message = "o comentário deve ter no máximo 1000 caracteres")
        String comentario
) {
}
