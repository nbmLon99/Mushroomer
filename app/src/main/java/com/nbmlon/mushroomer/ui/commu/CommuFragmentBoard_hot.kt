package com.nbmlon.mushroomer.ui.commu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.data.posts.PostsRepository
import com.nbmlon.mushroomer.databinding.FragmentCommuHotBinding

/**
 * 인기게시판
 */
class CommuFragmentBoard_hot private constructor(): CommuBoardFragment() {
    companion object {
        const val TAG = "CommuFragmentBoard_hot"

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun getInstance(param1: Int) =
            CommuFragmentBoard_hot().apply {
                arguments = Bundle().apply {
                    putInt(BOARD_TYPE_ORDINAL, param1)
                }
            }
    }

    private var board_typd_idx: Int? = null
    private lateinit var mBoardType: BoardType


    private var _binding: FragmentCommuHotBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            board_typd_idx = it.getInt(BOARD_TYPE_ORDINAL)
            mBoardType = BoardType.values()[board_typd_idx!!]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommuHotBinding.inflate(layoutInflater)
        val viewModelFactory = BoardViewModelFactory(
            owner = this,
            repository = PostsRepository(),
            boardType =  mBoardType
        )

        val viewModel: BoardViewModel by viewModels { viewModelFactory }
        bindView(
            boardType = mBoardType,
            adapter = AdapterBoardPost(boardType =mBoardType),
            searchBtn = binding.btnSearch,
            sortGroup = null,
            boardGroup = binding.boardRadioGroup,
            list = binding.postRV
        )

        bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}


/**
 *
 * val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
val radioButton1 = findViewById<RadioButton>(R.id.radio_button_1)
val radioButton2 = findViewById<RadioButton>(R.id.radio_button_2)

radioGroup.setOnCheckedChangeListener { _, checkedId ->
when (checkedId) {
R.id.radio_button_1 -> {
radioButton1.setTypeface(null, Typeface.BOLD)
radioButton1.paintFlags = radioButton1.paintFlags or Paint.UNDERLINE_TEXT_FLAG

radioButton2.setTypeface(null, Typeface.NORMAL)
radioButton2.paintFlags = radioButton2.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
}
R.id.radio_button_2 -> {
radioButton2.setTypeface(null, Typeface.BOLD)
radioButton2.paintFlags = radioButton2.paintFlags or Paint.UNDERLINE_TEXT_FLAG

radioButton1.setTypeface(null, Typeface.NORMAL)
radioButton1.paintFlags = radioButton1.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
}
}
}
 */