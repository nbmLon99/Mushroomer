package com.nbmlon.mushroomer.ui.commu

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.nbmlon.mushroomer.data.posts.PostsRepository
import com.nbmlon.mushroomer.data.posts.PostsSearchRepository
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Post

class BoardViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: PostsRepository,
    private val boardType: BoardType
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(ViewModelBoard::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return ViewModelBoard(repository, handle, boardType ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SearchViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: PostsSearchRepository,
    private val boardType: BoardType
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(ViewModelBoardSearch::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewModelBoardSearch(repository, handle, boardType ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

enum class ResponseCode{
    SUCCESS,
    FAIL
}

sealed class CommuRequest{
    data class ForReport(val post : Post?, val comment : Comment?) : CommuRequest()
    data class ForDelete(val post : Post?, val comment : Comment?) : CommuRequest()
    data class ForUpload(val post : Post?, val comment : Comment?) : CommuRequest()
    data class ForModify(val post : Post?, val comment : Comment?) : CommuRequest()
}


sealed class CommuResponse{
    data class ForReport(val code : ResponseCode) : CommuResponse()
    data class ForDelete(val code : ResponseCode) : CommuResponse()
    data class ForUpload(val code : ResponseCode) : CommuResponse()
    data class ForModify(val code : ResponseCode) : CommuResponse()
}
