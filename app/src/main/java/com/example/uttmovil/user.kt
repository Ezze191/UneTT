package com.example.uttmovil
import android.hardware.camera2.CameraExtensionSession.StillCaptureLatency
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("api.php") // Asegúrate de que la URL corresponda al lugar donde subes tu archivo PHP
    fun insertUser(
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<Void>

    //consultar el usuario a la base de datos
    @FormUrlEncoded
    @POST("viewperfileuser.php")
    fun searchUser(
        @Field("email") email: String
    ): Call<String>

}