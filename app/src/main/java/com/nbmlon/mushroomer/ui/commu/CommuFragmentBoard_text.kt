package com.nbmlon.mushroomer.ui.commu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.FragmentCommuTextBinding
import com.nbmlon.mushroomer.databinding.ItemCommuHomeTextBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 자유게시판 / QnA 게시판 띄울 프래그먼트
 */
class CommuFragmentBoard_text : Fragment() {
    companion object {
        const val TAG = "CommuFragment_text"
        @JvmStatic
        fun getInstance(param1: Int) =
            CommuFragmentBoard_text().apply {
                arguments = Bundle().apply {
                    putInt(BOARD_TYPE, param1)
                }
            }
    }



    // TODO: Rename and change types of parameters
    private  var boardType: Int? = null
    private var _binding: FragmentCommuTextBinding? = null
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
        _binding = FragmentCommuTextBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            boardTitle.text = if (boardType == BoardType.FreeBoard.ordinal) getString(R.string.FreeBoard) else getString(R.string.QnABoard)
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun FragmentCommuTextBinding.bindSort(){

    }


}

