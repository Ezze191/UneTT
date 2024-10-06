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
            //convierte las varibales del input a tedto
            val email = inputemail.text.toString()
            val password = inputpassword.text.toString()
            val username = inputusername.text.toString()

            //aqui detecta si el correo es de la escuela
            val keywords = listOf("@uttcampus.edu.mx")
            val escorrecto = keywords.all{email.contains(it)}

            if(escorrecto){
                //si el correo es correcto va a registrar el usuario en firebase
                registrar(email, password, username)

            }else{
                //si el correo es incorrecto va saltar un error
                AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    setMessage("EL CORREO DEBE SER INSTITUCIONAL")
                    setPositiveButton("OK", null)
                }.show()
            }
        }
    }
    //funcion que registra al usuario con los datos que ingreso a firebase
    private fun registrar(correo:String, password:String, username:String) {
        //aqui crea el nuevo usuario con las credenciales proporcionadas
        auth.createUserWithEmailAndPassword(correo, password)
            .addOnCompleteListener { authResult ->
                //si la accion se ejecuta correctamente lo va a subir a firebase y actualizar el nombre
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
                                    //si el correo se envio correctamente va hacer esto
                                    if (emailTask.isSuccessful) {
                                            //si es correcto se va a enviar a una pantalla de verificacion de correo
                                            val intent = Intent(this, verificationemaildisplay::class.java)
                                            startActivity(intent)
                                            finish()

                                    } else {
                                        //si el correo no se envio correctamente eso va hacer esto
                                        AlertDialog.Builder(this).apply {
                                            setTitle("Error")
                                            setMessage("ALGO SALIO MAL")
                                            setPositiveButton("OK", null)
                                        }
                                    }

                                }
                                .addOnFailureListener { exception ->
                                    //aqui sale el mensaje si el usuario no se agrego correctamente
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