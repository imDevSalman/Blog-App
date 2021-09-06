package com.sonicmaster.herokuapp.ui.base

import androidx.lifecycle.ViewModel
import com.sonicmaster.herokuapp.data.network.UserApi
import com.sonicmaster.herokuapp.data.repository.BaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class ApiStatus { LOADING, ERROR, DONE }
abstract class BaseViewModel(private val repository: BaseRepository) : ViewModel() {
    suspend fun logout(api: UserApi) = withContext(Dispatchers.IO) { repository.logout(api) }
}