package com.example.uttmovil

import Post
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.username)
        val postTextView: TextView = itemView.findViewById(R.id.post_content)
        val postImageView: ImageView = itemView.findViewById(R.id.media_image)  // ImageView para la imagen
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.usernameTextView.text = post.username
        holder.postTextView.text = post.post

        // Si hay una URL de imagen, cargarla en el ImageView
        post.mediaURL?.let { url ->
            Glide.with(holder.itemView.context)
                .load(url) // Cargar la URL de la imagen
                .into(holder.postImageView) // Establecer la imagen en el ImageView
        } ?: run {
            // Si no hay URL de imagen, ocultar el ImageView
            holder.postImageView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}
