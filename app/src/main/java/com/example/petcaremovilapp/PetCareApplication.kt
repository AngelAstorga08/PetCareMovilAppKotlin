package com.example.petcaremovilapp

import android.app.Application
import com.example.petcaremovilapp.data.api.ApiClient

class PetCareApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.initialize(this)
    }
}
