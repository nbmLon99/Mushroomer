package com.nbmlon.mushroomer.ui.commu.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbmlon.mushroomer.api.dto.CommuPostRequestDTO
import com.nbmlon.mushroomer.api.dto.CommuPostRequestDTO.DeleteDTO
import com.nbmlon.mushroomer.api.dto.CommuPostRequestDTO.ModifyDTO
import com.nbmlon.mushroomer.api.dto.CommuPostRequestDTO.ReportDTO
import com.nbmlon.mushroomer.api.dto.CommuPostRequestDTO.UploadDTO
import com.nbmlon.mushroomer.api.dto.CommuPostResponseDTO
import com.nbmlon.mushroomer.api.dto.CommuPostResponseDTO.SuccessResponseDTO
import com.nbmlon.mushroomer.data.posts.CommuPostRepository
import kotlinx.coroutines.launch

class ViewModelPost : ViewModel() {
    private val repository = CommuPostRepository()

    val _response = MutableLiveData<CommuPostResponse>()
    val response : LiveData<CommuPostResponse> = _response

    val request : (CommuPostRequest) -> Unit

    init{
        request = { request ->
            viewModelScope.launch{
                val dto = request.dto
                val type = request.targetType
                when( request ) {
                    is CommuPostRequest.ForReport -> {
                        _response.value =  repository.report(type, dto)
                    }
                    is CommuPostRequest.ForDelete -> {
                        _response.value =  repository.delete(type, dto)
                    }
                    is CommuPostRequest.ForUpload -> {
                        _response.value =  repository.upload(type, dto)
                    }
                    is CommuPostRequest.ForModify -> {
                        _response.value =  repository.modify(type, dto)
                    }
                }
            }
        }
    }
}

enum class TargetType { POST, COMMENT }

sealed class CommuPostRequest{
    abstract val dto : CommuPostRequestDTO
    abstract val targetType : TargetType
    data class ForReport(override val targetType: TargetType, override val dto : ReportDTO) : CommuPostRequest()
    data class ForDelete(override val targetType: TargetType, override val dto : DeleteDTO) : CommuPostRequest()
    data class ForUpload(override val targetType: TargetType, override val dto : UploadDTO) : CommuPostRequest()
    data class ForModify(override val targetType: TargetType, override val dto : ModifyDTO) : CommuPostRequest()
}

sealed class CommuPostResponse{
    abstract val dto : CommuPostResponseDTO
    data class SuccessResponse(override val dto : SuccessResponseDTO) : CommuPostResponse()
}