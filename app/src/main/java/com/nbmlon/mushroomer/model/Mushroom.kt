package com.nbmlon.mushroomer.model

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nbmlon.mushroomer.R
import org.joda.time.DateTime
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class MushType{
    EDIBLE,
    POISON
}


/**
 * @param dogamNo   도감 넘버
 * @param imageUrl  사진 url
 * @param name      버섯 이름
 * @param feature   특징
 * @param type      버섯 타입 ( 독버섯, 식용버섯 )
 * @param rarity    희귀도
 * @param myHistory 나의 발견 사진 path (path에 날짜 저장 예정) -> 0개일 시 발견 못한 버섯으로 취급
 */
data class Mushroom (
        val dogamNo : Int,
        val imageUrl : String,
        val name : String,
        val feature : String,
        val type : MushType,
        val rarity : Long,
        val myHistory : ArrayList<MushHistory>
        ) : Serializable{
    companion object {
        /** n(도감넘버), gotcha(발견 여부) 지정하여 더미데이터 생성 **/
        fun getDummy(n :Int, gotcha : Boolean, name: String? = null) : Mushroom{
            val mushName = name ?: "${n}번쨰 버섯"
            val mush = Mushroom(n,"", mushName,"설명입니다.", MushType.EDIBLE,20L,ArrayList())
            if(gotcha){ mush.myHistory.add(MushHistory(ArrayList(), DateTime(), 0,0)) }
            return mush
        }
    }
    val gotcha : Boolean get() =  myHistory.size > 0
}

class MushDataBindingAdapter{
    companion object{
        @JvmStatic
        @BindingAdapter("imageFromUrl")
        fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(view.context)
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(view)
            }
        }

        @JvmStatic
        @BindingAdapter("isDiscovered")
        fun bindIsGone(view: ImageView, gotcha: Boolean) {
            if (gotcha) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
            }
        }



        @JvmStatic
        @BindingAdapter("setMushType")
        fun bindMushType(view: TextView, type: MushType) {
            when(type){
                MushType.EDIBLE-> view.setText(R.string.typeEat)
                MushType.POISON-> view.setText(R.string.typePoison)
            }
        }

        @JvmStatic
        @BindingAdapter("picturedAt")
        fun bindDate(view : TextView, date : DateTime){
            view.text = date.toString("yyyy년\nM월 d일")
        }
    }

}
