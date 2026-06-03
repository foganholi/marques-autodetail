package com.MatheusFoganholi.marquesautodetail.network

import android.content.Context
import com.MatheusFoganholi.marquesautodetail.api.ApiService
import com.MatheusFoganholi.marquesautodetail.util.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Emulador Android: 10.0.2.2 acessa o localhost da máquina onde o Spring Boot está rodando.
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    fun api(context: Context): ApiService {
        val appContext = context.applicationContext
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = SessionManager(appContext).tokenAtual()
                val requestBuilder = chain.request().newBuilder()
                if (!token.isNullOrBlank()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
