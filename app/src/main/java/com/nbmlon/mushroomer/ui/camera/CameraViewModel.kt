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
import androidx.lifecycle.viewModelScope
import com.nbmlon.mushroomer.api.dto.AnalyzeResponse
import com.nbmlon.mushroomer.data.mushrooms.AnalyzeMushroomPictures
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraViewModel : ViewModel() {
    // MutableLiveData를 사용하여 분석 결과를 저장할 변수를 선언합니다.
    //    private val _analysisRequest = MutableLiveData<>
    private val _analysisResult = MutableLiveData<AnalyzeResponse>()
    private val _capturedImages: MutableLiveData<ArrayList<Bitmap>> = MutableLiveData(arrayListOf())

    val analysisResult: LiveData<AnalyzeResponse>
        get() = _analysisResult

    val capturedImages : LiveData<ArrayList<Bitmap>>
        get() = _capturedImages


    // 분석 작업을 수행하는 함수입니다.
    fun startAnalysis() {
        viewModelScope.launch(Dispatchers.IO){
            val result = AnalyzeMushroomPictures(capturedImages.value!!).analyzeMushroomPictures()
            delay(1000)
            withContext(Dispatchers.Main){
                _analysisResult.value = result
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun addPicture(image : ImageProxy){
        val bitmap = image.image?.toBitmap()
        val currentList = _capturedImages.value ?: arrayListOf()
        bitmap?.let { currentList.add(0, it) }
        _capturedImages.value = currentList
    }

    fun delPicture(id : Int){
        val currentList = _capturedImages.value ?: arrayListOf()
        try{
            currentList.removeAt(id)
            _capturedImages.value = currentList
        }catch (e : IndexOutOfBoundsException){
            e.printStackTrace()
        }catch (e : Exception){
            e.printStackTrace()
        }
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