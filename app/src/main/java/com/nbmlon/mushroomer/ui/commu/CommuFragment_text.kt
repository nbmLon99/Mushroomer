package com.nbmlon.mushroomer.ui.commu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.FragmentCommuTextBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val BOARD_TYPE = "board_type"

/**
 자유게시판 / QnA 게시판 띄울 프래그먼트
 */
class CommuFragment_text : Fragment() {
    // TODO: Rename and change types of parameters
    private var type: Int? = null

    private var _binding: FragmentCommuTextBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt(BOARD_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_commu_text, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PostsFragment_text.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun getInstance(param1: Int) =
            CommuFragment_text().apply {
                arguments = Bundle().apply {
                    putInt(BOARD_TYPE, param1)
                }
            }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}


enum class BoardType{
    FreeBoard, QnABoard
}