package br.com.marquesautodetail.api.horario.dto;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record HorarioRequest(
        @NotNull(message = "informe o dia da semana")
        DayOfWeek diaSemana,
        @NotNull(message = "informe o horário inicial")
        LocalTime horaInicio,
        @NotNull(message = "informe o horário final")
        LocalTime horaFim
) {
}
