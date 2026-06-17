package com.example.desafio_eyetec.presenter.allUsers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desafio_eyetec.domain.models.User
import com.example.desafio_eyetec.domain.repositories.UserRepository
import kotlinx.coroutines.launch

class AllUsersViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    init {
        loadAllUsers()
    }

    fun loadAllUsers() {
        viewModelScope.launch {
            _users.value = userRepository.getAllUsers()
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                loadAllUsers()
            } else {
                _users.value = userRepository.searchUsers(query)
            }
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            userRepository.deleteUser(user)
            loadAllUsers()
        }
    }
}
