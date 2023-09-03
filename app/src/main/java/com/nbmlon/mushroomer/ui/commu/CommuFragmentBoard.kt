package com.nbmlon.mushroomer.ui.commu

import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.model.Post
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


open abstract class CommuBoardFragment : Fragment(), PostClickListener{
    companion object{
        const val TAG = "CommuBoardFragment"
    }

    private lateinit var boardType: BoardType
    private lateinit var list : RecyclerView
    private lateinit var adapter : AdapterBoardPost
    private var sortGroup : RadioGroup? = null
    private var boardGroup : RadioGroup? = null



    protected fun bindView(
        boardType: BoardType,
        list : RecyclerView,
        sortGroup : RadioGroup?,
        boardGroup : RadioGroup?
    ){
        this.boardType = boardType
        this.adapter = AdapterBoardPost(boardType, this@CommuBoardFragment::openPost)
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
        if(boardType != BoardType.HotBoard){
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
        var currentJob : Job? = null // 페이징 데이터 collectLatest 작업을 추적하는 Job 객체
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(CommuUiAction.Scroll( currentSort = uiState.value.sort))
            }
        })

        lifecycleScope.launch {
            uiState
                .map { it.targetBoardType }
                .distinctUntilChanged()
                .collect{ type ->
                    // 이전 어댑터에서 수행하던 작업 취소
                    currentJob?.cancelChildren()

                    adapter = AdapterBoardPost(type, this@CommuBoardFragment::openPost)
                    list.adapter = adapter

                    val layoutManager = when (type) {
                        BoardType.QnABoard, BoardType.FreeBoard -> LinearLayoutManager(requireContext())
                        else -> {
                            val gridLayoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
                            if (type != BoardType.PicBoard) {
                                throw IllegalArgumentException("Invalid board type: $type")
                            }
                            gridLayoutManager
                        }
                    }
                    list.layoutManager = layoutManager

                    // 새로운 어댑터와 함께 페이징 데이터 처리
                    currentJob = bindBoardAdapter(
                        uiState =uiState, 
                        pagingData = pagingData
                    )
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

    private fun bindBoardAdapter (
        uiState: StateFlow<CommuUiState>,
        pagingData: Flow<PagingData<Post>>
    ) : Job{
        //Flow 생성
        val notLoading = adapter.loadStateFlow
            // Only emit when REFRESH LoadState for the paging source changes.
            .distinctUntilChangedBy { it.source.refresh }
            // Only react to cases where REFRESH completes i.e., NotLoading.
            .map {
                it.source.refresh is LoadState.NotLoading }

        //Flow 생성
        val hasNotScrolledForCurrentSort = uiState
            .map {
                it.hasNotScrolledForCurrentSort
            }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSort,
            Boolean::and
        )
            .distinctUntilChanged()
        val parentJob = Job()

        lifecycleScope.launch(parentJob) {
            //페이징 데이터 UI 반영
            pagingData.collectLatest{adapter.submitData(it)}
        }
        lifecycleScope.launch(parentJob) {
            //근데 굳이 이런식으로 안하고 radiobutton.onitem거기에 등록했어도 문제없었을듯?
            //radiobutton이면 어댑터 로드 상태가 반영이 안되긴하네
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll)
                    list.scrollToPosition(0)
            }
        }
        return parentJob
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
                    onRadioClicked(CommuUiAction.ChangeBoardType(BoardType.QnABoard))
                }
                R.id.radio_free -> {
                    onRadioClicked(CommuUiAction.ChangeBoardType(BoardType.FreeBoard))
                }
                R.id.radio_myPic -> {
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

    override fun openPost(post: Post) {
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.FragmentContainer, CommuFragment_post.getInstance(post),CommuFragment_post.TAG)
            .commit()
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
