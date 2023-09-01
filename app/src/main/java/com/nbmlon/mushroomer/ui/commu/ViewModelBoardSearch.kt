package com.nbmlon.mushroomer.ui.commu

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nbmlon.mushroomer.data.posts.PostsSearchRepository
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


/** 게시판 검색을 위한 Viewmodel **/
class BoardSearchViewModel(
    private val repository : PostsSearchRepository,
    private val savedStateHandle: SavedStateHandle,
    private val boardType: BoardType
) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<Post>>

    /**
     * Stream of immutable states representative of the UI.
     */
    val state: StateFlow<SearchUiState>
    /**
     * Processor of side effects from the UI which in turn feedback into [state]
     */
    val accept: (SearchUiAction) -> Unit

    init{
        val initialQuery : String = savedStateHandle[LAST_QUERY] ?: ""
        val initialQueryScrolled : String = savedStateHandle[LAST_QUERY_SCROLLED] ?: ""
        val actionStateFlow = MutableSharedFlow<SearchUiAction>()

        //emit 된 actionFlow가 sort -> 정렬 이벤트 발생
        val searches = actionStateFlow
            .filterIsInstance<SearchUiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(SearchUiAction.Search(initialQuery)) }
        //emit 된 actionFlow가 scroll -> 스크롤 이벤트 발생
        val queryScrolled = actionStateFlow
            .filterIsInstance<SearchUiAction.Scroll>()
            .distinctUntilChanged()
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(SearchUiAction.Scroll(initialQueryScrolled)) }

        // 상태 업데이트
        state = combine(
            searches,
            queryScrolled,
            ::Pair
        ).map {( _search, _scroll  ) ->
            SearchUiState(
                query = _search.query,
                lastQueryScrolled = _scroll.lastQueryScrolled,
                hasNotScrolledForCurrentQuery =
                    _search.query != _scroll.lastQueryScrolled
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = SearchUiState()
        )


        pagingDataFlow = searches.flatMapLatest {
            loadPostsPagingSearchResult(it.query)
        }.cachedIn(viewModelScope)



        // UiAction emit
        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }



    /** 페이징 데이터 요청 **/
    private fun loadPostsPagingSearchResult (query : String): Flow<PagingData<Post>> =
        repository.getSearchedPostStream(
            boardType = boardType,
            searchKeyword = query
        )


    override fun onCleared() {
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }


}

sealed class SearchUiAction {
    data class Search(val query : String) : SearchUiAction()
    data class Scroll(val lastQueryScrolled : String?) : SearchUiAction()
}

data class SearchUiState(
    //정렬 상태
    val query: String = "",
    val lastQueryScrolled: String? = null,

    val hasNotScrolledForCurrentQuery: Boolean = false,
)


private const val LAST_QUERY : String = "last_query"
private const val LAST_QUERY_SCROLLED : String = "last_query_scrolled"
