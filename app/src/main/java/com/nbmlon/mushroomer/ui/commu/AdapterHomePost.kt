package com.nbmlon.mushroomer.ui.commu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.databinding.ItemCommuHomeIamgeBinding
import com.nbmlon.mushroomer.databinding.ItemCommuHomeTextBinding
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.model.PostType

class AdapterHomePost : ListAdapter<Post, RecyclerView.ViewHolder>(PostDiffCallback()) {
    private enum class ItemViewType {
        PHOTO_POST, TEXT_POST
    }

    inner class TextPostViewHolder(private val itemBinding: ItemCommuHomeTextBinding) : RecyclerView.ViewHolder(itemBinding.root), HomeAdapterHolder {
        override fun bind(pos : Int){
            itemBinding.apply {
                post = getItem(pos)
                executePendingBindings()
            }
        }
    }

    inner class PhotoPostViewHolder(private val itemBinding: ItemCommuHomeIamgeBinding) : RecyclerView.ViewHolder(itemBinding.root), HomeAdapterHolder{
        override fun bind(pos : Int){
            itemBinding.apply {
                post = getItem(pos)
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(getItem(position).type == PostType.POST_PHOTO)
            ItemViewType.PHOTO_POST.ordinal
        else
            ItemViewType.TEXT_POST.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == ItemViewType.TEXT_POST.ordinal){
            TextPostViewHolder( ItemCommuHomeTextBinding.inflate(LayoutInflater.from(parent.context)) )
        } else{
            PhotoPostViewHolder( ItemCommuHomeIamgeBinding.inflate(LayoutInflater.from(parent.context)) )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HomeAdapterHolder).bind(position)
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

