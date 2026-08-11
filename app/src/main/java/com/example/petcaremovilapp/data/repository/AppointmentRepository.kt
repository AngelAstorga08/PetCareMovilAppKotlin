package com.example.petcaremovilapp.data.repository

import com.example.petcaremovilapp.data.api.ApiClient
import com.example.petcaremovilapp.models.dto.ApiResponse
import com.example.petcaremovilapp.models.dto.AppointmentDto
import com.example.petcaremovilapp.models.dto.ClinicDto
import com.example.petcaremovilapp.models.dto.CreateMyAppointmentRequest
import com.example.petcaremovilapp.models.dto.PetDto
import com.example.petcaremovilapp.models.dto.VeterinarianDto
import com.example.petcaremovilapp.models.dto.ChangeStatusRequest
import com.google.gson.JsonParser
import retrofit2.HttpException

class AppointmentRepository {
    private val api = ApiClient.service

    suspend fun getBookingClinic(): ClinicDto =
        request { api.getClinics() }.firstOrNull {
            it.name.equals(BOOKING_CLINIC, ignoreCase = true)
        } ?: throw Exception("No se encontró $BOOKING_CLINIC")

    suspend fun getVeterinarians(clinicId: String): List<VeterinarianDto> =
        request { api.getVeterinarians(clinicId) }

    suspend fun getMyPets(): List<PetDto> = request { api.getMyPets() }

    suspend fun getAvailableDates(veterinarianId: String): List<String> =
        request { api.getAvailableDates(veterinarianId) }

    suspend fun getAvailableSlots(veterinarianId: String, date: String): List<String> =
        request { api.getAvailableSlots(veterinarianId, date) }

    suspend fun create(request: CreateMyAppointmentRequest): AppointmentDto =
        request { api.createMyAppointment(request) }

    suspend fun getMine(): List<AppointmentDto> = request { api.getMyAppointments() }

    suspend fun getForRole(roleId: Int): List<AppointmentDto> = when (roleId) {
        1 -> request { api.getClinicAppointments() }
        2 -> request { api.getVeterinarianAppointments() }
        else -> getMine()
    }

    suspend fun cancel(id: String): AppointmentDto = request { api.cancelMyAppointment(id) }

    suspend fun changeStatus(id: String, status: String): AppointmentDto =
        request { api.changeAppointmentStatus(id, ChangeStatusRequest(status)) }

    private suspend fun <T> request(call: suspend () -> ApiResponse<T>): T = try {
        unwrap(call())
    } catch (error: HttpException) {
        val serverMessage = runCatching {
            JsonParser.parseString(error.response()?.errorBody()?.string())
                .asJsonObject.get("message")?.asString
        }.getOrNull()
        throw Exception(serverMessage ?: "El servidor rechazó la solicitud (${error.code()}).")
    }

    private fun <T> unwrap(response: ApiResponse<T>): T {
        if (!response.success || response.data == null) {
            throw Exception(response.message.ifBlank { "No se pudo completar la solicitud" })
        }
        return response.data
    }

    companion object {
        const val BOOKING_CLINIC = "Clinica Veterinaria La Chiquita"
    }
}
