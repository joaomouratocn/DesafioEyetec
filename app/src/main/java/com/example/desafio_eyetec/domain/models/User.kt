package com.example.desafio_eyetec.domain.models

data class User(
    val id: Long? = null,
    val name: String,
    val age: Int,
    val email: String,
    val enable: Boolean,
    val photoPath: String? = null
)
