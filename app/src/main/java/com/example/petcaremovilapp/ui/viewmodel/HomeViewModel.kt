package com.example.petcaremovilapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaremovilapp.data.repository.AppointmentRepository
import com.example.petcaremovilapp.data.repository.PetRepository
import com.example.petcaremovilapp.models.dto.AppointmentDto
import java.time.Instant
import kotlinx.coroutines.launch

data class HomeState(val appointments: List<AppointmentDto> = emptyList(), val petCount: Int = 0, val loading: Boolean = false, val error: String? = null)

class HomeViewModel : ViewModel() {
    private val appointments = AppointmentRepository()
    private val pets = PetRepository()
    private val _state = MutableLiveData(HomeState())
    val state: LiveData<HomeState> = _state

    fun refresh(roleId: Int) {
        if (roleId == 1) { _state.value = HomeState(); return }
        viewModelScope.launch {
            _state.value = HomeState(loading = true)
            runCatching {
                val values = appointments.getForRole(roleId)
                val petCount = if (roleId == 3) pets.getMine().size else 0
                HomeState(selectHomeAppointments(values, roleId, Instant.now()), petCount)
            }.onSuccess { _state.value = it }
             .onFailure { _state.value = HomeState(error = it.message ?: "Error de conexion") }
        }
    }
}

fun selectHomeAppointments(values: List<AppointmentDto>, roleId: Int, now: Instant): List<AppointmentDto> =
    if (roleId == 1) emptyList() else values.asSequence()
        .filter { item ->
            val future = runCatching { Instant.parse(item.date).isAfter(now) }.getOrDefault(false)
            val active = item.status.lowercase() !in setOf("atendida", "cancelada")
            future && active && (roleId != 2 || item.status.equals("pendiente", true))
        }
        .sortedBy { runCatching { Instant.parse(it.date) }.getOrDefault(Instant.MAX) }
        .take(2)
        .toList()
