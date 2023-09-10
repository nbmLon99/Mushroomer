package com.nbmlon.mushroomer.ui.dogam

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.nbmlon.mushroomer.data.dogam.DogamRepository
import com.nbmlon.mushroomer.model.Mushroom
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

class DogamViewModel(
    private val repository: DogamRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    /**
     * Stream of immutable states representative of the UI.
     */
    val state: StateFlow<DogamUiState>

    val pagingDataFlow: Flow<PagingData<DogamUiModel>>

    /**
     * Processor of side effects from the UI which in turn feedback into [state]
     */
    val accept: (DogamUiAction) -> Unit

    init {
        val initialQuery: String? = savedStateHandle[LAST_SEARCH_QUERY] ?: DEFAULT_QUERY
        val lastQueryScrolled: String? = savedStateHandle[LAST_QUERY_SCROLLED] ?: DEFAULT_QUERY
        val initialSorting : DogamSortingOption = savedStateHandle.get<DogamSortingOption>(LAST_SORTING_OPTION) ?: DEFAULT_SORTING
        val lastSortingScrolled : DogamSortingOption = savedStateHandle.get<DogamSortingOption>(LAST_SORTING_SCROLLED) ?: DEFAULT_SORTING
        val initialChecked : Boolean = savedStateHandle.get<Boolean>(LAST_CHECKED_STATE) ?: DEFAULT_CHECKED

        val actionStateFlow = MutableSharedFlow<DogamUiAction>()

        //emit 된 actionFlow가 search -> 검색 이벤트 발생
        val searches = actionStateFlow
            .filterIsInstance<DogamUiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(DogamUiAction.Search(query = initialQuery)) }
        //emit 된 actionFlow가 sort -> 정렬 이벤트 발생
        val sorting = actionStateFlow
            .filterIsInstance<DogamUiAction.Sort>()
            .distinctUntilChanged()
            .onStart { emit(DogamUiAction.Sort(initialSorting)) }
        //emit 된 actionFlow가 filter -> 체크 이벤트 발생
        val checked = actionStateFlow
            .filterIsInstance<DogamUiAction.Filter>()
            .distinctUntilChanged()
            .onStart { emit(DogamUiAction.Filter(initialChecked)) }
        //emit 된 actionFlow가 scroll -> 스크롤 이벤트 발생
        val queriesScrolled = actionStateFlow
            .filterIsInstance<DogamUiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(DogamUiAction.Scroll(currentQuery = lastQueryScrolled, currentSort = lastSortingScrolled)) }

        /** 정렬 / 체크 / 검색 모두 준비 되면, 플로우 열어서 변경될 때 마다 페이징 데이터 업데이트 **/
        pagingDataFlow = combine(
                searches,
                sorting,
                checked,
                ::Triple
            )
            .flatMapLatest { (_search, _sorting, _checked) ->
                val paging = loadDogam(
                    query = _search.query,
                    sortingWay = _sorting.sortOpt
                )

                if (_checked.checked) {
                    paging
                } else {
                    val filteredPaging = paging.map { pagingList ->
                        pagingList.filter { item -> (item as DogamUiModel.MushItem).mush.gotcha }
                    }
                    filteredPaging
                }
            }
            .cachedIn(viewModelScope)

        
        // 상태 업데이트
        state = combine(
            searches,
            queriesScrolled,
            sorting,
            checked
        ){ (  _search, _scroll, _sorting, _checked ) ->
            CombinedAction(
                searchState = _search as DogamUiAction.Search,
                scrollState = _scroll as DogamUiAction.Scroll,
                sortingState = _sorting as DogamUiAction.Sort,
                checkedState = _checked as DogamUiAction.Filter
            )
        }.map { act ->
            DogamUiState(
                query = act.searchState.query,
                sort = act.sortingState.sortOpt,
                lastSortScrolled = act.scrollState.currentSort,
                lastQueryScrolled = act.scrollState.currentQuery,
                // If the search query matches the scroll query, the user has scrolled
                hasNotScrolledForCurrentRV = 
                    act.searchState.query != act.scrollState.currentQuery ||
                    act.sortingState.sortOpt != act.scrollState.currentSort,
                lastCheckedState = act.checkedState.checked
            )
        }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = DogamUiState()
            )


        // UiAction emit
        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }


    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[LAST_SORTING_OPTION] = state.value.sort
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        savedStateHandle[LAST_SORTING_SCROLLED] = state.value.lastSortScrolled
        savedStateHandle[LAST_CHECKED_STATE] = state.value.lastCheckedState
        super.onCleared()
    }

    private fun loadDogam(query : String?, sortingWay : DogamSortingOption): Flow<PagingData<DogamUiModel>> =
        repository.getDogamstream(query, sortingWay)
            .map { pagingData -> pagingData.map { DogamUiModel.MushItem(it) } }
}

data class CombinedAction(
    val searchState: DogamUiAction.Search,
    val scrollState: DogamUiAction.Scroll,
    val sortingState: DogamUiAction.Sort,
    val checkedState: DogamUiAction.Filter
)




sealed class DogamUiAction {
    data class Search(val query: String?) : DogamUiAction()
    data class Scroll(val currentQuery: String?, val currentSort : DogamSortingOption) : DogamUiAction()
    data class Sort(val sortOpt: DogamSortingOption) : DogamUiAction()
    data class Filter(val checked: Boolean) : DogamUiAction()
}

data class DogamUiState(
    //검색 상태
    val query: String? = DEFAULT_QUERY,
    val lastQueryScrolled: String? = DEFAULT_QUERY,
    
    //정렬 상태
    val sort: DogamSortingOption = DEFAULT_SORTING,
    val lastSortScrolled: DogamSortingOption = DEFAULT_SORTING,
    
    //새로 고침(정렬 / 상태가 바뀜)
    val hasNotScrolledForCurrentRV: Boolean = false,
    
    //체크 상태
    val lastCheckedState : Boolean = DEFAULT_CHECKED
)

sealed class DogamUiModel {
    data class MushItem(val mush: Mushroom) : DogamUiModel()
}

private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val LAST_SORTING_OPTION : String = "last_sorting_option"
private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
private const val LAST_SORTING_SCROLLED : String = "last_sorting_scrolled"
private const val LAST_CHECKED_STATE : String = "last_checked_state"

private val DEFAULT_QUERY = null
private val DEFAULT_SORTING = DogamSortingOption.MUSH_NO
private const val DEFAULT_CHECKED = true