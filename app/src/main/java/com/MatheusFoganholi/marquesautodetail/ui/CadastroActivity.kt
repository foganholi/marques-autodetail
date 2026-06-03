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
import com.MatheusFoganholi.marquesautodetail.dto.RegisterRequest
import com.MatheusFoganholi.marquesautodetail.model.UserRole
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
    private lateinit var btnVoltar: Button
    private lateinit var radioGroupTipoConta: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        edtNome = findViewById(R.id.edtNome)
        edtEmail = findViewById(R.id.edtEmailCadastro)
        edtSenha = findViewById(R.id.edtSenhaCadastro)
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha)
        btnCriarConta = findViewById(R.id.btnCriarConta)
        btnVoltar = findViewById(R.id.btnVoltar)
        radioGroupTipoConta = findViewById(R.id.radioGroupTipoConta)

        btnVoltar.setOnClickListener { finish() }

        btnCriarConta.setOnClickListener {
            val nome = edtNome.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val senha = edtSenha.text.toString().trim()
            val confirmarSenha = edtConfirmarSenha.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            cadastrarUsuario(nome, email, senha, roleSelecionada())
        }
    }

    private fun roleSelecionada(): UserRole {
        return if (radioGroupTipoConta.checkedRadioButtonId == R.id.rbContaEmpresa) UserRole.EMPRESA else UserRole.CLIENTE
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String, role: UserRole) {
        val request = RegisterRequest(
            nome = nome,
            email = email,
            senha = senha,
            nomeFantasia = if (role == UserRole.EMPRESA) nome else null
        )
        val chamada = if (role == UserRole.EMPRESA) {
            RetrofitClient.api(this).cadastrarEmpresa(request)
        } else {
            RetrofitClient.api(this).cadastrarCliente(request)
        }

        chamada.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val auth = response.body()
                if (response.isSuccessful && auth != null) {
                    SessionManager(this@CadastroActivity).salvarAuth(auth)
                    Toast.makeText(this@CadastroActivity, "Cadastro real criado no backend", Toast.LENGTH_SHORT).show()
                    val destino = if (UserRole.from(auth.role) == UserRole.EMPRESA) EmpresaDashboardActivity::class.java else ClienteHomeActivity::class.java
                    startActivity(Intent(this@CadastroActivity, destino))
                    finish()
                } else {
                    Toast.makeText(this@CadastroActivity, "Erro ao cadastrar no backend", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@CadastroActivity, "Backend indisponível. Inicie a API Spring Boot.", Toast.LENGTH_LONG).show()
            }
        })
    }
}
