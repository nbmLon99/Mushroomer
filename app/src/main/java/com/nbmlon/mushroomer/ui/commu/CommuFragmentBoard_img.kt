package com.nbmlon.mushroomer.ui.commu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.data.posts.PostsRepository
import com.nbmlon.mushroomer.databinding.FragmentCommuImageBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/** 사진 게시판 **/
class CommuFragmentBoard_img private constructor(): CommuBoardFragment() {
    companion object {
        const val TAG = "CommuFragmentBoard_image"
        @JvmStatic
        fun getInstance(boardTypeOrd: Int) =
            CommuFragmentBoard_img().apply {
                arguments = Bundle().apply {
                    putInt(BOARD_TYPE_ORDINAL, boardTypeOrd)
                }
            }
    }
    private var board_typd_idx: Int? = null
    private lateinit var mBoardType: BoardType
    private var _binding: FragmentCommuImageBinding? = null
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
        _binding = FragmentCommuImageBinding.inflate(layoutInflater)
        val viewModelFactory = BoardViewModelFactory(
            owner = this,
            repository = PostsRepository(),
            boardType =  mBoardType
        )

        val viewModel: ViewModelBoard by viewModels { viewModelFactory }
        bindView(
            boardType = mBoardType,
            sortGroup = binding.sortRadioGroup,
            boardGroup = null,
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
            btnBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
            //검색버튼
            btnSearch.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer, CommuFragment_search.getInstance(board_typd_idx!!), CommuFragment_search.TAG)
                    .addToBackStack(null)
                    .commit()
            }

            //글쓰기 버튼
            btnWrite.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer, CommuFragment_write.getInstance(board_typd_idx ?: 1), CommuFragment_write.TAG)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}