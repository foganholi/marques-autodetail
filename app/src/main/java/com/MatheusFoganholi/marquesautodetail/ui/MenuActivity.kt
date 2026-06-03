package com.MatheusFoganholi.marquesautodetail.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.MatheusFoganholi.marquesautodetail.R

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnServicos = findViewById<Button>(R.id.btnServicos)
        val btnAgendamentos = findViewById<Button>(R.id.btnAgendamentos)
        val btnSair = findViewById<Button>(R.id.btnSair)
        val btnVoltar = findViewById<Button>(R.id.btnVoltar)

        btnServicos.setOnClickListener {
            startActivity(Intent(this, ServicosActivity::class.java))
        }

        btnAgendamentos.setOnClickListener {
            startActivity(Intent(this, AgendamentosActivity::class.java))
        }

        btnSair.setOnClickListener {
            finish()
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }
}