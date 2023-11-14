package com.nbmlon.mushroomer.ui.commu.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.api.ResponseCodeConstants
import com.nbmlon.mushroomer.data.posts.BoardPostsRepository
import com.nbmlon.mushroomer.databinding.FragmentCommuTextBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 자유게시판 / QnA 게시판 띄울 프래그먼트
 */
class CommuFragmentBoard_text private constructor(): CommuBoardFragment() {
    companion object {
        const val TAG = "CommuFragment_text"
        @JvmStatic
        fun getInstance(param1: Int) =
            CommuFragmentBoard_text().apply {
                arguments = Bundle().apply {
                    putInt(BOARD_TYPE_ORDINAL, param1)
                }
            }
    }


    private var board_type_idx: Int? = null
    private lateinit var mBoardType : BoardType
    private var _binding: FragmentCommuTextBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            board_type_idx = it.getInt(BOARD_TYPE_ORDINAL)
            mBoardType = BoardType.values()[board_type_idx!!]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommuTextBinding.inflate(layoutInflater)
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
        bindView(
            boardType = mBoardType,
            list = binding.postRV
        )
        viewModel.loadedPosts.observe(viewLifecycleOwner){
            if(it.success){
                binding.emptyList.visibility = View.GONE
                binding.postRV.visibility = View.VISIBLE
                if(it.posts.isNotEmpty())
                    (binding.postRV.adapter as AdapterBoardPaging).submitList(it.posts)
            }else {
                binding.emptyList.visibility = View.VISIBLE
                binding.postRV.visibility = View.GONE
                if(it.code == ResponseCodeConstants.NETWORK_ERROR_CODE){
                    binding.tvError.text = getString(R.string.network_error_msg)
                }else{
                    binding.tvError.text = getString(R.string.error_msg)
                }
            }
        }
        viewModel.fetchPosts(mBoardType)


        binding.apply {
            boardTitle.text = if (board_type_idx == BoardType.FreeBoard.ordinal) getString(R.string.FreeBoard) else getString(R.string.QnABoard)
            btnBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
            //검색버튼
            btnSearch.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer,
                        CommuFragment_search.getInstance(board_type_idx!!),
                        CommuFragment_search.TAG
                    )
                    .addToBackStack(null)
                    .commit()
            }

            //글쓰기 버튼
            btnWrite.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer,
                        CommuFragment_write.getInstance(board_type_idx ?: 1),
                        CommuFragment_write.TAG
                    )
                    .addToBackStack(null)
                    .commit()
            }
            sortRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                if(viewModel.loadedPosts.value?.success == true){
                    when(checkedId){
                        R.id.sort_time->
                            (binding.postRV.adapter as AdapterBoardPaging).submitList(viewModel.loadedPosts.value!!.posts.sortedByDescending { it.time })

                        R.id.sort_like->
                            (binding.postRV.adapter as AdapterBoardPaging).submitList(viewModel.loadedPosts.value!!.posts.sortedByDescending { it.ThumbsUpCount })
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


}

