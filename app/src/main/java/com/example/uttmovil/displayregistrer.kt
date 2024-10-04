package com.example.uttmovil

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import org.checkerframework.common.returnsreceiver.qual.This

class displayregistrer : AppCompatActivity() {
    //obtener objeto de firebase para la autotentificacion
    private val auth = FirebaseAuth.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_displayregistrer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        //bucar las ides de gmail , username , passowrd
        val inputemail = findViewById<EditText>(R.id.inputemail)
        val inputusername = findViewById<EditText>(R.id.inputusername)
        val inputpassword = findViewById<EditText>(R.id.inputpassword)

        //buscar botones
        val bregistrar = findViewById<Button>(R.id.btregister)

        //evento click al bregistrar
        bregistrar.setOnClickListener{
            val email = inputemail.text.toString()
            val password = inputpassword.text.toString()
            val username = inputusername.text.toString()
            registrar(email, password, username)
        }


    }
    private fun registrar(correo:String, password:String, username:String) {
        auth.createUserWithEmailAndPassword(correo, password)
            .addOnCompleteListener { authResult ->
                val user = auth.currentUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()

                user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Enviar el correo de verificación
                            user.sendEmailVerification()
                                .addOnCompleteListener { emailTask ->
                                    if (emailTask.isSuccessful) {
                                        AlertDialog.Builder(this).apply {
                                            setTitle("CORRE DE VERIFICACION")
                                            setMessage("VERIFICA TU CORREO PARA REGISTRARTE")
                                            setPositiveButton("OK", null)
                                            finish()
                                        }.show()


                                    } else {

                                    }

                                    AlertDialog.Builder(this).apply {
                                        setTitle("Registro")
                                        setMessage("Registro Exitoso")
                                        setPositiveButton("OK", null)
                                    }.show()
                                }
                                .addOnFailureListener { exception ->
                                    AlertDialog.Builder(this).apply {
                                        setTitle("Error")
                                        setMessage("AH OCURRIDO UN ERROR CON EL USUSARIO")
                                        setPositiveButton("OK", null)
                                    }.show()
                                }

                        }
                    }
            }
    }
}