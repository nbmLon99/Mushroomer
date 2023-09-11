package com.nbmlon.mushroomer.ui.commu.board

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.nbmlon.mushroomer.data.posts.BoardPostsRepository
import com.nbmlon.mushroomer.data.posts.PostsSearchRepository

class BoardViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: BoardPostsRepository,
    private val boardType: BoardType
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel> create(
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

    override fun <T : ViewModel> create(
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
