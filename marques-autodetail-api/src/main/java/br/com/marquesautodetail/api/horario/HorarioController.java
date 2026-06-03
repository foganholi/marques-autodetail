package br.com.marquesautodetail.api.horario;

import br.com.marquesautodetail.api.horario.dto.HorarioRequest;
import br.com.marquesautodetail.api.horario.dto.HorarioResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class HorarioController {
    private final HorarioService service;

    public HorarioController(HorarioService service) {
        this.service = service;
    }

    @GetMapping("/empresas/{empresaId}/horarios")
    public List<HorarioResponse> listar(@PathVariable Long empresaId) {
        return service.listar(empresaId);
    }

    @GetMapping("/empresas/{empresaId}/horarios/disponiveis")
    public List<String> listarDisponiveis(
            @PathVariable Long empresaId,
            @RequestParam Long servicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        return service.listarHorariosDisponiveis(empresaId, servicoId, data);
    }

    @PostMapping("/empresas/{empresaId}/horarios")
    public HorarioResponse criar(@PathVariable Long empresaId, @RequestBody HorarioRequest r) {
        return service.criar(empresaId, r);
    }

    @DeleteMapping("/horarios/{id}")
    public void remover(@PathVariable Long id) {
        service.remover(id);
    }
}
