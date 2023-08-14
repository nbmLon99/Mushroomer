package com.nbmlon.mushroomer.ui.camera


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.nbmlon.mushroomer.databinding.FragmentCameraAlertBinding

class CameraFragment_alert : DialogFragment() {

    companion object {
        const val TAG = "CameraFragment_alert"
        const val ITEM_COUNT = "itemCount"
    }

    private var itemCount = -1
    private lateinit var binding : FragmentCameraAlertBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemCount = it.getInt(ITEM_COUNT, -1)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentCameraAlertBinding.inflate(layoutInflater)
        binding.apply {
            picSize = itemCount
            btnCancel.setOnClickListener { this@CameraFragment_alert.dismiss() }
            btnAnalyze.setOnClickListener { (parentFragment as AnalyzeStartListener).startAnalyze() }
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(root)
            return builder.create()
        }
    }
}