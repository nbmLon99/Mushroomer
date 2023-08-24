package com.nbmlon.mushroomer.model

import android.graphics.Color
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.R
import org.joda.time.DateTime
import java.io.Serializable
import java.util.Date


/**
 * @param writer        작성자
 * @param content       댓글 내용
 * @param time          작성시간
 * @param writer        작성자
 * @param replies       답글 리스트 ( 코멘트 )
 */
data class Comment(
    val writer : User,
    val content : String,
    val time : DateTime,
    val replies : ArrayList<Comment>?,
) : Serializable{
    val isMine = writer == AppUser.user
}

class CommentDataBindingAdapter(){
    @BindingAdapter("checkMyComment")
    fun bindCommentBackground(linearLayout: LinearLayout, isMine : Boolean){
        if (isMine)
            linearLayout.setBackgroundColor(ContextCompat.getColor(linearLayout.context, R.color.CommentBg))
        else
            linearLayout.setBackgroundColor(ContextCompat.getColor(linearLayout.context, R.color.myCommentBg))

    }

}
