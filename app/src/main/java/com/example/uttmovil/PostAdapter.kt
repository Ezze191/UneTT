package com.example.uttmovil
import java.util.Locale
import java.text.SimpleDateFormat
import Post
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.Date
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response





class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.username)
        val postTextView: TextView = itemView.findViewById(R.id.post_content)
        val postImageView: ImageView = itemView.findViewById(R.id.media_image)  // ImageView para la imagen
        val dateTextView: TextView = itemView.findViewById(R.id.post_date)  // TextView para la fecha
        val likeButton : ImageButton = itemView.findViewById(R.id.like_button) //boton de like
        val likesCountTextView: TextView = itemView.findViewById(R.id.likes_count) //texview de contador de likes
        val commentEditText: TextView = itemView.findViewById(R.id.commentEditText) //input de comentario
        val commentButton: Button = itemView.findViewById(R.id.commentButton) //boton de comentario
        val commentTextView : TextView = itemView.findViewById(R.id.commentText) //texview de comentarios
        val deleteButton : Button = itemView.findViewById(R.id.btdelete) //boton de eliminar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.usernameTextView.text = post.username
        holder.postTextView.text = post.post
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid //para ver el id del usuario



        // Si hay una URL de imagen, cargarla en el ImageView
        post.mediaURL?.let { url ->
            Glide.with(holder.itemView.context)
                .load(url) // Cargar la URL de la imagen
                .into(holder.postImageView) // Establecer la imagen en el ImageView

            // Hacer visible el ImageView si hay imagen
            holder.postImageView.visibility = View.VISIBLE
        } ?: run {
            // Si no hay URL de imagen, ocultar el ImageView
            holder.postImageView.visibility = View.GONE
        }

        post.date?.toDate()?.let {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            holder.dateTextView.text = dateFormat.format(it)
        } ?: run {
            holder.dateTextView.text = "Fecha desconocida"
        }

        //configuracion del boton de like
        holder.likesCountTextView.text = post.likes.toString()
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        //manejar el clic en el boton de like
        holder.likeButton.setOnClickListener {
            if(post.likedBy.contains(userID)){
                post.likedBy.remove(userID)
                post.likes--
                holder.likesCountTextView.text = post.likes.toString()
                //elimar like de la base de datos de mysql


            }else{
                post.likedBy.add(userID!!)
                post.likes++
                holder.likesCountTextView.text = post.likes.toString()
                //subir like a la base de datos de mysql
                post.postId?.let { postId ->

                    val likeRequest = RequestLike(
                        postId = postId, // El ID del post, asegúrate de que está definido
                        comentUser = FirebaseAuth.getInstance().currentUser?.email ?: "usuario desconocido",
                        date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                            java.util.Date()
                        ) // Formato compatible con MySQL
                    )
                    //llama a retrofit
                    RetrofitClient.apiService.insertarLike(likeRequest).enqueue(object : Callback<Map<String, Any>> {
                        override fun onResponse(
                            call: Call<Map<String, Any>>,
                            response: Response<Map<String, Any>>
                        ) {
                            if (response.isSuccessful) {
                                val result = response.body()
                                val success = result?.get("success") as? Boolean ?: false
                                if (success) {
                                    Toast.makeText(holder.itemView.context, "Like guardado correctamente en MySQL", Toast.LENGTH_SHORT).show()
                                } else {
                                    val errorMessage = result?.get("message") as? String ?: "Error desconocido"
                                    Toast.makeText(holder.itemView.context, "Error en MySQL: $errorMessage", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(holder.itemView.context, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                            Toast.makeText(holder.itemView.context, "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }?: println("FirestoreError postId is null, cannot update Firestore.")

            }
            //actualizar la publicacion en la base de datos de firebase
            post.postId?.let { postId ->
                val postRef = FirebaseFirestore.getInstance().collection("post").document(postId)
                postRef.update("likedBy", post.likedBy, "likes", post.likes)
                    .addOnSuccessListener {
                        // Actualización exitosa
                        println("Likes actualizados correctamente")

                    }
                    .addOnFailureListener { e ->
                        // Manejar errores de actualización
                        println("Error al actualizar los likes: $e")
                    }

            }?: println("FirestoreError postId is null, cannot update Firestore.")

        }

        //manejar que se vean los comentarios aqui
        val commentText = buildString {
            post.comments.forEach { comment ->
                append("${comment.username}\n")
                append("Fecha: ${comment.date?.toDate()?.toString() ?: "Desconocida"}):\n")
                append("\n")
                append("${comment.comment}\n")
                append("\n")
            }
        }
        //asigna el contenido al texview
        holder.commentTextView.text = commentText

        //manejar el boton de comentar
        holder.commentButton.setOnClickListener {
            val postid = post.postId
            val commentText = holder.commentEditText.text.toString()

            if(commentText.isNotEmpty()){
                val comment = hashMapOf(
                    "username" to FirebaseAuth.getInstance().currentUser?.email,
                    "comment" to commentText,
                    "date" to FieldValue.serverTimestamp()
                )
                //guardar los comentarios en la coleccion de comentarios
                if (postid != null) {
                    FirebaseFirestore.getInstance()
                        .collection("post")
                        .document(postid)
                        .collection("comments")
                        .add(comment)
                        .addOnSuccessListener {
                            Toast.makeText(holder.itemView.context, "Comentario agregado", Toast.LENGTH_SHORT).show()
                            holder.commentEditText.text = ""
                        }
                        .addOnFailureListener{e ->
                            Toast.makeText(holder.itemView.context, "Error al agregar el comentario" + e.message, Toast.LENGTH_SHORT).show()
                        }
                    //subir a comentario a la base de datos de mysql
                    val comentarioRequest = ComentarioRequest(
                        comentario = commentText,
                        comentUser = FirebaseAuth.getInstance().currentUser?.email ?: "usuario desconocido",
                        comentPost = postid ?: "id desconocido"
                    )
                    //llama a retrofit
                    RetrofitClient.apiService.insertarComentario(comentarioRequest).enqueue(object : Callback<Map<String, Any>> {
                        override fun onResponse(
                            call: Call<Map<String, Any>>,
                            response: Response<Map<String, Any>>
                        ) {
                            if (response.isSuccessful) {
                                val result = response.body()
                                val success = result?.get("success") as? Boolean ?: false
                                if (success) {
                                    Toast.makeText(holder.itemView.context, "Comentario agregado en MySQL", Toast.LENGTH_SHORT).show()
                                } else {
                                    val errorMessage = result?.get("message") as? String ?: "Error desconocido"
                                    Toast.makeText(holder.itemView.context, "Error en MySQL: $errorMessage", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(holder.itemView.context, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                            Toast.makeText(holder.itemView.context, "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })

                }
            }

        }

        if (post.userId == currentUserId) {
            holder.deleteButton.visibility = View.VISIBLE
        } else {
            holder.deleteButton.visibility = View.GONE
        }

        //boton de eliminar publicacion
        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context).apply {
                setTitle("Eliminar publicación")
                setMessage("¿Estás seguro de que deseas eliminar esta publicación?")
                setPositiveButton("Sí") { _, _ ->
                    post.postId?.let { postId ->
                        FirebaseFirestore.getInstance().collection("post").document(postId)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(holder.itemView.context, "Publicación eliminada", Toast.LENGTH_SHORT).show()
                                (posts as MutableList).removeAt(position)
                                notifyItemRemoved(position)

                                    //aqui se va a eliminar de la base de datos de mysql
                                    val deletePostRequest = DeletePostRequest(
                                        idFB = postId  // Aquí pones el ID de la publicación que quieres eliminar
                                    )
                                    // Llamada a Retrofit para hacer el POST al archivo PHP
                                    RetrofitClient.apiService.deletePost(deletePostRequest).enqueue(object : Callback<Map<String, Any>> {
                                        override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                                            if (response.isSuccessful) {
                                                val result = response.body()
                                                val success = result?.get("success") as? Boolean ?: false
                                                if (success) {
                                                    // Muestra un mensaje de éxito
                                                    Toast.makeText(holder.itemView.context, "Publicación eliminada correctamente de mysql", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    // Muestra el mensaje de error desde la respuesta
                                                    val errorMessage = result?.get("message") as? String ?: "Error desconocido"
                                                    Toast.makeText(holder.itemView.context, "Error al eliminar: $errorMessage", Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                // Manejo de errores en caso de que la respuesta no sea exitosa
                                                Toast.makeText(holder.itemView.context, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                                            // Manejo de fallos en la conexión
                                            Toast.makeText(holder.itemView.context, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    })




                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(holder.itemView.context, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                setNegativeButton("No", null)
            }.show()
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }


}
