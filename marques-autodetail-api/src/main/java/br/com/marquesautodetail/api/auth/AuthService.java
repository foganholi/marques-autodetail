package br.com.marquesautodetail.api.auth;

import br.com.marquesautodetail.api.auth.dto.AuthResponse;
import br.com.marquesautodetail.api.auth.dto.LoginRequest;
import br.com.marquesautodetail.api.auth.dto.RegisterRequest;
import br.com.marquesautodetail.api.empresa.Empresa;
import br.com.marquesautodetail.api.empresa.EmpresaRepository;
import br.com.marquesautodetail.api.endereco.Endereco;
import br.com.marquesautodetail.api.security.JwtService;
import br.com.marquesautodetail.api.usuario.Role;
import br.com.marquesautodetail.api.usuario.Usuario;
import br.com.marquesautodetail.api.usuario.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final EmpresaRepository empresas;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final AuthenticationManager authManager;

    public AuthService(
            UsuarioRepository usuarios,
            EmpresaRepository empresas,
            PasswordEncoder encoder,
            JwtService jwt,
            AuthenticationManager authManager
    ) {
        this.usuarios = usuarios;
        this.empresas = empresas;
        this.encoder = encoder;
        this.jwt = jwt;
        this.authManager = authManager;
    }

    @Transactional
    public AuthResponse cadastrarCliente(RegisterRequest request) {
        return cadastrar(request, Role.CLIENTE);
    }

    @Transactional
    public AuthResponse cadastrarEmpresa(RegisterRequest request) {
        return cadastrar(request, Role.EMPRESA);
    }

    private AuthResponse cadastrar(RegisterRequest request, Role role) {
        String email = normalizarEmail(request.email());
        if (usuarios.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim());
        usuario.setEmail(email);
        usuario.setSenha(encoder.encode(request.senha()));
        usuario.setTelefone(textoOpcional(request.telefone()));
        usuario.setRole(role);
        usuarios.save(usuario);

        Long empresaId = null;
        if (role == Role.EMPRESA) {
            empresaId = empresas.save(criarEmpresaInicial(request, usuario)).getId();
        }

        return resposta(usuario, empresaId);
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizarEmail(request.email());
        authManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.senha()));

        Usuario usuario = usuarios.findByEmailIgnoreCase(email).orElseThrow();
        Long empresaId = empresas.findByUsuario(usuario).map(Empresa::getId).orElse(null);
        return resposta(usuario, empresaId);
    }

    private Empresa criarEmpresaInicial(RegisterRequest request, Usuario usuario) {
        Empresa empresa = new Empresa();
        empresa.setUsuario(usuario);
        empresa.setNomeFantasia(
                request.nomeFantasia() != null && !request.nomeFantasia().isBlank()
                        ? request.nomeFantasia().trim()
                        : request.nome().trim()
        );
        empresa.setDescricao("Estética automotiva cadastrada na plataforma Marques AutoDetail.");
        empresa.setTelefone(textoOpcional(request.telefone()));
        empresa.setLatitude(-23.5505);
        empresa.setLongitude(-46.6333);

        Endereco endereco = new Endereco();
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setBairro("Centro");
        endereco.setRua("Endereço a configurar");
        empresa.setEndereco(endereco);
        return empresa;
    }

    private AuthResponse resposta(Usuario usuario, Long empresaId) {
        return new AuthResponse(
                jwt.gerarToken(usuario),
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name(),
                empresaId
        );
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String textoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
