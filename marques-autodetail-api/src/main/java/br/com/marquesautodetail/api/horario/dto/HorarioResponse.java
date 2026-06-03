package br.com.marquesautodetail.api.horario.dto; import java.time.*; public record HorarioResponse(Long id,DayOfWeek diaSemana,LocalTime horaInicio,LocalTime horaFim,Boolean ativo){}
