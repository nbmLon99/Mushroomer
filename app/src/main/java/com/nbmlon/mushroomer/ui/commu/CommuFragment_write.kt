package com.nbmlon.mushroomer.ui.commu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nbmlon.mushroomer.databinding.FragmentCommuWriteBinding

class CommuFragment_write private constructor() : Fragment() {
    companion object {
        const val TAG = "CommuFragment_write"
        @JvmStatic
        fun getInstance(param1: Int) =
            CommuFragment_write().apply {
                arguments = Bundle().apply {
                    putInt(BOARD_TYPE_ORDINAL, param1)
                }
            }
    }
    // TODO: Rename and change types of parameters
    private var board_type_idx : Int? = null

    private var _binding: FragmentCommuWriteBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            board_type_idx = it.getInt(BOARD_TYPE_ORDINAL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommuWriteBinding.inflate(layoutInflater)


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            btnUpload.setOnClickListener { TODO("등록") }
            btnAddPic.setOnClickListener { TODO("사진 추가") }
        }
    }



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}