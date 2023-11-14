package com.nbmlon.mushroomer.ui.commu.board

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.data.posts.BoardPostsRepository
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
        bindView(
            boardType = mBoardType,
            list = binding.postRV
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = BoardViewModelFactory(
            owner = this,
            repository = BoardPostsRepository(),
            boardType =  mBoardType
        )

        val viewModel: ViewModelBoard by viewModels { viewModelFactory }

        viewModel.loadedPosts.observe(viewLifecycleOwner){
            if(it.success){
                binding.emptyList.visibility = View.GONE
                binding.postRV.visibility = View.VISIBLE
                if(it.posts.isNotEmpty())
                    (binding.postRV.adapter as AdapterBoardPaging).submitList(it.posts)
            }else {
                binding.emptyList.visibility = View.VISIBLE
                binding.postRV.visibility = View.GONE
                if(it.code == NETWORK_ERROR_CODE){
                    binding.tvError.text = getString(R.string.network_error_msg)
                }else{
                    binding.tvError.text = getString(R.string.error_msg)
                }
            }
        }
        viewModel.fetchPosts(BoardType.HotBoard)

        binding.apply {
            btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            btnSearch.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer,
                        CommuFragment_search.getInstance(board_typd_idx!!),
                        CommuFragment_search.TAG
                    )
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