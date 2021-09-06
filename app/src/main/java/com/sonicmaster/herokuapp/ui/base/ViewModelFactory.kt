package com.sonicmaster.herokuapp.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sonicmaster.herokuapp.data.repository.AuthRepository
import com.sonicmaster.herokuapp.data.repository.BaseRepository
import com.sonicmaster.herokuapp.data.repository.PostRepository
import com.sonicmaster.herokuapp.ui.auth.AuthViewModel
import com.sonicmaster.herokuapp.ui.home.HomeViewModel

class ViewModelFactory(private val repository: BaseRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository = repository as AuthRepository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository as PostRepository) as T
            else -> throw IllegalArgumentException("ViewModelClass not found")
        }
    }
}