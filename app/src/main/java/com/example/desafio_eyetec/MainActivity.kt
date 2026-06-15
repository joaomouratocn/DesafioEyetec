package com.example.desafio_eyetec

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.desafio_eyetec.data.local.UserDao
import com.example.desafio_eyetec.data.local.entities.UserEntity
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class MainActivity : AppCompatActivity() {
    private lateinit var userDao: UserDao
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
        userDao = app.database.UserDao()

        testDb()
    }

    private fun testDb(){
        lifecycleScope.launch {
            val newUserEnable = UserEntity(name = "Arthur", age = 7, email = "arthur@gmail.com", enable = true )
            val newUserDisable = UserEntity(name = "João Neto", age = 36, email = "joaomouratocn@gmail.com", enable = false )

            userDao.insertUser(listOf(newUserEnable, newUserDisable))

            Log.d("USERS", userDao.getUsersByStatus(true).toString())
            Log.d("USERS", userDao.getUsersByStatus(false).toString())

            val disableUsers = userDao.getUsersByStatus(false)
            val editedUser = disableUsers.first().copy(enable = true)
            userDao.updateUser(editedUser)

            Log.d("USERS", userDao.getUsersByStatus(true).toString())
            Log.d("USERS", userDao.getUsersByStatus(false).toString())

            userDao.deleteUser(editedUser)

            Log.d("USERS", userDao.getUsersByStatus(true).toString())



        }
    }
}