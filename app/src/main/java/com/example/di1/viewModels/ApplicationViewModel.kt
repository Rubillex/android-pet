package com.example.di1.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Timer
import java.util.TimerTask

class ApplicationViewModel(application: Application) : AndroidViewModel(application = application) {

    private val context = getApplication<Application>().applicationContext

    private val _uiState = MutableStateFlow<ApplicationState>(ApplicationState.CheckAuth)
    val uiState: StateFlow<ApplicationState> = _uiState

    private val _isWaitingResponse = MutableStateFlow<Boolean>(false)
    val isWaitingResponse: StateFlow<Boolean> = _isWaitingResponse

    fun setIsWaitingResponse(value: Boolean) {
        _isWaitingResponse.value = value
    }

    fun checkAuth() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                setIsWaitingResponse(false)
                _uiState.value = ApplicationState.LoginForm
            }
        }, 2000)
    }

    fun login() {
        setIsWaitingResponse(true)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                setIsWaitingResponse(false)
//                _uiState.value = ApplicationState.Profile
            }
        }, 2000)
    }

    sealed class ApplicationState {
        object CheckAuth : ApplicationState()
        object LoginForm : ApplicationState()
        object Profile : ApplicationState()
    }
}