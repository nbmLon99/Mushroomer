package com.nbmlon.mushroomer.ui.commu.post

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
import com.nbmlon.mushroomer.ui.commu.post.AdapterPostComment.CommentReplyAdapter.ReplyViewHolder
import com.nbmlon.mushroomer.ui.commu.post.AdapterPostComment.CommentViewHolder


/** 상세 포스트에 댓글 어댑터 **/
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
                val isLike = targetComment?.myThumbsUp ?: true

                //좋아요 표시
                menu?.findItem(R.id.like)?.isVisible = isLike
                menu?.findItem(R.id.dislike)?.isVisible = !isLike


                menu?.findItem(R.id.report)?.isVisible = !(targetComment?.isMine ?: false)
                menu?.setGroupVisible(R.id.menuForOwner, targetComment?.isMine ?: false)
                menu?.setGroupVisible(R.id.menuForComment, true)
                setOnMenuItemClickListener { menuItem ->
                    // 메뉴 아이템 클릭 시 동작 처리
                    when (menuItem.itemId) {
                        R.id.like ->{
                            menu_cl.onChangeLike(null, targetComment, true)
                            true
                        }
                        R.id.dislike ->{
                            menu_cl.onChangeLike(null, targetComment, false)
                            true
                        }
                        R.id.report -> {
                            menu_cl.openReportDialog(null,targetComment)
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
                            R.id.like ->{
                                menu_cl.onChangeLike(null, targetReply, true)
                                true
                            }
                            R.id.dislike ->{
                                menu_cl.onChangeLike(null, targetReply, false)
                                true
                            }
                            R.id.report -> {
                                menu_cl.openReportDialog(null,targetReply)
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
