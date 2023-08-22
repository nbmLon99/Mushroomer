package com.nbmlon.mushroomer.ui.commu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.FragmentCommuImageBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/** 사진 게시판 **/
class CommuFragmentBoard_image : Fragment() {
    // TODO: Rename and change types of parameters
    private var boardType: Int? = null
    private var _binding: FragmentCommuImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            boardType = it.getInt(BOARD_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommuImageBinding.inflate(LayoutInflater.from(context))
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
            //검색버튼
            btnSearch.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer, CommuFragment_search())
                    .addToBackStack(null)
                    .commit()
            }

            //글쓰기 버튼
            btnWrite.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer, CommuFragment_write.getInstance(boardType ?: 1))
                    .addToBackStack(null)
                    .commit()
            }

            bindSort()
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(param1: Int) =
            CommuFragmentBoard_image().apply {
                arguments = Bundle().apply {
                    putInt(BOARD_TYPE, param1)
                }
            }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun FragmentCommuImageBinding.bindSort(){

    }

}