package com.MatheusFoganholi.marquesautodetail.data

import android.location.Location
import com.MatheusFoganholi.marquesautodetail.model.EmpresaAutomotiva
import com.MatheusFoganholi.marquesautodetail.model.Servico

object LocalMarketplaceRepository {

    private val lavagemCompleta = Servico(
        id = 1,
        nome = "Lavagem completa",
        descricao = "Lavagem externa, interna e acabamento dos detalhes.",
        preco = 80.0,
        duracaoMinutos = 90
    )

    private val polimento = Servico(
        id = 2,
        nome = "Polimento técnico",
        descricao = "Correção de pintura, brilho e proteção da lataria.",
        preco = 150.0,
        duracaoMinutos = 180
    )

    private val higienizacao = Servico(
        id = 3,
        nome = "Higienização interna",
        descricao = "Limpeza profunda de bancos, carpetes, painel e teto.",
        preco = 120.0,
        duracaoMinutos = 150
    )

    private val vitrificacao = Servico(
        id = 4,
        nome = "Vitrificação",
        descricao = "Proteção premium da pintura com maior durabilidade.",
        preco = 450.0,
        duracaoMinutos = 360
    )

    private val empresasBase = listOf(
        EmpresaAutomotiva(
            id = 1,
            nome = "Marques AutoDetail",
            endereco = "Av. Paulista, 1000",
            bairro = "Bela Vista",
            latitude = -23.5632,
            longitude = -46.6544,
            avaliacao = 4.9,
            tempoMedio = "Hoje, 15:30",
            descricao = "Estética automotiva premium com foco em lavagem técnica, polimento e proteção.",
            servicos = listOf(lavagemCompleta, polimento, higienizacao, vitrificacao),
            favorita = true
        ),
        EmpresaAutomotiva(
            id = 2,
            nome = "Prime Car Studio",
            endereco = "Rua Augusta, 850",
            bairro = "Consolação",
            latitude = -23.5527,
            longitude = -46.6553,
            avaliacao = 4.7,
            tempoMedio = "Amanhã, 09:00",
            descricao = "Serviços rápidos de limpeza, enceramento e acabamento interno.",
            servicos = listOf(lavagemCompleta, higienizacao)
        ),
        EmpresaAutomotiva(
            id = 3,
            nome = "Detail Garage SP",
            endereco = "Rua Vergueiro, 1200",
            bairro = "Paraíso",
            latitude = -23.5750,
            longitude = -46.6400,
            avaliacao = 4.8,
            tempoMedio = "Hoje, 18:00",
            descricao = "Especialista em polimento, vitrificação e preparação estética.",
            servicos = listOf(polimento, vitrificacao, lavagemCompleta)
        ),
        EmpresaAutomotiva(
            id = 4,
            nome = "Auto Clean Express",
            endereco = "Av. Brigadeiro Luís Antônio, 2100",
            bairro = "Jardins",
            latitude = -23.5715,
            longitude = -46.6589,
            avaliacao = 4.5,
            tempoMedio = "Hoje, 16:45",
            descricao = "Atendimento ágil para quem precisa cuidar do carro na rotina.",
            servicos = listOf(lavagemCompleta, higienizacao)
        )
    )

    fun listarEmpresasProximas(userLocation: Location?): List<EmpresaAutomotiva> {
        return empresasBase.map { empresa ->
            val distancia = userLocation?.let {
                calcularDistanciaKm(it.latitude, it.longitude, empresa.latitude, empresa.longitude)
            } ?: calcularDistanciaKm(-23.5505, -46.6333, empresa.latitude, empresa.longitude)
            empresa.copy(distanciaKm = distancia)
        }.sortedBy { it.distanciaKm }
    }

    fun buscarEmpresa(id: Int): EmpresaAutomotiva? {
        return listarEmpresasProximas(null).firstOrNull { it.id == id }
    }

    fun servicosPadrao(): List<Servico> = listOf(lavagemCompleta, polimento, higienizacao, vitrificacao)

    private fun calcularDistanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val result = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, result)
        return result[0] / 1000.0
    }
}
