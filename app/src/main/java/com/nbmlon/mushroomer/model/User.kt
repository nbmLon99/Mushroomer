package com.nbmlon.mushroomer.model

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.nbmlon.mushroomer.utils.GlideApp


/**
 * @param id           user 구분자
 * @param icon              프로필 이미지
 * @param name              이름
 * @param nickname          닉네임
 * @param email             이메일
 */
data class User(
    val id: Int,
    val email: String,
    val name : String,
    val nickname: String,
    val icon: String,
    val phone_number: String,
    ){
    companion object {
        fun getDummy() : User {
            return User(-1,"rhfwleowkd77@naver.com","","nbmlon99","","010-2222-3333")
        }
    }

    fun getModifiedNickname(new_nickname: String) :User =
        User(
            id, email, name, new_nickname, icon, phone_number
        )
    fun getModifiedIcon(new_icon: String) :User =
        User(
            id, email, name, nickname, new_icon, phone_number
        )
}


class UserDataBindingAdapter{
    companion object{
        @JvmStatic
        @BindingAdapter("setUserIcon")
        fun bindReply(view: ImageView, url: String) {
            if (url.isNotBlank()) {
                GlideApp
                    .with(view.context)
                    .load(url)
                    .circleCrop()
                    .into(view)
            }
        }
    }
}