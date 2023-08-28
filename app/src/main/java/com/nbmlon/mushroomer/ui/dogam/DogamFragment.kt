package com.nbmlon.mushroomer.ui.dogam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.data.dogam.DogamRepository
import com.nbmlon.mushroomer.databinding.DialogEdittextBinding
import com.nbmlon.mushroomer.databinding.FragmentDogamBinding
import com.nbmlon.mushroomer.model.Mushroom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import taimoor.sultani.sweetalert2.Sweetalert

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "dogamNo"

/**
 * A simple [Fragment] subclass.
 * Use the [DogamFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DogamFragment : Fragment(), DogamItemClickListner {

    companion object {
        const val TAG = "DogamFragment"

        //도감 번호 입력해서 넘어갈떄 이렇게 넘어가면될듯?
        @JvmStatic
        fun newInstance() =
            DogamFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
    private var _binding: FragmentDogamBinding? = null
    private val binding get() = _binding!!

    private val dogamViewModel: DogamViewModel by viewModels()

    // TODO: Rename and change types of parameters
    private var dogamNo: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dogamNo = it.getInt(ARG_PARAM1)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogamBinding.inflate(inflater, container, false)
        val viewModelFactory = DogamViewModelFactory(
            owner = this,
            repository = DogamRepository()
        )

        val viewModel: DogamViewModel by viewModels { viewModelFactory }
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
            AppUser.percent?.let {
                progressBar.progress = it
                progressText.text = "$it%"
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }


    private fun showSearchDialog(onSearchBtnClicked: (DogamUiAction.Search) -> Unit){
        val title = "버섯 검색"
        val content = "검색할 버섯명을 입력해주세요"
        Sweetalert(context, Sweetalert.NORMAL_TYPE).apply {
            val dialogBinding = DialogEdittextBinding.inflate(layoutInflater).apply {
                tvContent.text = content
            }
            titleText = title
            setCustomView(dialogBinding.root)
            setCancelButton(resources.getString(R.string.CONFIRM)){
                val query: String? = dialogBinding.editText.text.takeIf { it.isNotEmpty() }?.toString()
                onSearchBtnClicked(DogamUiAction.Search(query = query))
                it.dismissWithAnimation()
            }
            setNeutralButton(resources.getString(R.string.cancel)){
                it.dismissWithAnimation()
            }
            show()
        }
    }


    private fun FragmentDogamBinding.bindState(
        uiState: StateFlow<DogamUiState>,
        pagingData: Flow<PagingData<DogamUiModel>>,
        uiActions: (DogamUiAction) -> Unit
    ) {
        val dogamItemAdapter = DogamItemAdapter(this@DogamFragment::onDogamItemClicked)

        dogamRV.adapter = dogamItemAdapter.withLoadStateFooter(
            footer = DogamLoadStateAdapter { dogamItemAdapter.retry() }
        )


        bindSearch(
            onSearchCall =  uiActions
        )

        bindSort(
            uiState = uiState,
            onSortingChanged = uiActions
        )
        bindFilter(
            uiState = uiState,
            onCheckedChanged = uiActions
        )
        bindList(
            dogamItemAdapter = dogamItemAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }


    private fun FragmentDogamBinding.bindSearch(
        onSearchCall: (DogamUiAction.Search) -> Unit
    ){
        btnSearch.setOnClickListener { showSearchDialog(onSearchCall) }
    }

    /** 체크박스 상태 바인딩 **/
    private fun FragmentDogamBinding.bindFilter(
        uiState: StateFlow<DogamUiState>,
        onCheckedChanged: (DogamUiAction.Filter) -> Unit
    ) {
        // 체크 박스의 체크 상태에 따라 데이터 필터링
        undiscoverDisplayCkbox.setOnCheckedChangeListener { _, isChecked ->
            // 체크 상태에 따라 데이터 필터링
            onCheckedChanged(DogamUiAction.Filter(isChecked))
        }



        lifecycleScope.launch {
            uiState
                .map { it.lastCheckedState }
                .distinctUntilChanged()
                .collect{ checkedState ->
                    // 체크 상태 반영
                    undiscoverDisplayCkbox.isChecked = checkedState
            }
        }
    }


    /** 스피너 연결 **/
    private fun FragmentDogamBinding.bindSort(
        uiState: StateFlow<DogamUiState>,
        onSortingChanged: (DogamUiAction.Sort) -> Unit
    ) {
        sortingWay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            private var initialSelection = true // Flag to ignore initial selection
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (initialSelection) {
                    initialSelection = false
                    return // Ignore initial selection
                }
                val selectedSorting = DogamSortingOption.values()[position]

                lifecycleScope.launch {
                    val sortedData = when (selectedSorting) {
                        // 도감넘버
                        DogamSortingOption.MUSH_NO -> {
                            onSortingChanged(DogamUiAction.Sort(DogamSortingOption.MUSH_NO))
                        }
                        //희귀도로 정렬
                        DogamSortingOption.MUSH_RARE -> {
                            onSortingChanged(DogamUiAction.Sort(DogamSortingOption.MUSH_NO))
                        }
                        //버섯이름으로 정렬
                        DogamSortingOption.MUSH_NAME -> {
                            onSortingChanged(DogamUiAction.Sort(DogamSortingOption.MUSH_NO))
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        lifecycleScope.launch {
            uiState
                .map { it.sort }
                .distinctUntilChanged()
                .collect{ selectedOption ->
                    if (selectedOption.ordinal != -1) {
                        sortingWay.setSelection(selectedOption.ordinal)
                    }
                }
        }
    }

        private fun FragmentDogamBinding.bindList(
            dogamItemAdapter: DogamItemAdapter,
            uiState: StateFlow<DogamUiState>,
            pagingData: Flow<PagingData<DogamUiModel>>,
            onScrollChanged: (DogamUiAction.Scroll) -> Unit
        ) {
            dogamRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy != 0) onScrollChanged(DogamUiAction.Scroll(currentQuery = uiState.value.query, currentSort = uiState.value.sort))
                }
            })

            //Flow 생성
            val notLoading = dogamItemAdapter.loadStateFlow
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
                pagingData.collectLatest(dogamItemAdapter::submitData)
            }

            lifecycleScope.launch {
                shouldScrollToTop.collect { shouldScroll ->
                    if (shouldScroll) dogamRV.scrollToPosition(0)
                }
            }

            lifecycleScope.launch {
                dogamItemAdapter.loadStateFlow.collect { loadState ->
                    val isListEmpty = loadState.refresh is LoadState.NotLoading && dogamItemAdapter.itemCount == 0
                    // show empty list
                    emptyList.isVisible = isListEmpty
                    // Only show the list if refresh succeeds.
                    dogamRV.isVisible = !isListEmpty
                    // Show loading spinner during initial load or refresh.
                    progressSpinner.isVisible = loadState.source.refresh is LoadState.Loading
                    // Show the retry state if initial load or refresh fails.
                    retryButton.isVisible = loadState.source.refresh is LoadState.Error

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

    override fun onDogamItemClicked(clickedMushroom: Mushroom) {
        /** 도감 상세보기 화면 넘기기 **/
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, DogamFragment_detail.newInstance(clickedMushroom), DogamFragment_detail.TAG)
            .addToBackStack(null) // 백 스택에 Fragment 트랜잭션 추가
            .commit()
    }

}


enum class DogamSortingOption {
    MUSH_NO,
    MUSH_RARE,
    MUSH_NAME
}