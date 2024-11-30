package com.example.uttmovil

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call

class VerOtroPerfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_otro_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val email = intent.getStringExtra("email")  // Obtén el valor de 'username' que se pasó
        obtenerDatosUsuario(email ?: "")

        //botones de barra inferior de navegacion
        val bt_inicio = findViewById<ImageButton>(R.id.boton_inicio)
        bt_inicio.setOnClickListener {
            val intent = Intent(this, displayfeed::class.java)
            startActivity(intent)

        }

        val perfilUsuarioButton: ImageButton = findViewById(R.id.boton_user)
        perfilUsuarioButton.setOnClickListener {
            val intent = Intent(this, perfil_usuario::class.java)
            startActivity(intent)

        }

        val btadd: ImageButton = findViewById(R.id.boton_agregar)
        btadd.setOnClickListener {
            val intent = Intent(this, EspacioPubli::class.java)
            startActivity(intent)

        }
        val verperfil = findViewById<ImageButton>(R.id.boton_user)
        verperfil.setOnClickListener {
            val intent = Intent(this, perfil_usuario::class.java)
            startActivity(intent)

        }
        val logout = findViewById<ImageButton>(R.id.logOutBt)
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //redirijir al la pantalla de inicio
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    }



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
                        val textfecha = findViewById<TextView>(R.id.fechatext)
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
                        AlertDialog.Builder(this@VerOtroPerfil).apply {
                            setTitle("Retrofit Error")
                            setMessage("API Error: Formato de respuesta inesperado.")
                            setPositiveButton("OK", null)
                        }.show()
                    }
                } else {
                    AlertDialog.Builder(this@VerOtroPerfil).apply {
                        setTitle("Retrofit Error")
                        setMessage("API Error: Error en la respuesta: ${response.message()}")
                        setPositiveButton("OK", null)
                    }.show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                AlertDialog.Builder(this@VerOtroPerfil).apply {
                    setTitle("Retrofit Error")
                    setMessage("API Error: Excepción: ${t.message}")
                    setPositiveButton("OK", null)
                }.show()
            }
        })
    }
}