package com.example.desafio_eyetec.presenter.insertUpdateUserFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desafio_eyetec.domain.models.User
import com.example.desafio_eyetec.domain.repositories.UserRepository
import kotlinx.coroutines.launch

class InsertUpdateUserViewModel(private val userRepository: UserRepository, val userId: Long) :  ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    init {
        Log.d("InsertUpdateVM", "Init com userId: $userId")
        if (userId != -1L) {
            loadUser()
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            try {
                val userData = userRepository.getUserById(userId)
                Log.d("InsertUpdateVM", "Usuário carregado: $userData")
                _user.value = userData
            } catch (e: Exception) {
                Log.e("InsertUpdateVM", "Erro ao carregar usuário com ID: $userId", e)
                _user.value = null
            }
        }
    }

    fun isInvalidName(name: String): Boolean {
        return name.isEmpty() || name.isBlank() || (name.length < 3)
    }

    fun isInvalidAge(age: Int): Boolean {
        return age <= 0
    }

    fun isInvalidEmail(email: String): Boolean {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    suspend fun saveUser(name: String, age: Int, email: String, enable: Boolean) {
        if (userId == -1L) {
            val user = User(name = name, age = age, email = email, enable = enable)
            userRepository.insertUsers(listOf(user))
        } else {
            val user = User(id = userId, name = name, age = age, email = email, enable = enable)
            userRepository.updateUser(user)
        }
    }
}
