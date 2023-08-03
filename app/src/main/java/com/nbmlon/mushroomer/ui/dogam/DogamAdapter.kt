package com.nbmlon.mushroomer.ui.dogam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.databinding.ItemDogamBinding
import com.nbmlon.mushroomer.model.Mushroom

/**
 * Adapter for the [RecyclerView] in [PlantListFragment].
 */
class DogamAdapter : ListAdapter<Mushroom, RecyclerView.ViewHolder>(PlantDiffCallback()) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val plant = getItem(position)
        (holder as PlantViewHolder).bind(plant)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DogamViewHolder(
            ItemDogamBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    class DogamViewHolder(
        private val binding: ListItemPlantBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Mushroom) {
            binding.apply {
                mushroom = item
                executePendingBindings()
            }
        }
    }
}

private class PlantDiffCallback : DiffUtil.ItemCallback<Mushroom>() {

    override fun areItemsTheSame(oldItem: Mushroom, newItem: Mushroom): Boolean {
        return oldItem.dogamNo == newItem.dogamNo
    }

    override fun areContentsTheSame(oldItem: Mushroom, newItem: Mushroom): Boolean {
        return oldItem == newItem
    }
}