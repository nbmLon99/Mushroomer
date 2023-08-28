package com.nbmlon.mushroomer.data.posts

import com.google.gson.annotations.SerializedName
import com.nbmlon.mushroomer.model.Post

data class PostsResponse(
    @SerializedName("") val items : ArrayList<Post> = ArrayList()
)
