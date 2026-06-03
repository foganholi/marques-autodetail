package com.MatheusFoganholi.marquesautodetail.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.data.LocalAgendamentoRepository
import com.MatheusFoganholi.marquesautodetail.data.LocalMarketplaceRepository
import com.MatheusFoganholi.marquesautodetail.dto.AgendamentoRequest
import com.MatheusFoganholi.marquesautodetail.dto.AgendamentoResponse
import com.MatheusFoganholi.marquesautodetail.dto.ServicoResponse
import com.MatheusFoganholi.marquesautodetail.dto.toModel
import com.MatheusFoganholi.marquesautodetail.model.Agendamento
import com.MatheusFoganholi.marquesautodetail.model.Servico
import com.MatheusFoganholi.marquesautodetail.network.RetrofitClient
import com.MatheusFoganholi.marquesautodetail.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AgendamentosActivity : AppCompatActivity() {

    private lateinit var edtNomeCliente: EditText
    private lateinit var txtDataSelecionada: TextView
    private lateinit var txtAjudaHorarios: TextView
    private lateinit var containerHorarios: LinearLayout
    private lateinit var spnServico: Spinner
    private lateinit var cbConfirmacao: CheckBox
    private lateinit var btnConfirmar: Button
    private lateinit var btnEscolherData: Button
    private lateinit var btnVoltar: TextView

    private var empresaId: Int? = null
    private var empresaNome: String? = null
    private var servicoIdPreSelecionado: Int? = null
    private var servicoPreSelecionado: String? = null
    private var servicosDisponiveis: List<Servico> = emptyList()

    private var dataSelecionadaApi: String? = null
    private var horaSelecionada: String? = null

    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("pt", "BR"))
    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendamentos)

        empresaId = intent.getIntExtra("empresaId", -1).takeIf { it != -1 }
        empresaNome = intent.getStringExtra("empresaNome")
        servicoIdPreSelecionado = intent.getIntExtra("servicoId", -1).takeIf { it != -1 }
        servicoPreSelecionado = intent.getStringExtra("servicoNome")

        edtNomeCliente = findViewById(R.id.edtNomeCliente)
        edtNomeCliente.setText(SessionManager(this).nomeAtual().takeIf { it != "Usuário" } ?: "")
        txtDataSelecionada = findViewById(R.id.txtDataSelecionada)
        txtAjudaHorarios = findViewById(R.id.txtAjudaHorarios)
        containerHorarios = findViewById(R.id.containerHorarios)
        spnServico = findViewById(R.id.spnServicoAgendamento)
        cbConfirmacao = findViewById(R.id.cbConfirmacao)
        btnConfirmar = findViewById(R.id.btnConfirmarAgendamento)
        btnEscolherData = findViewById(R.id.btnEscolherData)
        btnVoltar = findViewById(R.id.btnVoltar)

        findViewById<TextView>(R.id.txtSubAgendamento).text =
            empresaNome?.let { "Agendando em $it" } ?: "Escolha uma data e horário disponível"

        btnVoltar.setOnClickListener { finish() }
        btnEscolherData.setOnClickListener { abrirCalendario() }

        btnConfirmar.setOnClickListener {
            val nome = edtNomeCliente.text.toString().trim()
            val data = dataSelecionadaApi
            val hora = horaSelecionada

            if (nome.isEmpty()) {
                Toast.makeText(this, "Informe o nome do cliente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (data == null) {
                Toast.makeText(this, "Selecione uma data no calendário", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (hora == null) {
                Toast.makeText(this, "Selecione um horário disponível", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!cbConfirmacao.isChecked) {
                Toast.makeText(this, "Confirme o agendamento", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val servico = servicosDisponiveis.getOrNull(spnServico.selectedItemPosition)
            if (empresaId == null || servico == null) {
                Toast.makeText(this, "Empresa ou serviço inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            criarAgendamentoReal(nome, data, hora, servico)
        }

        carregarServicos()
    }

    private fun carregarServicos() {
        val id = empresaId
        if (id == null) {
            configurarSpinnerServicos(LocalMarketplaceRepository.servicosPadrao())
            return
        }

        RetrofitClient.api(this).listarServicosEmpresa(id.toLong()).enqueue(object : Callback<List<ServicoResponse>> {
            override fun onResponse(call: Call<List<ServicoResponse>>, response: Response<List<ServicoResponse>>) {
                val body = response.body()
                if (response.isSuccessful && body != null && body.isNotEmpty()) {
                    configurarSpinnerServicos(body.map { it.toModel() })
                } else {
                    configurarSpinnerServicos(LocalMarketplaceRepository.servicosPadrao())
                }
            }

            override fun onFailure(call: Call<List<ServicoResponse>>, t: Throwable) {
                configurarSpinnerServicos(LocalMarketplaceRepository.servicosPadrao())
            }
        })
    }

    private fun configurarSpinnerServicos(servicos: List<Servico>) {
        servicosDisponiveis = servicos
        val nomes = servicos.map { "${it.nome} • ${it.duracaoMinutos} min" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nomes)
        spnServico.adapter = adapter

        val index = servicoIdPreSelecionado?.let { id -> servicos.indexOfFirst { it.id == id } }
            ?: servicoPreSelecionado?.let { nome -> servicos.indexOfFirst { it.nome == nome } }
            ?: -1
        if (index >= 0) spnServico.setSelection(index)

        spnServico.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                horaSelecionada = null
                carregarHorariosDisponiveis()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        carregarHorariosDisponiveis()
    }

    private fun abrirCalendario() {
        val hoje = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selecionada = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                dataSelecionadaApi = apiDateFormat.format(selecionada.time)
                txtDataSelecionada.text = displayDateFormat.format(selecionada.time)
                horaSelecionada = null
                cbConfirmacao.isChecked = false
                carregarHorariosDisponiveis()
            },
            hoje.get(Calendar.YEAR),
            hoje.get(Calendar.MONTH),
            hoje.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.minDate = hoje.timeInMillis
        dialog.show()
    }

    private fun carregarHorariosDisponiveis() {
        val empresa = empresaId ?: return
        val data = dataSelecionadaApi ?: run {
            limparHorarios("Escolha uma data para ver horários disponíveis.")
            return
        }
        val servico = servicosDisponiveis.getOrNull(spnServico.selectedItemPosition) ?: return

        limparHorarios("Carregando horários disponíveis...")

        RetrofitClient.api(this).listarHorariosDisponiveis(
            empresaId = empresa.toLong(),
            servicoId = servico.id.toLong(),
            data = data
        ).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                val horarios = response.body().orEmpty()
                if (response.isSuccessful && horarios.isNotEmpty()) {
                    renderizarHorarios(horarios)
                } else {
                    limparHorarios("Nenhum horário disponível para este dia. Escolha outra data.")
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                limparHorarios("Não foi possível carregar horários. Verifique se a API está rodando.")
            }
        })
    }

    private fun limparHorarios(mensagem: String) {
        containerHorarios.removeAllViews()
        txtAjudaHorarios.text = mensagem
    }

    private fun renderizarHorarios(horarios: List<String>) {
        containerHorarios.removeAllViews()
        txtAjudaHorarios.text = "Toque em um horário para selecionar."

        horarios.forEach { horario ->
            val chip = TextView(this).apply {
                text = horario
                textSize = 15f
                setTextColor(android.graphics.Color.WHITE)
                setPadding(28, 16, 28, 16)
                setBackgroundColor(if (horario == horaSelecionada) android.graphics.Color.parseColor("#F59E0B") else android.graphics.Color.parseColor("#111827"))
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 14, 0) }
                setOnClickListener {
                    horaSelecionada = horario
                    cbConfirmacao.isChecked = false
                    renderizarHorarios(horarios)
                }
            }
            containerHorarios.addView(chip)
        }
    }

    private fun criarAgendamentoReal(nomeCliente: String, data: String, hora: String, servico: Servico) {
        val empresa = empresaId ?: return
        val request = AgendamentoRequest(
            empresaId = empresa.toLong(),
            servicoId = servico.id.toLong(),
            data = data,
            hora = hora,
            observacao = "Solicitado pelo app Android"
        )

        RetrofitClient.api(this).criarAgendamento(request).enqueue(object : Callback<AgendamentoResponse> {
            override fun onResponse(call: Call<AgendamentoResponse>, response: Response<AgendamentoResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AgendamentosActivity, "Agendamento enviado para a empresa", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AgendamentosActivity, "Horário indisponível ou dados inválidos", Toast.LENGTH_LONG).show()
                    carregarHorariosDisponiveis()
                }
            }

            override fun onFailure(call: Call<AgendamentoResponse>, t: Throwable) {
                val agendamentoFallback = Agendamento(
                    id = null,
                    nomeCliente = nomeCliente,
                    data = data,
                    hora = hora,
                    servico = servico.nome,
                    empresaId = empresaId,
                    empresaNome = empresaNome,
                    status = "PENDENTE"
                )
                LocalAgendamentoRepository(this@AgendamentosActivity).salvar(agendamentoFallback)
                Toast.makeText(this@AgendamentosActivity, "API indisponível. Salvo como fallback local temporário.", Toast.LENGTH_LONG).show()
                finish()
            }
        })
    }
}
