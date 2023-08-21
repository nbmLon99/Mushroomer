package com.nbmlon.mushroomer.model

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nbmlon.mushroomer.R
import org.joda.time.DateTime
import org.joda.time.Duration
import java.text.SimpleDateFormat

enum class PostType{
    POST_PHOTO,
    POST_TEXT
}


/**
 * @param title         포스팅 제목
 * @param images        포스팅에 들어갈 이미지 url 순서대로
 * @param content       포스팅 내용
 * @param time          최종 작성시간
 * @param writer        작성자
 * @param comments      댓글
 * @param ThumbsUpCount 좋아요 수
 *
 * @param type         포스팅 타입
 * @param myThumbsUp    내 좋아요 유무
 * @param updated       수정 유무 ( 수정됨 )
 */
data class Post(
    val title: String,
    val images: ArrayList<String>?,
    val content: ArrayList<String>,
    val time: DateTime,
    val writer: User,
    val comments: ArrayList<Comment>,
    val ThumbsUpCount: Int,

    val type : PostType,
    val myThumbsUp: Boolean,
    val updated: Boolean
    ) {
    companion object {
        fun getDummy(type : PostType): Post {
            return Post(
                title = "제목",
                images = null,
                content = arrayListOf("내용"),
                time = DateTime(),
                writer = User.getDummy(),
                comments = arrayListOf(),
                ThumbsUpCount = 0,
                myThumbsUp = false,
                type = type,
                updated = false
            )
        }

        fun getDummys(type : PostType) : ArrayList<Post>{
            val items = arrayListOf<Post>()
            for ( i in 1..10) {
                items.add(Post.getDummy(type))
            }
            return items
        }
    }
}

class PostDataBindingAdapter{
    @BindingAdapter("imageFromUrlArray")
    fun bindImageFromUrlArray(view: ImageView, imageUrl: ArrayList<String>?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(view.context)
                .load(imageUrl[0])
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
        }
    }

    @BindingAdapter("setImageIntoTextPost")
    fun bindImageIntoTextPost(view: ImageView, imageUrl: ArrayList<String>?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(view.context)
                .load(imageUrl[0])
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
        }
        else{
            view.visibility = View.GONE
        }
    }
    @BindingAdapter("checkMyLove")
    fun checkMyLoveFromPost(view: ImageView, myLove : Boolean) {
        if( myLove ){
            view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.icons_love))
        }else{
            view.setImageDrawable(
                ContextCompat.getDrawable(
                    view.context,
                    R.drawable.icons_emp_love
                )
            )
        }
    }
    @BindingAdapter("checkMyLike")
    fun checkMyLikeFromPost(view: ImageView, myLike : Boolean) {
        if( myLike ){
            view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.icons_like))
        }else{
            view.setImageDrawable(
                ContextCompat.getDrawable(
                    view.context,
                    R.drawable.icons_like_emp
                )
            )
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
}