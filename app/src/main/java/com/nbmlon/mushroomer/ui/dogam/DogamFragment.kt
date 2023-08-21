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
import androidx.paging.filter
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.data.dogam.DogamRepository
import com.nbmlon.mushroomer.databinding.FragmentDogamBinding
import com.nbmlon.mushroomer.model.Mushroom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
        private const val TAG = "DogamFragment"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DogamFragment.
         */
        // TODO: Rename and change types and number of parameters


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
    ): View? {
        _binding = FragmentDogamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        AppUser.percent?.let {
            binding.progressBar.progress = it
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }






    private fun FragmentDogamBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<UiModel>>,
        uiActions: (UiAction) -> Unit
    ) {
        val dogamItemAdapter = DogamItemAdapter(this@DogamFragment::onDogamItemClicked)
        val header = DogamLoadStateAdapter { dogamItemAdapter.retry() }

        dogamRV.adapter = dogamItemAdapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = DogamLoadStateAdapter { dogamItemAdapter.retry() }
        )

        bindSort(
        )
        bindFilter(
            pagingData = pagingData,
            uiState = uiState,
            dogamItemAdapter = dogamItemAdapter
        )
        bindList(
            header = header,
            dogamItemAdapter = dogamItemAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )

    }

    private fun FragmentDogamBinding.bindFilter(
        pagingData: Flow<PagingData<UiModel>>,
        uiState: StateFlow<UiState>,
        dogamItemAdapter: DogamItemAdapter
        ) {
        // 체크 박스의 체크 상태에 따라 데이터 필터링
        binding.undiscoverDisplayCkbox.setOnCheckedChangeListener { _, isChecked ->
            // 체크 상태에 따라 데이터 필터링
            val filteredPagingData = if (isChecked) {
                pagingData // 원본 데이터 유지
            } else {
                pagingData.map { pagingData ->
                    pagingData.filter { item -> (item as UiModel.MushItem).mush.gotcha }
                }
            }

            lifecycleScope.launch {
                filteredPagingData.collectLatest { filteredData ->
                    dogamItemAdapter.submitData(filteredData)
                }
            }
        }
    }


    //정렬하여 다시 페이징 로드
    private fun FragmentDogamBinding.bindSort(

    ) {
        binding.sortingWay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedSorting =
                    parent?.getItemAtPosition(position) as? SortingOption ?: return

                lifecycleScope.launch {
                    val sortedData = when (selectedSorting) {
                        // 도감넘버
                        SortingOption.MUSH_NO -> {
                        }
                        //희귀도로 정렬
                        SortingOption.MUSH_RARE -> {
                        }
                        //버섯이름으로 정렬
                        SortingOption.MUSH_NAME -> {
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

        private fun FragmentDogamBinding.bindList(
            header: DogamLoadStateAdapter,
            dogamItemAdapter: DogamItemAdapter,
            uiState: StateFlow<UiState>,
            pagingData: Flow<PagingData<UiModel>>,
            onScrollChanged: (UiAction.Scroll) -> Unit
        ) {
            dogamRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
                }
            })
            val notLoading = dogamItemAdapter.loadStateFlow
                .asRemotePresentationState()
                .map { it == RemotePresentationState.PRESENTED }

            val hasNotScrolledForCurrentSearch = uiState
                .map { it.hasNotScrolledForCurrentSearch }
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
                    // Show a retry header if there was an error refreshing, and items were previously
                    // cached OR default to the default prepend state
                    header.loadState = loadState.mediator
                        ?.refresh
                        ?.takeIf { it is LoadState.Error && dogamItemAdapter.itemCount > 0 }
                        ?: loadState.prepend

                    val isListEmpty =
                        loadState.refresh is LoadState.NotLoading && dogamItemAdapter.itemCount == 0
                    // show empty list
                    emptyList.isVisible = isListEmpty
                    // Only show the list if refresh succeeds, either from the the local db or the remote.
                    //dogamRV.isVisible =  loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                    // Show loading spinner during initial load or refresh.

                    // 상태 나타낼 스피너로 대체
                    //progressSpinner.isVisible = loadState.mediator?.refresh is LoadState.Loading
                    // Show the retry state if initial load or refresh fails.
                    //dogamRV.isVisible = loadState.mediator?.refresh is LoadState.Error && dogamAdapter.itemCount == 0
                    // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                    val errorState = loadState.source.append as? LoadState.Error
                        ?: loadState.source.prepend as? LoadState.Error
                        ?: loadState.append as? LoadState.Error
                        ?: loadState.prepend as? LoadState.Error
                    errorState?.let {
                        Toast.makeText(
                            requireActivity(),
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
            .replace(R.id.FragmentContainer, DogamFragment_detail.newInstance(clickedMushroom))
            .addToBackStack(null) // 백 스택에 Fragment 트랜잭션 추가
            .commit()
    }

}


enum class SortingOption {
    MUSH_NO,
    MUSH_RARE,
    MUSH_NAME
}