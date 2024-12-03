package com.example.uttmovil
/*Crea una data class que guarda los atributos que va enviar en la consulta de la base de datos
* con retrofit*/
data class ComentarioRequest(
    val comentario: String,
    val comentUser: String,
    val comentPost: String
)
