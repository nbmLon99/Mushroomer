package com.nbmlon.mushroomer.ui.dogam

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
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
    val state: StateFlow<UiState>

    val pagingDataFlow: Flow<PagingData<UiModel>>

    /**
     * Processor of side effects from the UI which in turn feedback into [state]
     */
    val accept: (UiAction) -> Unit

    init {
        val initialQuery: String = savedStateHandle.get(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        val lastQueryScrolled: String = savedStateHandle.get(LAST_QUERY_SCROLLED) ?: DEFAULT_QUERY
        val initialSorting : SortingOption = savedStateHandle.get<SortingOption>(LAST_SORTING_OPTION) ?: SortingOption.values()[DEFAULT_SORTING_ORD]
        val initialChecked : Boolean = savedStateHandle.get<Boolean>(LAST_CHECKED_STATE) ?: DEFAULT_CHECKED

        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(query = initialQuery)) }
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }

        // 추가
        val sorting = actionStateFlow
            .filterIsInstance<UiAction.Sort>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Sort(initialSorting)) }
        val checked = actionStateFlow
            .filterIsInstance<UiAction.Filter>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Filter(initialChecked)) }

        /** search가 변경될 때 마다 페이징 데이터 업데이트 **/
        pagingDataFlow = searches
            .flatMapLatest { loadDogam(queryString = it.query) }
            .cachedIn(viewModelScope)

        state = combine(
            searches,
            queriesScrolled,
            sorting,
            checked
        ){ (  _search, _scroll, _sorting, _checked ) ->
            CombinedAction(
                searchState = _search,
                scrollState = _scroll,
                sortingState = _sorting,
                checkedState = _checked
            )
        }.map { act ->
            UiState(
                query = act.searchState.query,
                lastQueryScrolled = act.scrollState.currentQuery,
                // If the search query matches the scroll query, the user has scrolled
                hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery,
                lastSortingOption = ,
                lastCheckedState =
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }


    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        savedStateHandle[LAST_SORTING_OPTION] = state.value.lastSortingOption
        savedStateHandle[LAST_CHECKED_STATE] = state.value.lastCheckedState
        super.onCleared()
    }

    private fun loadDogam(queryString: String): Flow<PagingData<UiModel>> =
        repository.getDogamstream(queryString)
            .map { pagingData -> pagingData.map { UiModel.MushItem(it) } }
}

data class CombinedAction(
    val searchState: UiAction.Search,
    val scrollState: UiAction.Scroll,
    val sortingState: UiAction,
    val checkedState: UiAction
)




sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(val currentQuery: String) : UiAction()
    data class Sort(val sortOpt: SortingOption) : UiAction()
    data class Filter(val boolean: Boolean) : UiAction()
}

data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false,
    val lastSortingOption: SortingOption,
    val lastCheckedState : Boolean
)

sealed class UiModel {
    data class MushItem(val mush: Mushroom) : UiModel()
}

private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val LAST_SORTING_OPTION : String = "last_sorting_option"
private const val LAST_CHECKED_STATE : String = "last_checked_state"
private const val DEFAULT_QUERY = "Android"
private const val DEFAULT_SORTING_ORD = 0
private const val DEFAULT_CHECKED = false