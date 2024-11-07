package com.example.uttmovil

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

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
        //encuentra el id del boton de cerrar session
        val bt_cerrar  = findViewById<Button>(R.id.btcerrarsession)

        bt_cerrar.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //redirijir al la pantalla de inicio

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()


        }


    }
}