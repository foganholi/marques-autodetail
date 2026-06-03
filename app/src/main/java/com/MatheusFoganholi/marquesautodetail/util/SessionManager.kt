package com.MatheusFoganholi.marquesautodetail.util

import android.content.Context
import com.MatheusFoganholi.marquesautodetail.dto.AuthResponse
import com.MatheusFoganholi.marquesautodetail.model.UserRole
import com.MatheusFoganholi.marquesautodetail.model.Usuario

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("marques_session", Context.MODE_PRIVATE)

    fun salvarAuth(auth: AuthResponse) {
        prefs.edit()
            .putString("token", auth.token)
            .putLong("usuarioId", auth.id)
            .putString("nome", auth.nome)
            .putString("email", auth.email)
            .putString("role", auth.role)
            .putLong("empresaId", auth.empresaId ?: -1L)
            .apply()
    }

    // Mantido apenas para compatibilidade com telas antigas. O fluxo real usa salvarAuth().
    fun salvarUsuario(usuario: Usuario) {
        val nomeSeguro = usuario.nome.takeIf { it.isNotBlank() }
            ?: usuario.email.substringBefore("@").replaceFirstChar { it.uppercase() }
        prefs.edit()
            .putString("nome", nomeSeguro)
            .putString("email", usuario.email)
            .putString("role", usuario.role)
            .apply()
    }

    fun tokenAtual(): String? = prefs.getString("token", null)

    fun usuarioIdAtual(): Long = prefs.getLong("usuarioId", -1L)

    fun empresaIdAtual(): Long? = prefs.getLong("empresaId", -1L).takeIf { it > 0 }

    fun salvarRole(role: UserRole) {
        prefs.edit().putString("role", role.name).apply()
    }

    fun roleAtual(): UserRole = UserRole.from(prefs.getString("role", UserRole.CLIENTE.name))

    fun nomeAtual(): String = prefs.getString("nome", "Usuário") ?: "Usuário"

    fun emailAtual(): String = prefs.getString("email", "email@exemplo.com") ?: "email@exemplo.com"

    fun limpar() {
        prefs.edit().clear().apply()
    }
}
