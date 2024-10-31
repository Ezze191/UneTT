package com.example.uttmovil

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class displayfeed : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_displayfeed)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.texto_Unett)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
            
            
        }

        val bt_inicio = findViewById<ImageButton>(R.id.boton_inicio)

        // Configurar el listener para hacer algo cuando se presione
        bt_inicio.setOnClickListener {
            val intent = Intent(this, displayregistrer::class.java)
            startActivity(intent)

        }

        val botonInicio: ImageButton = findViewById(R.id.boton_inicio)
        botonInicio.setOnClickListener {
            // Acción para ir al layout de inicio
            val intent = Intent(this, displayfeed::class.java)
            startActivity(intent)
        }
        val seguidoresButton: ImageButton = findViewById(R.id.seguidores_button)
        seguidoresButton.setOnClickListener {
            // Acción para ir a la pantalla de seguidores
            val intent = Intent(this, display_seguidores::class.java)
            startActivity(intent)
        }
        val perfilUsuarioButton: ImageButton = findViewById(R.id.boton_user)
        seguidoresButton.setOnClickListener {
            // Acción para ir a la pantalla de seguidores
            val intent = Intent(this, perfil_usuario::class.java)
            startActivity(intent)
        }
    }

}

