package com.example.desafio_eyetec.domain.repositories

import com.example.desafio_eyetec.domain.models.User

interface UserRepository {
    suspend fun insertUsers(users: List<User>)
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
    suspend fun getUserByStatus(status: Boolean): List<User>
}