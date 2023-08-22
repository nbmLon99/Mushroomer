package com.nbmlon.mushroomer.ui.commu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nbmlon.mushroomer.databinding.FragmentCommuWriteBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val WRITE_BOARD_TYPE = "type"

class CommuFragment_write : Fragment() {
    // TODO: Rename and change types of parameters
    private var caller_type: Int? = null

    private var _binding: FragmentCommuWriteBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            caller_type = it.getInt(WRITE_BOARD_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommuWriteBinding.inflate(LayoutInflater.from(context))


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }
    }

    companion object {
        const val TAG = "CommuFragment_write"
        @JvmStatic
        fun getInstance(param1: Int) =
            CommuFragment_write().apply {
                arguments = Bundle().apply {
                    putInt(WRITE_BOARD_TYPE, param1)
                }
            }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}