package com.example.petcaremovilapp.models.dto

data class ClinicDto(
    val id_clinic: String,
    val name: String,
    val location: String,
    val schedule: String
)

data class VeterinarianDto(
    val id_user: String,
    val name: String,
    val email: String,
    val id_role: Int,
    val phone: String?,
    val schedule: String?,
    val id_clinic: String?
)

data class PetDto(
    val id_pet: String,
    val name: String,
    val breed: String,
    val species: String?,
    val weight: Double,
    val age: Int
)

data class AppointmentDto(
    val id_appointment: String,
    val user_name: String,
    val pet_name: String,
    val pet_breed: String,
    val pet_weight: Double,
    val pet_age: Int,
    val veterinarian_name: String,
    val veterinarian_email: String,
    val veterinarian_phone: String?,
    val clinic_name: String?,
    val clinic_location: String?,
    val date: String,
    val service: String,
    val cost: Double,
    val status: String
)

data class UserProfileDto(
    val id_user: String,
    val name: String,
    val email: String,
    val id_role: Int
)

data class CreateMyAppointmentRequest(
    val id_pet: String,
    val id_clinic: String,
    val id_veterinarian: String,
    val appointment_date: String,
    val service: String
)

data class ServiceOption(val name: String, val price: Int) {
    override fun toString(): String = "$name — $$price MXN"
}
