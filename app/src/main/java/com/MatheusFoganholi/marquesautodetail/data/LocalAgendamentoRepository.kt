package com.MatheusFoganholi.marquesautodetail.data

import android.content.Context
import com.MatheusFoganholi.marquesautodetail.model.Agendamento
import org.json.JSONArray
import org.json.JSONObject

class LocalAgendamentoRepository(context: Context) {
    private val prefs = context.getSharedPreferences("marques_agendamentos", Context.MODE_PRIVATE)

    fun salvar(agendamento: Agendamento): Agendamento {
        val listaAtual = listar().toMutableList()
        val novo = agendamento.copy(id = agendamento.id ?: proximoId(listaAtual))
        listaAtual.add(0, novo)
        persistir(listaAtual)
        return novo
    }

    fun listar(): List<Agendamento> {
        val json = prefs.getString(CHAVE_AGENDAMENTOS, "[]") ?: "[]"
        val array = JSONArray(json)
        return (0 until array.length()).map { index ->
            val obj = array.getJSONObject(index)
            Agendamento(
                id = obj.optIntOrNull("id"),
                nomeCliente = obj.optString("nomeCliente"),
                data = obj.optString("data"),
                hora = obj.optString("hora"),
                servico = obj.optString("servico"),
                empresaId = obj.optIntOrNull("empresaId"),
                empresaNome = obj.optStringOrNull("empresaNome"),
                status = obj.optString("status", "PENDENTE")
            )
        }
    }

    fun atualizarStatus(id: Int, novoStatus: String) {
        val atualizada = listar().map { agendamento ->
            if (agendamento.id == id) agendamento.copy(status = novoStatus) else agendamento
        }
        persistir(atualizada)
    }

    fun popularExemploSeVazio() {
        if (listar().isNotEmpty()) return
        persistir(
            listOf(
                Agendamento(
                    id = 1,
                    nomeCliente = "Cliente demonstração",
                    data = "05/06/2026",
                    hora = "14:30",
                    servico = "Lavagem completa",
                    empresaId = 1,
                    empresaNome = "Marques AutoDetail",
                    status = "PENDENTE"
                ),
                Agendamento(
                    id = 2,
                    nomeCliente = "Matheus",
                    data = "08/06/2026",
                    hora = "10:00",
                    servico = "Polimento técnico",
                    empresaId = 1,
                    empresaNome = "Marques AutoDetail",
                    status = "CONFIRMADO"
                )
            )
        )
    }

    private fun persistir(lista: List<Agendamento>) {
        val array = JSONArray()
        lista.forEach { agendamento ->
            array.put(
                JSONObject()
                    .put("id", agendamento.id)
                    .put("nomeCliente", agendamento.nomeCliente)
                    .put("data", agendamento.data)
                    .put("hora", agendamento.hora)
                    .put("servico", agendamento.servico)
                    .put("empresaId", agendamento.empresaId)
                    .put("empresaNome", agendamento.empresaNome)
                    .put("status", agendamento.status)
            )
        }
        prefs.edit().putString(CHAVE_AGENDAMENTOS, array.toString()).apply()
    }

    private fun proximoId(lista: List<Agendamento>): Int {
        return (lista.mapNotNull { it.id }.maxOrNull() ?: 0) + 1
    }

    private fun JSONObject.optIntOrNull(name: String): Int? {
        return if (has(name) && !isNull(name)) optInt(name) else null
    }

    private fun JSONObject.optStringOrNull(name: String): String? {
        return if (has(name) && !isNull(name)) optString(name) else null
    }

    companion object {
        private const val CHAVE_AGENDAMENTOS = "lista"
    }
}
