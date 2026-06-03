package com.MatheusFoganholi.marquesautodetail.model

data class EmpresaAutomotiva(
    val id: Int,
    val nome: String,
    val endereco: String,
    val bairro: String,
    val latitude: Double,
    val longitude: Double,
    val avaliacao: Double,
    val tempoMedio: String,
    val descricao: String,
    val servicos: List<Servico>,
    val distanciaKm: Double = 0.0,
    val favorita: Boolean = false
)
