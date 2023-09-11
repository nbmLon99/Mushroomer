package com.nbmlon.mushroomer.ui.commu.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbmlon.mushroomer.api.dto.CommuRequestDTO
import com.nbmlon.mushroomer.api.dto.CommuRequestDTO.DeleteDTO
import com.nbmlon.mushroomer.api.dto.CommuRequestDTO.ModifyDTO
import com.nbmlon.mushroomer.api.dto.CommuRequestDTO.ReportDTO
import com.nbmlon.mushroomer.api.dto.CommuRequestDTO.UploadDTO
import com.nbmlon.mushroomer.api.dto.CommuResponseDTO
import com.nbmlon.mushroomer.api.dto.CommuResponseDTO.SuccessResponseDTO
import com.nbmlon.mushroomer.data.posts.CommuPostRepository
import kotlinx.coroutines.launch

class ViewModelPost : ViewModel() {
    val repository = CommuPostRepository()

    val _response = MutableLiveData<CommuResponse>()
    val response : LiveData<CommuResponse> = _response

    val request : (CommuRequest) -> Unit

    init{
        request = { request ->
            viewModelScope.launch{
                val dto = request.dto
                val type = request.targetType
                when( request ) {
                    is CommuRequest.ForReport -> {
                        _response.value =  repository.report(type, dto)
                    }
                    is CommuRequest.ForDelete -> {
                        _response.value =  repository.delete(type, dto)
                    }
                    is CommuRequest.ForUpload -> {
                        _response.value =  repository.upload(type, dto)
                    }
                    is CommuRequest.ForModify -> {
                        _response.value =  repository.modify(type, dto)
                    }
                }
            }
        }
    }
}

enum class TargetType { POST, COMMENT }

sealed class CommuRequest{
    abstract val dto : CommuRequestDTO
    abstract val targetType : TargetType
    data class ForReport(override val targetType: TargetType, override val dto : ReportDTO) : CommuRequest()
    data class ForDelete(override val targetType: TargetType, override val dto : DeleteDTO) : CommuRequest()
    data class ForUpload(override val targetType: TargetType, override val dto : UploadDTO) : CommuRequest()
    data class ForModify(override val targetType: TargetType, override val dto : ModifyDTO) : CommuRequest()
}

sealed class CommuResponse{
    abstract val dto : CommuResponseDTO
    data class SuccessResponse(override val dto : SuccessResponseDTO) : CommuResponse()
}