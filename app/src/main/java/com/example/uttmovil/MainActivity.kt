package com.example.uttmovil
import retrofit2.Call
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay

/* INDICE
* L - LOGIN
* R - REGISTER */



class MainActivity : AppCompatActivity() {
    //esta es una funcion de firebase que permite conectar con google para la autotentificacion del user
    private val auth = FirebaseAuth.getInstance()


    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)




        /*funcion de firebase que permite detectar si el usuario ya habia iniciado session antes
        * guarda la session y lo manda al displayfeed */
        if(auth.currentUser != null){
            val intent = Intent(this, displayfeed::class.java)
            startActivity(intent)
            finish()
        }




        //boton de registrarte en la app
        val btreg = findViewById<Button>(R.id.bt_registrar)
        //boton ingreasar a la app
        val ingresar = findViewById<Button>(R.id.bt_ingresar)
        //input del gmail
        val inputgmail = findViewById<EditText>(R.id.input_email)
        //input de la password
        val inputpass = findViewById<EditText>(R.id.input_password)

        // evento del click del boton de ingresar
        ingresar.setOnClickListener {
            //extraer el email y password de los inputs
            val email = inputgmail.text.toString()
            val password = inputpass.text.toString()

            //L1.verificar si existe en la base de datos de mysql basandose en el email y password
            //que el usuario ha proporcionado en los inputs inputgmail y inputpass
            loginUsuario(email, password)




        }
        //evento de click en el boton de registrarte
        btreg.setOnClickListener {
            val intent = Intent(this, displayregistrer::class.java)
            startActivity(intent)
        }

    }
    //L2.esta es una funcion que hace una consulta a la base de datos de mysql para verificar el login
    private fun loginUsuario(email: String, password: String) {
        // Llamamos a la API que hace la consulta a log.php para verificar el login
        val call = RetrofitClient.apiService.loginUser(email, password)
        call.enqueue(object : retrofit2.Callback<String> {
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if (response.isSuccessful) {
                    // Obtener el cuerpo de la respuesta
                    val responseBody = response.body()?.trim() ?: ""

                    // Limpiar la respuesta eliminando el prefijo 'conexionexitosa' si está presente
                    val cleanedResponse = responseBody.replace("conexionexitosa", "").trim()

                    // Verificamos si el login fue exitoso
                    if (cleanedResponse ==  responseBody.replace("conexionexitosa", "").trim()) {
                        // Si el login es exitoso
                        //l3.Verificar si el mismo usuario proporcionado existe en firebase
                        LoginFireBase(email, password)

                    } else {
                        // Si no se encuentra el usuario, mostramos un mensaje de error
                        AlertDialog.Builder(this@MainActivity).apply {
                            setTitle("Login Error")
                            setMessage("Usuario no encontrado o contraseña incorrecta.")
                            setPositiveButton("OK", null)
                        }.show()
                    }
                } else {
                    // En caso de error en la respuesta de la API
                    AlertDialog.Builder(this@MainActivity).apply {
                        setTitle("Retrofit Error")
                        setMessage("Error en la respuesta: ${response.message()}")
                        setPositiveButton("OK", null)
                    }.show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // En caso de error en la llamada (fallo en la red, por ejemplo)
                AlertDialog.Builder(this@MainActivity).apply {
                    setTitle("Retrofit Error")
                    setMessage("Excepción: ${t.message}")
                    setPositiveButton("OK", null)
                }.show()
            }
        })
    }
    //L4. esta funcion nos permite checar si el usuario proporcionado existe en firebase
    private fun LoginFireBase(email: String, password: String){
        //aqui le estamos diciendo que tiene que iniciar seccion con el email y password
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //si todo esta bien aqui va hacer algo
                //crea una instancia para si el usuario verifico su correo pueda iniciar session
                val user = FirebaseAuth.getInstance().currentUser

                if(user?.isEmailVerified == true) {
                    //L5.si el correo esta verificado lo dirije a la pantalla del feed
                    /*val intent = Intent(this, displayfeed::class.java)
                    startActivity(intent) */


                    val intent = Intent(this, displayfeed::class.java)
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(intent)
                        finish() // Opcional, si quieres cerrar esta actividad
                    }, 2000)



                }else{
                    // si el correo no esta verficado lanza un error
                    FirebaseAuth.getInstance().signOut()
                    AlertDialog.Builder(this).apply {
                        setTitle("ERROR AL INICIAR SESSION  ")
                        setMessage("Debes de verificar tu correo para continuar")
                        setPositiveButton("OK", null)
                    }.show()
                }
            }
            .addOnFailureListener {
                //si todo esta mal aqui va hacer algo
                utiles.showerror(this, it.message.toString())
            }
    }
}