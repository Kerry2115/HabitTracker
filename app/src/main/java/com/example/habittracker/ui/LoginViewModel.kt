package com.example.habittracker.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.api.RetrofitClient
import com.example.habittracker.data.AuthResponse
import com.example.habittracker.data.UserCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class LoginViewModel : ViewModel() {

    private val _authResult = MutableLiveData<AuthResponse>()
    val authResult: LiveData<AuthResponse> = _authResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(credentials: UserCredentials) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = withTimeout(10_000) {
                    withContext(Dispatchers.IO) {
                        RetrofitClient.service.registerUser(credentials)
                    }
                }
                _authResult.postValue(response)
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Register failed", e)
                _authResult.postValue(AuthResponse(success = false, message = "BĹ‚Ä…d sieci: ${e.message}"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun login(credentials: UserCredentials) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = withTimeout(10_000) {
                    withContext(Dispatchers.IO) {
                        RetrofitClient.service.loginUser(credentials)
                    }
                }
                _authResult.postValue(response)
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login failed", e)
                _authResult.postValue(AuthResponse(success = false, message = "BĹ‚Ä…d sieci: ${e.message}"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
