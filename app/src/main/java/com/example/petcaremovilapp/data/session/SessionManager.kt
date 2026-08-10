package com.example.petcaremovilapp.data.session

import android.content.Context

class SessionManager(context: Context) {
    private val preferences = context.getSharedPreferences("petcare_session", Context.MODE_PRIVATE)

    val token: String?
        get() = preferences.getString(KEY_TOKEN, null)

    val roleId: Int
        get() = preferences.getInt(KEY_ROLE, 0)

    fun saveToken(token: String) {
        preferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun saveProfile(id: String, name: String, roleId: Int) {
        preferences.edit()
            .putString(KEY_USER_ID, id)
            .putString(KEY_USER_NAME, name)
            .putInt(KEY_ROLE, roleId)
            .apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val KEY_TOKEN = "token"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_NAME = "user_name"
        const val KEY_ROLE = "role_id"
    }
}
