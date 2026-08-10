package com.example.petcaremovilapp.data.api

import com.example.petcaremovilapp.models.dto.ClientResponseDto
import com.example.petcaremovilapp.models.dto.LoginResponseDto
import com.example.petcaremovilapp.models.dto.ApiResponse
import com.example.petcaremovilapp.models.dto.AppointmentDto
import com.example.petcaremovilapp.models.dto.ClinicDto
import com.example.petcaremovilapp.models.dto.CreateMyAppointmentRequest
import com.example.petcaremovilapp.models.dto.PetDto
import com.example.petcaremovilapp.models.dto.UserProfileDto
import com.example.petcaremovilapp.models.dto.VeterinarianDto
import com.example.petcaremovilapp.models.entities.Client
import com.example.petcaremovilapp.models.entities.User
import retrofit2.http.*

interface ApiService {

    // NOTA: se renombró el método a `login` (antes `loginUser`) a pedido
    // explícito. La ruta "api/Auth/LogIn" se dejó tal cual: es un backend
    // real (ver Constants.BASE_URL) que no puedo verificar sin acceso a
    // red en este entorno, así que no arriesgué a adivinar un nombre de
    // ruta distinto.
    @POST("/api/v1/auth/login")
    suspend fun login(@Body user: User): ApiResponse<LoginResponseDto>

    @GET("/api/v1/Users/perfil")
    suspend fun getProfile(): ApiResponse<UserProfileDto>

    @GET("/api/v1/Clinics")
    suspend fun getClinics(): ApiResponse<List<ClinicDto>>

    @GET("/api/v1/Users/veterinarians")
    suspend fun getVeterinarians(@Query("clinicId") clinicId: String): ApiResponse<List<VeterinarianDto>>

    @GET("/api/v1/Pets/My-Pets")
    suspend fun getMyPets(): ApiResponse<List<PetDto>>

    @GET("/api/v1/Appointments/available-dates")
    suspend fun getAvailableDates(@Query("veterinarianId") veterinarianId: String): ApiResponse<List<String>>

    @GET("/api/v1/Appointments/available-slots")
    suspend fun getAvailableSlots(
        @Query("veterinarianId") veterinarianId: String,
        @Query("date") date: String
    ): ApiResponse<List<String>>

    @POST("/api/v1/Appointments/mias")
    suspend fun createMyAppointment(@Body request: CreateMyAppointmentRequest): ApiResponse<AppointmentDto>

    @GET("/api/v1/Appointments/mias")
    suspend fun getMyAppointments(): ApiResponse<List<AppointmentDto>>

    @POST("api/Auth/Create/User")
    suspend fun createUser(@Body user: User): User
    @GET("api/Client/Get/Clientes")
    suspend fun getAllClients(): List<Client>

    @GET("api/Client/Get/Cliente/{clave}")
    suspend fun getClientByClave(@Path("clave") clave: String): Client

    @POST("api/Client/Create/Cliente")
    suspend fun createClient(@Body cliente: Client): ClientResponseDto

    @PUT("api/Client/Update/Cliente")
    suspend fun updateClient(@Body cliente: Client): ClientResponseDto

    @DELETE("api/Client/{clave}")
    suspend fun deleteClient(@Path("clave") clave: String): Client
}
