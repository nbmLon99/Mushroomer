package com.nbmlon.mushroomer.ui.dogam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.DogamLoadStateFooterViewItemBinding

class DogamLoadStateAdapter (
    private val retry: () -> Unit
    ) : LoadStateAdapter<DogamLoadStateViewHolder>() {
        override fun onBindViewHolder(holder: DogamLoadStateViewHolder, loadState: LoadState) {
            holder.bind(loadState)
        }

        override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): DogamLoadStateViewHolder {
            return DogamLoadStateViewHolder.create(parent, retry)
        }
}

class DogamLoadStateViewHolder(
    private val binding: DogamLoadStateFooterViewItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): DogamLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.dogam_load_state_footer_view_item, parent, false)
            val binding = DogamLoadStateFooterViewItemBinding.bind(view)
            return DogamLoadStateViewHolder(binding, retry)
        }
    }
}