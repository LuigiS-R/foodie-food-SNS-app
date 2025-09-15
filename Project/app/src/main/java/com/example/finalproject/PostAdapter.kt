package com.example.finalproject

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.bumptech.glide.Glide
import android.graphics.BitmapFactory

class PostAdapter(private val context: Context, private val postList: List<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameField: TextView = itemView.findViewById(R.id.usernameField)
        val restaurantField: TextView = itemView.findViewById(R.id.restaurantField)
        val pictureField: ImageView = itemView.findViewById(R.id.pictureField)
        val postContentField: TextView = itemView.findViewById(R.id.postContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.usernameField.text = post.username
        holder.restaurantField.text = post.restaurant
        Glide.with(context)
            .load(post.image)
            .into(holder.pictureField)
        holder.postContentField.text = post.content
    }

    override fun getItemCount(): Int = postList.size
}