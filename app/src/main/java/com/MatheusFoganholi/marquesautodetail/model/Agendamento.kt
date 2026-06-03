package com.MatheusFoganholi.marquesautodetail.model

data class Agendamento(
    val id: Int? = null,
    val nomeCliente: String,
    val data: String,
    val hora: String,
    val servico: String,
    val empresaId: Int? = null,
    val empresaNome: String? = null,
    val status: String = "PENDENTE"
)
