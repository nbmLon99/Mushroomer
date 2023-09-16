package com.nbmlon.mushroomer.ui.commu.board

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.data.posts.BoardPostsRepository
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


/**
 *  게시판 페이징 데이터를 위한 viewModel
 *  **/
class ViewModelBoard(
    private val repository : BoardPostsRepository,
    private val savedStateHandle: SavedStateHandle,
    boardType: BoardType
) : ViewModel() {


    val pagingDataFlow: Flow<PagingData<Post>>
    var isHotBoard : Boolean = false

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
            if (boardType == BoardType.HotBoard) {
                isHotBoard = true
                BoardType.QnABoard
            } else { boardType }
        val initialSorting : PostSortingOption = savedStateHandle.get<PostSortingOption>(
            LAST_SORTING_OPTION
        ) ?: DEFAULT_SORTING
        val lastSortingScrolled : PostSortingOption = savedStateHandle.get<PostSortingOption>(
            LAST_SORTING_SCROLLED
        ) ?: DEFAULT_SORTING

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
                    sortOpt = _sorting.sortOpt,
                )
            }
            .cachedIn(viewModelScope)



        // UiAction emit
        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }

    }



    /** 페이징 데이터 요청 **/
    private fun loadPostsPaging(boardType: BoardType, sortOpt : PostSortingOption): Flow<PagingData<Post>> =
        repository.getPostStream(
            boardType = boardType,
            sortOpt = sortOpt,
            isHotBoard = isHotBoard
        )



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