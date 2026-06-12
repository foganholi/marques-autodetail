package br.com.marquesautodetail.api.servico;

import br.com.marquesautodetail.api.servico.dto.ServicoRequest;
import br.com.marquesautodetail.api.servico.dto.ServicoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServicoController {

    private final ServicoService service;

    public ServicoController(ServicoService service) {
        this.service = service;
    }

    @GetMapping("/empresas/{empresaId}/servicos")
    public List<ServicoResponse> listar(@PathVariable Long empresaId) {
        return service.listarPorEmpresa(empresaId);
    }

    @PostMapping("/empresas/{empresaId}/servicos")
    @ResponseStatus(HttpStatus.CREATED)
    public ServicoResponse criar(@PathVariable Long empresaId, @Valid @RequestBody ServicoRequest request) {
        return service.criar(empresaId, request);
    }

    @PutMapping("/servicos/{id}")
    public ServicoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ServicoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/servicos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        service.remover(id);
    }
}
