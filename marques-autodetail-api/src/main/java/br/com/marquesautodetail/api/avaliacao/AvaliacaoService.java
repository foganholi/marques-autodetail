package br.com.marquesautodetail.api.avaliacao;

import br.com.marquesautodetail.api.agendamento.Agendamento;
import br.com.marquesautodetail.api.agendamento.AgendamentoRepository;
import br.com.marquesautodetail.api.agendamento.StatusAgendamento;
import br.com.marquesautodetail.api.avaliacao.dto.AvaliacaoRequest;
import br.com.marquesautodetail.api.avaliacao.dto.AvaliacaoResponse;
import br.com.marquesautodetail.api.empresa.Empresa;
import br.com.marquesautodetail.api.empresa.EmpresaRepository;
import br.com.marquesautodetail.api.security.AuthenticatedUserService;
import br.com.marquesautodetail.api.usuario.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository repo;
    private final EmpresaRepository empresas;
    private final AgendamentoRepository agendamentos;
    private final AuthenticatedUserService authenticatedUser;

    public AvaliacaoService(
            AvaliacaoRepository repo,
            EmpresaRepository empresas,
            AgendamentoRepository agendamentos,
            AuthenticatedUserService authenticatedUser
    ) {
        this.repo = repo;
        this.empresas = empresas;
        this.agendamentos = agendamentos;
        this.authenticatedUser = authenticatedUser;
    }

    @Transactional
    public AvaliacaoResponse criar(AvaliacaoRequest request) {
        Usuario cliente = authenticatedUser.clienteAtual();
        Empresa empresa = empresas.findById(request.empresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));
        Agendamento agendamento = agendamentos.findById(request.agendamentoId())
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado"));

        if (!agendamento.getCliente().getId().equals(cliente.getId())) {
            throw new AccessDeniedException("Você só pode avaliar seus próprios agendamentos");
        }
        if (!agendamento.getEmpresa().getId().equals(empresa.getId())) {
            throw new IllegalArgumentException("A empresa não corresponde ao agendamento");
        }
        if (agendamento.getStatus() != StatusAgendamento.CONCLUIDO) {
            throw new IllegalArgumentException("Só é possível avaliar atendimento concluído");
        }
        if (repo.existsByAgendamento(agendamento)) {
            throw new IllegalArgumentException("Este agendamento já foi avaliado");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setCliente(cliente);
        avaliacao.setEmpresa(empresa);
        avaliacao.setAgendamento(agendamento);
        avaliacao.setNota(request.nota());
        avaliacao.setComentario(request.comentario());
        repo.save(avaliacao);
        atualizarMedia(empresa);
        return toResponse(avaliacao);
    }

    public List<AvaliacaoResponse> porEmpresa(Long id) {
        Empresa empresa = empresas.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));
        return repo.findByEmpresa(empresa).stream().map(this::toResponse).toList();
    }

    private void atualizarMedia(Empresa empresa) {
        var lista = repo.findByEmpresa(empresa);
        double media = lista.stream().mapToInt(Avaliacao::getNota).average().orElse(0);
        empresa.setMediaAvaliacao(Math.round(media * 10) / 10.0);
        empresas.save(empresa);
    }

    private AvaliacaoResponse toResponse(Avaliacao avaliacao) {
        return new AvaliacaoResponse(
                avaliacao.getId(),
                avaliacao.getCliente().getNome(),
                avaliacao.getEmpresa().getId(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getCriadoEm()
        );
    }
}
