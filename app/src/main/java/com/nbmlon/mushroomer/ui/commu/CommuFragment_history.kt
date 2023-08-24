package com.nbmlon.mushroomer.ui.commu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nbmlon.mushroomer.databinding.FragmentCommuHistoryBinding

/**
     내 댓글, 내 포스트 띄워주는 창
 */
class CommuFragment_history private constructor():  Fragment() {
    // TODO: Rename and change types of parameters
    private var board_type_idx: Int? = null
    companion object {
        const val TAG = "CommuFragment_history"
        @JvmStatic
        fun getInstance(bt: BoardType) =
            CommuFragment_history().apply {
                arguments = Bundle().apply {
                    putInt(BOARD_TYPE_ORDINAL, bt.ordinal)
                }
            }
    }


    var _binding : FragmentCommuHistoryBinding? = null
    val binding get() = _binding!!


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
        _binding = FragmentCommuHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            var boardType : BoardType = BoardType.values().get(board_type_idx!!)
            boardTitle.text = resources.getString(boardType.boardNameResId)
            historyRV.adapter = AdapterBoardPost(boardType)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}