package com.example.uttmovil
import android.widget.Toast


import android.annotation.SuppressLint
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


class perfil_usuario : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_usuario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.boton_inicio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //mando a llamar a la funcion para obtener los datos desde mysql
        obtenerDatosUsuario("22170039@uttcampus.edu.mx")


        //encuentra el id del boton de cerrar session
        val bt_cerrar  = findViewById<ImageButton>(R.id.logOutBt)

        bt_cerrar.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //redirijir al la pantalla de inicio

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()


        }





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
                    } else {
                        AlertDialog.Builder(this@perfil_usuario).apply {
                            setTitle("Retrofit Error")
                            setMessage("API Error: Formato de respuesta inesperado.")
                            setPositiveButton("OK", null)
                        }.show()
                    }
                } else {
                    AlertDialog.Builder(this@perfil_usuario).apply {
                        setTitle("Retrofit Error")
                        setMessage("API Error: Error en la respuesta: ${response.message()}")
                        setPositiveButton("OK", null)
                    }.show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                AlertDialog.Builder(this@perfil_usuario).apply {
                    setTitle("Retrofit Error")
                    setMessage("API Error: Excepción: ${t.message}")
                    setPositiveButton("OK", null)
                }.show()
            }
        })
    }

}