package com.MatheusFoganholi.marquesautodetail.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.MatheusFoganholi.marquesautodetail.R
import com.MatheusFoganholi.marquesautodetail.dto.AuthResponse
import com.MatheusFoganholi.marquesautodetail.dto.RegisterRequest
import com.MatheusFoganholi.marquesautodetail.model.UserRole
import com.MatheusFoganholi.marquesautodetail.network.ApiErrorParser
import com.MatheusFoganholi.marquesautodetail.network.RetrofitClient
import com.MatheusFoganholi.marquesautodetail.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroActivity : AppCompatActivity() {

    private lateinit var edtNome: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtSenha: EditText
    private lateinit var edtConfirmarSenha: EditText
    private lateinit var btnCriarConta: Button
    private lateinit var radioGroupTipoConta: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        edtNome = findViewById(R.id.edtNome)
        edtEmail = findViewById(R.id.edtEmailCadastro)
        edtSenha = findViewById(R.id.edtSenhaCadastro)
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha)
        btnCriarConta = findViewById(R.id.btnCriarConta)
        radioGroupTipoConta = findViewById(R.id.radioGroupTipoConta)

        findViewById<Button>(R.id.btnVoltar).setOnClickListener { finish() }
        btnCriarConta.setOnClickListener { validarECadastrar() }
    }

    private fun validarECadastrar() {
        val nome = edtNome.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val senha = edtSenha.text.toString()
        val confirmarSenha = edtConfirmarSenha.text.toString()

        when {
            nome.isBlank() || email.isBlank() || senha.isBlank() || confirmarSenha.isBlank() ->
                Toast.makeText(this, "Preencha os campos obrigatórios", Toast.LENGTH_SHORT).show()
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                Toast.makeText(this, "Informe um e-mail válido", Toast.LENGTH_SHORT).show()
            senha.length < 6 ->
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
            senha != confirmarSenha ->
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
            else -> cadastrarUsuario(nome, email, senha, roleSelecionada())
        }
    }

    private fun roleSelecionada(): UserRole {
        return if (radioGroupTipoConta.checkedRadioButtonId == R.id.rbContaEmpresa) {
            UserRole.EMPRESA
        } else {
            UserRole.CLIENTE
        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String, role: UserRole) {
        btnCriarConta.isEnabled = false
        val request = RegisterRequest(
            nome = nome,
            email = email,
            senha = senha,
            nomeFantasia = nome.takeIf { role == UserRole.EMPRESA }
        )
        val chamada = if (role == UserRole.EMPRESA) {
            RetrofitClient.api(this).cadastrarEmpresa(request)
        } else {
            RetrofitClient.api(this).cadastrarCliente(request)
        }

        chamada.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                btnCriarConta.isEnabled = true
                val auth = response.body()
                if (response.isSuccessful && auth != null) {
                    SessionManager(this@CadastroActivity).salvarAuth(auth)
                    val destino = if (UserRole.from(auth.role) == UserRole.EMPRESA) {
                        EmpresaDashboardActivity::class.java
                    } else {
                        ClienteHomeActivity::class.java
                    }
                    startActivity(Intent(this@CadastroActivity, destino))
                    finish()
                } else {
                    Toast.makeText(
                        this@CadastroActivity,
                        ApiErrorParser.mensagem(response, "Não foi possível criar a conta"),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, throwable: Throwable) {
                btnCriarConta.isEnabled = true
                Toast.makeText(
                    this@CadastroActivity,
                    "Não foi possível acessar a API. Verifique sua internet e tente novamente.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
