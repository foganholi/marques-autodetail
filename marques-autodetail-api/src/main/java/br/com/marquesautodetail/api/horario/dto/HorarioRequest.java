package br.com.marquesautodetail.api.horario.dto; import java.time.*; public record HorarioRequest(DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFim){}
