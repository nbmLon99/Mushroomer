package com.nbmlon.mushroomer.ui.commu;

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.databinding.ItemPhotoCheckingBinding;
import com.nbmlon.mushroomer.ui.camera.ImageDeleteListner;
import com.nbmlon.mushroomer.utils.GlideApp

class PicturesAdapterForWriting(im:ImageDeleteListner) : ListAdapter<Uri, PicturesAdapterForWriting.PictureViewHolder>(UriDiffCallback){
        private lateinit var itemBinding: ItemPhotoCheckingBinding
        private var imageManager: ImageDeleteListner = im

        inner class PictureViewHolder(private val itemBinding:ItemPhotoCheckingBinding) : RecyclerView.ViewHolder(itemBinding.root){
            fun bind(pos : Int) {
                val targetUri = getItem(pos)
                GlideApp
                        .with(itemView.context)
                        .load(targetUri)
                        .into(itemBinding.photo)
                itemBinding.clearBtn.setOnClickListener { imageManager.deleteImage(pos) }
             }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
            itemBinding = ItemPhotoCheckingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PictureViewHolder(itemBinding)
        }


        override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
            holder.bind(position)
        }
}
object UriDiffCallback : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
                return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
                return oldItem == newItem
        }
}
