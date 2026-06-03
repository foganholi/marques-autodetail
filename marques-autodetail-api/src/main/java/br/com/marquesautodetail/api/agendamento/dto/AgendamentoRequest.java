package br.com.marquesautodetail.api.agendamento.dto; import java.time.*; public record AgendamentoRequest(Long empresaId,Long servicoId,LocalDate data,LocalTime hora,String observacao){}
