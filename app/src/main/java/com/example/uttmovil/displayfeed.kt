package com.example.uttmovil

import Post
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FieldValue
import java.io.File

class displayfeed : AppCompatActivity() {

    private lateinit var postList: MutableList<Post>
    private lateinit var postAdapter: PostAdapter
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("MissingInflatedId")
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var selectedFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_displayfeed)

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
                    val post = document.toObject(Post::class.java)
                    if (post != null) {
                        postList.add(post)
                    }
                }
                postAdapter.notifyDataSetChanged()
            }



        val bt_inicio = findViewById<ImageButton>(R.id.boton_inicio)

        bt_inicio.setOnClickListener {
            val intent = Intent(this, displayregistrer::class.java)
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

            if (selectedFile != null) {
                val storageRef = storage.reference.child("posts/${user.uid}/${selectedFile?.name}")
                val uploadTask = storageRef.putFile(selectedFile!!.toUri())
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
            "mediaURL" to mediaUrl
        )

        db.collection("post").add(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Publicación exitosa", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al publicar", Toast.LENGTH_SHORT).show()
            }
    }

    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedFile = File(uri.path)
        }
    }

    fun openFilePicker() {
        pickFileLauncher.launch("*/*")
    }
}
