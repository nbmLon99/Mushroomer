package com.nbmlon.mushroomer.model

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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
 * @param boardType     포스팅 게시판 소속  (자유게시판/ QnA게시판 / 사진게시판 으로만 구성되어야함.)
 * @param myThumbsUp    내 좋아요 유무
 * @param updated       수정 유무 ( 수정됨 )
 */
data class Post(
    val title: String,
    val images: ArrayList<String>?,
    val content: String,
    val time: DateTime,
    val writer: User,
    val comments: ArrayList<Comment>?,
    var ThumbsUpCount: Int,

    val boardType : BoardType,
    val myThumbsUp: Boolean = false,
    val updated: Boolean = false
    ) :Serializable {
    companion object {
        fun getDummy(type : BoardType, query: String?, writer: User?): Post {
            return Post(
                title = query ?: "제목",
                images = null,
                content = "내용",
                time = DateTime(),
                writer = writer ?: User.getDummy(),
                comments = Comment.getDummyswithReplies(),
                ThumbsUpCount = 0,
                myThumbsUp = false,
                boardType = type,
                updated = false
            )
        }

        fun getDummys(type : BoardType, query : String? = null, writer: User? = null) : ArrayList<Post>{
            val items = arrayListOf<Post>()
            for ( i in 1..10) {
                items.add(getDummy(type, query, writer))
            }
            return items
        }
    }
}

class PostDataBindingAdapter{
    companion object {
        @JvmStatic
        @BindingAdapter("imageFromUrlArrayIntoPicPostPreview")
        fun bindImageFromUrlArray(view: ImageView, imageUrl: ArrayList<String>?) {
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(view.context)
                    .load(imageUrl[0])
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view)
            }
        }

        @JvmStatic
        @BindingAdapter("setImageIntoPreview")
        fun bindImageIntoTextPost(view: ImageView, imageUrl: ArrayList<String>?) {
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(view.context)
                    .load(imageUrl[0])
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view)
            } else {
                view.visibility = View.GONE
            }
        }

        @JvmStatic
        @BindingAdapter("checkMyLove")
        fun checkMyLoveFromPost(view: ImageView, myLove: Boolean) {
            if (myLove) {
                view.setImageDrawable(
                    ContextCompat.getDrawable(
                        view.context,
                        R.drawable.icons_love
                    )
                )
            } else {
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
        fun checkMyLikeFromPost(view: ImageView, myLike: Boolean) {
            if (myLike) {
                view.setImageDrawable(
                    ContextCompat.getDrawable(
                        view.context,
                        R.drawable.icons_like
                    )
                )
            } else {
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
        fun setTimeRelatively(view: TextView, dateAt: DateTime) {
            val currentTime = DateTime()
            val duration = Duration(dateAt, currentTime)

            val minutesDifference = duration.standardMinutes
            val equalDay = (
                    currentTime.year == dateAt.year &&
                            currentTime.dayOfYear == dateAt.dayOfYear
                    )
            if (minutesDifference <= 0L) {
                view.text = "지금"
            } else if (minutesDifference < 60) {
                view.text = "${minutesDifference}분 전"

            } else if (!equalDay) {
                view.text = SimpleDateFormat("yy.MM.dd").format(dateAt)

            } else {
                view.text = SimpleDateFormat("HH:mm").format(dateAt)

            }
        }
    }
}


object PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}

