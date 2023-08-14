package com.nbmlon.mushroomer.ui.camera

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nbmlon.mushroomer.R
import java.nio.ByteBuffer

class CameraViewModel : ViewModel() {
    // MutableLiveData를 사용하여 분석 결과를 저장할 변수를 선언합니다.
    private val _analysisResult = MutableLiveData<String>()
    private val _capturedImages: MutableLiveData<ArrayList<ImageProxy>> = MutableLiveData(arrayListOf())

    val analysisResult: LiveData<String>
        get() = _analysisResult

    val capturedImages : LiveData<ArrayList<ImageProxy>>
        get() = _capturedImages


    // 분석 작업을 수행하는 함수입니다.
    fun startAnalysis() {
        // 여기서 실제 데이터 분석 작업을 수행하고 결과를 _analysisResult에 저장합니다.
        // 결과를 LiveData에 전달하여 UI에 반영될 수 있도록 합니다.
        val data = "테스트결과"
        _analysisResult.value = "분석 결과: $data"
    }

    fun saveProxyPicture(image : ImageProxy){
        _capturedImages.value?.add(image)
    }
    fun delProxyPicture(idx : Int){
        _capturedImages.value?.removeAt(idx)
    }


    private fun saveImages(images: List<ImageProxy>) {
        for (image in images) {
            val buffer: ByteBuffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.capacity())
            buffer.get(bytes)
            image.close()

            // Here you can save the bytes to your storage, or do whatever you want with them.
            // Save them to your PicturesAdapter when you're ready.
        }

        // Clear the capturedImages list after processing
        _capturedImages.value = arrayListOf()
    }
}