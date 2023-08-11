package com.nbmlon.mushroomer.ui.dogam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.databinding.ItemDogamBinding
import com.nbmlon.mushroomer.model.Mushroom




class DogamItemAdapter(
    val dogamItemClickListner: DogamItemClickListner
) : PagingDataAdapter<UiModel, DogamItemAdapter.DogamViewHolder>(UIMODEL_COMPARATOR) {
    inner class DogamViewHolder(
        private val binding: ItemDogamBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Mushroom) {
            binding.apply {
                mushroom = item
                executePendingBindings()
                if (item.gotcha){
                    root.setOnClickListener {
                        dogamItemClickListner.onDogamItemClicked(item)
                    }
                }
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
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiModel.MushItem -> (holder as DogamViewHolder).bind(uiModel.mush)
                null -> throw UnsupportedOperationException("Unknown view")
            }
        }
    }
    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return (oldItem is UiModel.MushItem && newItem is UiModel.MushItem &&
                        oldItem.mush.dogamNo == newItem.mush.dogamNo)
            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                oldItem == newItem
        }
    }
}
