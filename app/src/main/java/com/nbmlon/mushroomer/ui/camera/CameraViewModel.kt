package com.nbmlon.mushroomer.ui.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Environment
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraViewModel : ViewModel() {
    // MutableLiveData를 사용하여 분석 결과를 저장할 변수를 선언합니다.
    // private val _analysisRequest = MutableLiveData<>
    private val _analysisResult = MutableLiveData<String>()
    private val _capturedImages: MutableLiveData<ArrayList<Bitmap>> = MutableLiveData(arrayListOf())


    val analysisResult: LiveData<String>
        get() = _analysisResult

    val capturedImages : LiveData<ArrayList<Bitmap>>
        get() = _capturedImages


    // 분석 작업을 수행하는 함수입니다.
    fun startAnalysis() {
        // 여기서 실제 데이터 분석 작업을 수행하고 결과를 _analysisResult에 저장합니다.
        // 결과를 LiveData에 전달하여 UI에 반영될 수 있도록 합니다.
        val data = "테스트결과"
        _analysisResult.value = "분석 결과: $data"
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun addPicture(image : ImageProxy){
        val bitmap = image.image?.toBitmap()
        val currentList = _capturedImages.value ?: arrayListOf()
        bitmap?.let { currentList.add(0, it) }
        _capturedImages.value = currentList
    }
    fun delPicture(idx : Int){
        val currentList = _capturedImages.value ?: arrayListOf()
        currentList.removeAt(idx)
        _capturedImages.value = currentList
    }


    suspend fun savePictureFromBitmaps(context: Context) : Boolean{
        val timestampFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val timestamp = timestampFormat.format(Date())
        var success = true
        capturedImages.value?.let {
            for ((idx, bitmap) in it.withIndex()){
                val filename = "${timestamp}_${idx}"
                val result = savePictureFromBitmap(context, bitmap, filename)
                if(!result){
                    success = false
                }
            }
        }
        return success
    }
    fun clearImages() {
        _capturedImages.value = arrayListOf()
    }

    suspend fun savePictureFromBitmap(context: Context, bitmap: Bitmap, filename: String): Boolean {
        return withContext(Dispatchers.IO) {
            val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File(directory, filename)

            var success = false
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                Log.d("성공",file.path)
                success = true
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                fos?.close()
            }
            success
        }
    }


    private fun Image.toBitmap(): Bitmap {
        val buffer = planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }


}