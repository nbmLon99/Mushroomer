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

        val viewModelFactory = SearchViewModelFactory(
            owner = this,
            repository = PostsSearchRepository(),
            boardType = mBoardType
        )
        val viewModel: ViewModelBoardSearch by viewModels { viewModelFactory }

        binding.bindState(
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
            etSearch.requestFocus()
            (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT)
        }
    }



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    private fun FragmentCommuSearchBinding.bindState(
        uiState: StateFlow<SearchUiState>,
        pagingData: Flow<PagingData<Post>>,
        uiActions: (SearchUiAction) -> Unit,
    ){
        val searchAdapter =
            AdapterBoardPaging(boardType = mBoardType, cl = this@CommuFragment_search::openPost)
        val layoutManager = when (mBoardType) {
            BoardType.PicBoard -> StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            else -> LinearLayoutManager(requireContext())
        }
        postRV.adapter = searchAdapter
        postRV.layoutManager = layoutManager

        bindList(
            uiState = uiState,
            adapter = searchAdapter,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )

        bindSearch(
            uiState = uiState,
            onSearchBtnClicked = uiActions
        )

    }

    private fun FragmentCommuSearchBinding.bindList(
        uiState: StateFlow<SearchUiState>,
        adapter : AdapterBoardPaging,
        pagingData: Flow<PagingData<Post>>,
        onScrollChanged: (SearchUiAction.Scroll) -> Unit
    ){
        postRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(SearchUiAction.Scroll(uiState.value.query))
            }
        })
        //Flow 생성
        val notLoading = adapter.loadStateFlow
            // Only emit when REFRESH LoadState for the paging source changes.
            .distinctUntilChangedBy { it.source.refresh }
            // Only react to cases where REFRESH completes i.e., NotLoading.
            .map {
                it.source.refresh is LoadState.NotLoading
            }

        //Flow 생성
        val hasNotScrolledForCurrentSort = uiState
            .map {
                it.hasNotScrolledForCurrentQuery
            }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSort,
            Boolean::and
        )
            .distinctUntilChanged()

        lifecycleScope.launch{
            //페이징 데이터 UI 반영
            pagingData.collectLatest{adapter.submitData(it)}
        }
        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll)
                    postRV.scrollToPosition(0)
            }
        }


        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                // 검색상태 표현하기
                // Check if the query is not empty before updating UI for empty list
                if (uiState.value.query.isEmpty()) {
                    emptyList.isVisible = false
                }else{
                    emptyList.isVisible = isListEmpty
                }

                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    private fun FragmentCommuSearchBinding.bindSearch(
        uiState: StateFlow<SearchUiState>,
        onSearchBtnClicked: (SearchUiAction.Search) -> Unit,
    ){
        btnSearch.setOnClickListener {
            if(etSearch.text.isNotEmpty()){
                onSearchBtnClicked(SearchUiAction.Search(etSearch.text.toString()))
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect{query ->
                    etSearch.setText(query)
                    etSearch.setSelection(query.length)
                }
        }
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