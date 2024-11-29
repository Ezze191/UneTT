package com.example.uttmovil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FieldValue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EspacioPubli : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var selectedFileUri: Uri? = null

    var name = ""





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_espacio_publi)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val postContent = findViewById<EditText>(R.id.comenttext)
        val mediaUpload = findViewById<Button>(R.id.mediaUpload)
        val submitButton = findViewById<Button>(R.id.btcomentar)

        val email = auth.currentUser?.email
        obtenerDatosUsuario(email.toString())



        mediaUpload.setOnClickListener {
            openFilePicker()  // Abre el selector de archivos para elegir una imagen
        }

        submitButton.setOnClickListener {
            val user = auth.currentUser
            if (user == null) {
                Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val content = postContent.text.toString()
            var mediaUrl: String? = null

            if (selectedFileUri != null) {
                val storageRef = storage.reference.child("posts/${user.uid}/${selectedFileUri?.lastPathSegment}")
                val uploadTask = storageRef.putFile(selectedFileUri!!)  // Usa la URI directamente aquí
                uploadTask.addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        mediaUrl = uri.toString()
                        savePost(content, mediaUrl)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Error al subir archivo", Toast.LENGTH_SHORT).show()
                }
            } else {
                savePost(content, mediaUrl)
            }
        }
    }
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

                        name = userName




                    } else {

                    }
                } else {

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {

            }
        })
    }




    private fun savePost(content: String, mediaUrl: String?) {

        val postId = db.collection("post").document().id // Genera un ID único
        val post = hashMapOf(
            "name" to name,
            "username" to auth.currentUser?.email,
            "date" to FieldValue.serverTimestamp(),
            "post" to content,
            "likes" to 0,
            "likedBy" to emptyList<String>(),
            "mediaURL" to mediaUrl,
            "userId" to auth.currentUser?.uid
        )

        db.collection("post").add(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Publicación exitosa en FB", Toast.LENGTH_SHORT).show()
                //se sube a la base de datos de mysql

                uploadpost(auth.currentUser?.email.toString(), content, mediaUrl.toString(), FieldValue.serverTimestamp().toString(),postId)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al publicar en FB", Toast.LENGTH_SHORT).show()
            }
    }

    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedFileUri = uri  // Almacena la URI seleccionada
        }
    }

    fun openFilePicker() {
        pickFileLauncher.launch("image/*")  // Limita la selección solo a imágenes
    }
    fun uploadpost(username : String, post : String, mediaURL : String, date: String,postId: String){
        // Llamada a Retrofit para enviar la publicación
        val postRequest = PostRequest(
            username = username,
            post = post,
            mediaURL = mediaURL,  // URL de la imagen, si hay
            date = date,
            postId = postId

        )

        RetrofitClient.apiService.createPost(postRequest).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val success = result?.get("success") as? Boolean ?: false
                    if (success) {
                        Toast.makeText(this@EspacioPubli, "Publicación creada exitosamente en la base de datos", Toast.LENGTH_LONG).show()
                        val Intent = Intent(this@EspacioPubli, displayfeed::class.java)
                        startActivity(Intent)
                        finish()
                    } else {
                        val errorMessage = result?.get("error") as? String ?: "Error desconocido"
                        Toast.makeText(this@EspacioPubli, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@EspacioPubli, "Error en la respuesta del servidor", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(this@EspacioPubli, "Fallo en la conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })





    }







    }
