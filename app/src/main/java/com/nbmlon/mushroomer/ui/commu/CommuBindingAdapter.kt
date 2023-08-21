package com.nbmlon.mushroomer.ui.commu

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getDrawable
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nbmlon.mushroomer.R
import org.joda.time.DateTime
import org.joda.time.Duration
import java.text.SimpleDateFormat


@BindingAdapter("imageFromUrlArray")
fun bindImageFromUrlArray(view: ImageView, imageUrl: ArrayList<String>?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl[0])
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}
@BindingAdapter("checkMyLove")
fun checkMyLoveFromPost(view: ImageView, myLove : Boolean) {
    if( myLove ){
        view.setImageDrawable(getDrawable(view.context,R.drawable.icons_love))
    }else{
        view.setImageDrawable(getDrawable(view.context,R.drawable.icons_emp_love))
    }
}


@BindingAdapter("setTimeRelatively")
fun setTimeRelatively(view: TextView, dateAt : DateTime) {
    val currentTime = DateTime()
    val duration = Duration(dateAt, currentTime)

    val minutesDifference = duration.standardMinutes
    val equalDay = (
            currentTime.year == dateAt.year &&
            currentTime.dayOfYear == dateAt.dayOfYear
            )
    if( minutesDifference < 60){
        view.text = "${minutesDifference}분 전"

    }else if( !equalDay ){
        view.text = SimpleDateFormat("yy.MM.dd").format(dateAt)

    }else{
        view.text = SimpleDateFormat("HH:mm").format(dateAt)

    }
}
