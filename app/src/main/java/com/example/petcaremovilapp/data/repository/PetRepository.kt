package com.example.petcaremovilapp.data.repository

import com.example.petcaremovilapp.data.api.ApiClient
import com.example.petcaremovilapp.models.dto.ApiResponse
import com.example.petcaremovilapp.models.dto.PetDto
import com.example.petcaremovilapp.models.dto.PetRequest

class PetRepository {
    private val api = ApiClient.service

    suspend fun getMine(): List<PetDto> = unwrap(api.getMyPets())
    suspend fun create(request: PetRequest): PetDto = unwrap(api.createPet(request))
    suspend fun update(id: String, request: PetRequest): PetDto = unwrap(api.updatePet(id, request))
    suspend fun delete(id: String) { unwrap(api.deletePet(id)) }

    private fun <T> unwrap(response: ApiResponse<T>): T {
        if (!response.success || response.data == null) throw Exception(response.message.ifBlank { "No se pudo completar la solicitud" })
        return response.data
    }
}
