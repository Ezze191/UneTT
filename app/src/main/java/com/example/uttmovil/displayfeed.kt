package com.example.uttmovil

import Comment
import Post
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FieldValue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.airbnb.lottie.LottieAnimationView

class displayfeed : AppCompatActivity() {

    private lateinit var postList: MutableList<Post>
    private lateinit var postAdapter: PostAdapter
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("MissingInflatedId")
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_displayfeed)

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





        // Inicializa el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPosts)
        postList = mutableListOf()
        postAdapter = PostAdapter(postList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = postAdapter

        // Escucha en tiempo real los cambios en la colección "post"
        db.collection("post")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(this, "Error al cargar publicaciones", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                postList.clear()
                snapshot?.documents?.forEach { document ->
                    val post = document.toObject(Post::class.java)?.copy(postId = document.id)
                    if (post != null) {
                        postList.add(post)

                        // Cargar los comentarios
                        db.collection("post")
                            .document(post.postId!!)
                            .collection("comments")
                            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                            .addSnapshotListener { commentSnapshot, commentException ->
                                if (commentException != null) {
                                    Toast.makeText(this, "Error al cargar comentarios", Toast.LENGTH_SHORT).show()
                                    return@addSnapshotListener
                                }

                                // Limpiar los comentarios actuales
                                post.comments = mutableListOf() // Asegúrate de que la lista sea mutable

                                // Agregar comentarios nuevos
                                commentSnapshot?.documents?.forEach { commentDocument ->
                                    val comment = commentDocument.toObject(Comment::class.java)
                                    if (comment != null) {
                                        post.comments.add(comment)
                                    }
                                }
                                // Notificar al adaptador para que se actualice
                                postAdapter.notifyDataSetChanged()
                            }
                    }
                }

                // Notificar al adaptador que los datos de publicaciones han cambiado
                postAdapter.notifyDataSetChanged()
            }

        val bt_inicio = findViewById<ImageButton>(R.id.boton_inicio)

        bt_inicio.setOnClickListener {
            val intent = Intent(this, displayfeed::class.java)
            startActivity(intent)
        }

        val seguidoresButton: ImageButton = findViewById(R.id.seguidores_button)
        seguidoresButton.setOnClickListener {
            val intent = Intent(this, display_seguidores::class.java)
            startActivity(intent)
        }

        val perfilUsuarioButton: ImageButton = findViewById(R.id.boton_user)
        perfilUsuarioButton.setOnClickListener {
            val intent = Intent(this, perfil_usuario::class.java)
            startActivity(intent)
        }

        val postContent = findViewById<EditText>(R.id.textcomment)
        val mediaUpload = findViewById<Button>(R.id.mediaUpload)
        val submitButton = findViewById<Button>(R.id.comentarbt)

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

    private fun savePost(content: String, mediaUrl: String?) {
        val post = hashMapOf(
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
                Toast.makeText(this, "Publicación exitosa", Toast.LENGTH_SHORT).show()
                uploadpost(auth.currentUser?.email.toString(), content, mediaUrl.toString(), FieldValue.serverTimestamp().toString())
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al publicar", Toast.LENGTH_SHORT).show()
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
    fun uploadpost(username : String, post : String, mediaURL : String, date: String){
        // Llamada a Retrofit para enviar la publicación
        val postRequest = PostRequest(
            username = username,
            post = post,
            mediaURL = mediaURL,  // URL de la imagen, si hay
            date = date
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
                        Toast.makeText(this@displayfeed, "Publicación creada exitosamente", Toast.LENGTH_LONG).show()
                    } else {
                        val errorMessage = result?.get("error") as? String ?: "Error desconocido"
                        Toast.makeText(this@displayfeed, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@displayfeed, "Error en la respuesta del servidor", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(this@displayfeed, "Fallo en la conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })

    }
}
