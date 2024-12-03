package com.example.uttmovil

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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

    //atributos para de firebase

    //auth es para la autotentifiacion
    val auth = FirebaseAuth.getInstance()
    /*es la base de datos para guardar los post*/
    val db = FirebaseFirestore.getInstance()
    /*guarda las imagenes si hay en la publicacion  */
    val storage = FirebaseStorage.getInstance()

    //es para guardar el nombre del archivo seleccionado para subir al post
    var selectedFileUri: Uri? = null

    //donde va guardar el nombre de la consulta de la base de datos
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
        //botones de barra inferior de navegacion
        val bt_inicio = findViewById<ImageButton>(R.id.boton_inicio)
        bt_inicio.setOnClickListener {
            val intent = Intent(this, displayfeed::class.java)
            startActivity(intent)

        }

        val perfilUsuarioButton: ImageButton = findViewById(R.id.boton_user)
        perfilUsuarioButton.setOnClickListener {
            val intent = Intent(this, perfil_usuario::class.java)
            startActivity(intent)

        }

        val btadd: ImageButton = findViewById(R.id.boton_agregar)
        btadd.setOnClickListener {
            val intent = Intent(this, EspacioPubli::class.java)
            startActivity(intent)

        }
        val verperfil = findViewById<ImageButton>(R.id.boton_user)
        verperfil.setOnClickListener {
            val intent = Intent(this, perfil_usuario::class.java)
            startActivity(intent)

        }
        val logout = findViewById<ImageButton>(R.id.logOutBt)
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //redirijir al la pantalla de inicio
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //encontrar los botones del espacio de comentar
        val postContent = findViewById<EditText>(R.id.comenttext)
        val mediaUpload = findViewById<Button>(R.id.mediaUpload)
        val submitButton = findViewById<Button>(R.id.btcomentar)
        val deleteFileButton = findViewById<Button>(R.id.deleteFileButton)
        val fileNameTextView = findViewById<TextView>(R.id.fileNameTextView)

        //guarda el email actualmente en firebse
        val email = auth.currentUser?.email
        obtenerDatosUsuario(email.toString())

        //es el boton que elimina el archivo seleccionado para publicar como una foto
        deleteFileButton.setOnClickListener {
            selectedFileUri = null
            fileNameTextView.text = "No file selected"
            Toast.makeText(this, "Archivo eliminado. Puedes seleccionar otro.", Toast.LENGTH_SHORT).show()
        }

        /*es una funcion que permite abrir la galeria para seleccionar una foto */
        mediaUpload.setOnClickListener {
            openFilePicker()  // Abre el selector de archivos para elegir una imagen
        }
        //boton sumbit de publicar
        submitButton.setOnClickListener {
            //guarda el usuario que esta logeado actualmente
            val user = auth.currentUser
            if (user == null) {
                Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val content = postContent.text.toString()
            var mediaUrl: String? = null

            //para ver cual archivo se acaba de arrastrar de la galeria hacia la publicacion
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

    //funcion para obtener los datos consultados de sql con retrofit
    private fun obtenerDatosUsuario(email: String) {
        val call = RetrofitClient.apiService.searchUser(email)
        call.enqueue(object : retrofit2.Callback<String> {
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.split("\n") ?: listOf()
                    if (responseBody.size >= 3) {
                        //guarda los atributos consultados
                        var userName = responseBody[0]
                        val biografia = responseBody[1]
                        val fecha = responseBody[2]

                        //reasigna la variable del name y guarda el nombre de usuario
                        name = userName
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // Manejo de errores
            }
        })
    }

    //funcion para guardar el post en firebase
    private fun savePost(content: String, mediaUrl: String?) {
        //genera un postid para cada publicacion
        val postId = db.collection("post").document().id // Genera un ID único
        //guarda los atributos para comentar en un objeto para enviarlos a firebase
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
        //indica que lo va a guardar en la coleccion post de firebase
        db.collection("post").add(post)
            .addOnSuccessListener {
                //se sube a la base de datos de mysql
                uploadpost(auth.currentUser?.email.toString(), content, mediaUrl.toString(), FieldValue.serverTimestamp().toString(), postId)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al publicar en FB", Toast.LENGTH_SHORT).show()
            }
    }

    //funcion para detectar el archivo seleccionado de la galeria
    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val fileNameTextView = findViewById<TextView>(R.id.fileNameTextView)
        if (uri != null) {
            selectedFileUri = uri  // Almacena la URI seleccionada
            val fileName = getFileName(uri)  // Obtiene el nombre del archivo
            fileNameTextView.text = fileName  // Actualiza el TextView con el nombre del archivo
        }
    }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        var fileName = "Unknown"
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                fileName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return fileName
    }

    fun openFilePicker() {
        pickFileLauncher.launch("image/*")  // Limita la selección solo a imágenes
    }

    //funcion donde sube la publicacion a la base de datos de sql
    fun uploadpost(username: String, post: String, mediaURL: String, date: String, postId: String) {
        // Llamada a Retrofit para enviar la publicación
        val postRequest = PostRequest(
            username = username,
            post = post,
            mediaURL = mediaURL,  // URL de la imagen, si hay
            date = date,
            postId = postId
        )

        RetrofitClient.apiService.createPost(postRequest).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val success = result?.get("success") as? Boolean ?: false
                    if (success) {
                        val intent = Intent(this@EspacioPubli, displayfeed::class.java)
                        startActivity(intent)
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