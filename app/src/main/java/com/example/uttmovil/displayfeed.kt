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
import androidx.appcompat.app.AlertDialog
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

    //busca los atributos para ver las publicaciones
    private lateinit var postList: MutableList<Post>
    //postlist es una lista donde se almacenana las publicaciones
    private lateinit var postAdapter: PostAdapter
    /*el postAdapter es el que guarda todo el codigo de las publicaciones como los botones
    *                                  de comentar y darle like */

    private lateinit var recyclerView: RecyclerView
    //es el espacio donde publica

    //atributos de firebase para la autotentificacion y la base de datos y el almacenamiento en la nube
    @SuppressLint("MissingInflatedId")
    //auth es la autotentificacion
    val auth = FirebaseAuth.getInstance()
    //db es la base de datos donde guarda las publicaciones
    val db = FirebaseFirestore.getInstance()
    // storage es donde guarda las imagenes en la nube
    val storage = FirebaseStorage.getInstance()

    //es pra ver si se ha seleccionado una foto con publicacion
    var selectedFileUri: Uri? = null



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
                //limpia los posts actuales
                postList.clear()
                snapshot?.documents?.forEach { document ->
                    val post = document.toObject(Post::class.java)?.copy(postId = document.id)
                    if (post != null) {
                        postList.add(post)

                        // Cargar los comentarios
                        db.collection("post")
                            .document(post.postId!!)
                            .collection("comments")
                            .orderBy(
                                "date",
                                com.google.firebase.firestore.Query.Direction.DESCENDING
                            )
                            .addSnapshotListener { commentSnapshot, commentException ->
                                if (commentException != null) {
                                    Toast.makeText(
                                        this,
                                        "Error al cargar comentarios",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@addSnapshotListener
                                }

                                // Limpiar los comentarios actuales
                                post.comments =
                                    mutableListOf() // es una lista manipulable

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


    }
}
