package com.example.desafio_eyetec.utils

import com.example.desafio_eyetec.data.local.entities.UserEntity
import com.example.desafio_eyetec.domain.models.User

fun UserEntity.toUser() = User(
    id = this.id,
    name = this.name,
    age = this.age,
    email = this.email,
    enable = this.enable
)

fun User.toUserEntity() : UserEntity{
    val entity = UserEntity(
        name = this.name,
        age = this.age,
        email = this.email,
        enable = this.enable
    )

    this.id?.let { entity.id = it }
    return entity
}