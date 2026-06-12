package br.com.marquesautodetail.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "informe o nome")
        String nome,
        @NotBlank(message = "informe o e-mail")
        @Email(message = "e-mail inválido")
        String email,
        @NotBlank(message = "informe a senha")
        @Size(min = 6, message = "a senha deve ter pelo menos 6 caracteres")
        String senha,
        String telefone,
        String nomeFantasia
) {
}
