package com.example.uttmovil

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    //obtener objeto de firebase para la autotentificacion
    private val auth = FirebaseAuth.getInstance()


    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
            val email = inputgmail.text.toString()
            val password = inputpass.text.toString()

            //aqui le estamos diciendo que tiene que iniciar seccion con el email y password
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    //si todo esta bien aqui va hacer algo
                    //crea una instancia para si el usuario verifico su correo pueda iniciar session
                    val user = FirebaseAuth.getInstance().currentUser

                    if(user?.isEmailVerified == true) {
                        //si el correo esta verificado lo dirije a la pantalla del feed
                        val intent = Intent(this, displayfeed::class.java)
                        startActivity(intent)
                        finish()

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
        //evento de click en el boton de registrarte
        btreg.setOnClickListener {
            val intent = Intent(this, displayregistrer::class.java)
            startActivity(intent)
        }

    }
}