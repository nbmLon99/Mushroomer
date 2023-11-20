package com.nbmlon.mushroomer.ui.dogam

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.api.ResponseCodeConstants.UNDEFINED_ERROR_CODE
import com.nbmlon.mushroomer.data.dogam.DogamRepository
import com.nbmlon.mushroomer.databinding.DialogEdittextBinding
import com.nbmlon.mushroomer.databinding.FragmentDogamBinding
import com.nbmlon.mushroomer.domain.DogamUseCaseResponse
import com.nbmlon.mushroomer.model.MushHistory
import com.nbmlon.mushroomer.model.Mushroom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import taimoor.sultani.sweetalert2.Sweetalert
private const val TARGET_DOGAM_DETAIL = "target_dogam_detail"

class DogamFragment : Fragment(), DogamItemClickListner {

    companion object {
        const val TAG = "DogamFragment"

        /** 지도에서 도감으로 넘어올 떄 사용 **/
        @JvmStatic
        fun openDetail(target : MushHistory) =
            DogamFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TARGET_DOGAM_DETAIL, target.mushroom)
                }
            }
    }
    private var _binding: FragmentDogamBinding? = null
    private val binding get() = _binding!!
    private var targetMush: Mushroom? = null
    private var fetchState : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                targetMush = it.getSerializable(TARGET_DOGAM_DETAIL, Mushroom::class.java)!!
            }else{
                targetMush = it.getSerializable(TARGET_DOGAM_DETAIL) as Mushroom
            }
            targetMush?.let { mush->
                onDogamItemClicked(mush)
            }
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
        val loading = Sweetalert(requireActivity(),Sweetalert.PROGRESS_TYPE).apply {
            setTitleText(R.string.loading)
            setCancelable(false)
            show()
        }


        viewModel.responseLiveData.observe(viewLifecycleOwner, object : Observer<DogamUseCaseResponse.LoadDogamResponse> {
            override fun onChanged(response: DogamUseCaseResponse.LoadDogamResponse) {
                loading.dismiss()

                if(response.success){
                    fetchState = true
                    binding.bindState(
                        uiState = viewModel.state,
                        pagingData = viewModel.pagingDataFlow,
                        uiActions = viewModel.accept
                    )
                    // 성공시 Observer를 제거하여 한 번만 호출되게 함
                    viewModel.responseLiveData.removeObserver(this)
                    GlobalScope.launch {
                        withContext(Dispatchers.Default){
                            try{
                                val dogams = viewModel.responseLiveData.value?.items
                                val gotchas = dogams?.filter { it.gotcha }
                                val percent = gotchas?.size ?: 0 / 54
                                binding.progressBar.progress = percent
                                binding.progressText.text = "$percent%"

                            }catch (e :Exception){

                            }
                        }
                    }
                }else {
                    //에러 처리
                    binding.dogamRV.visibility = View.GONE
                    binding.errorFrame.visibility = View.VISIBLE
                    binding.retryButton.setOnClickListener {
                        CoroutineScope(Dispatchers.IO).launch { viewModel.fetchData() }
                        loading.show()
                    }

                    if(response.code == NETWORK_ERROR_CODE){
                        binding.tvErrorMsg.text = getString(R.string.network_error_msg)
                    }else if(response.code == UNDEFINED_ERROR_CODE){
                        binding.tvErrorMsg.text = getString(R.string.error_msg)
                    }
                }
            }
        })

        return binding.root
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
                it.dismiss()
            }
            setNeutralButton(resources.getString(R.string.cancel)){
                it.dismiss()
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
        dogamRV.adapter = dogamItemAdapter
//        dogamRV.adapter = dogamItemAdapter.withLoadStateFooter(
//            footer = DogamLoadStateAdapter { dogamItemAdapter.retry() }
//        )

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
                            onSortingChanged(DogamUiAction.Sort(DogamSortingOption.MUSH_RARE))
                        }
                        //버섯이름으로 정렬
                        DogamSortingOption.MUSH_NAME -> {
                            onSortingChanged(DogamUiAction.Sort(DogamSortingOption.MUSH_NAME))
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

//            lifecycleScope.launch {
//                dogamItemAdapter.loadStateFlow.collect { loadState ->
//                    val isListEmpty = loadState.refresh is LoadState.NotLoading && dogamItemAdapter.itemCount == 0
//                    // show empty list
//                    emptyList.isVisible = isListEmpty
//                    // Only show the list if refresh succeeds.
//                    dogamRV.isVisible = !isListEmpty
//                    // Show loading spinner during initial load or refresh.
//                    progressSpinner.isVisible = loadState.source.refresh is LoadState.Loading
//                    // Show the retry state if initial load or refresh fails.
//                    retryButton.isVisible = loadState.source.refresh is LoadState.Error
//
//                    // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
//                    val errorState = loadState.source.append as? LoadState.Error
//                        ?: loadState.source.prepend as? LoadState.Error
//                        ?: loadState.append as? LoadState.Error
//                        ?: loadState.prepend as? LoadState.Error
//                    errorState?.let {
//                        Toast.makeText(
//                            requireContext(),
//                            "\uD83D\uDE28 Wooops ${it.error}",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//            }

        }

    override fun onDogamItemClicked(clickedMushroom: Mushroom) {
        /** 도감 상세보기 화면 넘기기 **/
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, DogamFragment_detail.newInstance(clickedMushroom), DogamFragment_detail.TAG)
            .addToBackStack(null)
            .commit()
    }

}


enum class DogamSortingOption {
    MUSH_NO,
    MUSH_RARE,
    MUSH_NAME
}