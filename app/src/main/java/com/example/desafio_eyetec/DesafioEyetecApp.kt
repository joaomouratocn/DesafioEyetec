package com.example.desafio_eyetec

import android.app.Application
import com.example.desafio_eyetec.data.local.AppDatabase
import com.example.desafio_eyetec.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class DesafioEyetecApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DesafioEyetecApp)
            modules(appModules)
        }
    }

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}