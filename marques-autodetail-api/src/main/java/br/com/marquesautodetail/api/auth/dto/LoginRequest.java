package br.com.marquesautodetail.api.auth.dto; import jakarta.validation.constraints.*; public record LoginRequest(@Email String email,@NotBlank String senha){}
