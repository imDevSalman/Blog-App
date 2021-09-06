package com.sonicmaster.herokuapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sonicmaster.herokuapp.data.network.Resource
import com.sonicmaster.herokuapp.data.repository.AuthRepository
import com.sonicmaster.herokuapp.data.responses.LoginResponse
import com.sonicmaster.herokuapp.data.responses.RegisterResponse
import com.sonicmaster.herokuapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : BaseViewModel(repository) {

    private val _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>> = _loginResponse

    private val _registerResponse: MutableLiveData<Resource<RegisterResponse>> = MutableLiveData()
    val registerResponse: LiveData<Resource<RegisterResponse>> = _registerResponse

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = repository.login(email, password)
        println("debug: ${_loginResponse.value}")
    }

    fun register(name: String, email: String, password: String) = viewModelScope.launch {
        _registerResponse.value = Resource.Loading
        _registerResponse.value = repository.register(name, email, password)
        println("debug: ${_registerResponse.value}")
    }

    suspend fun saveAuthToken(token: String) {
        repository.saveAuthToken(token)

    }
}