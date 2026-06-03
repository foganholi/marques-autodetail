package com.MatheusFoganholi.marquesautodetail.model

data class Servico(
    val id: Int,
    val nome: String,
    val descricao: String,
    val preco: Double,
    val duracaoMinutos: Int
)
