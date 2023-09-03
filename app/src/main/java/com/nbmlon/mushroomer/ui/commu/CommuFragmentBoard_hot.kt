package com.nbmlon.mushroomer.ui.commu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.R
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
            btnSearch.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.FragmentContainer, CommuFragment_search.getInstance(board_typd_idx!!), CommuFragment_search.TAG)
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}