package com.example.petcaremovilapp.data.repository

import com.example.petcaremovilapp.data.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.petcaremovilapp.models.dto.LoginResponseDto
import com.example.petcaremovilapp.models.dto.Result
import com.example.petcaremovilapp.models.entities.User
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import android.util.Log
import com.example.petcaremovilapp.constants.Constants.BASE_URL

class LoginRepository {

    private val apiService: ApiService

    init {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("HTTP_LOG", message)
        }

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()

            // Header necesario para Dev Tunnels
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader(
                        "X-Tunnel-Skip-AntiPhishing-Page",
                        "true"
                    )
                    .build()

                chain.proceed(request)
            }

            // Logs HTTP
            .addInterceptor(loggingInterceptor)

            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }
    // Login (antes loginUser(userName, password); renombrado a login(email, password)
    // para reflejar el campo real del formulario)
    suspend fun login(email: String, password: String): Result<LoginResponseDto> =
        try {
            val user = User(email = email, password = password)
            val response = apiService.login(user)
            Result.Success(response)
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
