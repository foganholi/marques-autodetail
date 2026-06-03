package com.MatheusFoganholi.marquesautodetail.dto

import com.MatheusFoganholi.marquesautodetail.model.Agendamento
import com.MatheusFoganholi.marquesautodetail.model.EmpresaAutomotiva
import com.MatheusFoganholi.marquesautodetail.model.Servico

fun EmpresaResponse.toModel(servicos: List<Servico> = emptyList()): EmpresaAutomotiva = EmpresaAutomotiva(
    id = id.toInt(),
    nome = nomeFantasia,
    endereco = endereco ?: "Endereço não informado",
    bairro = bairro ?: "",
    latitude = latitude ?: 0.0,
    longitude = longitude ?: 0.0,
    avaliacao = mediaAvaliacao ?: 0.0,
    tempoMedio = if (aberta == true) "Aberta agora" else "Fechada",
    descricao = descricao ?: "Empresa cadastrada na plataforma Marques AutoDetail.",
    servicos = servicos,
    distanciaKm = distanciaKm ?: 0.0,
    favorita = false
)

fun ServicoResponse.toModel(): Servico = Servico(
    id = id.toInt(),
    nome = nome,
    descricao = descricao ?: "",
    preco = preco,
    duracaoMinutos = duracaoMinutos
)

fun AgendamentoResponse.toModel(): Agendamento = Agendamento(
    id = id.toInt(),
    nomeCliente = nomeCliente,
    data = data,
    hora = hora,
    servico = servicoNome,
    empresaId = empresaId.toInt(),
    empresaNome = empresaNome,
    status = status
)
