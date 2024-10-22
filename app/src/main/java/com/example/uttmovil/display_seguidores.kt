package com.example.uttmovil

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class display_seguidores : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_display_seguidores)


        val bt_inicio = findViewById<ImageButton>(R.id.boton_inicio)

        // Configurar el listener para hacer algo cuando se presione
        bt_inicio.setOnClickListener {
            val intent = Intent(this, displayregistrer::class.java)
            startActivity(intent)
        }
        val intent = Intent(this, display_seguidores::class.java)
        startActivity(intent)
    }
}
