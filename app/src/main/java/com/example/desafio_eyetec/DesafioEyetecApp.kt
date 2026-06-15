package com.example.desafio_eyetec

import android.app.Application
import com.example.desafio_eyetec.data.local.AppDatabase

class DesafioEyetecApp: Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}