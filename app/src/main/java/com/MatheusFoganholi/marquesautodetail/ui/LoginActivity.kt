package com.MatheusFoganholi.marquesautodetail.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.dto.AuthResponse
import com.MatheusFoganholi.marquesautodetail.dto.LoginRequest
import com.MatheusFoganholi.marquesautodetail.model.UserRole
import com.MatheusFoganholi.marquesautodetail.network.ApiErrorParser
import com.MatheusFoganholi.marquesautodetail.network.RetrofitClient
import com.MatheusFoganholi.marquesautodetail.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtSenha: EditText
    private lateinit var btnEntrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtEmail = findViewById(R.id.edtEmail)
        edtSenha = findViewById(R.id.edtSenha)
        btnEntrar = findViewById(R.id.btnEntrar)

        findViewById<Button>(R.id.btnVoltar).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnCadastrar).setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
        btnEntrar.setOnClickListener { validarEEntrar() }
    }

    private fun validarEEntrar() {
        val email = edtEmail.text.toString().trim()
        val senha = edtSenha.text.toString()

        when {
            email.isBlank() || senha.isBlank() ->
                Toast.makeText(this, "Preencha e-mail e senha", Toast.LENGTH_SHORT).show()
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                Toast.makeText(this, "Informe um e-mail válido", Toast.LENGTH_SHORT).show()
            else -> fazerLogin(email, senha)
        }
    }

    private fun fazerLogin(email: String, senha: String) {
        btnEntrar.isEnabled = false
        RetrofitClient.api(this).login(LoginRequest(email, senha)).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                btnEntrar.isEnabled = true
                val auth = response.body()
                if (response.isSuccessful && auth != null) {
                    SessionManager(this@LoginActivity).salvarAuth(auth)
                    abrirFluxoPorRole(UserRole.from(auth.role))
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        ApiErrorParser.mensagem(response, "E-mail ou senha inválidos"),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, throwable: Throwable) {
                btnEntrar.isEnabled = true
                Toast.makeText(
                    this@LoginActivity,
                    "Não foi possível acessar a API. Verifique sua internet e tente novamente.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun abrirFluxoPorRole(role: UserRole) {
        val destino = if (role == UserRole.EMPRESA) {
            EmpresaDashboardActivity::class.java
        } else {
            ClienteHomeActivity::class.java
        }
        startActivity(Intent(this, destino))
        finish()
    }
}
