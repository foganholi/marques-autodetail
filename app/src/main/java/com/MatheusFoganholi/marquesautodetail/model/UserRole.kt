package com.MatheusFoganholi.marquesautodetail.model

enum class UserRole {
    CLIENTE,
    EMPRESA;

    companion object {
        fun from(value: String?): UserRole {
            return values().firstOrNull { it.name.equals(value, ignoreCase = true) } ?: CLIENTE
        }
    }
}
