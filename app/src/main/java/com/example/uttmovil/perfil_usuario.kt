package com.example.uttmovil
import android.animation.Animator
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
import com.airbnb.lottie.LottieAnimationView
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
        //atributos de firebase
        //auth es para la autotentifiacion
        val auth = FirebaseAuth.getInstance()
        //currentUser guarda el usuario actualmente iniciado
        val currentUser = auth.currentUser
        //userEmail guarda el correo electronico del usuario actual
        val userEmail: String? = currentUser?.email


        /*manda a llamar a una funcion que trae los datos del usuario de la base de datos de sql
        dependiendo del correo proporcionado*/

        obtenerDatosUsuario(userEmail.toString())


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


        //boton de editar perfil del usuario
        val edit = findViewById<Button>(R.id.bt_editar)
        edit.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //redirijir al la pantalla de editar perfil del usuario
            val intent = Intent(this, editarperfil::class.java)
            startActivity(intent)

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