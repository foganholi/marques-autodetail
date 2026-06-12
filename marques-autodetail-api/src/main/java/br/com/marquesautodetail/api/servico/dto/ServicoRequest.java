package br.com.marquesautodetail.api.servico.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServicoRequest(
        @NotBlank(message = "informe o nome")
        String nome,
        @Size(max = 1000, message = "a descrição deve ter no máximo 1000 caracteres")
        String descricao,
        @NotNull(message = "informe o preço")
        @DecimalMin(value = "0.0", inclusive = true, message = "o preço não pode ser negativo")
        BigDecimal preco,
        @NotNull(message = "informe a duração")
        @Positive(message = "a duração deve ser maior que zero")
        Integer duracaoMinutos
) {
}
