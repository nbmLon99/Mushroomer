package com.example.mushroomer.ui.camera

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mushroomer.databinding.ItemPhotoCheckingBinding
import com.example.mushroomer.ui.camera.PicturesAdapter.PictureViewHolder
import java.io.File




class PicturesAdapter() : RecyclerView.Adapter<PictureViewHolder>() {
    private lateinit var itemBinding: ItemPhotoCheckingBinding
    private val pictures : ArrayList<String> = ArrayList()


    inner class PictureViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(pos : Int){
            Glide.with(itemView)
                .load(pictures[pos])
                .into(itemBinding.photo)
            itemBinding.clearBtn.setOnClickListener {
                removePicture(pos)
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
        holder.bind(position)
    }


    private fun removePicture(pos : Int){
        //사진 삭제
        val photoFile: File = File(pictures[pos])
        if (photoFile.exists()) {
            photoFile.delete()
        } else {
            Log.d("에러","사진 반환 실패")
        }
        pictures.removeAt(pos)
        notifyItemRemoved(pos)
    }

    fun addPicture(path : String){
        pictures.add(path)
        notifyItemInserted(pictures.size-1)
    }
}