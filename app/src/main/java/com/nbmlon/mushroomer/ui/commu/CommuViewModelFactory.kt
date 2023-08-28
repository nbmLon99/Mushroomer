package com.nbmlon.mushroomer.ui.commu

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.nbmlon.mushroomer.data.posts.PostsRepository
import com.nbmlon.mushroomer.ui.dogam.DogamViewModel

class CommuViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: PostsRepository
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(DogamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommuViewModel(repository, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}