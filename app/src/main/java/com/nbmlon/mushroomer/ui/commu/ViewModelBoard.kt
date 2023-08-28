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
        val initialQuery: String? = savedStateHandle[LAST_SEARCH_QUERY] ?: DEFAULT_QUERY
        val lastQueryScrolled: String? = savedStateHandle[LAST_QUERY_SCROLLED] ?: DEFAULT_QUERY
        val initialSorting : PostSortingOption = savedStateHandle.get<PostSortingOption>(LAST_SORTING_OPTION) ?: DEFAULT_SORTING
        val lastSortingScrolled : PostSortingOption = savedStateHandle.get<PostSortingOption>(LAST_SORTING_SCROLLED) ?: DEFAULT_SORTING

        val actionStateFlow = MutableSharedFlow<CommuUiAction>()


        //emit 된 actionFlow가 search -> 검색 이벤트 발생
        val searches = actionStateFlow
            .filterIsInstance<CommuUiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(CommuUiAction.Search(query = initialQuery)) }
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
        val queriesScrolled = actionStateFlow
            .filterIsInstance<CommuUiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(CommuUiAction.Scroll(currentQuery = lastQueryScrolled, currentSort = lastSortingScrolled)) }

        // 상태 업데이트
        state = combine(
            searches,
            queriesScrolled,
            sorting,
            boardChanges
        ){ (  _search, _scroll, _sorting, _board ) ->
            CombinedAction(
                searchState = _search as CommuUiAction.Search,
                scrollState = _scroll as CommuUiAction.Scroll,
                sortingState = _sorting as CommuUiAction.Sort,
                boardState = _board as CommuUiAction.ChangeBoardType
            )
        }.map { act ->
            CommuUiState(
                targetBoardType = act.boardState.postingBoardType,
                query = act.searchState.query,
                lastQueryScrolled = act.scrollState.currentQuery,
                sort = act.sortingState.sortOpt,
                lastSortScrolled = act.scrollState.currentSort,
                hasNotScrolledForCurrentRV =
                    act.searchState.query != act.scrollState.currentQuery ||
                    act.sortingState.sortOpt != act.scrollState.currentSort
            )
        }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = CommuUiState(initialBoardType)
            )


        pagingDataFlow = combine(
            searches,
            sorting,
            boardChanges,
            ::Triple
        ).flatMapLatest { (_search, _sorting, _board) ->
                loadPostsPaging(
                    boardType = _board.postingBoardType,
                    query = _search.query,
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
    private fun loadPostsPaging(boardType: BoardType, query : String?, sortOpt : PostSortingOption): Flow<PagingData<Post>> =
        repository.getPostStream(
            boardType = boardType,
            query = query,
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
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[LAST_SORTING_OPTION] = state.value.sort
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        savedStateHandle[LAST_SORTING_SCROLLED] = state.value.lastSortScrolled
        super.onCleared()
    }


}

data class CombinedAction(
    val searchState: CommuUiAction.Search,
    val scrollState: CommuUiAction.Scroll,
    val sortingState: CommuUiAction.Sort,
    val boardState: CommuUiAction.ChangeBoardType
)
sealed class CommuUiAction {
    data class Search(val query: String?) : CommuUiAction()
    data class Scroll(val currentQuery: String?, val currentSort : PostSortingOption) : CommuUiAction()
    data class Sort(val sortOpt: PostSortingOption) : CommuUiAction()

    // 인기게시판 -> 게시판 변경
    data class ChangeBoardType(val postingBoardType : BoardType) : CommuUiAction()
}

data class CommuUiState(
    val targetBoardType: BoardType,

    //검색 상태
    val query: String? = DEFAULT_QUERY,
    val lastQueryScrolled: String? = DEFAULT_QUERY,

    //정렬 상태
    val sort: PostSortingOption = DEFAULT_SORTING,
    val lastSortScrolled: PostSortingOption = DEFAULT_SORTING,

    //Board상태 -> 인기게시판

    //새로 고침(정렬 / 상태가 바뀜)
    val hasNotScrolledForCurrentRV: Boolean = false,
)



enum class PostSortingOption(val textResId : Int) {
    SORTING_TIME(R.string.sort_time),
    SORTING_LIKE(R.string.sort_like)
}

enum class ResponseCode{
    SUCCESS,
    FAIL
}

sealed class CommuRequest{
    data class ForReport(val post : Post?, val comment : Comment?) : CommuRequest()
    data class ForDelete(val post : Post?, val comment : Comment?) : CommuRequest()
    data class ForUpload(val post : Post?, val comment : Comment?) : CommuRequest()
    data class ForModify(val post : Post?, val comment : Comment?) : CommuRequest()
}


sealed class CommuResponse{
    data class ForReport(val code : ResponseCode) : CommuResponse()
    data class ForDelete(val code : ResponseCode) : CommuResponse()
    data class ForUpload(val code : ResponseCode) : CommuResponse()
    data class ForModify(val code : ResponseCode) : CommuResponse()
}



private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val LAST_SORTING_OPTION : String = "last_sorting_option"
private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
private const val LAST_SORTING_SCROLLED : String = "last_sorting_scrolled"
private const val LAST_BOARD_TYPE : String = "last_board_type"

private val DEFAULT_QUERY = null
private val DEFAULT_SORTING = PostSortingOption.SORTING_TIME