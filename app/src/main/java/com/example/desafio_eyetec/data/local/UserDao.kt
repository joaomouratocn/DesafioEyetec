package com.example.desafio_eyetec.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.desafio_eyetec.data.local.entities.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(users: List<UserEntity>)
    @Update
    suspend fun updateUser(user: UserEntity)
    @Delete
    suspend fun deleteUser(user: UserEntity)
    @Query("SELECT * FROM users WHERE enable = :status")
    suspend fun getUsersByStatus(status: Boolean): List<UserEntity>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity?
}