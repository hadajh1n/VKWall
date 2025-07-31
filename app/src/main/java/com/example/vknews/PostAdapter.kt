package com.example.vknews

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vknews.databinding.ItemPostBinding

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = ItemPostBinding.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.binding.postTitle.text = "Пост #${position + 1}"
        holder.binding.postText.text = post.text

        // Пропорциональное изображение
        if (post.imageUrl != null) {
            holder.binding.postImage.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(post.imageUrl)
                .into(holder.binding.postImage)
        } else {
            holder.binding.postImage.visibility = View.GONE
        }

        // Фон
        holder.itemView.setBackgroundColor(
            if (post.isEven) Color.parseColor("#F0F0F0") else Color.WHITE
        )
    }

    override fun getItemCount(): Int = posts.size
}
