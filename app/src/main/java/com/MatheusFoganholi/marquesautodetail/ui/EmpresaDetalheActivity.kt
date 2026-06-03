package com.MatheusFoganholi.marquesautodetail.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.adapter.ServicoAdapter
import com.MatheusFoganholi.marquesautodetail.data.LocalMarketplaceRepository
import com.MatheusFoganholi.marquesautodetail.dto.EmpresaResponse
import com.MatheusFoganholi.marquesautodetail.dto.ServicoResponse
import com.MatheusFoganholi.marquesautodetail.dto.toModel
import com.MatheusFoganholi.marquesautodetail.model.EmpresaAutomotiva
import com.MatheusFoganholi.marquesautodetail.model.Servico
import com.MatheusFoganholi.marquesautodetail.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class EmpresaDetalheActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EMPRESA_ID = "extra_empresa_id"
    }

    private var empresaId: Int = -1
    private var empresaAtual: EmpresaAutomotiva? = null
    private var servicosAtuais: List<Servico> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empresa_detalhe)
        empresaId = intent.getIntExtra(EXTRA_EMPRESA_ID, -1)
        findViewById<TextView>(R.id.btnVoltarDetalhe).setOnClickListener { finish() }
        carregarEmpresaReal()
    }

    private fun carregarEmpresaReal() {
        if (empresaId <= 0) {
            Toast.makeText(this, "Empresa inválida", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        RetrofitClient.api(this).buscarEmpresa(empresaId.toLong()).enqueue(object : Callback<EmpresaResponse> {
            override fun onResponse(call: Call<EmpresaResponse>, response: Response<EmpresaResponse>) {
                val empresa = response.body()
                if (response.isSuccessful && empresa != null) {
                    empresaAtual = empresa.toModel()
                    preencherEmpresa(empresaAtual!!)
                    carregarServicosReais(empresa.id)
                } else {
                    carregarFallbackLocal()
                }
            }

            override fun onFailure(call: Call<EmpresaResponse>, t: Throwable) {
                carregarFallbackLocal()
            }
        })
    }

    private fun carregarServicosReais(id: Long) {
        RetrofitClient.api(this).listarServicosEmpresa(id).enqueue(object : Callback<List<ServicoResponse>> {
            override fun onResponse(call: Call<List<ServicoResponse>>, response: Response<List<ServicoResponse>>) {
                val servicos = response.body()
                if (response.isSuccessful && servicos != null) {
                    servicosAtuais = servicos.map { it.toModel() }
                    configurarServicos(servicosAtuais)
                } else {
                    configurarServicos(empresaAtual?.servicos.orEmpty())
                }
            }

            override fun onFailure(call: Call<List<ServicoResponse>>, t: Throwable) {
                configurarServicos(empresaAtual?.servicos.orEmpty())
            }
        })
    }

    private fun carregarFallbackLocal() {
        val local = LocalMarketplaceRepository.buscarEmpresa(empresaId)
        if (local == null) {
            Toast.makeText(this, "Empresa não encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        Toast.makeText(this, "API indisponível. Exibindo fallback local.", Toast.LENGTH_SHORT).show()
        empresaAtual = local
        preencherEmpresa(local)
        configurarServicos(local.servicos)
    }

    private fun preencherEmpresa(empresa: EmpresaAutomotiva) {
        findViewById<TextView>(R.id.txtDetalheNomeEmpresa).text = empresa.nome
        findViewById<TextView>(R.id.txtDetalheEndereco).text = "${empresa.bairro} • ${empresa.endereco}"
        findViewById<TextView>(R.id.txtDetalheAvaliacao).text = "★ ${empresa.avaliacao} • ${empresa.tempoMedio}"
        findViewById<TextView>(R.id.txtDetalheDistancia).text = String.format(Locale("pt", "BR"), "%.1f km", empresa.distanciaKm)
        findViewById<TextView>(R.id.txtDetalheDescricao).text = empresa.descricao
    }

    private fun configurarServicos(servicos: List<Servico>) {
        servicosAtuais = servicos
        findViewById<RecyclerView>(R.id.recyclerServicosEmpresa).apply {
            layoutManager = LinearLayoutManager(this@EmpresaDetalheActivity)
            adapter = ServicoAdapter(servicos) { servico ->
                val empresa = empresaAtual ?: return@ServicoAdapter
                val intent = Intent(this@EmpresaDetalheActivity, AgendamentosActivity::class.java)
                intent.putExtra("empresaId", empresa.id)
                intent.putExtra("empresaNome", empresa.nome)
                intent.putExtra("servicoId", servico.id)
                intent.putExtra("servicoNome", servico.nome)
                startActivity(intent)
            }
        }
    }
}
