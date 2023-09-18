package com.nbmlon.mushroomer.data.analyze

import android.graphics.Bitmap
import android.util.Log
import com.nbmlon.mushroomer.domain.AnalyzeUseCaseRequest
import com.nbmlon.mushroomer.ml.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject


internal class AnalyzerTF(private val domain: AnalyzeUseCaseRequest.AnalyzeRequestDomain) {
    @Inject
    lateinit var model: Model

    suspend fun getResponseTF() : ResponseTF?{
        val candidate = arrayListOf<ResponseTF>()
        domain.pictures.forEach { bitmap ->
            val imageBitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, false)
            candidate.add(classifyImage(imageBitmap))
        }
        return candidate.maxByOrNull { it.accuracy }
    }


    private suspend fun classifyImage( image: Bitmap) : ResponseTF {
        return withContext(Dispatchers.Default){


            // 이미지의 rgb값을 담을 bytebuffer 객체 생성.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
            var byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder()) // 바이트 순서를 해당 시스템의 네이티브 바이트 순서로 설정

            val intValues = IntArray(imageSize * imageSize)
            image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0

            // RGB 값을 ByteBuffer에 추가
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val value = intValues[pixel++]
                    byteBuffer.putFloat(((value shr 16) and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat(((value shr 8) and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((value and 0xFF) * (1f / 255f))
                }
            }

            // 이미지의 가로와 세로 크기 설정
            val width = imageSize
            val height = imageSize

            // ByteBuffer를 rewind하여 처음부터 읽을 수 있도록 준비
            byteBuffer.rewind()

            // 비어있는 Bitmap 생성
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            // ByteBuffer에서 픽셀 값을 읽어와 Bitmap에 설정
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val r = (byteBuffer.float * 255).toInt() and 0xFF
                    val g = (byteBuffer.float * 255).toInt() and 0xFF
                    val b = (byteBuffer.float * 255).toInt() and 0xFF
                    val pixel = 0xFF shl 24 or (r shl 16) or (g shl 8) or b
                    bitmap.setPixel(x, y, pixel)
                }
            }
            // 이미지 로드(model실행)
            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            val confidences = outputFeature0.floatArray
            var maxPos = 0 // 가장 높은 정확도 인덱스 값
            var maxConfidence = 0.0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }
            var Confidence = confidences[maxPos] * 100

            val classes = arrayOf(
                "가닥버섯", "갓버섯", "계란모자버섯", "고무버섯", "곰보버섯",
                "광대버섯", "국수버섯", "굴뚝버섯", "굽은꽃애기버섯", "그물버섯", "기와버섯", "긴뿌리버섯",
                "까치버섯", "깔때기버섯", "꼭지버섯", "꽃송이버섯", "꾀꼬리버섯", "나팔버섯", "난버섯", "노란다발버섯", "노란띠버섯", "노루궁댕이버섯",
                "눈물버섯", "느타리버섯", "능이버섯", "달걀버섯", "덕다리버섯", "말똥버섯", "말불버섯", "말징버섯", "망태버섯", "먹물버섯", "목이버섯", "못버섯",
                "무당버섯", "미치광이버섯", "방패버섯", "배꼽버섯", "벚꽃버섯", "비늘버섯", "비단털버섯", "사슴뿔버섯", "송이버섯", "싸리버섯", "알버섯", "양송이버섯",
                "영지버섯", "우단버섯", "잎새버섯", "치마버섯", "팽이버섯", "표고버섯", "화경버섯"
            )

            Log.d("Predicted Label", maxPos.toString()) // maxPos = 정확도가 가장 높은 index
            Log.d("Predicted Label", classes[maxPos]) // 예측한 버섯 이름
            Log.d("Predicted Label", Confidence.toString()) // 정확도

            // 예측 확인용
            val outputValues = FloatArray(outputFeature0.shape[1]) // Assuming shape[0] is 1
            outputFeature0.buffer.rewind() // Rewind the buffer
            outputFeature0.buffer.asFloatBuffer().get(outputValues)

            Log.d("Output", "Predicted values: ${outputValues.joinToString(", ")}")


            ResponseTF(mushId = maxPos, accuracy = Confidence.toInt())
        }
    }


}

data class ResponseTF(
    val mushId : Int,
    val accuracy : Int
)


/*** **/

private const val imageSize = 224