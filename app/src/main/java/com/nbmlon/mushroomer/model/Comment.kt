package com.nbmlon.mushroomer.model

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.R
import org.joda.time.DateTime
import java.io.Serializable


/**
 * @param writer        작성자
 * @param content       댓글 내용
 * @param time          작성시간
 * @param writer        작성자
 * @param replies       답글 리스트 ( 코멘트 )
 */
data class Comment(
    val id : Int,
    val boardId : Int = -1,
    val writer : User,
    var content : String,
    val time : DateTime,
    val replies : ArrayList<Comment>?,
    var thumbsUpCount : Int = 0,
    var myThumbsUp : Boolean = false,
    ): Serializable {
    companion object {
        fun getDummyswithReplies(): ArrayList<Comment> {
            val replies = arrayListOf<Comment>()
            for (i in 1..2)
                replies.add(getDummy())

            val comments = arrayListOf<Comment>()
            for (i in 1..3)
                comments.add(Comment(i,i,User.getDummy(), "댓글 예시", DateTime(), replies))
            return comments
        }

        fun getDummy(): Comment {
            return Comment(0,0,User.getDummy(), "댓글 예시", DateTime(), null)
        }
    }

    val isMine = (writer == AppUser.user)
}
class CommentDataBindingAdapter{
    companion object{
        @JvmStatic
        @BindingAdapter("checkMyComment")
        fun bindCommentBackground(linearLayout: LinearLayout, isMine : Boolean){
            if (isMine)
                linearLayout.setBackgroundColor(ContextCompat.getColor(linearLayout.context, R.color.CommentBg))
            else
                linearLayout.setBackgroundColor(ContextCompat.getColor(linearLayout.context, R.color.myCommentBg))
        }

        @JvmStatic
        @BindingAdapter("setLikeVisible")
        fun bindLikeVisibility(iv : ImageView, count : Int){
            if( count <= 0)
                iv.visibility = View.GONE
            else
                iv.visibility = View.VISIBLE
        }
        @JvmStatic
        @BindingAdapter("setLikeText")
        fun bindLikeText(tv : TextView, count : Int){
            if( count <= 0)
                tv.visibility = View.GONE
            else{
                tv.visibility = View.VISIBLE
                tv.text = count.toString()
            }

        }
    }


}
class CommentDiffCallback : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem == newItem
    }
}
