package com.nbmlon.mushroomer.ui.dogam

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.nbmlon.mushroomer.data.dogam.DogamRepository

class DogamViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val repository: DogamRepository
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(DogamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DogamViewModel(repository, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
