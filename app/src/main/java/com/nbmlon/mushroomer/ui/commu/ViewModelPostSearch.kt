package com.nbmlon.mushroomer.ui.commu

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.onStart

class ViewModelPostSearch : ViewModel() {


//    //emit 된 actionFlow가 search -> 검색 이벤트 발생
//    val searches = actionStateFlow
//        .filterIsInstance<CommuUiAction.Search>()
//        .distinctUntilChanged()
//        .onStart { emit(CommuUiAction.Search(query = initialQuery)) }
}