package com.MatheusFoganholi.marquesautodetail.network

import android.content.Context
import com.MatheusFoganholi.marquesautodetail.BuildConfig
import com.MatheusFoganholi.marquesautodetail.api.ApiService
import com.MatheusFoganholi.marquesautodetail.util.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Para uma API local no emulador, altere API_BASE_URL no build.gradle.kts
    // para http://10.0.2.2:8080/api/ e habilite cleartext somente no debug.
    @Volatile
    private var service: ApiService? = null

    fun api(context: Context): ApiService {
        return service ?: synchronized(this) {
            service ?: criarServico(context.applicationContext).also { service = it }
        }
    }

    private fun criarServico(context: Context): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            redactHeader("Authorization")
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                SessionManager(context).tokenAtual()
                    ?.takeIf { it.isNotBlank() }
                    ?.let { request.header("Authorization", "Bearer $it") }
                chain.proceed(request.build())
            }
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
