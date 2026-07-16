package com.example.petcaremovilapp.models.entities

import java.util.Date

data class Client(
    val clave: String? = null,
    val nombre: String? = null,
    val edad: Int = 0,
    val fechaNacimiento: Date? = null
)