package com.MatheusFoganholi.marquesautodetail.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.adapter.EmpresaAdapter
import com.MatheusFoganholi.marquesautodetail.data.LocalMarketplaceRepository
import com.MatheusFoganholi.marquesautodetail.dto.EmpresaResponse
import com.MatheusFoganholi.marquesautodetail.dto.toModel
import com.MatheusFoganholi.marquesautodetail.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritosActivity : AppCompatActivity() {
    private lateinit var resumo: TextView
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)
        resumo = findViewById(R.id.txtFavoritosResumo)
        recycler = findViewById(R.id.recyclerFavoritos)
        findViewById<TextView>(R.id.btnVoltarFavoritos).setOnClickListener { finish() }
        carregarFavoritosReais()
    }

    private fun carregarFavoritosReais() {
        RetrofitClient.api(this).listarFavoritos().enqueue(object : Callback<List<EmpresaResponse>> {
            override fun onResponse(call: Call<List<EmpresaResponse>>, response: Response<List<EmpresaResponse>>) {
                val favoritos = response.body()
                if (response.isSuccessful && favoritos != null) {
                    val lista = favoritos.map { it.toModel() }
                    resumo.text = if (lista.isEmpty()) "Você ainda não possui empresas favoritas." else "${lista.size} empresa(s) salva(s) no backend."
                    configurarLista(lista)
                } else {
                    carregarFallback()
                }
            }

            override fun onFailure(call: Call<List<EmpresaResponse>>, t: Throwable) = carregarFallback()
        })
    }

    private fun carregarFallback() {
        val favoritos = LocalMarketplaceRepository.listarEmpresasProximas(null).filter { it.favorita }
        resumo.text = if (favoritos.isEmpty()) "Você ainda não possui empresas favoritas." else "${favoritos.size} empresa(s) no fallback local temporário."
        Toast.makeText(this, "API indisponível. Exibindo fallback local.", Toast.LENGTH_SHORT).show()
        configurarLista(favoritos)
    }

    private fun configurarLista(lista: List<com.MatheusFoganholi.marquesautodetail.model.EmpresaAutomotiva>) {
        recycler.apply {
            layoutManager = LinearLayoutManager(this@FavoritosActivity)
            adapter = EmpresaAdapter(lista) { empresa ->
                startActivity(Intent(this@FavoritosActivity, EmpresaDetalheActivity::class.java).putExtra(EmpresaDetalheActivity.EXTRA_EMPRESA_ID, empresa.id))
            }
        }
    }
}
