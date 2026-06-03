package com.MatheusFoganholi.marquesautodetail.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.adapter.ServicoAdapter
import com.MatheusFoganholi.marquesautodetail.dto.ServicoResponse
import com.MatheusFoganholi.marquesautodetail.dto.toModel
import com.MatheusFoganholi.marquesautodetail.model.Servico
import com.MatheusFoganholi.marquesautodetail.network.RetrofitClient
import com.MatheusFoganholi.marquesautodetail.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmpresaServicosActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    private lateinit var txtStatus: TextView
    private var empresaId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empresa_servicos)
        empresaId = SessionManager(this).empresaIdAtual() ?: -1
        recycler = findViewById(R.id.recyclerEmpresaServicos)
        txtStatus = findViewById(R.id.txtStatusEmpresaServicos)
        recycler.layoutManager = LinearLayoutManager(this)

        findViewById<TextView>(R.id.btnVoltarEmpresaServicos).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnNovoServicoEmpresa).setOnClickListener {
            startActivity(Intent(this, ServicoFormActivity::class.java))
        }
        carregarServicos()
    }

    override fun onResume() {
        super.onResume()
        carregarServicos()
    }

    private fun carregarServicos() {
        if (empresaId <= 0) {
            txtStatus.text = "Empresa não encontrada na sessão. Faça login novamente."
            return
        }
        txtStatus.text = "Carregando serviços do backend..."
        RetrofitClient.api(this).listarServicosEmpresa(empresaId).enqueue(object : Callback<List<ServicoResponse>> {
            override fun onResponse(call: Call<List<ServicoResponse>>, response: Response<List<ServicoResponse>>) {
                val servicos = response.body()
                if (response.isSuccessful && servicos != null) {
                    txtStatus.text = if (servicos.isEmpty()) "Nenhum serviço cadastrado. Toque em Novo serviço." else "${servicos.size} serviço(s) cadastrado(s). Toque em um serviço para editar."
                    configurarLista(servicos.map { it.toModel() })
                } else {
                    txtStatus.text = "Não foi possível carregar serviços."
                    Toast.makeText(this@EmpresaServicosActivity, "Erro ao carregar serviços", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ServicoResponse>>, t: Throwable) {
                txtStatus.text = "Backend indisponível. Inicie a API Spring Boot."
                Toast.makeText(this@EmpresaServicosActivity, "Falha de conexão", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun configurarLista(servicos: List<Servico>) {
        recycler.adapter = ServicoAdapter(servicos) { servico ->
            val intent = Intent(this, ServicoFormActivity::class.java)
            intent.putExtra(ServicoFormActivity.EXTRA_SERVICO_ID, servico.id.toLong())
            intent.putExtra(ServicoFormActivity.EXTRA_NOME, servico.nome)
            intent.putExtra(ServicoFormActivity.EXTRA_DESCRICAO, servico.descricao)
            intent.putExtra(ServicoFormActivity.EXTRA_PRECO, servico.preco)
            intent.putExtra(ServicoFormActivity.EXTRA_DURACAO, servico.duracaoMinutos)
            startActivity(intent)
        }
    }
}
