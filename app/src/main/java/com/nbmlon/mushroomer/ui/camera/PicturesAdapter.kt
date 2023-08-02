package com.nbmlon.mushroomer.ui.camera

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.GlideApp
import com.nbmlon.mushroomer.databinding.ItemPhotoCheckingBinding
import com.nbmlon.mushroomer.ui.camera.PicturesAdapter.PictureViewHolder


class PicturesAdapter(im: ImageManager) : RecyclerView.Adapter<PictureViewHolder>() {
    private lateinit var itemBinding: ItemPhotoCheckingBinding
    private var imageManager: ImageManager = im
    private val pictures : ArrayList<Uri> = ArrayList()


    inner class PictureViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(pos : Int){
            GlideApp.with(itemView.context)
                .load(pictures[pos])
                    .into(itemBinding.photo)

            itemBinding.clearBtn.setOnClickListener {
                imageManager.deleteImage(pictures[pos])
                pictures.removeAt(pos)
                notifyItemRemoved(pos)
                // 포스팅!! -> 왜 필요할까??
                notifyItemRangeChanged(pos,itemCount)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        itemBinding = ItemPhotoCheckingBinding.inflate(LayoutInflater.from(parent.context))
        return PictureViewHolder(itemBinding.root)
    }

    override fun getItemCount(): Int {
        return pictures.size
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        holder.bind(holder.bindingAdapterPosition)
    }

    fun addPicture(uri : Uri){
        pictures.add(uri)
        notifyItemInserted(pictures.size-1)

    }
}