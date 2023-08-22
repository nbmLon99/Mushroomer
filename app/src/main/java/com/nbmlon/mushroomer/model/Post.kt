package com.nbmlon.mushroomer.model

import android.graphics.drawable.Drawable
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.ui.commu.BoardType
import org.joda.time.DateTime
import org.joda.time.Duration
import java.io.Serializable
import java.text.SimpleDateFormat


/**
 * @param title         포스팅 제목
 * @param images        포스팅에 들어갈 이미지 url 순서대로
 * @param content       포스팅 내용
 * @param time          최종 작성시간
 * @param writer        작성자
 * @param comments      댓글
 * @param ThumbsUpCount 좋아요 수
 *
 * @param boardType         포스팅 타입
 * @param myThumbsUp    내 좋아요 유무
 * @param updated       수정 유무 ( 수정됨 )
 */
data class Post(
    val title: String,
    val images: ArrayList<String>?,
    val content: ArrayList<String>,
    val time: DateTime,
    val writer: User,
    val comments: ArrayList<Comment>?,
    val ThumbsUpCount: Int,

    val boardType : BoardType,
    val myThumbsUp: Boolean,
    val updated: Boolean
    ) :Serializable {
    companion object {
        fun getDummy(type : BoardType): Post {
            return Post(
                title = "제목",
                images = null,
                content = arrayListOf("내용"),
                time = DateTime(),
                writer = User.getDummy(),
                comments = null,
                ThumbsUpCount = 0,
                myThumbsUp = false,
                boardType = type,
                updated = false
            )
        }

        fun getDummys(type : BoardType) : ArrayList<Post>{
            val items = arrayListOf<Post>()
            for ( i in 1..10) {
                items.add(getDummy(type))
            }
            return items
        }
    }
}

class PostDataBindingAdapter{
    companion object{
        @JvmStatic
        @BindingAdapter("imageFromUrlArray")
        fun bindImageFromUrlArray(view: ImageView, imageUrl: ArrayList<String>?) {
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(view.context)
                    .load(imageUrl[0])
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view)
            }
        }

        @JvmStatic
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
        @JvmStatic
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
        @JvmStatic
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


        @JvmStatic
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

        @JvmStatic
        @BindingAdapter("setPostContent")
        fun bindPostContent(view: TextView, post: Post) {
            //제대로 된 post라면 content개수랑 images개수가 하나 차이나야함
            val content =  SpannableStringBuilder()
            if(post.content.isNotEmpty()){
                for ( (idx, str) in post.content.withIndex()){
                    content.append(str)
                    content.append(getImageSpannableStringFromUrl(view, post.images?.getOrNull(idx)))
                }
            }
            view.text = content
        }


        @JvmStatic
        private fun getImageSpannableStringFromUrl(textView: TextView, url: String?) : SpannableString {
            val spannable = SpannableString("")
            url?.let{
                val imageGetter = Html.ImageGetter {
                    val imageSpan = ImageSpan(textView.context, R.drawable.drawable_error) // 이미지 로딩 전에 표시할 placeholder 이미지 리소스
                    spannable.setSpan(imageSpan, spannable.length, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    Glide.with(textView.context)
                        .load(url)
                        .into(object : CustomTarget<Drawable>() {
                            override fun onLoadCleared(placeholder: Drawable?) {}
                            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                val finalImageSpan = ImageSpan(resource)
                                spannable.removeSpan(imageSpan) // 이전의 placeholder 이미지 삭제
                                spannable.setSpan(finalImageSpan, spannable.length - 1, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                        })
                    imageSpan.drawable
                }
                imageGetter.getDrawable(url)
            }
            return spannable
        }
    }

}


class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}

