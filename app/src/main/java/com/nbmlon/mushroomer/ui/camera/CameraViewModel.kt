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
import com.nbmlon.mushroomer.api.ResponseCodeConstants.BITMAP_SAVE_ERROR
import com.nbmlon.mushroomer.data.analyze.AnalyzeRepository
import com.nbmlon.mushroomer.data.analyze.AnalyzerTF
import com.nbmlon.mushroomer.domain.AnalyzeUseCaseRequest
import com.nbmlon.mushroomer.domain.AnalyzeUseCaseResponse
import com.nbmlon.mushroomer.model.MushHistory
import com.nbmlon.mushroomer.model.Mushroom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraViewModel : ViewModel() {
    private val repository = AnalyzeRepository()
    // MutableLiveData를 사용하여 분석 결과를 저장할 변수를 선언합니다.
    //    private val _analysisRequest = MutableLiveData<>
    private val _response = MutableLiveData<AnalyzeUseCaseResponse>()
    val response: LiveData<AnalyzeUseCaseResponse> get() = _response


    private val _capturedImages: MutableLiveData<ArrayList<Bitmap>> = MutableLiveData(arrayListOf())
    val capturedImages : LiveData<ArrayList<Bitmap>> get() = _capturedImages


    // 분석 작업을 수행하는 함수입니다.
    fun startAnalysis() {
        viewModelScope.launch {
            _response.value = repository.analyze(AnalyzeUseCaseRequest.AnalyzeRequestDomain(capturedImages.value!!))
        }
    }



    /** 분석 성공 -> 사진, MushHistory 저장 **/
    fun onSuccessAnalyze(context: Context, mush : Mushroom, lat : Double?, lon : Double?){
        viewModelScope.launch(Dispatchers.Default) {
            val timestampFormat = SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault())
            val timestamp = timestampFormat.format(Date())
            var success = true
            val paths = arrayListOf<String>()

            //사진 저장 시도
            capturedImages.value?.let {
                for ((idx, bitmap) in it.withIndex()){
                    val filename = "${timestamp}_${idx}"
                    val result = savePictureFromBitmap(context, bitmap, filename)
                    if(result == null){
                        success = false
                    }else {
                        paths.add(result)
                    }
                }
            }

            //사진 저장 성공 -> History 저장
            if(success){
                saveHistory(mush, paths, lat, lon)
                clearImages()
            }
            //사진 저장 실패
            else{
                _response.value = AnalyzeUseCaseResponse.SuccessResponseDomain(false, BITMAP_SAVE_ERROR)
            }
        }
    }




    fun clearImages() {
        _capturedImages.value = arrayListOf()
    }

    /** 사진 저장 시도 **/
    private suspend fun savePictureFromBitmap(context: Context, bitmap: Bitmap, filename: String): String? {
        return withContext(Dispatchers.IO) {
            val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File(directory, filename)

            var path : String? = null
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                Log.d("성공",file.path)
                path = file.path
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                fos?.close()
            }
            path
        }
    }
    private fun saveHistory(mush : Mushroom, paths : ArrayList<String>, lat : Double?, lon : Double?){
        val history_for_save = MushHistory(
            mushroom = mush,
            picPath = paths,
            date = DateTime(),
            lat = lat,
            lon = lon
        )
        viewModelScope.launch{
            _response.value = repository.saveHistory(AnalyzeUseCaseRequest.SaveHistoryDomain(history_for_save))
        }
    }

    private fun Image.toBitmap(): Bitmap {
        val buffer = planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
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

}


private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
