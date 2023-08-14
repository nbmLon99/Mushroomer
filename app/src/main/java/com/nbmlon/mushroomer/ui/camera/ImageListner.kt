package com.nbmlon.mushroomer.ui.camera

import android.net.Uri


/** PicturesAdapter에서 아이템 삭제 요구 **/
interface ImageListner {
    fun deleteImage(idx : Int)
}