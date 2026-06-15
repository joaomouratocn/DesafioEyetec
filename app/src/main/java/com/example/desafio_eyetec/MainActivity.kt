package com.example.desafio_eyetec

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.desafio_eyetec.domain.models.User
import com.example.desafio_eyetec.domain.repositories.UserRepository
import com.example.desafio_eyetec.domain.repositories.UserRepositoryLocalImpl
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val app = applicationContext as DesafioEyetecApp
        userRepository = UserRepositoryLocalImpl(app.database.UserDao())

        testDb()
    }

    private fun testDb() {
        lifecycleScope.launch {
            val newUserEnable =
                User(
                    name = "Arthur",
                    age = 7,
                    email = "arthur@gmail.com",
                    enable = true
                )
            val newUserDisable = User(
                name = "João Neto",
                age = 36,
                email = "joaomouratocn@gmail.com",
                enable = false
            )
            userRepository.insertUsers(listOf(newUserEnable, newUserDisable))
            Log.d("USERS", userRepository.getUserByStatus(true).toString())
            Log.d("USERS", userRepository.getUserByStatus(false).toString())
            Log.d("USERS", "Usuários cadasrados")

            val disableUsers = userRepository.getUserByStatus(false)
            val editedUser = disableUsers.first().copy(enable = true)
            userRepository.updateUser(editedUser)
            Log.d("USERS", userRepository.getUserByStatus(true).toString())
            Log.d("USERS", userRepository.getUserByStatus(false).toString())
            Log.d("USERS", "Usuarios alterados")

            userRepository.deleteUser(editedUser)
            Log.d("USERS", userRepository.getUserByStatus(true).toString())
            Log.d("USERS", "Usuarios alterados")
        }
    }
}