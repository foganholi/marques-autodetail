package br.com.marquesautodetail.api.agendamento.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoRequest(
        @NotNull(message = "informe a empresa")
        Long empresaId,
        @NotNull(message = "informe o serviço")
        Long servicoId,
        @NotNull(message = "informe a data")
        @FutureOrPresent(message = "a data não pode estar no passado")
        LocalDate data,
        @NotNull(message = "informe o horário")
        LocalTime hora,
        @Size(max = 1000, message = "a observação deve ter no máximo 1000 caracteres")
        String observacao
) {
}
