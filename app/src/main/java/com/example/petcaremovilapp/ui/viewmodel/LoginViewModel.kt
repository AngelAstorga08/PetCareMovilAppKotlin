package com.example.petcaremovilapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaremovilapp.data.repository.LoginRepository
import com.example.petcaremovilapp.models.dto.LoginResponseDto
import com.example.petcaremovilapp.models.entities.User
import kotlinx.coroutines.launch
import com.example.petcaremovilapp.models.dto.Result

class LoginViewModel : ViewModel() {

    private val repository = LoginRepository()

    private val _loginResult = MutableLiveData<Result<LoginResponseDto>>()
    val loginResult: LiveData<Result<LoginResponseDto>> = _loginResult
    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    // Renombrado de loginUser(userName, password) a login(email, password)
    fun login(email: String, password: String) {
        _loginResult.value = Result.Loading
        viewModelScope.launch {
            _loginResult.value = repository.login(email, password)
        }
    }
    fun register(name: String, email: String, password: String, phone: String?) {
        _registerResult.value = Result.Loading
        viewModelScope.launch {
            _registerResult.value = repository.register(name, email, password, phone)
        }
    }
}
