package com.nbmlon.mushroomer.ui.camera

import java.io.Serializable


/** CameraFragment_alert 다이얼로그에서 검사 진행을 클릭 **/
fun interface AnalyzeStartListener : Serializable {
    fun startAnalyze()
}