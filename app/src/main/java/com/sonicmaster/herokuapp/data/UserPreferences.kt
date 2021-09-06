package com.sonicmaster.herokuapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "datastore_token")

object UserPreferences {

    suspend fun saveToken(context: Context, token: String) {
        context.dataStore.edit {
            val key = stringPreferencesKey("token")
            it[key] = token
            println("debug:$token is saving to ${it[key]}")
        }
    }

    fun getToken(context: Context): Flow<String> {
        val wrappedKey = stringPreferencesKey("token")
        val valueFlow: Flow<String> = context.dataStore.data.map {
            it[wrappedKey] ?: "No token found"
        }
        return valueFlow
    }

    suspend fun clear(context: Context) {
        context.dataStore.edit {
            it.clear()
        }
    }
}

