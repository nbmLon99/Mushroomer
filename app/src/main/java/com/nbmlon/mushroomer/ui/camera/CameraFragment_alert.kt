package com.nbmlon.mushroomer.ui.camera


import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.nbmlon.mushroomer.databinding.FragmentCameraAlertBinding

class CameraFragment_alert private constructor(): DialogFragment() {

    companion object {
        const val TAG = "CameraFragment_alert"
        const val ITEM_COUNT = "itemCount"
        const val START_ANALYZE_LISTENER = "AnalyzeStartListener"

        @JvmStatic
        fun getInstance(item_count : Int, sal : AnalyzeStartListener) =
            CameraFragment_alert().apply {
                arguments = Bundle().apply {
                    putInt(ITEM_COUNT, item_count)
                    putSerializable(START_ANALYZE_LISTENER, sal as AnalyzeStartListener )
                }
            }
    }

    private var itemCount = -1
    private lateinit var startListener : AnalyzeStartListener
    private lateinit var binding : FragmentCameraAlertBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemCount = it.getInt(ITEM_COUNT, -1)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                startListener = it.getSerializable(START_ANALYZE_LISTENER, AnalyzeStartListener::class.java)!!
            }else{
                startListener = it.getSerializable(START_ANALYZE_LISTENER) as AnalyzeStartListener
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentCameraAlertBinding.inflate(layoutInflater)
        binding.apply {
            picSize = itemCount
            btnCancel.setOnClickListener { this@CameraFragment_alert.dismiss() }
            btnAnalyze.setOnClickListener {
                startListener.startAnalyze()
                this@CameraFragment_alert.dismiss()
            }
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(root)
            return builder.create()
        }
    }
}