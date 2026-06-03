package com.MatheusFoganholi.marquesautodetail.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.dto.HorarioRequest
import com.MatheusFoganholi.marquesautodetail.dto.HorarioResponse
import com.MatheusFoganholi.marquesautodetail.network.RetrofitClient
import com.MatheusFoganholi.marquesautodetail.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HorariosDisponiveisActivity : AppCompatActivity() {

    private var empresaId: Long = -1
    private lateinit var spnDia: Spinner
    private lateinit var edtInicio: EditText
    private lateinit var edtFim: EditText
    private lateinit var txtStatus: TextView
    private lateinit var container: LinearLayout

    private val dias = listOf(
        "MONDAY" to "Segunda-feira",
        "TUESDAY" to "Terça-feira",
        "WEDNESDAY" to "Quarta-feira",
        "THURSDAY" to "Quinta-feira",
        "FRIDAY" to "Sexta-feira",
        "SATURDAY" to "Sábado",
        "SUNDAY" to "Domingo"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horarios_disponiveis)
        empresaId = SessionManager(this).empresaIdAtual() ?: -1

        spnDia = findViewById(R.id.spnDiaSemanaHorario)
        edtInicio = findViewById(R.id.edtHoraInicio)
        edtFim = findViewById(R.id.edtHoraFim)
        txtStatus = findViewById(R.id.txtHorariosStatus)
        container = findViewById(R.id.containerHorariosEmpresa)

        spnDia.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dias.map { it.second })
        findViewById<TextView>(R.id.btnVoltarHorarios).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnAdicionarHorario).setOnClickListener { adicionarHorario() }

        carregarHorarios()
    }

    override fun onResume() {
        super.onResume()
        carregarHorarios()
    }

    private fun carregarHorarios() {
        if (empresaId <= 0) {
            txtStatus.text = "Empresa não encontrada. Faça login novamente."
            return
        }
        txtStatus.text = "Carregando horários..."
        RetrofitClient.api(this).listarHorariosEmpresa(empresaId).enqueue(object : Callback<List<HorarioResponse>> {
            override fun onResponse(call: Call<List<HorarioResponse>>, response: Response<List<HorarioResponse>>) {
                val horarios = response.body()
                if (response.isSuccessful && horarios != null) {
                    txtStatus.text = if (horarios.isEmpty()) "Nenhum horário cadastrado." else "${horarios.size} faixa(s) de atendimento cadastrada(s)."
                    renderizar(horarios)
                } else {
                    txtStatus.text = "Erro ao carregar horários."
                }
            }

            override fun onFailure(call: Call<List<HorarioResponse>>, t: Throwable) {
                txtStatus.text = "Backend indisponível."
            }
        })
    }

    private fun renderizar(horarios: List<HorarioResponse>) {
        container.removeAllViews()
        if (horarios.isEmpty()) {
            val vazio = TextView(this).apply {
                text = "Cadastre os dias e horários para os clientes conseguirem agendar."
                setTextColor(resources.getColor(R.color.mad_text_muted, theme))
                setPadding(0, 14, 0, 14)
            }
            container.addView(vazio)
            return
        }
        horarios.forEach { h ->
            val linha = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
                background = getDrawable(R.drawable.bg_card_dark)
            }
            val diaPt = dias.firstOrNull { it.first == h.diaSemana }?.second ?: h.diaSemana
            val texto = TextView(this).apply {
                text = "$diaPt • ${h.horaInicio.take(5)} às ${h.horaFim.take(5)}"
                setTextColor(resources.getColor(R.color.mad_text, theme))
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            val btnRemover = Button(this).apply {
                text = "Remover"
                setOnClickListener { removerHorario(h.id) }
            }
            linha.addView(texto)
            linha.addView(btnRemover)
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 12)
            }
            container.addView(linha, params)
        }
    }

    private fun adicionarHorario() {
        val inicio = normalizarHora(edtInicio.text.toString())
        val fim = normalizarHora(edtFim.text.toString())
        if (inicio == null || fim == null) {
            Toast.makeText(this, "Use horários no formato HH:mm. Ex: 08:00", Toast.LENGTH_SHORT).show()
            return
        }
        val dia = dias[spnDia.selectedItemPosition].first
        val request = HorarioRequest(diaSemana = dia, horaInicio = inicio, horaFim = fim)
        txtStatus.text = "Salvando horário..."
        RetrofitClient.api(this).criarHorario(empresaId, request).enqueue(object : Callback<HorarioResponse> {
            override fun onResponse(call: Call<HorarioResponse>, response: Response<HorarioResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@HorariosDisponiveisActivity, "Horário adicionado", Toast.LENGTH_SHORT).show()
                    edtInicio.setText("")
                    edtFim.setText("")
                    carregarHorarios()
                } else {
                    txtStatus.text = "Erro ao adicionar horário."
                }
            }

            override fun onFailure(call: Call<HorarioResponse>, t: Throwable) {
                txtStatus.text = "Backend indisponível."
            }
        })
    }

    private fun removerHorario(id: Long) {
        RetrofitClient.api(this).removerHorario(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Toast.makeText(this@HorariosDisponiveisActivity, "Horário removido", Toast.LENGTH_SHORT).show()
                carregarHorarios()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@HorariosDisponiveisActivity, "Falha ao remover", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun normalizarHora(valor: String): String? {
        val v = valor.trim()
        val regex = Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")
        if (!regex.matches(v)) return null
        val partes = v.split(":")
        return partes[0].padStart(2, '0') + ":" + partes[1]
    }
}
