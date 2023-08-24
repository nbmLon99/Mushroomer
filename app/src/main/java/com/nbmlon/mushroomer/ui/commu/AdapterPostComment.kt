package com.nbmlon.mushroomer.ui.commu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.ItemPostCommentBinding
import com.nbmlon.mushroomer.databinding.ItemPostReplyBinding
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.CommentDiffCallback
import com.nbmlon.mushroomer.ui.commu.AdapterPostComment.CommentReplyAdapter.ReplyViewHolder
import com.nbmlon.mushroomer.ui.commu.AdapterPostComment.CommentViewHolder


class AdapterPostComment(private val menu_cl: PopupMenuClickListener) : ListAdapter<Comment, CommentViewHolder>(CommentDiffCallback()) {
    inner class CommentViewHolder(private val itemBinding : ItemPostCommentBinding) : RecyclerView.ViewHolder(itemBinding.root){
        fun bind(pos : Int) {
            itemBinding.apply {
                val target = getItem(pos)
                comment = target
                btnCommentMore.setOnClickListener { showContextMenu( getItem(pos), btnCommentMore) }
                target.replies?.let{ replies ->
                    repliesRV.adapter = CommentReplyAdapter().apply { submitList(replies) }
                }
                executePendingBindings()
            }
        }

        private fun showContextMenu(targetComment : Comment, view: View) {
            val popupMenu = PopupMenu(view.context, view)
            val inflater = popupMenu.menuInflater
            inflater.inflate(R.menu.post_context_menu, popupMenu.menu)
            popupMenu.apply {
                menu?.findItem(R.id.menuForOwner)?.isVisible = targetComment?.isMine == true
                menu?.findItem(R.id.menuForComment)?.isVisible = true

                setOnMenuItemClickListener { menuItem ->
                    // 메뉴 아이템 클릭 시 동작 처리
                    when (menuItem.itemId) {
                        R.id.report -> {
                            menu_cl.onClickReport(null,targetComment)
                            true
                        }
                        R.id.modify_post_or_comment -> {
                            menu_cl.onClickModify(null,targetComment)
                            true
                        }
                        R.id.delete_post_or_comment -> {
                            menu_cl.onClickDel(null,targetComment)
                            true
                        }
                        R.id.write_reply -> {
                            //
                            menu_cl.onClickWriteReply(targetComment)
                            true
                        }
                        // 다른 메뉴 아이템에 대한 처리
                        else -> false
                    }
                }
                show()
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
                    btnCommentMore.setOnClickListener { showContextMenu( getItem(pos), btnCommentMore) }
                    executePendingBindings()
                }
            }
            private fun showContextMenu(targetReply : Comment, view: View) {
                val popupMenu = PopupMenu(view.context, view)
                val inflater = popupMenu.menuInflater
                inflater.inflate(R.menu.post_context_menu, popupMenu.menu)
                popupMenu.apply {
                    menu?.findItem(R.id.menuForOwner)?.isVisible = targetReply?.isMine == true
                    menu?.findItem(R.id.menuForComment)?.isVisible = false

                    setOnMenuItemClickListener { menuItem ->
                        // 메뉴 아이템 클릭 시 동작 처리
                        when (menuItem.itemId) {
                            R.id.report -> {
                                menu_cl.onClickReport(null,targetReply)
                                true
                            }

                            R.id.modify_post_or_comment -> {
                                menu_cl.onClickModify(null,targetReply)
                                true
                            }

                            R.id.delete_post_or_comment -> {
                                menu_cl.onClickDel(null,targetReply)
                                true
                            }

                            R.id.write_reply -> {
                                error("답글에서 답글 작성이 클릭됨")
                                true
                            }
                            // 다른 메뉴 아이템에 대한 처리
                            else -> false;
                        }
                    }
                    show()
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
