package br.com.marquesautodetail.api.servico;

import br.com.marquesautodetail.api.empresa.Empresa;
import br.com.marquesautodetail.api.security.AuthenticatedUserService;
import br.com.marquesautodetail.api.servico.dto.ServicoRequest;
import br.com.marquesautodetail.api.servico.dto.ServicoResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository repo;
    private final AuthenticatedUserService authenticatedUser;

    public ServicoService(ServicoRepository repo, AuthenticatedUserService authenticatedUser) {
        this.repo = repo;
        this.authenticatedUser = authenticatedUser;
    }

    public List<ServicoResponse> listarPorEmpresa(Long empresaId) {
        return repo.findByEmpresaIdAndAtivoTrue(empresaId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public ServicoResponse criar(Long empresaId, ServicoRequest request) {
        Empresa empresa = authenticatedUser.exigirEmpresa(empresaId);
        Servico servico = new Servico();
        servico.setEmpresa(empresa);
        aplicar(servico, request);
        servico.setAtivo(true);
        return toResponse(repo.save(servico));
    }

    @Transactional
    public ServicoResponse atualizar(Long id, ServicoRequest request) {
        Servico servico = buscarDoProprietario(id);
        aplicar(servico, request);
        return toResponse(repo.save(servico));
    }

    @Transactional
    public void remover(Long id) {
        Servico servico = buscarDoProprietario(id);
        servico.setAtivo(false);
        repo.save(servico);
    }

    private Servico buscarDoProprietario(Long id) {
        Servico servico = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        Empresa empresaAtual = authenticatedUser.empresaAtual();
        if (!servico.getEmpresa().getId().equals(empresaAtual.getId())) {
            throw new AccessDeniedException("Você não pode alterar serviços de outra empresa");
        }
        return servico;
    }

    private void aplicar(Servico servico, ServicoRequest request) {
        servico.setNome(request.nome().trim());
        servico.setDescricao(request.descricao() == null ? null : request.descricao().trim());
        servico.setPreco(request.preco());
        servico.setDuracaoMinutos(request.duracaoMinutos());
    }

    public ServicoResponse toResponse(Servico servico) {
        return new ServicoResponse(
                servico.getId(),
                servico.getNome(),
                servico.getDescricao(),
                servico.getPreco(),
                servico.getDuracaoMinutos(),
                servico.getAtivo()
        );
    }
}
