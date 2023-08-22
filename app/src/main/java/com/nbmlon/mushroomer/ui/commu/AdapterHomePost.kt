package com.nbmlon.mushroomer.ui.commu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.databinding.ItemCommuHomeIamgeBinding
import com.nbmlon.mushroomer.databinding.ItemCommuHomeTextBinding
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.model.PostDiffCallback


/** CommuHome에서 표시할 최신글 들 Adapter **/
class AdapterHomePost(val cl : PostClickListener) : ListAdapter<Post, RecyclerView.ViewHolder>(PostDiffCallback()) {
    inner class TextPostViewHolder(private val itemBinding: ItemCommuHomeTextBinding) : RecyclerView.ViewHolder(itemBinding.root), HomeAdapterHolder {
        override fun bind(pos : Int){
            itemBinding.apply {
                post = getItem(pos)
                executePendingBindings()
                frame.setOnClickListener { cl.openPost(post) }
            }
        }
    }

    inner class PhotoPostViewHolder(private val itemBinding: ItemCommuHomeIamgeBinding) : RecyclerView.ViewHolder(itemBinding.root), HomeAdapterHolder{
        override fun bind(pos : Int){
            itemBinding.apply {
                post = getItem(pos)
                executePendingBindings()
                frame.setOnClickListener { cl.openPost(post) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).boardType.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == BoardType.PicBoard.ordinal){
            PhotoPostViewHolder( ItemCommuHomeIamgeBinding.inflate(LayoutInflater.from(parent.context)) )
        } else{
            TextPostViewHolder( ItemCommuHomeTextBinding.inflate(LayoutInflater.from(parent.context)) )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HomeAdapterHolder).bind(position)
    }
}

