package com.MatheusFoganholi.marquesautodetail.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.dto.ServicoRequest
import com.MatheusFoganholi.marquesautodetail.dto.ServicoResponse
import com.MatheusFoganholi.marquesautodetail.network.RetrofitClient
import com.MatheusFoganholi.marquesautodetail.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServicoFormActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SERVICO_ID = "servicoId"
        const val EXTRA_NOME = "nome"
        const val EXTRA_DESCRICAO = "descricao"
        const val EXTRA_PRECO = "preco"
        const val EXTRA_DURACAO = "duracao"
    }

    private var servicoId: Long = -1
    private var empresaId: Long = -1
    private lateinit var edtNome: EditText
    private lateinit var edtDescricao: EditText
    private lateinit var edtPreco: EditText
    private lateinit var edtDuracao: EditText
    private lateinit var txtStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servico_form)
        empresaId = SessionManager(this).empresaIdAtual() ?: -1
        servicoId = intent.getLongExtra(EXTRA_SERVICO_ID, -1)

        edtNome = findViewById(R.id.edtServicoNome)
        edtDescricao = findViewById(R.id.edtServicoDescricao)
        edtPreco = findViewById(R.id.edtServicoPreco)
        edtDuracao = findViewById(R.id.edtServicoDuracao)
        txtStatus = findViewById(R.id.txtServicoFormStatus)

        findViewById<TextView>(R.id.btnVoltarServicoForm).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnSalvarServico).setOnClickListener { salvar() }
        findViewById<Button>(R.id.btnExcluirServico).setOnClickListener { excluir() }

        if (servicoId > 0) {
            findViewById<TextView>(R.id.txtTituloServicoForm).text = "Editar serviço"
            edtNome.setText(intent.getStringExtra(EXTRA_NOME) ?: "")
            edtDescricao.setText(intent.getStringExtra(EXTRA_DESCRICAO) ?: "")
            edtPreco.setText(intent.getDoubleExtra(EXTRA_PRECO, 0.0).toString())
            edtDuracao.setText(intent.getIntExtra(EXTRA_DURACAO, 60).toString())
            findViewById<Button>(R.id.btnExcluirServico).visibility = View.VISIBLE
        } else {
            findViewById<TextView>(R.id.txtTituloServicoForm).text = "Novo serviço"
            findViewById<Button>(R.id.btnExcluirServico).visibility = View.GONE
        }
    }

    private fun montarRequest(): ServicoRequest? {
        val nome = edtNome.text.toString().trim()
        val preco = edtPreco.text.toString().replace(",", ".").toDoubleOrNull()
        val duracao = edtDuracao.text.toString().toIntOrNull()
        if (nome.isBlank() || preco == null || duracao == null || duracao <= 0) {
            Toast.makeText(this, "Preencha nome, preço e duração corretamente", Toast.LENGTH_SHORT).show()
            return null
        }
        return ServicoRequest(
            nome = nome,
            descricao = edtDescricao.text.toString().trim(),
            preco = preco,
            duracaoMinutos = duracao,
            ativo = true
        )
    }

    private fun salvar() {
        val request = montarRequest() ?: return
        if (empresaId <= 0) {
            Toast.makeText(this, "Empresa não encontrada. Faça login novamente.", Toast.LENGTH_LONG).show()
            return
        }
        txtStatus.text = "Salvando no backend..."
        val call = if (servicoId > 0) RetrofitClient.api(this).atualizarServico(servicoId, request)
        else RetrofitClient.api(this).criarServico(empresaId, request)
        call.enqueue(object : Callback<ServicoResponse> {
            override fun onResponse(call: Call<ServicoResponse>, response: Response<ServicoResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ServicoFormActivity, "Serviço salvo", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    txtStatus.text = "Erro ao salvar serviço."
                    Toast.makeText(this@ServicoFormActivity, "Não foi possível salvar", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ServicoResponse>, t: Throwable) {
                txtStatus.text = "Backend indisponível."
                Toast.makeText(this@ServicoFormActivity, "Falha de conexão", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun excluir() {
        if (servicoId <= 0) return
        txtStatus.text = "Excluindo..."
        RetrofitClient.api(this).removerServico(servicoId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ServicoFormActivity, "Serviço removido", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    txtStatus.text = "Não foi possível excluir."
                    Toast.makeText(this@ServicoFormActivity, "Erro ao excluir", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                txtStatus.text = "Backend indisponível."
                Toast.makeText(this@ServicoFormActivity, "Falha de conexão", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
