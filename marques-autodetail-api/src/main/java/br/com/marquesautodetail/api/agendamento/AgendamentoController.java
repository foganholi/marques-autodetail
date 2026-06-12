package br.com.marquesautodetail.api.agendamento;

import br.com.marquesautodetail.api.agendamento.dto.AgendamentoRequest;
import br.com.marquesautodetail.api.agendamento.dto.AgendamentoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgendamentoResponse criar(@Valid @RequestBody AgendamentoRequest request) {
        return service.criar(request);
    }

    @GetMapping("/me")
    public List<AgendamentoResponse> meus() {
        return service.meus();
    }

    @PutMapping("/{id}/confirmar")
    public AgendamentoResponse confirmar(@PathVariable Long id) {
        return service.alterarStatus(id, StatusAgendamento.CONFIRMADO);
    }

    @PutMapping("/{id}/recusar")
    public AgendamentoResponse recusar(@PathVariable Long id) {
        return service.alterarStatus(id, StatusAgendamento.RECUSADO);
    }

    @PutMapping("/{id}/cancelar")
    public AgendamentoResponse cancelar(@PathVariable Long id) {
        return service.alterarStatus(id, StatusAgendamento.CANCELADO);
    }

    @PutMapping("/{id}/concluir")
    public AgendamentoResponse concluir(@PathVariable Long id) {
        return service.alterarStatus(id, StatusAgendamento.CONCLUIDO);
    }
}
