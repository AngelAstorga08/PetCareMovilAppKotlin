package com.example.petcaremovilapp.data.repository

import com.example.petcaremovilapp.data.api.ApiClient
import com.example.petcaremovilapp.models.dto.ApiResponse
import com.example.petcaremovilapp.models.dto.UpdateProfileRequest
import com.example.petcaremovilapp.models.dto.UserProfileDto

class ProfileRepository {
    private val api = ApiClient.service
    suspend fun get(): UserProfileDto = unwrap(api.getProfile())
    suspend fun update(request: UpdateProfileRequest): UserProfileDto = unwrap(api.updateProfile(request))
    private fun <T> unwrap(response: ApiResponse<T>): T {
        if (!response.success || response.data == null) throw Exception(response.message.ifBlank { "No se pudo completar la solicitud" })
        return response.data
    }
}
