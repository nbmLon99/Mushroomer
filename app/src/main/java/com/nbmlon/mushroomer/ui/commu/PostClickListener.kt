package com.nbmlon.mushroomer.ui.commu

import com.nbmlon.mushroomer.model.Post

fun interface PostClickListener {
    fun openPost(post : Post)
}