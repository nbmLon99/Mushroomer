package com.nbmlon.mushroomer.ui.camera

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nbmlon.mushroomer.api.dto.AnalyzeResponse
import com.nbmlon.mushroomer.databinding.FragmentCameraResultBinding

private const val ANALYZE_RESULT = "analyze_result"

/**
 * 검사결과
 */
class CameraFragment_result private constructor(): DialogFragment() {
    companion object {
        const val TAG: String = "CameraFragment_result"
        @JvmStatic
        fun getInstance(response : AnalyzeResponse) =
            CameraFragment_result().apply {
                arguments = Bundle().apply {
                    putSerializable(ANALYZE_RESULT, response)
                }
            }
    }

    private var response : AnalyzeResponse? = null

    private var _binding : FragmentCameraResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                response = it.getSerializable(ANALYZE_RESULT, AnalyzeResponse::class.java)
            }else{
                response = it.getSerializable(ANALYZE_RESULT) as? AnalyzeResponse
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCameraResultBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnClose.setOnClickListener { this@CameraFragment_result.dismiss() }
            if( response == null){
                failFrame.visibility = View.VISIBLE
                successFrame.visibility = View.GONE
            }
            else{
                analyzeResult = response
            }
        }
    }
    override fun onStart() {
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        super.onStart()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}