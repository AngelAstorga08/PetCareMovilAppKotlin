package com.example.petcaremovilapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaremovilapp.data.api.ApiClient
import com.example.petcaremovilapp.data.repository.ProfileRepository
import com.example.petcaremovilapp.models.dto.UpdateProfileRequest
import com.example.petcaremovilapp.models.dto.UserProfileDto
import kotlinx.coroutines.launch

data class ProfileState(val profile: UserProfileDto? = null, val loading: Boolean = false, val error: String? = null, val saved: Boolean = false)

class ProfileViewModel : ViewModel() {
    private val repository = ProfileRepository()
    private val _state = MutableLiveData(ProfileState())
    val state: LiveData<ProfileState> = _state

    fun load() = launch { repository.get() }
    fun save(name: String, email: String, phone: String?) = launch(true) {
        repository.update(UpdateProfileRequest(name, email, phone = phone))
    }

    private fun launch(saved: Boolean = false, call: suspend () -> UserProfileDto) {
        viewModelScope.launch {
            _state.value = (_state.value ?: ProfileState()).copy(loading = true, error = null, saved = false)
            runCatching { call() }
                .onSuccess { profile ->
                    if (saved) ApiClient.session.updateUserName(profile.name)
                    _state.value = ProfileState(profile = profile, saved = saved)
                }
                .onFailure { _state.value = (_state.value ?: ProfileState()).copy(loading = false, error = it.message ?: "Error de conexion") }
        }
    }
}
