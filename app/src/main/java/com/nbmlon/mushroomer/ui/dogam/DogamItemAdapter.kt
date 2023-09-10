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
) : PagingDataAdapter<DogamUiModel, DogamItemAdapter.DogamViewHolder>(UIMODEL_COMPARATOR) {
    inner class DogamViewHolder(
        private val binding: ItemDogamBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Mushroom) {
            binding.apply {
                mushroom = item
                executePendingBindings()
                root.setOnClickListener {
                    dogamItemClickListner.onDogamItemClicked(item)
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
                is DogamUiModel.MushItem -> (holder as DogamViewHolder).bind(uiModel.mush)
                null -> throw UnsupportedOperationException("Unknown view")
            }
        }
    }
    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<DogamUiModel>() {
            override fun areItemsTheSame(oldItem: DogamUiModel, newItem: DogamUiModel): Boolean {
                return (oldItem is DogamUiModel.MushItem && newItem is DogamUiModel.MushItem &&
                        oldItem.mush.dogamNo == newItem.mush.dogamNo)
            }

            override fun areContentsTheSame(oldItem: DogamUiModel, newItem: DogamUiModel): Boolean =
                oldItem == newItem
        }
    }
}
