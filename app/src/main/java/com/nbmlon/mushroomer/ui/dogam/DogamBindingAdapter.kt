package com.nbmlon.mushroomer.ui.dogam

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.model.MushType
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.Locale


@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

@BindingAdapter("isDiscovered")
fun bindIsGone(view: ImageView, gotcha: Boolean) {
    if (gotcha) {
        view.visibility = View.GONE
    } else {
        view.visibility = View.VISIBLE
    }
}



@BindingAdapter("setMushType")
fun bindMushType(view: TextView, type: MushType) {
    when(type){
        MushType.EDIBLE-> view.setText(R.string.typeEat)
        MushType.POISON-> view.setText(R.string.typePoison)
    }
}

@BindingAdapter("picturedAt")
fun bindDate(view : TextView, date : DateTime){
    val format = SimpleDateFormat("yyyy년\nM월 d일", Locale.getDefault())
    view.text = format.format(date)
}