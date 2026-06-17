package com.example.desafio_eyetec.domain.repositories

import com.example.desafio_eyetec.domain.models.User

interface UserRepository {
    suspend fun insertUsers(users: List<User>)
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
    suspend fun getUsersByStatus(status: Boolean): List<User>
    suspend fun getUserById(id: Long): User?
    suspend fun getAllUsers(): List<User>
    suspend fun searchUsers(query: String): List<User>
}