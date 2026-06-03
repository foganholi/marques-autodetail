package com.MatheusFoganholi.marquesautodetail.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.util.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class EmpresaDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empresa_dashboard)

        findViewById<TextView>(R.id.txtDashboardEmpresaNome).text = "Painel da empresa"
        findViewById<TextView>(R.id.txtDashboardResumo).text = "Gerencie perfil, agenda, serviços e agendamentos recebidos."

        findViewById<Button>(R.id.btnEmpresaAgendamentos).setOnClickListener {
            startActivity(Intent(this, AgendamentosListaActivity::class.java))
        }

        findViewById<Button>(R.id.btnEmpresaServicos).setOnClickListener {
            startActivity(Intent(this, EmpresaServicosActivity::class.java))
        }

        findViewById<Button>(R.id.btnEmpresaHorarios).setOnClickListener {
            startActivity(Intent(this, HorariosDisponiveisActivity::class.java))
        }

        findViewById<Button>(R.id.btnEmpresaPerfil).setOnClickListener {
            startActivity(Intent(this, EmpresaPerfilActivity::class.java))
        }

        findViewById<Button>(R.id.btnEmpresaSair).setOnClickListener {
            SessionManager(this).limpar()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<BottomNavigationView>(R.id.bottomEmpresa).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_empresa_dashboard -> true
                R.id.nav_empresa_agenda -> {
                    startActivity(Intent(this, AgendamentosListaActivity::class.java))
                    true
                }
                R.id.nav_empresa_servicos -> {
                    startActivity(Intent(this, EmpresaServicosActivity::class.java))
                    true
                }
                R.id.nav_empresa_perfil -> {
                    startActivity(Intent(this, EmpresaPerfilActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
