package com.MatheusFoganholi.marquesautodetail.dto

data class LoginRequest(val email: String, val senha: String)
data class RegisterRequest(val nome: String, val email: String, val senha: String, val telefone: String? = null, val nomeFantasia: String? = null)
data class AuthResponse(val token: String, val id: Long, val nome: String, val email: String, val role: String, val empresaId: Long? = null)
