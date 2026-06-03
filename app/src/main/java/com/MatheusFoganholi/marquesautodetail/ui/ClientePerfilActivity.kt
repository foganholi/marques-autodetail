package com.MatheusFoganholi.marquesautodetail.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.util.SessionManager

class ClientePerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_perfil)

        val session = SessionManager(this)
        findViewById<TextView>(R.id.txtPerfilClienteNome).text = session.nomeAtual()
        findViewById<TextView>(R.id.txtPerfilClienteEmail).text = session.emailAtual()
        findViewById<TextView>(R.id.txtPerfilClienteRole).text = "Conta: Cliente"

        findViewById<TextView>(R.id.btnVoltarPerfilCliente).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnPerfilHistorico).setOnClickListener {
            startActivity(Intent(this, AgendamentosListaActivity::class.java))
        }
        findViewById<Button>(R.id.btnPerfilFavoritos).setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }
        findViewById<Button>(R.id.btnPerfilSair).setOnClickListener {
            session.limpar()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
}
