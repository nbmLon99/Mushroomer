package com.nbmlon.mushroomer.ui.commu.board

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.api.ResponseCodeConstants
import com.nbmlon.mushroomer.data.posts.PostsSearchRepository
import com.nbmlon.mushroomer.databinding.FragmentCommuSearchBinding
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.post.CommuFragment_post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 게시글 검색
 */
class CommuFragment_search private constructor(): Fragment(), PostClickListener {
    companion object {
        const val TAG = "CommuFragment_search"

        @JvmStatic
        fun getInstance(param1: Int) =
            CommuFragment_search().apply {
                arguments = Bundle().apply {
                    putInt(BOARD_TYPE_ORDINAL, param1)
                }
            }
    }
    // TODO: Rename and change types of parameters
    private var search_board_type_idx: Int? = null
    private lateinit var mBoardType: BoardType
    private var _binding: FragmentCommuSearchBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            search_board_type_idx = it.getInt(BOARD_TYPE_ORDINAL)
            search_board_type_idx?.let {idx->
                mBoardType = BoardType.values()[idx]
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCommuSearchBinding.inflate(layoutInflater)

//        val layoutManager = when (mBoardType) {
//            BoardType.PicBoard -> StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//            else -> LinearLayoutManager(requireContext())
//        }

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchAdapter =
            AdapterBoardPaging(boardType = mBoardType, cl = this@CommuFragment_search::openPost)
        binding.postRV.adapter = searchAdapter
        val viewModelFactory = SearchViewModelFactory(
            owner = this,
            repository = PostsSearchRepository(),
            boardType = mBoardType
        )
        val viewModel: ViewModelBoardSearch by viewModels { viewModelFactory }
        viewModel.loadedPosts.observe(viewLifecycleOwner){
            if(it.success){
                val filteredPosts = it.posts.filter { p -> p.boardType == mBoardType }
                if(filteredPosts.isEmpty()){
                    binding.emptyList.visibility = View.VISIBLE
                    binding.postRV.visibility = View.GONE
                }else{
                    binding.emptyList.visibility = View.GONE
                    binding.postRV.visibility = View.VISIBLE
                    searchAdapter.submitList(filteredPosts)
                }
            }else{
                binding.emptyList.visibility = View.VISIBLE
                binding.postRV.visibility = View.GONE
                if(it.code == ResponseCodeConstants.NETWORK_ERROR_CODE){
                    binding.tvError.text = getString(R.string.network_error_msg)
                }else{
                    binding.tvError.text = getString(R.string.error_msg)
                }
            }
        }

        binding.apply {
            btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            btnSearch.setOnClickListener {
                if(etSearch.text.isNotEmpty()){
                    viewModel.searchPosts(etSearch.text.toString())
                }
            }
            etSearch.requestFocus()
            (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT)
        }


    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    override fun openPost(post: Post) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(
                R.id.FragmentContainer,
                CommuFragment_post.getInstance(post),
                CommuFragment_post.TAG
            )
            .addToBackStack(null)
            .commit()
    }
}