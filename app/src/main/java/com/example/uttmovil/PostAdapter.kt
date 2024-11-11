package com.example.uttmovil

import Post
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.username)
        val postTextView: TextView = itemView.findViewById(R.id.post_content)
        val postImageView: ImageView = itemView.findViewById(R.id.media_image)  // ImageView para la imagen
        val dateTextView: TextView = itemView.findViewById(R.id.post_date)  // TextView para la fecha
        val likeButton : ImageButton = itemView.findViewById(R.id.like_button) //boton de like
        val likesCountTextView: TextView = itemView.findViewById(R.id.likes_count) //texview de contador de likes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.usernameTextView.text = post.username
        holder.postTextView.text = post.post

        // Mostrar la fecha formateada
        val formattedDate = post.getFormattedDate()  // Obtén la fecha formateada
        holder.dateTextView.text = formattedDate

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

        //configuracion del boton de like
        holder.likesCountTextView.text = post.likes.toString()
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        //manejar e; clic en el boton de like
        holder.likeButton.setOnClickListener {
            if(post.likedBy.contains(userID)){
                post.likedBy.remove(userID)
                post.likes--
                holder.likesCountTextView.text = post.likes.toString()
            }else{
                post.likedBy.add(userID!!)
                post.likes++
                holder.likesCountTextView.text = post.likes.toString()
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

    }

    override fun getItemCount(): Int {
        return posts.size
    }
}
