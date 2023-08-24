package com.nbmlon.mushroomer.ui.commu

import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Post


interface PopupMenuClickListener {
    fun onClickReport(target_post: Post?, target_comment: Comment?)
    fun onClickDel(target_post: Post?, target_comment: Comment?)
    fun onClickModify(target_post: Post?, target_comment: Comment?)
    fun onClickWriteReply(target_comment: Comment)
}