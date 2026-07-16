package com.example.petcaremovilapp.data.repository

import android.util.Log
import com.example.petcaremovilapp.constants.Constants.BASE_URL
import com.example.petcaremovilapp.data.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.petcaremovilapp.models.dto.ClientResponseDto
import com.example.petcaremovilapp.models.dto.Result
import com.example.petcaremovilapp.models.entities.Client
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class ClientRepository {

    private val apiService: ApiService

    init {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("HTTP_LOG", message)
        }
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }
    // Obtener todos los clientes
    suspend fun getAllClientes(): Result<List<Client>> =
        try {
            val clientes = apiService.getAllClients()
            Result.Success(clientes)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al obtener clientes")
        }

    // Obtener cliente por ID
    suspend fun getClientByClave(clave: String): Result<Client> =
        try {
            val cliente = apiService.getClientByClave(clave)
            Result.Success(cliente)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al obtener cliente")
        }

    suspend fun deleteClient(clave: String): Result<Client> =
        try {
            val cliente = apiService.deleteClient(clave)
            Result.Success(cliente)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al obtener cliente")
        }

    // Crear cliente
    suspend fun createClient(cliente: Client): Result<ClientResponseDto> =
        try {
            val response = apiService.createClient(cliente)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al crear cliente")
        }

    // update cliente
    suspend fun updateClient(cliente: Client): Result<ClientResponseDto> =
        try {
            val response = apiService.updateClient(cliente)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al actualizar cliente")
        }
}