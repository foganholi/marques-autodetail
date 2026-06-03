package com.MatheusFoganholi.marquesautodetail.dto

data class EmpresaResponse(
    val id: Long,
    val nomeFantasia: String,
    val descricao: String? = null,
    val telefone: String? = null,
    val endereco: String? = null,
    val bairro: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val mediaAvaliacao: Double? = null,
    val aberta: Boolean? = null,
    val distanciaKm: Double? = null
)

data class EmpresaRequest(
    val nomeFantasia: String? = null,
    val descricao: String? = null,
    val telefone: String? = null,
    val cnpj: String? = null,
    val cep: String? = null,
    val rua: String? = null,
    val numero: String? = null,
    val bairro: String? = null,
    val cidade: String? = null,
    val estado: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val aberta: Boolean? = null
)

data class ServicoResponse(
    val id: Long,
    val nome: String,
    val descricao: String? = null,
    val preco: Double,
    val duracaoMinutos: Int,
    val ativo: Boolean? = true
)

data class ServicoRequest(
    val nome: String,
    val descricao: String? = null,
    val preco: Double,
    val duracaoMinutos: Int,
    val ativo: Boolean? = true
)

data class HorarioResponse(
    val id: Long,
    val diaSemana: String,
    val horaInicio: String,
    val horaFim: String,
    val ativo: Boolean? = true
)

data class HorarioRequest(
    val diaSemana: String,
    val horaInicio: String,
    val horaFim: String
)

data class AgendamentoRequest(
    val empresaId: Long,
    val servicoId: Long,
    val data: String,
    val hora: String,
    val observacao: String? = null
)

data class AgendamentoResponse(
    val id: Long,
    val nomeCliente: String,
    val empresaId: Long,
    val empresaNome: String,
    val servicoId: Long,
    val servicoNome: String,
    val data: String,
    val hora: String,
    val status: String,
    val observacao: String? = null
)
