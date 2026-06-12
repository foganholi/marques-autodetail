package com.MatheusFoganholi.marquesautodetail.network

import com.google.gson.JsonParser
import retrofit2.Response

object ApiErrorParser {

    fun mensagem(response: Response<*>, fallback: String): String {
        return runCatching {
            response.errorBody()
                ?.string()
                ?.takeIf { it.isNotBlank() }
                ?.let { JsonParser.parseString(it).asJsonObject.get("erro")?.asString }
        }.getOrNull() ?: fallback
    }
}
