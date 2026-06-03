package br.com.marquesautodetail.api.servico.dto; import java.math.BigDecimal; public record ServicoRequest(String nome,String descricao,BigDecimal preco,Integer duracaoMinutos){}
