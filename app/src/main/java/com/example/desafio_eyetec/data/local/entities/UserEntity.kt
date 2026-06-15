package com.example.desafio_eyetec.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import kotlin.uuid.Uuid

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val age: Int,
    val email: String,
    val enable: Boolean
)