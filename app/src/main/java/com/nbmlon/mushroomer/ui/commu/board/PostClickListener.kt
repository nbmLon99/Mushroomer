package com.nbmlon.mushroomer.ui.commu.board

import com.nbmlon.mushroomer.model.Post

fun interface PostClickListener {
    fun openPost(post : Post)
}