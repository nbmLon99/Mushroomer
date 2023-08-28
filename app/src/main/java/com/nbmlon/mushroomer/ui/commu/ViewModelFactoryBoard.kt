package com.nbmlon.mushroomer.ui.commu

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.nbmlon.mushroomer.data.posts.PostsRepository

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
        if (modelClass.isAssignableFrom(BoardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BoardViewModel(repository, handle, boardType ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}