package br.com.marquesautodetail.api.horario;

import br.com.marquesautodetail.api.agendamento.Agendamento;
import br.com.marquesautodetail.api.agendamento.AgendamentoRepository;
import br.com.marquesautodetail.api.agendamento.StatusAgendamento;
import br.com.marquesautodetail.api.empresa.Empresa;
import br.com.marquesautodetail.api.empresa.EmpresaRepository;
import br.com.marquesautodetail.api.horario.dto.HorarioRequest;
import br.com.marquesautodetail.api.horario.dto.HorarioResponse;
import br.com.marquesautodetail.api.servico.Servico;
import br.com.marquesautodetail.api.servico.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class HorarioService {

    private static final int INTERVALO_MINUTOS = 30;

    private final HorarioRepository repo;
    private final EmpresaRepository empresas;
    private final ServicoRepository servicos;
    private final AgendamentoRepository agendamentos;

    public HorarioService(
            HorarioRepository repo,
            EmpresaRepository empresas,
            ServicoRepository servicos,
            AgendamentoRepository agendamentos
    ) {
        this.repo = repo;
        this.empresas = empresas;
        this.servicos = servicos;
        this.agendamentos = agendamentos;
    }

    public List<HorarioResponse> listar(Long empresaId) {
        return repo.findByEmpresaIdAndAtivoTrue(empresaId).stream()
                .sorted(Comparator.comparing(HorarioDisponivel::getDiaSemana).thenComparing(HorarioDisponivel::getHoraInicio))
                .map(this::toResponse)
                .toList();
    }

    public List<String> listarHorariosDisponiveis(Long empresaId, Long servicoId, LocalDate data) {
        Empresa empresa = empresas.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));

        Servico servico = servicos.findById(servicoId)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

        var horariosDoDia = repo.findByEmpresaIdAndDiaSemanaAndAtivoTrue(empresaId, data.getDayOfWeek());
        if (horariosDoDia.isEmpty()) return List.of();

        var agendamentosDoDia = agendamentos.findByEmpresaAndDataAndStatusIn(
                empresa,
                data,
                List.of(StatusAgendamento.PENDENTE, StatusAgendamento.CONFIRMADO)
        );

        List<String> disponiveis = new ArrayList<>();
        int duracao = servico.getDuracaoMinutos() == null ? 60 : servico.getDuracaoMinutos();

        for (HorarioDisponivel faixa : horariosDoDia) {
            LocalTime cursor = faixa.getHoraInicio();
            LocalTime limite = faixa.getHoraFim();

            while (!cursor.plusMinutes(duracao).isAfter(limite)) {
                LocalTime inicio = cursor;
                LocalTime fim = cursor.plusMinutes(duracao);
                boolean ocupado = agendamentosDoDia.stream().anyMatch(a -> conflita(inicio, fim, a));

                if (!ocupado) {
                    disponiveis.add(inicio.toString().substring(0, 5));
                }
                cursor = cursor.plusMinutes(INTERVALO_MINUTOS);
            }
        }

        return disponiveis.stream().distinct().sorted().toList();
    }

    public boolean horarioDisponivel(Long empresaId, Long servicoId, LocalDate data, LocalTime hora) {
        return listarHorariosDisponiveis(empresaId, servicoId, data).contains(hora.toString().substring(0, 5));
    }

    @Transactional
    public HorarioResponse criar(Long empresaId, HorarioRequest r) {
        var e = empresas.findById(empresaId).orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));
        HorarioDisponivel h = new HorarioDisponivel();
        h.setEmpresa(e);
        h.setDiaSemana(r.diaSemana());
        h.setHoraInicio(r.horaInicio());
        h.setHoraFim(r.horaFim());
        h.setAtivo(true);
        return toResponse(repo.save(h));
    }

    @Transactional
    public void remover(Long id) {
        var h = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Horário não encontrado"));
        h.setAtivo(false);
        repo.save(h);
    }

    private boolean conflita(LocalTime inicioNovo, LocalTime fimNovo, Agendamento existente) {
        int duracaoExistente = existente.getServico().getDuracaoMinutos() == null ? 60 : existente.getServico().getDuracaoMinutos();
        LocalTime inicioExistente = existente.getHora();
        LocalTime fimExistente = inicioExistente.plusMinutes(duracaoExistente);
        return inicioNovo.isBefore(fimExistente) && fimNovo.isAfter(inicioExistente);
    }

    private HorarioResponse toResponse(HorarioDisponivel h) {
        return new HorarioResponse(h.getId(), h.getDiaSemana(), h.getHoraInicio(), h.getHoraFim(), h.getAtivo());
    }
}
