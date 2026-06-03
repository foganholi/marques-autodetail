package com.MatheusFoganholi.marquesautodetail.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.adapter.EmpresaAdapter
import com.MatheusFoganholi.marquesautodetail.data.LocalMarketplaceRepository
import com.MatheusFoganholi.marquesautodetail.dto.EmpresaResponse
import com.MatheusFoganholi.marquesautodetail.dto.toModel
import com.MatheusFoganholi.marquesautodetail.network.RetrofitClient
import com.MatheusFoganholi.marquesautodetail.util.LocationHelper
import com.MatheusFoganholi.marquesautodetail.util.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClienteHomeActivity : AppCompatActivity() {

    private lateinit var adapter: EmpresaAdapter
    private lateinit var txtLocalizacao: TextView

    private val pedirPermissaoLocalizacao = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        carregarEmpresas()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_home)

        val session = SessionManager(this)
        findViewById<TextView>(R.id.txtSaudacaoCliente).text = "Olá, ${session.nomeAtual()}"
        txtLocalizacao = findViewById(R.id.txtLocalizacaoCliente)

        adapter = EmpresaAdapter(emptyList()) { empresa ->
            val intent = Intent(this, EmpresaDetalheActivity::class.java)
            intent.putExtra(EmpresaDetalheActivity.EXTRA_EMPRESA_ID, empresa.id)
            startActivity(intent)
        }

        findViewById<RecyclerView>(R.id.recyclerEmpresasProximas).apply {
            layoutManager = LinearLayoutManager(this@ClienteHomeActivity)
            adapter = this@ClienteHomeActivity.adapter
        }

        findViewById<Chip>(R.id.chipUsarLocalizacao).setOnClickListener {
            pedirPermissaoLocalizacao.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }

        findViewById<BottomNavigationView>(R.id.bottomCliente).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_cliente_home -> true
                R.id.nav_cliente_historico -> { startActivity(Intent(this, AgendamentosListaActivity::class.java)); true }
                R.id.nav_cliente_favoritos -> { startActivity(Intent(this, FavoritosActivity::class.java)); true }
                R.id.nav_cliente_perfil -> { startActivity(Intent(this, ClientePerfilActivity::class.java)); true }
                else -> false
            }
        }

        if (!LocationHelper.temPermissao(this)) {
            pedirPermissaoLocalizacao.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            carregarEmpresas()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarEmpresas()
    }

    private fun carregarEmpresas() {
        val localizacao = LocationHelper.ultimaLocalizacao(this)
        val lat = localizacao?.latitude ?: -23.5505
        val lng = localizacao?.longitude ?: -46.6333
        txtLocalizacao.text = if (localizacao != null) "Empresas reais próximas da sua localização" else "Empresas reais próximas de São Paulo"

        RetrofitClient.api(this).listarEmpresasProximas(lat, lng).enqueue(object : Callback<List<EmpresaResponse>> {
            override fun onResponse(call: Call<List<EmpresaResponse>>, response: Response<List<EmpresaResponse>>) {
                val empresas = response.body()
                if (response.isSuccessful && empresas != null) {
                    adapter.atualizar(empresas.map { it.toModel() })
                } else {
                    carregarFallbackLocal()
                }
            }

            override fun onFailure(call: Call<List<EmpresaResponse>>, t: Throwable) {
                carregarFallbackLocal()
            }
        })
    }

    private fun carregarFallbackLocal() {
        val localizacao = LocationHelper.ultimaLocalizacao(this)
        Toast.makeText(this, "API indisponível. Exibindo fallback local temporário.", Toast.LENGTH_SHORT).show()
        adapter.atualizar(LocalMarketplaceRepository.listarEmpresasProximas(localizacao))
    }
}
