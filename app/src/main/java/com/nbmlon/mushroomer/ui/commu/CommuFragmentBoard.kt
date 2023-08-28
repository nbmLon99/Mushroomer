package com.nbmlon.mushroomer.ui.commu

import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.DialogEdittextBinding
import com.nbmlon.mushroomer.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import taimoor.sultani.sweetalert2.Sweetalert


open abstract class CommuBoardFragment : Fragment(){

    private lateinit var boardType: BoardType
    private lateinit var list : RecyclerView
    private lateinit var adapter : AdapterBoardPost
    private lateinit var searchBtn : ImageView
    private var sortGroup : RadioGroup? = null
    private var boardGroup : RadioGroup? = null


    protected fun bindView(
        boardType: BoardType,
        list : RecyclerView,
        adapter : AdapterBoardPost,
        searchBtn : ImageView,
        sortGroup : RadioGroup?,
        boardGroup : RadioGroup?
    ){
        this.boardType = boardType
        this.adapter = adapter
        this.searchBtn = searchBtn
        this.sortGroup = sortGroup
        this.boardGroup = boardGroup
        this.list = list
    }
    protected fun bindState(
        uiState: StateFlow<CommuUiState>,
        pagingData: Flow<PagingData<Post>>,
        uiActions: (CommuUiAction) -> Unit,
    ){
        bindList(
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )

        bindSearch(
            onSearchClicked = uiActions
        )
        if(boardType != BoardType.HotBoard) {
            bindSort(
                uiState = uiState,
                onRadioClicked = uiActions
            )
        }

        if(boardType == BoardType.HotBoard){
            bindChangeBoard(
                uiState = uiState,
                onRadioClicked = uiActions
            )
        }
    }

    private fun bindList(
        uiState: StateFlow<CommuUiState>,
        pagingData: Flow<PagingData<Post>>,
        onScrollChanged: (CommuUiAction.Scroll) -> Unit
    ){
        list.adapter = this.adapter
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(CommuUiAction.Scroll(currentQuery = uiState.value.query, currentSort = uiState.value.sort))
            }
        })

        //Flow 생성
        val notLoading = adapter.loadStateFlow
            // Only emit when REFRESH LoadState for the paging source changes.
            .distinctUntilChangedBy { it.source.refresh }
            // Only react to cases where REFRESH completes i.e., NotLoading.
            .map { it.source.refresh is LoadState.NotLoading }

        //Flow 생성
        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentRV }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()


        lifecycleScope.launch {
            pagingData.collectLatest(adapter::submitData)
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) list.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                // show empty list
                //emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds.
                list.isVisible = !isListEmpty
                // Show loading spinner during initial load or refresh.
                //progressSpinner.isVisible = loadState.source.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                //retryButton.isVisible = loadState.source.refresh is LoadState.Error

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
        adapter

    }

    // 핫 게시판은 지금 안되고있음.
    /** Search Frg로 수정 필요 **/
    private fun bindSearch(
        onSearchClicked: (CommuUiAction.Search) -> Unit,
    ){
        if( searchBtn == null )
            return

        searchBtn.setOnClickListener {
            Sweetalert(context,Sweetalert.NORMAL_TYPE).apply {
                titleText = "검색"
                val dialogBinding = DialogEdittextBinding.inflate(layoutInflater).apply {
                    tvContent.text = "검색어를 입력해주세요"
                }
                setCustomView(dialogBinding.root)
                setCancelButton("검색"){
                    val query : String? = dialogBinding.editText.text.takeIf { it.isNotEmpty() }?.toString()
                    onSearchClicked(CommuUiAction.Search(query))
                    it.dismissWithAnimation()
                }
                setNeutralButton("취소"){
                    it.dismissWithAnimation()
                }
                show()
            }
        }
    }
    private fun bindSort(
        uiState: StateFlow<CommuUiState>,
        onRadioClicked : (CommuUiAction.Sort) -> Unit
    ){
        if(sortGroup == null)
            return

        sortGroup!!.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.sort_time -> {
                    onRadioClicked(CommuUiAction.Sort(PostSortingOption.SORTING_TIME))
                }
                R.id.sort_like -> {
                    onRadioClicked(CommuUiAction.Sort(PostSortingOption.SORTING_LIKE))
                }
                else -> error("정렬 선택 오류")
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.sort }
                .distinctUntilChanged()
                .collect{ opt ->
                    when(opt){
                        PostSortingOption.SORTING_LIKE -> sortGroup!!.check(R.id.sort_like)
                        PostSortingOption.SORTING_TIME -> sortGroup!!.check(R.id.sort_time)
                    }
                }
        }
    }

    private fun bindChangeBoard(
        uiState: StateFlow<CommuUiState>,
        onRadioClicked: (CommuUiAction.ChangeBoardType) -> Unit
    ){
        if(boardGroup == null)
            return

        boardGroup!!.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_qna -> {
                    adapter = AdapterBoardPost(BoardType.QnABoard)
                    onRadioClicked(CommuUiAction.ChangeBoardType(BoardType.QnABoard))
                }
                R.id.radio_free -> {
                    adapter = AdapterBoardPost(BoardType.FreeBoard)
                    onRadioClicked(CommuUiAction.ChangeBoardType(BoardType.FreeBoard))
                }
                R.id.radio_myPic -> {
                    adapter = AdapterBoardPost(BoardType.PicBoard)
                    onRadioClicked(CommuUiAction.ChangeBoardType(BoardType.PicBoard))
                }
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.targetBoardType }
                .distinctUntilChanged()
                .collect{type ->
                    when(type){
                        BoardType.FreeBoard -> boardGroup!!.check(R.id.radio_free)
                        BoardType.QnABoard -> boardGroup!!.check(R.id.radio_qna)
                        BoardType.PicBoard -> boardGroup!!.check(R.id.radio_myPic)
                        else -> error("인기 게시판, 게시판 선택 오류")
                    }
                }
        }
    }
}

fun getBoardFragment(boardType: BoardType) : CommuBoardFragment{
    return when(boardType){
        BoardType.FreeBoard -> CommuFragmentBoard_text.getInstance(boardType.ordinal)
        BoardType.PicBoard -> CommuFragmentBoard_img.getInstance(boardType.ordinal)
        BoardType.QnABoard -> CommuFragmentBoard_text.getInstance(boardType.ordinal)
        BoardType.HotBoard -> CommuFragmentBoard_hot.getInstance(boardType.ordinal)
        else -> error("게시판 형태 오류")
    }
}

/** 게시판 열기 **/
class CommuFragmentBoard {
    companion object {
        const val TAG = "CommuFragmentBoard"

        @JvmStatic
        fun getInstance(param1: Int) =
            when(param1) {
                BoardType.FreeBoard.ordinal -> CommuFragmentBoard_text.getInstance(param1)
                BoardType.PicBoard.ordinal -> CommuFragmentBoard_img.getInstance(param1)
                BoardType.QnABoard.ordinal -> CommuFragmentBoard_text.getInstance(param1)
                //BoardType.HotBoard.ordinal -> CommuFragmentBoard_hot()
                else ->  error("Invalid board type")
            }
    }
}