package com.sonicmaster.herokuapp.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sonicmaster.herokuapp.R
import com.sonicmaster.herokuapp.data.model.Post
import com.sonicmaster.herokuapp.data.network.RemoteDataSource.Companion.BASE_URL
import de.hdodenhof.circleimageview.CircleImageView

class PostsAdapter :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    private val callBack = object : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }


    val differ = AsyncListDiffer(this, callBack)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImageView: CircleImageView = itemView.findViewById(R.id.user_image)
        val username: TextView = itemView.findViewById(R.id.username)
        val postLayout: LinearLayout = itemView.findViewById(R.id.post_layout)
        val imageView: ImageView = itemView.findViewById(R.id.post_image)
        val postTitle: TextView = itemView.findViewById(R.id.post_title)
        val postContent: TextView = itemView.findViewById(R.id.post_content)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = differ.currentList[position]
        holder.userImageView.apply {
            Glide.with(this)
                .load(BASE_URL + "images/user.png")
                .into(this)
        }
        holder.username.text = post.userName

        holder.postLayout.setOnClickListener {
            onItemClickListener?.let {
                it(post._id)
            }
        }

        holder.postTitle.text = post.title
        holder.postContent.text = post.content

        holder.imageView.apply {
            Glide.with(this)
                .load(BASE_URL + post.imageUrl)
                .into(this)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }
}