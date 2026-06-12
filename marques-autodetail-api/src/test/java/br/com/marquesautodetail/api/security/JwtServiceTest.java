package br.com.marquesautodetail.api.security;

import br.com.marquesautodetail.api.usuario.Role;
import br.com.marquesautodetail.api.usuario.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    @Test
    void deveGerarTokenERecuperarEmail() {
        JwtService service = new JwtService();
        ReflectionTestUtils.setField(service, "secret", "segredo-de-teste-com-mais-de-trinta-e-dois-caracteres");
        ReflectionTestUtils.setField(service, "expirationMs", 60_000L);

        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setEmail("cliente@teste.com");
        usuario.setRole(Role.CLIENTE);

        String token = service.gerarToken(usuario);

        assertThat(service.getEmail(token)).isEqualTo("cliente@teste.com");
    }
}
