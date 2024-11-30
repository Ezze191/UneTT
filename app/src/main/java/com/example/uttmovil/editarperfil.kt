package com.example.uttmovil

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.uttmovil.RetrofitClient.apiService
import com.google.android.gms.common.api.Response
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback

class editarperfil : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editarperfil)

        //obtener los datos del usuario desde la base de datos para llenar los campos a editar

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userEmail: String? = currentUser?.email

        //mando a llamar a la funcion para obtener los datos desde mysql
        obtenerDatosUsuario(userEmail.toString())






    }
    //metodo para obtener datos del usuario desde mysql
    private fun obtenerDatosUsuario(email: String) {
        val call = RetrofitClient.apiService.searchUser(email)
        call.enqueue(object : retrofit2.Callback<String> {
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.split("\n") ?: listOf()
                    if (responseBody.size >= 3) {
                        var userName = responseBody[0]
                        val biografia = responseBody[1]
                        val fecha = responseBody[2]

                        // Aquí guardas el nombre de usuario limpio en el TextView
                        val textuser = findViewById<TextView>(R.id.textname)
                        textuser.text = userName

                        //aqui remplaza la fecha
                        val textfecha = findViewById<TextView>(R.id.fechaCreacion_txt)
                        textfecha.text = fecha

                        //aqui remplaza la biografia
                        val textbiografia = findViewById<TextView>(R.id.biografiatext)
                        textbiografia.text = biografia

                        //aqui remplaza el correo
                        val textcorreo = findViewById<TextView>(R.id.correotext)
                        textcorreo.text = email

                        //aqui remplaza la matricula
                        val matricula = email.filter { it.isDigit() }.toIntOrNull() ?: 0
                        val textmatricula = findViewById<TextView>(R.id.matriculatext)
                        textmatricula.text = matricula.toString()

                        //boton de editar perfil
                        val bt_aplicar = findViewById<LottieAnimationView>(R.id.bt_aplicar)
                        bt_aplicar.setOnClickListener {
                            val username = findViewById<TextView>(R.id.textname).text.toString()
                            val biografia = findViewById<TextView>(R.id.biografiatext).text.toString()
                            val password = findViewById<TextView>(R.id.passwordtext).text.toString()

                            // Llama al servicio Retrofit
                            RetrofitClient.apiService.updateProfile(
                                email,
                                username,
                                if (password.isBlank()) "" else password, // Envía "" si el password está vacío
                                biografia
                            ).enqueue(object : retrofit2.Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                                    if (response.isSuccessful) {
                                        //si pone nueva password actualizarla en firebase
                                        if (password.isNotBlank()) {
                                            actualizarPassword(
                                                password,
                                                onSuccess = {
                                                    // Acción en caso de éxito
                                                    val intent = Intent(this@editarperfil, perfil_usuario::class.java)
                                                    startActivity(intent)
                                                },
                                                onError = { error ->
                                                    // Acción en caso de error
                                                    AlertDialog.Builder(this@editarperfil).apply {
                                                        setTitle("Error")
                                                        setMessage("Error al actualizar el perfil: ${error}")
                                                        setPositiveButton("OK", null)
                                                    }.show()

                                                }
                                            )
                                        } else {
                                            // Si no hay cambio de contraseña, solo guarda los otros datos

                                        }


                                    } else {
                                        AlertDialog.Builder(this@editarperfil).apply {
                                            setTitle("Error")
                                            setMessage("Error al actualizar el perfil: ${response.message()}")
                                            setPositiveButton("OK", null)
                                        }.show()
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    AlertDialog.Builder(this@editarperfil).apply {
                                        setTitle("Error de conexión")
                                        setMessage("No se pudo conectar con el servidor: ${t.message}")
                                        setPositiveButton("OK", null)
                                    }.show()
                                }
                            })

                            if (!bt_aplicar.isAnimating) {
                                bt_aplicar.playAnimation()

                                bt_aplicar.addAnimatorListener(object : Animator.AnimatorListener {
                                    override fun onAnimationStart(p0: Animator) {
                                        // No se necesita acción aquí
                                    }

                                    override fun onAnimationEnd(p0: Animator) {
                                        // Navegar a la otra pantalla
                                        val intent = Intent(this@editarperfil, perfil_usuario::class.java)
                                        startActivity(intent)

                                        // Elimina el listener para evitar múltiples llamadas
                                        bt_aplicar.removeAnimatorListener(this)
                                    }

                                    override fun onAnimationCancel(p0: Animator) {
                                        // Sin acción necesaria al cancelar
                                    }

                                    override fun onAnimationRepeat(p0: Animator) {
                                        // Sin acción necesaria al repetir
                                    }
                                })
                            }



                        }


                    } else {
                        AlertDialog.Builder(this@editarperfil).apply {
                            setTitle("Retrofit Error")
                            setMessage("API Error: Formato de respuesta inesperado.")
                            setPositiveButton("OK", null)
                        }.show()
                    }
                } else {
                    AlertDialog.Builder(this@editarperfil).apply {
                        setTitle("Retrofit Error")
                        setMessage("API Error: Error en la respuesta: ${response.message()}")
                        setPositiveButton("OK", null)
                    }.show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                AlertDialog.Builder(this@editarperfil).apply {
                    setTitle("Retrofit Error")
                    setMessage("API Error: Excepción: ${t.message}")
                    setPositiveButton("OK", null)
                }.show()
            }


        })
    }

    fun actualizarPassword(
        nuevaPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val userEmail = user.email
            if (userEmail != null) {
                // Solicitar al usuario su contraseña actual para la reautenticación
                val currentPasswordField = findViewById<TextView>(R.id.currentPasswordText)
                val currentPassword = currentPasswordField.text.toString()

                if (currentPassword.isBlank()) {
                    onError("Por favor, ingresa tu contraseña actual.")
                    return
                }

                // Crear credenciales de reautenticación
                val credential = EmailAuthProvider.getCredential(userEmail, currentPassword)

                // Reautenticar al usuario
                user.reauthenticate(credential)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            // Después de reautenticarse, actualizar la contraseña
                            user.updatePassword(nuevaPassword)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        onSuccess()
                                    } else {
                                        val errorMessage = updateTask.exception?.message ?: "Error desconocido"
                                        onError(errorMessage)
                                    }
                                }
                        } else {
                            val errorMessage = reauthTask.exception?.message ?: "Error al reautenticar"
                            onError(errorMessage)
                        }
                    }
            } else {
                onError("No se encontró el correo electrónico del usuario.")
            }
        } else {
            onError("No se encontró un usuario autenticado.")
        }
    }
    }


