package com.nbmlon.mushroomer.ui.dogam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.nbmlon.mushroomer.databinding.FragmentDogamBinding
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
class DogamFragment : Fragment() {

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
        fun getInfoFromDogamNo(dogamNo : Int) =
            DogamFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, dogamNo)
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
        dogamViewModel.apply {
//            this.dogam.observe(this@DogamFragment, { dogam ->
//                // Dogam 데이터가 변경될 때마다 이 블록이 실행됩니다.
//                // 변경된 Dogam 객체를 사용하여 UI를 업데이트할 수 있습니다.
//                binding.progressBar.setProgress(dogam.progress)
//                }
//            )
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

        binding.dogamRV.adapter = DogamAdapter()
        // Activities can use lifecycleScope directly, but Fragments should instead use
        // viewLifecycleOwner.lifecycleScope.
//        lifecycleScope.launch {
//            dogamViewModel.flow.collectLatest { pagingData ->
//                (binding.dogamRV.adapter as DogamAdapter).submitData(pagingData)
//            }
//        }
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
        val dogamAdapter = DogamAdapter()
        val header = DogamLoadStateAdapter { dogamAdapter.retry() }

        dogamRV.adapter = dogamAdapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = DogamLoadStateAdapter { dogamAdapter.retry() }
        )

        bindSort(

        )
        bindList(
            header = header,
            dogamAdapter = dogamAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }



    private fun FragmentDogamBinding.bindList(
        header: DogamLoadStateAdapter,
        dogamAdapter: DogamAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<UiModel>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        dogamRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = dogamAdapter.loadStateFlow
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
            pagingData.collectLatest(dogamAdapter::submitData)
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) dogamRV.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            dogamAdapter.loadStateFlow.collect { loadState ->
                // Show a retry header if there was an error refreshing, and items were previously
                // cached OR default to the default prepend state
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && dogamAdapter.itemCount > 0 }
                    ?: loadState.prepend

                val isListEmpty = loadState.refresh is LoadState.NotLoading && dogamAdapter.itemCount == 0
                // show empty list
                emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds, either from the the local db or the remote.
                dogamRV.isVisible =  loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                // Show loading spinner during initial load or refresh.
                // 상태 나타낼 스피너로 대체
                progressSpinner.isVisible = loadState.mediator?.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                dogamRV.isVisible = loadState.mediator?.refresh is LoadState.Error && dogamAdapter.itemCount == 0
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

    /** 도감 상세보기 화면 넘기기 **/
    fun openDetail(dogamNo : Int){

    }
}