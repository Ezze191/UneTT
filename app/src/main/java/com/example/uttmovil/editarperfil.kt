package com.example.uttmovil

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.airbnb.lottie.LottieAnimationView

class editarperfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val lottieAnimationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)

        // Iniciar la animación
        lottieAnimationView.playAnimation()

        // Crear un Handler para ejecutar después de 3 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            // Aplicar animación de desvanecimiento
            lottieAnimationView.animate()
                .alpha(0f) // Cambiar la opacidad a 0
                .setDuration(500) // Duración del fade-out (en milisegundos)
                .withEndAction {
                    // Ocultar el LottieAnimationView después de desvanecerse
                    lottieAnimationView.visibility = View.GONE
                }
        }, 3000)




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
