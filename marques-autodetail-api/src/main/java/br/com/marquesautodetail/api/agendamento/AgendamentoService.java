package br.com.marquesautodetail.api.agendamento;

import br.com.marquesautodetail.api.agendamento.dto.AgendamentoRequest;
import br.com.marquesautodetail.api.agendamento.dto.AgendamentoResponse;
import br.com.marquesautodetail.api.empresa.Empresa;
import br.com.marquesautodetail.api.empresa.EmpresaRepository;
import br.com.marquesautodetail.api.horario.HorarioService;
import br.com.marquesautodetail.api.security.AuthenticatedUserService;
import br.com.marquesautodetail.api.servico.Servico;
import br.com.marquesautodetail.api.servico.ServicoRepository;
import br.com.marquesautodetail.api.usuario.Role;
import br.com.marquesautodetail.api.usuario.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepository repo;
    private final EmpresaRepository empresas;
    private final ServicoRepository servicos;
    private final HorarioService horarioService;
    private final AuthenticatedUserService authenticatedUser;

    public AgendamentoService(
            AgendamentoRepository repo,
            EmpresaRepository empresas,
            ServicoRepository servicos,
            HorarioService horarioService,
            AuthenticatedUserService authenticatedUser
    ) {
        this.repo = repo;
        this.empresas = empresas;
        this.servicos = servicos;
        this.horarioService = horarioService;
        this.authenticatedUser = authenticatedUser;
    }

    @Transactional
    public AgendamentoResponse criar(AgendamentoRequest request) {
        Usuario cliente = authenticatedUser.clienteAtual();
        if (request.data().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data do agendamento não pode estar no passado");
        }

        Empresa empresa = empresas.findById(request.empresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));
        Servico servico = servicos.findById(request.servicoId())
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

        if (!servico.getEmpresa().getId().equals(empresa.getId()) || !Boolean.TRUE.equals(servico.getAtivo())) {
            throw new IllegalArgumentException("O serviço não pertence à empresa informada ou está inativo");
        }

        boolean disponivel = horarioService.horarioDisponivel(
                request.empresaId(),
                request.servicoId(),
                request.data(),
                request.hora()
        );
        if (!disponivel) {
            throw new IllegalArgumentException("Horário indisponível para esta empresa, serviço ou data");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setEmpresa(empresa);
        agendamento.setServico(servico);
        agendamento.setData(request.data());
        agendamento.setHora(request.hora());
        agendamento.setObservacao(request.observacao());
        agendamento.setStatus(StatusAgendamento.PENDENTE);
        return toResponse(repo.save(agendamento));
    }

    public List<AgendamentoResponse> meus() {
        Usuario usuario = authenticatedUser.usuarioAtual();
        if (usuario.getRole() == Role.EMPRESA) {
            Empresa empresa = authenticatedUser.empresaAtual();
            return repo.findByEmpresaOrderByDataDescHoraDesc(empresa).stream().map(this::toResponse).toList();
        }
        return repo.findByClienteOrderByDataDescHoraDesc(usuario).stream().map(this::toResponse).toList();
    }

    public List<AgendamentoResponse> porEmpresa(Long empresaId) {
        Empresa empresa = authenticatedUser.exigirEmpresa(empresaId);
        return repo.findByEmpresaOrderByDataDescHoraDesc(empresa).stream().map(this::toResponse).toList();
    }

    @Transactional
    public AgendamentoResponse alterarStatus(Long id, StatusAgendamento novoStatus) {
        Agendamento agendamento = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado"));
        Usuario usuario = authenticatedUser.usuarioAtual();

        if (novoStatus == StatusAgendamento.CANCELADO) {
            if (usuario.getRole() != Role.CLIENTE || !agendamento.getCliente().getId().equals(usuario.getId())) {
                throw new AccessDeniedException("Somente o cliente do agendamento pode cancelá-lo");
            }
            if (agendamento.getStatus() == StatusAgendamento.CONCLUIDO) {
                throw new IllegalArgumentException("Um agendamento concluído não pode ser cancelado");
            }
        } else {
            Empresa empresa = authenticatedUser.empresaAtual();
            if (!agendamento.getEmpresa().getId().equals(empresa.getId())) {
                throw new AccessDeniedException("Você não pode alterar agendamentos de outra empresa");
            }
            validarTransicaoEmpresa(agendamento.getStatus(), novoStatus);
        }

        agendamento.setStatus(novoStatus);
        return toResponse(repo.save(agendamento));
    }

    private void validarTransicaoEmpresa(StatusAgendamento atual, StatusAgendamento novoStatus) {
        boolean valida = switch (novoStatus) {
            case CONFIRMADO, RECUSADO -> atual == StatusAgendamento.PENDENTE;
            case CONCLUIDO -> atual == StatusAgendamento.CONFIRMADO;
            default -> false;
        };
        if (!valida) {
            throw new IllegalArgumentException("Transição de status inválida: " + atual + " para " + novoStatus);
        }
    }

    private AgendamentoResponse toResponse(Agendamento agendamento) {
        return new AgendamentoResponse(
                agendamento.getId(),
                agendamento.getCliente().getNome(),
                agendamento.getEmpresa().getId(),
                agendamento.getEmpresa().getNomeFantasia(),
                agendamento.getServico().getId(),
                agendamento.getServico().getNome(),
                agendamento.getData(),
                agendamento.getHora(),
                agendamento.getStatus().name(),
                agendamento.getObservacao()
        );
    }
}
