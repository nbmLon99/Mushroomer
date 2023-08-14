package com.nbmlon.mushroomer.ui.camera

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.camera.core.ImageProxy
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.nbmlon.mushroomer.databinding.ItemPhotoCheckingBinding
import com.nbmlon.mushroomer.ui.camera.PicturesAdapter.PictureViewHolder
import com.nbmlon.mushroomer.utils.GlideApp


class PicturesAdapter(im: ImageListner) : ListAdapter<ImageProxy,PictureViewHolder>(MyDiffCallback){
    private lateinit var itemBinding: ItemPhotoCheckingBinding
    private var imageManager: ImageListner = im
    private val pictures : ArrayList<Uri> = ArrayList()

    inner class PictureViewHolder(private val itemBinding: ItemPhotoCheckingBinding) : RecyclerView.ViewHolder(itemBinding.root){
        fun bind(pos : Int) {
            val targetProxy = getItem(pos)
            // Load the ImageProxy into the ImageView using Glide
            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Prevent caching for ImageProxy
                .skipMemoryCache(true) // Skip memory caching as well

            GlideApp.with(itemView)
                .load(targetProxy)
                .apply(requestOptions)
                .into(itemBinding.photo)

            itemBinding.clearBtn.setOnClickListener { imageManager.deleteImage(pos) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        itemBinding = ItemPhotoCheckingBinding.inflate(LayoutInflater.from(parent.context))
        return PictureViewHolder(itemBinding)
    }


    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        holder.bind(position)
    }
}


object MyDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}