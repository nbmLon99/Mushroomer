package com.nbmlon.mushroomer.ui.commu.post

import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Post


interface PopupMenuClickListener {
    fun openReportDialog(target_post: Post?, target_comment: Comment?)
    fun onClickDel(target_post: Post?, target_comment: Comment?)
    fun onClickModify(target_post: Post?, target_comment: Comment?)
    fun onClickWriteReply(target_comment: Comment)
    fun onChangeLike(target_post: Post?, target_comment: Comment?, like : Boolean)
}