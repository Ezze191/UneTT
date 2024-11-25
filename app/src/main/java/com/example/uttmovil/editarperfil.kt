package com.example.uttmovil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class editarperfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editarperfil)

        val btEditar: Button = findViewById(R.id.bt_editarPerfil)
        btEditar.setOnClickListener {
            val intent = Intent(this, editarperfil::class.java)
            startActivity(intent)
        }

    }

}
