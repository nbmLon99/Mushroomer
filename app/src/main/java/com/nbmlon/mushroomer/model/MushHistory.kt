package com.nbmlon.mushroomer.model

import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter
import org.joda.time.DateTime
import java.io.Serializable

data class MushHistory (
    val mushroom : Mushroom,
    val picPath : ArrayList<String>,
    val date : DateTime,
    val lat : Double,
    val lon : Double
) : Serializable{
    companion object{
        fun getDummy() = MushHistory(
            mushroom = Mushroom.getDummy(123,true),
            picPath = arrayListOf(),
            date = DateTime(),
            lat = 0.0,
            lon = 0.0

        )
    }
}

class MushHistoryDataAdapter(){

    companion object{
        @JvmStatic
        @BindingAdapter("discoverdAt")
        fun bindDateToDialog(view : TextView, date : DateTime){
            Log.d("널??", date.toString())
            view.text = date.toString("yy.MM.dd")
        }


        @JvmStatic
        @BindingAdapter("picturedAt")
        fun bindDateToPreview(view : TextView, date : DateTime){
            view.text = date.toString("yyyy년\nM월 d일")
        }
    }
}
