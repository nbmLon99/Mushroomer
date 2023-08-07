package com.nbmlon.mushroomer.ui.dogam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.databinding.ItemDogamBinding
import com.nbmlon.mushroomer.model.Mushroom




class DogamAdapter() :
    PagingDataAdapter<Mushroom, DogamAdapter.DogamViewHolder>(MushDiffCallback()) {
    class DogamViewHolder(
        private val binding: ItemDogamBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Mushroom) {
            binding.apply {
                mushroom = item
                executePendingBindings()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogamViewHolder {
        return DogamViewHolder(
            ItemDogamBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }
    override fun onBindViewHolder(holder: DogamViewHolder, position: Int) {
        val item = getItem(position)
        // Note that item may be null. ViewHolder must support binding a
        // null item as a placeholder.
        holder.bind(item!!)
    }
}

private class MushDiffCallback : DiffUtil.ItemCallback<Mushroom>() {
    override fun areItemsTheSame(oldItem: Mushroom, newItem: Mushroom): Boolean {
        return oldItem.dogamNo == newItem.dogamNo
    }
    override fun areContentsTheSame(oldItem: Mushroom, newItem: Mushroom): Boolean {
        return oldItem == newItem
    }
}
