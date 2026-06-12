package br.com.marquesautodetail.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter filter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtAuthFilter filter, ObjectMapper objectMapper) {
        this.filter = filter;
        this.objectMapper = objectMapper;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) ->
                                escreverErro(response, HttpServletResponse.SC_UNAUTHORIZED, "Token ausente, inválido ou expirado"))
                        .accessDeniedHandler((request, response, exception) ->
                                escreverErro(response, HttpServletResponse.SC_FORBIDDEN, exception.getMessage())))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/register/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/empresas",
                                "/empresas/proximas",
                                "/empresas/*",
                                "/empresas/*/servicos",
                                "/empresas/*/horarios",
                                "/empresas/*/horarios/disponiveis",
                                "/empresas/*/avaliacoes"
                        ).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private void escreverErro(HttpServletResponse response, int status, String mensagem) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), Map.of("erro", mensagem));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
