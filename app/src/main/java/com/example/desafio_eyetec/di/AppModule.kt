package com.example.desafio_eyetec.di

import androidx.room.Room
import com.example.desafio_eyetec.data.local.AppDatabase
import com.example.desafio_eyetec.domain.repositories.UserRepository
import com.example.desafio_eyetec.domain.repositories.UserRepositoryLocalImpl
import com.example.desafio_eyetec.presenter.allUsers.AllUsersViewModel
import com.example.desafio_eyetec.presenter.insertUpdateUserFragment.InsertUpdateUserViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    viewModel() { (userId: Long) ->
        InsertUpdateUserViewModel(
            userRepository = UserRepositoryLocalImpl(get()),
            userId = userId
        )
    }

    viewModel {
        AllUsersViewModel(get())
    }

    single<UserRepository> {
        UserRepositoryLocalImpl(get())
    }

    single {
        get<AppDatabase>().UserDao()
    }

    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "desafio.db"
        ).build()
    }
}