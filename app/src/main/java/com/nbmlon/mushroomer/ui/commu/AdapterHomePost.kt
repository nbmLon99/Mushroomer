package com.nbmlon.mushroomer.ui.commu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.databinding.ItemCommuHomeIamgeBinding
import com.nbmlon.mushroomer.databinding.ItemCommuHomeTextBinding
import com.nbmlon.mushroomer.model.Post

sealed class AdapterHomePost {
    /** 글자 어댑터 ( Free , QnA ) **/
    class AdapterTextPosts : ListAdapter<Post, AdapterTextPosts.TextPostViewHolder>(PostDiffCallback()) {
        lateinit var itemBinding : ItemCommuHomeTextBinding
        inner class TextPostViewHolder(itemBinding: ItemCommuHomeTextBinding) : RecyclerView.ViewHolder(itemBinding.root) {
            fun bind(pos : Int){

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextPostViewHolder {
            itemBinding = ItemCommuHomeTextBinding.inflate(LayoutInflater.from(parent.context))
            return TextPostViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: TextPostViewHolder, position: Int) {
            holder.bind(position)
        }
    }

    /** 그림 어댑터 ( 자랑 ) **/
    class AdapterPhotoPosts : ListAdapter<Post, AdapterPhotoPosts.PhotoPostViewHolder>(PostDiffCallback()) {
        private lateinit var itemBinding: ItemCommuHomeIamgeBinding

        inner class PhotoPostViewHolder(itemBinding: ItemCommuHomeIamgeBinding) : RecyclerView.ViewHolder(itemBinding.root){
            fun bind(pos : Int){

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoPostViewHolder {
            itemBinding = ItemCommuHomeIamgeBinding.inflate(LayoutInflater.from(parent.context))
            return PhotoPostViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: PhotoPostViewHolder, position: Int) {
            holder.bind(position)
        }
    }

}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}

