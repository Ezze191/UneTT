package com.example.uttmovil
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("insert_usuario.php") // Asegúrate de que la URL corresponda al lugar donde subes tu archivo PHP
    fun insertUser(
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<Void>

    //consultar el usuario a la base de datos y extraer sus datos
    @FormUrlEncoded
    @POST("viewperfileuser.php")
    fun searchUser(
        @Field("email") email: String
    ): Call<String>

    //login user
    @FormUrlEncoded
    @POST("log.php")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ):Call<String>

    // Agregar publicación a la base de datos
    @POST("guardarPublicacion.php") // Ruta de tu archivo PHP que maneja publicaciones
    fun createPost(
        @Body postRequest: PostRequest  // Asegúrate de que el modelo tenga las variables correctas
    ): Call<Map<String, Any>>

    //subir comentario a la base de datos
    @POST("savecoment.php")  // Asegúrate de que la URL corresponda al archivo PHP correcto
    fun insertarComentario(
        @Body comentario: ComentarioRequest // Asegúrate de que el modelo tenga las variables correctas
    ): Call<Map<String, Any>>

    //subir like a la base de datos
    @POST("savelike.php")  // Asegúrate de que la URL corresponda al archivo PHP correcto
    fun insertarLike(@Body likeRequest: RequestLike): Call<Map<String, Any>>

    //eliminar publicacion de la base de datos
    @POST("eliminar_publicacion.php")  // Cambia esto por la URL correcta en tu servidor
    fun deletePost(@Body request: DeletePostRequest): Call<Map<String, Any>>

    //actualizar datos del usuario
    @FormUrlEncoded
    @POST("updateProfile.php") // Cambia esto por la ruta correcta de tu archivo PHP
    fun updateProfile(
        @Field("correoelectronico") email: String,
        @Field("username") username: String,
        @Field("password") password: String?,
        @Field("bio") bio: String?
    ): Call<Void>

}