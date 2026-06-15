package com.example.desafio_eyetec.domain.models

import java.util.UUID

data class User(
    val id: UUID? = null,
    val name: String,
    val age: Int,
    val email: String,
    val enable: Boolean
)
