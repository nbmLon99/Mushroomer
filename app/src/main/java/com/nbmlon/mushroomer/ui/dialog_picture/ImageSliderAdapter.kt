package com.nbmlon.mushroomer.ui.dialog_picture

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.ItemImagesliderBinding
import com.nbmlon.mushroomer.utils.GlideApp
import com.smarteist.autoimageslider.SliderViewAdapter

class ImageSliderAdapter(private val sliderItems : List<String>) : SliderViewAdapter<ImageSliderAdapter.MySliderViewHolder>() {
    inner class MySliderViewHolder(val itembinding: ItemImagesliderBinding) : SliderViewAdapter.ViewHolder(itembinding.root){
        fun bind(pos : Int){
            GlideApp.with(itemView)
                .load(sliderItems[pos])
                .error(R.drawable.drawable_error)
                .into(itembinding.ivImage)
        }
    }

    override fun getCount(): Int {
        return sliderItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): MySliderViewHolder {
        val binding = ItemImagesliderBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return MySliderViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: MySliderViewHolder?, position: Int) {
        viewHolder?.bind(position)
    }
}