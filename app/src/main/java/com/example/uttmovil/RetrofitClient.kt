package com.example.uttmovil

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2/"

    // Configuración de Retrofit con Scalars para texto plano y Gson para JSON
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create()) // Para manejar texto plano
            .addConverterFactory(GsonConverterFactory.create())    // Para manejar JSON si necesitas
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}