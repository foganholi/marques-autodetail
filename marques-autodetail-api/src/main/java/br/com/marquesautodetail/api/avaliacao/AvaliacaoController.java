package br.com.marquesautodetail.api.avaliacao;

import br.com.marquesautodetail.api.avaliacao.dto.AvaliacaoRequest;
import br.com.marquesautodetail.api.avaliacao.dto.AvaliacaoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @PostMapping("/avaliacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public AvaliacaoResponse criar(@Valid @RequestBody AvaliacaoRequest request) {
        return service.criar(request);
    }

    @GetMapping("/empresas/{empresaId}/avaliacoes")
    public List<AvaliacaoResponse> porEmpresa(@PathVariable Long empresaId) {
        return service.porEmpresa(empresaId);
    }
}
