package com.nbmlon.mushroomer.ui.commu

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.data.posts.PostsRepository
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BoardViewModel(
    private val repository : PostsRepository,
    private val savedStateHandle: SavedStateHandle,
    boardType: BoardType
) : ViewModel() {

    //요청 후 take(1) 하여 반영
    val responseFlow = MutableSharedFlow<CommuResponse>()
    val request : (CommuRequest) -> Unit
    val pagingDataFlow: Flow<PagingData<Post>>

    /**
     * Stream of immutable states representative of the UI.
     */
    val state: StateFlow<CommuUiState>

    /**
     * Processor of side effects from the UI which in turn feedback into [state]
     */
    val accept: (CommuUiAction) -> Unit

    init{
        val initialBoardType = savedStateHandle.get<BoardType>(LAST_BOARD_TYPE) ?:
            if (boardType == BoardType.HotBoard) { BoardType.QnABoard } else { boardType }
        val initialSorting : PostSortingOption = savedStateHandle.get<PostSortingOption>(LAST_SORTING_OPTION) ?: DEFAULT_SORTING
        val lastSortingScrolled : PostSortingOption = savedStateHandle.get<PostSortingOption>(LAST_SORTING_SCROLLED) ?: DEFAULT_SORTING

        val actionStateFlow = MutableSharedFlow<CommuUiAction>()

        //emit 된 actionFlow가 sort -> 정렬 이벤트 발생
        val sorting = actionStateFlow
            .filterIsInstance<CommuUiAction.Sort>()
            .distinctUntilChanged()
            .onStart { emit(CommuUiAction.Sort(initialSorting)) }
        val boardChanges = actionStateFlow
            .filterIsInstance<CommuUiAction.ChangeBoardType>()
            .distinctUntilChanged()
            .onStart { emit(CommuUiAction.ChangeBoardType(initialBoardType)) }
        //emit 된 actionFlow가 scroll -> 스크롤 이벤트 발생
        val sortScrolled = actionStateFlow
            .filterIsInstance<CommuUiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(CommuUiAction.Scroll(currentSort = lastSortingScrolled)) }

        // 상태 업데이트
        state = combine(
            boardChanges,
            sorting,
            sortScrolled,
            ::Triple
        ).map {( _board, _sorting, _scroll  ) ->
            CommuUiState(
                targetBoardType = _board.postingBoardType,
                sort = _sorting.sortOpt,
                lastSortScrolled = _scroll.currentSort,
                hasNotScrolledForCurrentSort =
                     _sorting.sortOpt != _scroll.currentSort
            )
        }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = CommuUiState(initialBoardType)
            )


        pagingDataFlow = combine(
            sorting,
            boardChanges,
            ::Pair
        ).flatMapLatest { (_sorting, _board) ->
                loadPostsPaging(
                    boardType = _board.postingBoardType,
                    sortOpt = _sorting.sortOpt
                )
            }
            .cachedIn(viewModelScope)



        // UiAction emit
        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
        request = { commuRequest ->
            viewModelScope.launch{
                when( commuRequest ) {
                    is CommuRequest.ForReport -> {
                        val post = commuRequest.post
                        val comment = commuRequest.comment
                        // ForReport 처리 로직
                        responseFlow.emit(CommuResponse.ForReport(code = ResponseCode.SUCCESS))
                    }
                    is CommuRequest.ForDelete -> {
                        val post = commuRequest.post
                        val comment = commuRequest.comment
                        // ForDelete 처리 로직
                        responseFlow.emit(CommuResponse.ForDelete(code = ResponseCode.SUCCESS))
                    }
                    is CommuRequest.ForUpload -> {
                        val post = commuRequest.post
                        val comment = commuRequest.comment
                        // ForUpload 처리 로직
                        responseFlow.emit(CommuResponse.ForUpload(code = ResponseCode.SUCCESS))
                    }
                    is CommuRequest.ForModify -> {
                        val post = commuRequest.post
                        val comment = commuRequest.comment
                        // ForModify 처리 로직
                        responseFlow.emit(CommuResponse.ForModify(code = ResponseCode.SUCCESS))
                    }
                }
            }
        }
    }



    /** 페이징 데이터 요청 **/
    private fun loadPostsPaging(boardType: BoardType, sortOpt : PostSortingOption): Flow<PagingData<Post>> =
        repository.getPostStream(
            boardType = boardType,
            sortOpt = sortOpt
        )


    /** 서버에 수정 요청 **/
    fun requestModify(targetPost : Post?, targetComment : Comment?){
        if (targetPost != null || targetComment == null)
            //Comment 수정
            return
    }

    /** 서버에 등록 요청 **/
    fun requestUpload(targetPost : Post?, targetComment : Comment?){
        if (targetPost != null || targetComment == null)
        //Comment 수정
            return

    }

    /** 서버에 삭제 요청 **/
    fun requestDelete(targetPost : Post?, targetComment : Comment?){
        if (targetPost != null || targetComment == null)
        //Comment 수정
            return

    }


    /** 서버에 신고 접수 **/
    suspend fun requestReport(targetPost : Post?, targetComment : Comment?){
        if (targetPost != null || targetComment == null)
        //Comment 수정
            return

    }


    override fun onCleared() {
        savedStateHandle[LAST_BOARD_TYPE] = state.value.targetBoardType
        savedStateHandle[LAST_SORTING_OPTION] = state.value.sort
        savedStateHandle[LAST_SORTING_SCROLLED] = state.value.lastSortScrolled
        super.onCleared()
    }


}

sealed class CommuUiAction {
    data class Scroll(val currentSort : PostSortingOption) : CommuUiAction()
    data class Sort(val sortOpt: PostSortingOption) : CommuUiAction()

    // 인기게시판 -> 게시판 변경
    data class ChangeBoardType(val postingBoardType : BoardType) : CommuUiAction()
}

data class CommuUiState(
    //Board상태 -> 인기게시판
    val targetBoardType: BoardType,

    //정렬 상태
    val sort: PostSortingOption = DEFAULT_SORTING,
    val lastSortScrolled: PostSortingOption = DEFAULT_SORTING,

    //새로 고침(정렬 / 상태가 바뀜)
    val hasNotScrolledForCurrentSort: Boolean = false,
)



enum class PostSortingOption(val textResId : Int) {
    SORTING_TIME(R.string.sort_time),
    SORTING_LIKE(R.string.sort_like)
}




private const val LAST_SORTING_OPTION : String = "last_sorting_option"
private const val LAST_SORTING_SCROLLED : String = "last_sorting_scrolled"
private const val LAST_BOARD_TYPE : String = "last_board_type"

private val DEFAULT_SORTING = PostSortingOption.SORTING_TIME