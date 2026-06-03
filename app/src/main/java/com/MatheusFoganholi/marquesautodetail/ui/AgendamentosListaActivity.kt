package com.MatheusFoganholi.marquesautodetail.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.adapter.AgendamentoAdapter
import com.MatheusFoganholi.marquesautodetail.data.LocalAgendamentoRepository
import com.MatheusFoganholi.marquesautodetail.dto.AgendamentoResponse
import com.MatheusFoganholi.marquesautodetail.dto.toModel
import com.MatheusFoganholi.marquesautodetail.model.Agendamento
import com.MatheusFoganholi.marquesautodetail.model.UserRole
import com.MatheusFoganholi.marquesautodetail.network.RetrofitClient
import com.MatheusFoganholi.marquesautodetail.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AgendamentosListaActivity : AppCompatActivity() {

    private lateinit var adapter: AgendamentoAdapter
    private lateinit var repository: LocalAgendamentoRepository
    private var modoEmpresa: Boolean = false
    private lateinit var txtEstadoVazio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendamentos_lista)

        repository = LocalAgendamentoRepository(this)
        modoEmpresa = SessionManager(this).roleAtual() == UserRole.EMPRESA

        findViewById<TextView>(R.id.btnVoltarListaAgendamentos).setOnClickListener { finish() }
        txtEstadoVazio = findViewById(R.id.txtEstadoVazioAgendamentos)
        findViewById<TextView>(R.id.txtTituloListaAgendamentos).text = if (modoEmpresa) "Agendamentos recebidos" else "Meu histórico"
        findViewById<TextView>(R.id.txtResumoListaAgendamentos).text = if (modoEmpresa) "Confirme ou recuse horários solicitados pelos clientes." else "Acompanhe seus pedidos de agendamento."

        adapter = AgendamentoAdapter(
            lista = emptyList(),
            modoEmpresa = modoEmpresa,
            onConfirmar = { atualizarStatusBackend(it, true) },
            onRecusar = { atualizarStatusBackend(it, false) }
        )

        findViewById<RecyclerView>(R.id.recyclerAgendamentos).apply {
            layoutManager = LinearLayoutManager(this@AgendamentosListaActivity)
            adapter = this@AgendamentosListaActivity.adapter
        }

        carregarAgendamentos()
    }

    override fun onResume() {
        super.onResume()
        carregarAgendamentos()
    }

    private fun carregarAgendamentos() {
        val session = SessionManager(this)
        val chamada = if (modoEmpresa && session.empresaIdAtual() != null) {
            RetrofitClient.api(this).listarAgendamentosEmpresa(session.empresaIdAtual()!!)
        } else {
            RetrofitClient.api(this).listarMeusAgendamentos()
        }

        chamada.enqueue(object : Callback<List<AgendamentoResponse>> {
            override fun onResponse(call: Call<List<AgendamentoResponse>>, response: Response<List<AgendamentoResponse>>) {
                val lista = response.body()
                if (response.isSuccessful && lista != null) {
                    val modelos = lista.map { it.toModel() }
                    adapter.atualizarLista(modelos)
                    atualizarEstadoVazio(modelos)
                } else {
                    carregarLocal()
                }
            }

            override fun onFailure(call: Call<List<AgendamentoResponse>>, t: Throwable) {
                carregarLocal()
            }
        })
    }

    private fun carregarLocal() {
        val lista = repository.listar()
        adapter.atualizarLista(lista)
        atualizarEstadoVazio(lista)
        Toast.makeText(this, "API indisponível. Exibindo fallback local temporário.", Toast.LENGTH_SHORT).show()
    }

    private fun atualizarEstadoVazio(lista: List<Agendamento>) {
        if (lista.isEmpty()) {
            txtEstadoVazio.visibility = android.view.View.VISIBLE
            txtEstadoVazio.text = if (modoEmpresa) {
                "Nenhum agendamento recebido ainda. Quando um cliente agendar, o pedido aparecerá aqui."
            } else {
                "Você ainda não tem agendamentos. Escolha uma empresa e marque um horário."
            }
        } else {
            txtEstadoVazio.visibility = android.view.View.GONE
        }
    }

    private fun atualizarStatusBackend(agendamento: Agendamento, confirmar: Boolean) {
        val id = agendamento.id ?: return
        val chamada = if (confirmar) RetrofitClient.api(this).confirmarAgendamento(id.toLong()) else RetrofitClient.api(this).recusarAgendamento(id.toLong())
        chamada.enqueue(object : Callback<AgendamentoResponse> {
            override fun onResponse(call: Call<AgendamentoResponse>, response: Response<AgendamentoResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AgendamentosListaActivity, "Status atualizado no backend", Toast.LENGTH_SHORT).show()
                    carregarAgendamentos()
                } else {
                    atualizarLocal(agendamento, if (confirmar) "CONFIRMADO" else "RECUSADO")
                }
            }

            override fun onFailure(call: Call<AgendamentoResponse>, t: Throwable) {
                atualizarLocal(agendamento, if (confirmar) "CONFIRMADO" else "RECUSADO")
            }
        })
    }

    private fun atualizarLocal(agendamento: Agendamento, status: String) {
        val id = agendamento.id ?: return
        repository.atualizarStatus(id, status)
        Toast.makeText(this, "API indisponível. Status alterado só no fallback local.", Toast.LENGTH_SHORT).show()
        carregarLocal()
    }
}
