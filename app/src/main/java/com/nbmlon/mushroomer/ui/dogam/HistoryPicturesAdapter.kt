package com.nbmlon.mushroomer.ui.dogam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.databinding.ItemHistoryPicturesBinding
import com.nbmlon.mushroomer.model.MushHistory

class HistoryPicturesAdapter(private val cl : DogamHistoryClickListener) : ListAdapter<MushHistory, HistoryPicturesAdapter.HistoryPicturesViewHolder>(DiffCallback()) {
    private lateinit var itemBinding: ItemHistoryPicturesBinding

    inner class HistoryPicturesViewHolder(val binding : ItemHistoryPicturesBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
            binding.apply {
                mushHistory = getItem(position)
                frame.setOnClickListener { cl.openPictureDialog( getItem(position) ) }
                executePendingBindings()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryPicturesViewHolder {
        itemBinding = ItemHistoryPicturesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryPicturesViewHolder(itemBinding)
    }
    override fun onBindViewHolder(holder: HistoryPicturesViewHolder, position: Int) {
        holder.bind(position)
    }

}

class DiffCallback : DiffUtil.ItemCallback<MushHistory>() {
    override fun areItemsTheSame(oldItem: MushHistory, newItem: MushHistory): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: MushHistory, newItem: MushHistory): Boolean {
        return oldItem == newItem
    }
}

