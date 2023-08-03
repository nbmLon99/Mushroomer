package com.nbmlon.mushroomer.ui.camera

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.GlideApp
import com.nbmlon.mushroomer.databinding.ItemPhotoCheckingBinding
import com.nbmlon.mushroomer.ui.camera.PicturesAdapter.PictureViewHolder


class PicturesAdapter(im: ImageListner) : RecyclerView.Adapter<PictureViewHolder>() {
    private lateinit var itemBinding: ItemPhotoCheckingBinding
    private var imageManager: ImageListner = im
    private val pictures : ArrayList<Uri> = ArrayList()

    inner class PictureViewHolder(private val itemBinding: ItemPhotoCheckingBinding) : RecyclerView.ViewHolder(itemBinding.root){
        fun bind(pos : Int){
            val targetUri = pictures[pos]
            GlideApp.with(itemView.context)
                .load(targetUri)
                    .into(itemBinding.photo)
            itemBinding.clearBtn.setOnClickListener {
                imageManager.deleteImage(targetUri)
                removeItem(targetUri)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        itemBinding = ItemPhotoCheckingBinding.inflate(LayoutInflater.from(parent.context))
        return PictureViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return pictures.size
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        holder.bind(position)
    }


    private fun removeItem(targetUri :Uri){
        val pos = pictures.indexOf(targetUri)
        pictures.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos,itemCount)
    }

    fun addPicture(uri : Uri){
        pictures.add(0,uri)
        notifyItemInserted(0)
    }

    fun getPictures() : ArrayList<Uri>{
        return pictures
    }
}