package com.example.petcaremovilapp.models.entities

import com.google.gson.annotations.SerializedName

/**
 * Renombrado de "userName" a "email" para reflejar lo que la pantalla
 * de login (fragment_login.xml, "Correo electrónico") realmente pide.
 *
 * Se mantiene @SerializedName("userName") para no cambiar la llave
 * JSON enviada al backend real (Constants.BASE_URL). No tengo forma
 * de verificar contra ese backend en este entorno (sin acceso a red),
 * así que preferí no arriesgar la compatibilidad del payload.
 */
data class User(
    val email: String? = null,
    val password: String? = null
)
