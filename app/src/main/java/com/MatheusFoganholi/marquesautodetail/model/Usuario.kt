package com.MatheusFoganholi.marquesautodetail.model

data class Usuario(
    val id: Int? = null,
    val nome: String,
    val email: String,
    val senha: String,
    val role: String = UserRole.CLIENTE.name
)
