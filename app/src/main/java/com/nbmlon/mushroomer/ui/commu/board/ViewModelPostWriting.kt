package com.nbmlon.mushroomer.ui.commu.board

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nbmlon.mushroomer.model.Post

/** 글쓰기 사진을 위한 ViewModel **/
class ViewModelPostWriting :ViewModel(){
    private val _images : MutableLiveData<ArrayList<Uri>> = MutableLiveData(arrayListOf())
    val images get() = _images

    fun addUris(uris : ArrayList<Uri>){
        val currentList = _images.value ?: arrayListOf()
        _images?.let { currentList.addAll(uris) }
        _images.value = currentList
    }

    fun delUri(pos : Int){
        val currentList = _images.value ?: arrayListOf()
        _images?.let { currentList.removeAt(pos) }
        _images.value = currentList
    }


    /** 서버에 등록 요청 **/
    fun requestUpload(targetPost : Post?){

    }


}