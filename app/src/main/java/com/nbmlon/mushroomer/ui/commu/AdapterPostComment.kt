package com.nbmlon.mushroomer.ui.commu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.databinding.ItemPostCommentBinding
import com.nbmlon.mushroomer.databinding.ItemPostReplyBinding
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.CommentDiffCallback
import com.nbmlon.mushroomer.ui.commu.AdapterPostComment.CommentReplyAdapter.ReplyViewHolder
import com.nbmlon.mushroomer.ui.commu.AdapterPostComment.CommentViewHolder

class AdapterPostComment : ListAdapter<Comment, CommentViewHolder>(CommentDiffCallback()) {
    inner class CommentViewHolder(private val itemBinding : ItemPostCommentBinding) : RecyclerView.ViewHolder(itemBinding.root){
        fun bind(pos : Int) {
            itemBinding.apply {
                val target = getItem(pos)
                comment = target
                executePendingBindings()
                target.replies?.let{ replies ->
                    repliesRV.adapter = CommentReplyAdapter().apply { submitList(replies) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemBinding = ItemPostCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(position)
    }




    /** 답글 어댑터**/
    inner class CommentReplyAdapter : ListAdapter<Comment, ReplyViewHolder>(CommentDiffCallback()){
        inner class ReplyViewHolder(private val itemBinding: ItemPostReplyBinding) : RecyclerView.ViewHolder(itemBinding.root){
            fun bind(pos :Int){
                itemBinding.apply {
                    comment = getItem(pos)
                    executePendingBindings()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
            val itemBinding = ItemPostReplyBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ReplyViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
            holder.bind(position)
        }
    }
}
