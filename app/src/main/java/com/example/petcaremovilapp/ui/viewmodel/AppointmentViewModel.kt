package com.example.petcaremovilapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaremovilapp.data.repository.AppointmentRepository
import com.example.petcaremovilapp.models.dto.AppointmentDto
import com.example.petcaremovilapp.models.dto.ClinicDto
import com.example.petcaremovilapp.models.dto.CreateMyAppointmentRequest
import com.example.petcaremovilapp.models.dto.PetDto
import com.example.petcaremovilapp.models.dto.ServiceOption
import com.example.petcaremovilapp.models.dto.VeterinarianDto
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

data class AppointmentFormState(
    val clinic: ClinicDto? = null,
    val veterinarians: List<VeterinarianDto> = emptyList(),
    val pets: List<PetDto> = emptyList(),
    val availableDates: Set<String> = emptySet(),
    val slots: List<String> = emptyList(),
    val loading: Boolean = false,
    val submitting: Boolean = false,
    val error: String? = null,
    val created: AppointmentDto? = null
)

class AppointmentViewModel : ViewModel() {
    private val repository = AppointmentRepository()
    private val _state = MutableLiveData(AppointmentFormState())
    val state: LiveData<AppointmentFormState> = _state

    fun loadForm() {
        if (_state.value?.clinic != null || _state.value?.loading == true) return
        viewModelScope.launch {
            update { it.copy(loading = true, error = null) }
            runCatching {
                val clinic = repository.getBookingClinic()
                Triple(clinic, repository.getVeterinarians(clinic.id_clinic), repository.getMyPets())
            }.onSuccess { (clinic, veterinarians, pets) ->
                update {
                    it.copy(
                        clinic = clinic,
                        veterinarians = veterinarians,
                        pets = pets,
                        loading = false,
                        error = when {
                            pets.isEmpty() -> "Necesitas registrar una mascota antes de agendar."
                            veterinarians.isEmpty() -> "No hay veterinarios disponibles en esta clínica."
                            else -> null
                        }
                    )
                }
            }.onFailure { failure ->
                update { it.copy(loading = false, error = failure.userMessage()) }
            }
        }
    }

    fun loadDates(veterinarianId: String) {
        update { it.copy(availableDates = emptySet(), slots = emptyList(), error = null) }
        viewModelScope.launch {
            update { it.copy(loading = true) }
            runCatching { repository.getAvailableDates(veterinarianId).toSet() }
                .onSuccess { dates ->
                    update {
                        it.copy(
                            availableDates = dates,
                            loading = false,
                            error = if (dates.isEmpty()) "El veterinario no tiene fechas disponibles." else null
                        )
                    }
                }
                .onFailure { failure ->
                    update { it.copy(loading = false, error = failure.userMessage()) }
                }
        }
    }

    fun loadSlots(veterinarianId: String, date: String) {
        viewModelScope.launch {
            update { it.copy(loading = true, slots = emptyList(), error = null) }
            runCatching { repository.getAvailableSlots(veterinarianId, date) }
                .onSuccess { slots ->
                    update {
                        it.copy(
                            slots = slots,
                            loading = false,
                            error = if (slots.isEmpty()) "No quedan horarios para esta fecha." else null
                        )
                    }
                }
                .onFailure { failure ->
                    update { it.copy(loading = false, error = failure.userMessage()) }
                }
        }
    }

    fun create(
        veterinarianId: String,
        petId: String,
        date: String,
        time: String,
        service: String
    ) {
        val clinic = _state.value?.clinic ?: return
        viewModelScope.launch {
            update { it.copy(submitting = true, error = null) }
            val request = CreateMyAppointmentRequest(
                id_pet = petId,
                id_clinic = clinic.id_clinic,
                id_veterinarian = veterinarianId,
                appointment_date = toOffsetDateTime(date, time),
                service = service
            )
            runCatching { repository.create(request) }
                .onSuccess { created -> update { it.copy(submitting = false, created = created) } }
                .onFailure { failure ->
                    update { it.copy(submitting = false, error = failure.userMessage()) }
                    loadSlots(veterinarianId, date)
                }
        }
    }

    fun consumeCreated() = update { it.copy(created = null) }

    private fun toOffsetDateTime(date: String, time: String): String {
        val localDate = LocalDate.parse(date)
        val localTime = LocalTime.parse(time)
        return localDate.atTime(localTime)
            .atZone(ZoneId.of("America/Chihuahua"))
            .toOffsetDateTime()
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    private fun update(transform: (AppointmentFormState) -> AppointmentFormState) {
        _state.value = transform(_state.value ?: AppointmentFormState())
    }

    companion object {
        val SERVICES = listOf(
            ServiceOption("Consulta general", 350),
            ServiceOption("Vacunación", 450),
            ServiceOption("Desparasitación", 300),
            ServiceOption("Corte de uñas", 180),
            ServiceOption("Limpieza de oídos", 220),
            ServiceOption("Baño básico", 400),
            ServiceOption("Curación menor", 500)
        )
    }
}

class AppointmentListViewModel : ViewModel() {
    private val repository = AppointmentRepository()
    private val _appointments = MutableLiveData<List<AppointmentDto>>(emptyList())
    val appointments: LiveData<List<AppointmentDto>> = _appointments
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun refresh() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            runCatching { repository.getMine() }
                .onSuccess { _appointments.value = it.sortedByDescending { item -> item.date } }
                .onFailure { _error.value = it.userMessage() }
            _loading.value = false
        }
    }
}

private fun Throwable.userMessage(): String =
    message?.takeIf { it.isNotBlank() } ?: "No se pudo conectar con PetCare. Intenta de nuevo."
