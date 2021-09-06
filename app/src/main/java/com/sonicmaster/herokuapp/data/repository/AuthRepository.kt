package com.sonicmaster.herokuapp.data.repository

import android.content.Context
import com.sonicmaster.herokuapp.data.UserPreferences
import com.sonicmaster.herokuapp.data.network.AuthApi

class AuthRepository(private val api: AuthApi, private val context: Context) :
    BaseRepository() {
    suspend fun login(
        email: String,
        password: String
    ) = safeApiCall {
        api.login(email, password)
    }

    suspend fun register(name: String, email: String, password: String) = safeApiCall {
        api.register(name, email, password)
    }

    suspend fun saveAuthToken(token: String) {
        UserPreferences.saveToken(context, token)
    }
}