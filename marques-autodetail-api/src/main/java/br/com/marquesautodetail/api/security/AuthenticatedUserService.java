package br.com.marquesautodetail.api.security;

import br.com.marquesautodetail.api.empresa.Empresa;
import br.com.marquesautodetail.api.empresa.EmpresaRepository;
import br.com.marquesautodetail.api.usuario.Role;
import br.com.marquesautodetail.api.usuario.Usuario;
import br.com.marquesautodetail.api.usuario.UsuarioRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    private final UsuarioRepository usuarios;
    private final EmpresaRepository empresas;

    public AuthenticatedUserService(UsuarioRepository usuarios, EmpresaRepository empresas) {
        this.usuarios = usuarios;
        this.empresas = empresas;
    }

    public Usuario usuarioAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Autenticação obrigatória");
        }

        return usuarios.findByEmail(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("Usuário autenticado não encontrado"));
    }

    public Usuario clienteAtual() {
        Usuario usuario = usuarioAtual();
        if (usuario.getRole() != Role.CLIENTE) {
            throw new AccessDeniedException("Operação permitida apenas para clientes");
        }
        return usuario;
    }

    public Empresa empresaAtual() {
        Usuario usuario = usuarioAtual();
        if (usuario.getRole() != Role.EMPRESA) {
            throw new AccessDeniedException("Operação permitida apenas para empresas");
        }

        return empresas.findByUsuario(usuario)
                .orElseThrow(() -> new AccessDeniedException("Empresa não encontrada para o usuário autenticado"));
    }

    public Empresa exigirEmpresa(Long empresaId) {
        Empresa empresa = empresaAtual();
        if (!empresa.getId().equals(empresaId)) {
            throw new AccessDeniedException("Você não pode alterar dados de outra empresa");
        }
        return empresa;
    }
}
