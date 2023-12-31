package com.nbmlon.mushroomer.ui.camera

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.databinding.ItemPhotoCheckingBinding
import com.nbmlon.mushroomer.ui.camera.PicturesAdapter.PictureViewHolder
import com.nbmlon.mushroomer.utils.GlideApp


/**
 * 홈 화면 찍은 사진
 * 커뮤니티 글 작성시 카메라 업로드
 */
class PicturesAdapter(im: ImageDeleteListner) : ListAdapter<Bitmap,PictureViewHolder>(BitmapDiffCallback){
    private lateinit var itemBinding: ItemPhotoCheckingBinding
    private var imageManager: ImageDeleteListner = im

    inner class PictureViewHolder(private val itemBinding: ItemPhotoCheckingBinding) : RecyclerView.ViewHolder(itemBinding.root){
        fun bind(pos : Int) {
            val targetBitmap = getItem(pos)
            GlideApp
                .with(itemView.context)
                .load(targetBitmap)
                .into(itemBinding.photo)
            itemBinding.clearBtn.setOnClickListener { imageManager.deleteImage(bindingAdapterPosition) }
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


object BitmapDiffCallback : DiffUtil.ItemCallback<Bitmap>() {
    override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
        return oldItem == newItem
    }
}
