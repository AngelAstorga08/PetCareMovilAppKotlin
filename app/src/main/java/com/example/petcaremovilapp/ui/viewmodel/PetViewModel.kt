package com.example.petcaremovilapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaremovilapp.data.repository.PetRepository
import com.example.petcaremovilapp.models.dto.PetDto
import com.example.petcaremovilapp.models.dto.PetRequest
import kotlinx.coroutines.launch

data class PetState(
    val pets: List<PetDto> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

class PetViewModel : ViewModel() {
    private val repository = PetRepository()
    private val _state = MutableLiveData(PetState())
    val state: LiveData<PetState> = _state

    fun refresh() = runOperation { repository.getMine() }
    fun save(id: String?, request: PetRequest) = mutate(if (id == null) "Mascota registrada" else "Mascota actualizada") {
        if (id == null) repository.create(request) else repository.update(id, request)
    }
    fun delete(id: String) = mutate("Mascota eliminada") { repository.delete(id) }
    fun consumeMessage() { update { it.copy(message = null) } }

    private fun runOperation(call: suspend () -> List<PetDto>) {
        viewModelScope.launch {
            update { it.copy(loading = true, error = null) }
            runCatching { call() }
                .onSuccess { pets -> update { it.copy(pets = pets, loading = false) } }
                .onFailure { e -> update { it.copy(loading = false, error = e.message ?: "Error de conexion") } }
        }
    }

    private fun mutate(message: String, call: suspend () -> Any?) {
        viewModelScope.launch {
            update { it.copy(loading = true, error = null) }
            runCatching { call() }
                .onSuccess {
                    runCatching { repository.getMine() }
                        .onSuccess { pets -> update { it.copy(pets = pets, loading = false, message = message) } }
                        .onFailure { e -> update { it.copy(loading = false, error = e.message) } }
                }
                .onFailure { e -> update { it.copy(loading = false, error = e.message ?: "No se pudo guardar") } }
        }
    }

    private fun update(block: (PetState) -> PetState) { _state.value = block(_state.value ?: PetState()) }
}
