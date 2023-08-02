package com.nbmlon.mushroomer.ui.camera

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AnalyzingViewModel : ViewModel() {
    // MutableLiveData를 사용하여 분석 결과를 저장할 변수를 선언합니다.
    private val _analysisResult = MutableLiveData<String>()
    val analysisResult: LiveData<String>
        get() = _analysisResult

    // 분석 작업을 수행하는 함수입니다.
    fun performAnalysis(pictureUris : ArrayList<Uri>) {
        // 여기서 실제 데이터 분석 작업을 수행하고 결과를 _analysisResult에 저장합니다.
        // 결과를 LiveData에 전달하여 UI에 반영될 수 있도록 합니다.
        val data = "테스트결과"
        _analysisResult.value = "분석 결과: $data"
    }
}