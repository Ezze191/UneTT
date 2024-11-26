package com.example.uttmovil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call

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



}
