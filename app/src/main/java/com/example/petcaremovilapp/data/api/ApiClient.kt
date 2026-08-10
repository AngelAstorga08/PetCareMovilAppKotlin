package com.example.petcaremovilapp.data.api

import android.content.Context
import com.example.petcaremovilapp.constants.Constants.BASE_URL
import com.example.petcaremovilapp.data.session.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    lateinit var session: SessionManager
        private set

    lateinit var service: ApiService
        private set

    fun initialize(context: Context) {
        if (::service.isInitialized) return
        session = SessionManager(context.applicationContext)

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                    .addHeader("X-Tunnel-Skip-AntiPhishing-Page", "true")
                session.token?.takeIf { it.isNotBlank() }?.let {
                    builder.addHeader("Authorization", "Bearer $it")
                }
                chain.proceed(builder.build())
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()

        service = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
