package br.com.marquesautodetail.api.agendamento;

import br.com.marquesautodetail.api.agendamento.dto.AgendamentoRequest;
import br.com.marquesautodetail.api.agendamento.dto.AgendamentoResponse;
import br.com.marquesautodetail.api.empresa.Empresa;
import br.com.marquesautodetail.api.empresa.EmpresaRepository;
import br.com.marquesautodetail.api.horario.HorarioService;
import br.com.marquesautodetail.api.servico.Servico;
import br.com.marquesautodetail.api.servico.ServicoRepository;
import br.com.marquesautodetail.api.usuario.Role;
import br.com.marquesautodetail.api.usuario.Usuario;
import br.com.marquesautodetail.api.usuario.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AgendamentoService {
    private final AgendamentoRepository repo;
    private final UsuarioRepository usuarios;
    private final EmpresaRepository empresas;
    private final ServicoRepository servicos;
    private final HorarioService horarioService;

    public AgendamentoService(
            AgendamentoRepository repo,
            UsuarioRepository usuarios,
            EmpresaRepository empresas,
            ServicoRepository servicos,
            HorarioService horarioService
    ) {
        this.repo = repo;
        this.usuarios = usuarios;
        this.empresas = empresas;
        this.servicos = servicos;
        this.horarioService = horarioService;
    }

    private Usuario usuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarios.findByEmail(email).orElseThrow();
    }

    @Transactional
    public AgendamentoResponse criar(AgendamentoRequest r) {
        Usuario cliente = usuarioLogado();
        if (cliente.getRole() != Role.CLIENTE) {
            throw new IllegalArgumentException("Somente cliente pode criar agendamento");
        }

        Empresa empresa = empresas.findById(r.empresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));
        Servico servico = servicos.findById(r.servicoId())
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

        boolean disponivel = horarioService.horarioDisponivel(r.empresaId(), r.servicoId(), r.data(), r.hora());
        if (!disponivel) {
            throw new IllegalArgumentException("Horário indisponível para esta empresa, serviço ou data");
        }

        Agendamento a = new Agendamento();
        a.setCliente(cliente);
        a.setEmpresa(empresa);
        a.setServico(servico);
        a.setData(r.data());
        a.setHora(r.hora());
        a.setObservacao(r.observacao());
        a.setStatus(StatusAgendamento.PENDENTE);
        return toResponse(repo.save(a));
    }

    public List<AgendamentoResponse> meus() {
        Usuario u = usuarioLogado();
        if (u.getRole() == Role.EMPRESA) {
            Empresa e = empresas.findByUsuario(u)
                    .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada para usuário"));
            return repo.findByEmpresaOrderByDataDescHoraDesc(e).stream().map(this::toResponse).toList();
        }
        return repo.findByClienteOrderByDataDescHoraDesc(u).stream().map(this::toResponse).toList();
    }

    public List<AgendamentoResponse> porEmpresa(Long empresaId) {
        Empresa e = empresas.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));
        return repo.findByEmpresaOrderByDataDescHoraDesc(e).stream().map(this::toResponse).toList();
    }

    @Transactional
    public AgendamentoResponse status(Long id, StatusAgendamento st) {
        Agendamento a = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado"));
        a.setStatus(st);
        return toResponse(repo.save(a));
    }

    private AgendamentoResponse toResponse(Agendamento a) {
        return new AgendamentoResponse(
                a.getId(),
                a.getCliente().getNome(),
                a.getEmpresa().getId(),
                a.getEmpresa().getNomeFantasia(),
                a.getServico().getId(),
                a.getServico().getNome(),
                a.getData(),
                a.getHora(),
                a.getStatus().name(),
                a.getObservacao()
        );
    }
}
