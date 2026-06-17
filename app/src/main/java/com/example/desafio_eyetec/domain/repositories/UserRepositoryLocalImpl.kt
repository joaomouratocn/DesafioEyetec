package com.example.desafio_eyetec.domain.repositories

import com.example.desafio_eyetec.data.local.UserDao
import com.example.desafio_eyetec.domain.models.User
import com.example.desafio_eyetec.utils.toUser
import com.example.desafio_eyetec.utils.toUserEntity

class UserRepositoryLocalImpl(private val userDao: UserDao): UserRepository {
    override suspend fun insertUsers(users: List<User>) {
        val userEntities = users.map { user -> user.toUserEntity() }
        userDao.insertUser(userEntities)
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user.toUserEntity())
    }

    override suspend fun deleteUser(user: User) {
        userDao.deleteUser(user.toUserEntity())
    }

    override suspend fun getUserByStatus(status: Boolean): List<User> {
        return userDao.getUsersByStatus(status).map { userEntity -> userEntity.toUser() }
    }

    override suspend fun getUserById(id: Long): User? {
        return userDao.getUserById(id)?.toUser()
    }

    override suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers().map { it.toUser() }
    }

    override suspend fun searchUsers(query: String): List<User> {
        return userDao.searchUsers(query).map { it.toUser() }
    }
}