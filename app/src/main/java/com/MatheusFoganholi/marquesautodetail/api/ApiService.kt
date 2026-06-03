package com.MatheusFoganholi.marquesautodetail.api

import com.MatheusFoganholi.marquesautodetail.dto.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("auth/register/cliente")
    fun cadastrarCliente(@Body request: RegisterRequest): Call<AuthResponse>

    @POST("auth/register/empresa")
    fun cadastrarEmpresa(@Body request: RegisterRequest): Call<AuthResponse>

    @GET("empresas/proximas")
    fun listarEmpresasProximas(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double
    ): Call<List<EmpresaResponse>>

    @GET("empresas/{id}")
    fun buscarEmpresa(@Path("id") id: Long): Call<EmpresaResponse>

    @PUT("empresas/{id}")
    fun atualizarEmpresa(@Path("id") id: Long, @Body request: EmpresaRequest): Call<EmpresaResponse>

    @GET("empresas/{empresaId}/servicos")
    fun listarServicosEmpresa(@Path("empresaId") empresaId: Long): Call<List<ServicoResponse>>

    @POST("empresas/{empresaId}/servicos")
    fun criarServico(@Path("empresaId") empresaId: Long, @Body request: ServicoRequest): Call<ServicoResponse>

    @PUT("servicos/{id}")
    fun atualizarServico(@Path("id") id: Long, @Body request: ServicoRequest): Call<ServicoResponse>

    @DELETE("servicos/{id}")
    fun removerServico(@Path("id") id: Long): Call<Void>

    @GET("empresas/{empresaId}/horarios")
    fun listarHorariosEmpresa(@Path("empresaId") empresaId: Long): Call<List<HorarioResponse>>

    @POST("empresas/{empresaId}/horarios")
    fun criarHorario(@Path("empresaId") empresaId: Long, @Body request: HorarioRequest): Call<HorarioResponse>

    @DELETE("horarios/{id}")
    fun removerHorario(@Path("id") id: Long): Call<Void>

    @GET("empresas/{empresaId}/horarios/disponiveis")
    fun listarHorariosDisponiveis(
        @Path("empresaId") empresaId: Long,
        @Query("servicoId") servicoId: Long,
        @Query("data") data: String
    ): Call<List<String>>

    @POST("agendamentos")
    fun criarAgendamento(@Body request: AgendamentoRequest): Call<AgendamentoResponse>

    @GET("agendamentos/me")
    fun listarMeusAgendamentos(): Call<List<AgendamentoResponse>>

    @GET("empresas/{empresaId}/agendamentos")
    fun listarAgendamentosEmpresa(@Path("empresaId") empresaId: Long): Call<List<AgendamentoResponse>>

    @PUT("agendamentos/{id}/confirmar")
    fun confirmarAgendamento(@Path("id") id: Long): Call<AgendamentoResponse>

    @PUT("agendamentos/{id}/recusar")
    fun recusarAgendamento(@Path("id") id: Long): Call<AgendamentoResponse>

    @PUT("agendamentos/{id}/concluir")
    fun concluirAgendamento(@Path("id") id: Long): Call<AgendamentoResponse>

    @PUT("agendamentos/{id}/cancelar")
    fun cancelarAgendamento(@Path("id") id: Long): Call<AgendamentoResponse>

    @GET("favoritos/me")
    fun listarFavoritos(): Call<List<EmpresaResponse>>

    @POST("favoritos/{empresaId}")
    fun favoritarEmpresa(@Path("empresaId") empresaId: Long): Call<Void>

    @DELETE("favoritos/{empresaId}")
    fun removerFavorito(@Path("empresaId") empresaId: Long): Call<Void>
}
