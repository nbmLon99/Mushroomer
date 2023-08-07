package com.nbmlon.mushroomer.ui.dogam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.nbmlon.mushroomer.databinding.FragmentDogamBinding
import com.nbmlon.mushroomer.ui.camera.AnalyzingViewModel
import kotlinx.coroutines.flow.collectLatest
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
            this.dogam.observe(this@DogamFragment, { dogam ->
                // Dogam 데이터가 변경될 때마다 이 블록이 실행됩니다.
                // 변경된 Dogam 객체를 사용하여 UI를 업데이트할 수 있습니다.
                binding.progressBar.setProgress(dogam.progress)
                }
            )
            this.pages.observe(this@DogamFragment,{ page ->

                }
            )
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
        lifecycleScope.launch {
            dogamViewModel.flow.collectLatest { pagingData ->
                (binding.dogamRV.adapter as DogamAdapter).submitData(pagingData)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    /** 도감 상세보기 화면 넘기기 **/
    fun openDetail(dogamNo : Int){

    }
}