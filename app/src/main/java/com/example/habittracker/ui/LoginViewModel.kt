package com.example.habittracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.api.RetrofitClient
import com.example.habittracker.data.AuthResponse
import com.example.habittracker.data.UserCredentials
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _authResult = MutableLiveData<AuthResponse>()
    val authResult: LiveData<AuthResponse> = _authResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(credentials: UserCredentials) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.service.registerUser(credentials)
                _authResult.postValue(response)
            } catch (e: Exception) {
                _authResult.postValue(AuthResponse(success = false, message = "Błąd sieci: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(credentials: UserCredentials) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.service.loginUser(credentials)
                _authResult.postValue(response)
            } catch (e: Exception) {
                _authResult.postValue(AuthResponse(success = false, message = "Błąd sieci: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }
}