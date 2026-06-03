package com.MatheusFoganholi.marquesautodetail.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.dto.AuthResponse
import com.MatheusFoganholi.marquesautodetail.dto.LoginRequest
import com.MatheusFoganholi.marquesautodetail.model.UserRole
import com.MatheusFoganholi.marquesautodetail.network.RetrofitClient
import com.MatheusFoganholi.marquesautodetail.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtSenha: EditText
    private lateinit var btnEntrar: Button
    private lateinit var btnCadastrar: Button
    private lateinit var btnVoltar: Button
    private lateinit var radioGroupRole: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtEmail = findViewById(R.id.edtEmail)
        edtSenha = findViewById(R.id.edtSenha)
        btnEntrar = findViewById(R.id.btnEntrar)
        btnCadastrar = findViewById(R.id.btnCadastrar)
        btnVoltar = findViewById(R.id.btnVoltar)
        radioGroupRole = findViewById(R.id.radioGroupLoginRole)

        btnVoltar.setOnClickListener { finish() }

        btnEntrar.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val senha = edtSenha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else {
                fazerLogin(email, senha)
            }
        }

        btnCadastrar.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }

    private fun abrirFluxoPorRole(role: UserRole) {
        val destino = if (role == UserRole.EMPRESA) EmpresaDashboardActivity::class.java else ClienteHomeActivity::class.java
        startActivity(Intent(this, destino))
        finish()
    }

    private fun fazerLogin(email: String, senha: String) {
        RetrofitClient.api(this).login(LoginRequest(email, senha)).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val auth = response.body()
                if (response.isSuccessful && auth != null) {
                    SessionManager(this@LoginActivity).salvarAuth(auth)
                    Toast.makeText(this@LoginActivity, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
                    abrirFluxoPorRole(UserRole.from(auth.role))
                } else {
                    Toast.makeText(this@LoginActivity, "E-mail ou senha inválidos no backend", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Backend indisponível. Inicie a API Spring Boot na porta 8080.", Toast.LENGTH_LONG).show()
            }
        })
    }
}
