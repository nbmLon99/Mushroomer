package com.nbmlon.mushroomer.ui.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.camera.core.ImageProxy
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.nbmlon.mushroomer.databinding.ItemPhotoCheckingBinding
import com.nbmlon.mushroomer.ui.camera.PicturesAdapter.PictureViewHolder
import com.nbmlon.mushroomer.utils.GlideApp
import java.io.ByteArrayOutputStream


class PicturesAdapter(im: ImageListner) : ListAdapter<ImageProxy,PictureViewHolder>(MyDiffCallback){
    private lateinit var itemBinding: ItemPhotoCheckingBinding
    private var imageManager: ImageListner = im

    inner class PictureViewHolder(private val itemBinding: ItemPhotoCheckingBinding) : RecyclerView.ViewHolder(itemBinding.root){
        fun bind(pos : Int) {
            val targetProxy = getItem(pos)
            // Load the ImageProxy into the ImageView using Glide
            itemBinding.photo.setImageBitmap(targetProxy.image?.toBitmap())
            targetProxy.close()
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


object MyDiffCallback : DiffUtil.ItemCallback<ImageProxy>() {
    override fun areItemsTheSame(oldItem: ImageProxy, newItem: ImageProxy): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ImageProxy, newItem: ImageProxy): Boolean {
        return oldItem == newItem
    }
}

fun Image.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}