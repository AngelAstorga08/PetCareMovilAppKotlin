package com.example.petcaremovilapp.data.repository

import com.example.petcaremovilapp.data.api.ApiService
import com.example.petcaremovilapp.models.dto.LoginResponseDto
import com.example.petcaremovilapp.models.dto.Result
import com.example.petcaremovilapp.models.entities.User
import com.example.petcaremovilapp.data.api.ApiClient

class LoginRepository {

    private val apiService: ApiService = ApiClient.service
    // Login (antes loginUser(userName, password); renombrado a login(email, password)
    // para reflejar el campo real del formulario)
    suspend fun login(email: String, password: String): Result<LoginResponseDto> =
        try {
            val user = User(email = email, password = password)
            val response = apiService.login(user)
            val login = response.data
            if (!response.success || login == null) {
                Result.Error(response.message)
            } else {
                ApiClient.session.saveToken(login.token)
                val profileResponse = apiService.getProfile()
                val profile = profileResponse.data
                if (!profileResponse.success || profile == null) {
                    ApiClient.session.clear()
                    Result.Error(profileResponse.message)
                } else {
                    ApiClient.session.saveProfile(profile.id_user, profile.name, profile.id_role)
                    Result.Success(login)
                }
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconocido en login")
        }

    // Register
    suspend fun registerUser(email: String, password: String): Result<User> =
        try {
            val user = User(email = email, password = password)
            val response = apiService.createUser(user)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconocido en el registro")
        }
}
