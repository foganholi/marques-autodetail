package com.MatheusFoganholi.marquesautodetail.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.dto.EmpresaRequest
import com.MatheusFoganholi.marquesautodetail.dto.EmpresaResponse
import com.MatheusFoganholi.marquesautodetail.network.RetrofitClient
import com.MatheusFoganholi.marquesautodetail.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmpresaPerfilActivity : AppCompatActivity() {

    private var empresaId: Long = -1
    private lateinit var edtNome: EditText
    private lateinit var edtDescricao: EditText
    private lateinit var edtTelefone: EditText
    private lateinit var edtRua: EditText
    private lateinit var edtNumero: EditText
    private lateinit var edtBairro: EditText
    private lateinit var edtCidade: EditText
    private lateinit var edtEstado: EditText
    private lateinit var edtLatitude: EditText
    private lateinit var edtLongitude: EditText
    private lateinit var switchAberta: Switch
    private lateinit var txtStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empresa_perfil)

        empresaId = SessionManager(this).empresaIdAtual() ?: -1
        bindViews()

        findViewById<TextView>(R.id.btnVoltarPerfilEmpresa).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnSalvarPerfilEmpresa).setOnClickListener { salvarPerfil() }
        findViewById<Button>(R.id.btnGerenciarHorariosEmpresa).setOnClickListener {
            startActivity(android.content.Intent(this, HorariosDisponiveisActivity::class.java))
        }

        if (empresaId > 0) carregarPerfil() else {
            Toast.makeText(this, "Empresa não encontrada na sessão. Faça login novamente.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun bindViews() {
        edtNome = findViewById(R.id.edtEmpresaNome)
        edtDescricao = findViewById(R.id.edtEmpresaDescricao)
        edtTelefone = findViewById(R.id.edtEmpresaTelefone)
        edtRua = findViewById(R.id.edtEmpresaRua)
        edtNumero = findViewById(R.id.edtEmpresaNumero)
        edtBairro = findViewById(R.id.edtEmpresaBairro)
        edtCidade = findViewById(R.id.edtEmpresaCidade)
        edtEstado = findViewById(R.id.edtEmpresaEstado)
        edtLatitude = findViewById(R.id.edtEmpresaLatitude)
        edtLongitude = findViewById(R.id.edtEmpresaLongitude)
        switchAberta = findViewById(R.id.switchEmpresaAberta)
        txtStatus = findViewById(R.id.txtEmpresaPerfilStatus)
    }

    private fun carregarPerfil() {
        txtStatus.text = "Carregando dados reais da empresa..."
        RetrofitClient.api(this).buscarEmpresa(empresaId).enqueue(object : Callback<EmpresaResponse> {
            override fun onResponse(call: Call<EmpresaResponse>, response: Response<EmpresaResponse>) {
                val empresa = response.body()
                if (response.isSuccessful && empresa != null) preencher(empresa) else {
                    txtStatus.text = "Não foi possível carregar a empresa."
                    Toast.makeText(this@EmpresaPerfilActivity, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EmpresaResponse>, t: Throwable) {
                txtStatus.text = "Backend indisponível. Verifique a API na porta 8080."
                Toast.makeText(this@EmpresaPerfilActivity, "Falha de conexão com backend", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun preencher(e: EmpresaResponse) {
        edtNome.setText(e.nomeFantasia)
        edtDescricao.setText(e.descricao ?: "")
        edtTelefone.setText(e.telefone ?: "")
        edtRua.setText(e.endereco?.substringBefore(",") ?: "")
        edtNumero.setText(e.endereco?.substringAfter(",", "")?.trim() ?: "")
        edtBairro.setText(e.bairro ?: "")
        edtCidade.setText("São Paulo")
        edtEstado.setText("SP")
        edtLatitude.setText(e.latitude?.toString() ?: "")
        edtLongitude.setText(e.longitude?.toString() ?: "")
        switchAberta.isChecked = e.aberta == true
        txtStatus.text = "Perfil carregado • Avaliação ${e.mediaAvaliacao ?: 0.0} ★"
    }

    private fun salvarPerfil() {
        val nome = edtNome.text.toString().trim()
        if (nome.isBlank()) {
            Toast.makeText(this, "Informe o nome da empresa", Toast.LENGTH_SHORT).show()
            return
        }
        val request = EmpresaRequest(
            nomeFantasia = nome,
            descricao = edtDescricao.text.toString().trim(),
            telefone = edtTelefone.text.toString().trim(),
            rua = edtRua.text.toString().trim(),
            numero = edtNumero.text.toString().trim(),
            bairro = edtBairro.text.toString().trim(),
            cidade = edtCidade.text.toString().trim().ifBlank { "São Paulo" },
            estado = edtEstado.text.toString().trim().ifBlank { "SP" },
            latitude = edtLatitude.text.toString().replace(",", ".").toDoubleOrNull(),
            longitude = edtLongitude.text.toString().replace(",", ".").toDoubleOrNull(),
            aberta = switchAberta.isChecked
        )
        txtStatus.text = "Salvando no backend..."
        RetrofitClient.api(this).atualizarEmpresa(empresaId, request).enqueue(object : Callback<EmpresaResponse> {
            override fun onResponse(call: Call<EmpresaResponse>, response: Response<EmpresaResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    preencher(response.body()!!)
                    Toast.makeText(this@EmpresaPerfilActivity, "Perfil atualizado", Toast.LENGTH_SHORT).show()
                } else {
                    txtStatus.text = "Erro ao salvar. Confira os campos."
                    Toast.makeText(this@EmpresaPerfilActivity, "Não foi possível salvar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EmpresaResponse>, t: Throwable) {
                txtStatus.text = "Backend indisponível. Não foi salvo."
                Toast.makeText(this@EmpresaPerfilActivity, "Falha de conexão", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
