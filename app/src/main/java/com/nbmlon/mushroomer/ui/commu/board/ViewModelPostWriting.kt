package com.nbmlon.mushroomer.ui.commu.board

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbmlon.mushroomer.data.posts.CommuWriteRepository
import com.nbmlon.mushroomer.domain.CommuWriteUseCaseRequest
import com.nbmlon.mushroomer.domain.CommuWriteUseCaseResponse
import com.nbmlon.mushroomer.model.MushHistory
import com.nbmlon.mushroomer.model.Post
import kotlinx.coroutines.launch

/** 글쓰기 사진을 위한 ViewModel **/
class ViewModelPostWriting :ViewModel(){
    private val _images : MutableLiveData<ArrayList<Uri>> = MutableLiveData(arrayListOf())
    val images get() = _images


    private val repository : CommuWriteRepository = CommuWriteRepository()
    private val _response = MutableLiveData<CommuWriteUseCaseResponse>()
    val response : LiveData<CommuWriteUseCaseResponse> = _response
    val request : (CommuWriteUseCaseRequest) -> Unit


    init {
        request = {request ->
            viewModelScope.launch {
                when(request){
                    //포스팅 등록
                    is CommuWriteUseCaseRequest.UploadPostDomain ->{
                        _response.value = repository.uploadPost(request)
                    }

                    //포스팅 수정
                    is CommuWriteUseCaseRequest.ModifiyPostDomain ->{
                        _response.value = repository.modifyPost(request)
                    }
                }
            }
        }
    }









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

}

/**
 * 포스팅 등록
 * 포스팅 수정
 */