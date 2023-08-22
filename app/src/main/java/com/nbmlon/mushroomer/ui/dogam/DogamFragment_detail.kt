package com.nbmlon.mushroomer.ui.dogam

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nbmlon.mushroomer.databinding.FragmentDogamDetailBinding
import com.nbmlon.mushroomer.model.Mushroom

private const val TARGET_MUSH = "mush"


/**
 * 특정 버섯 표시하는 도감 디테일
 */
class DogamFragment_detail : Fragment() {

    companion object {
        const val TAG ="DogamFragment_Detail"
        @JvmStatic
        fun newInstance(mush: Mushroom) =
            DogamFragment_detail().apply {
                arguments = Bundle().apply {
                    putSerializable(TARGET_MUSH, mush)
                }
            }
    }
    // TODO: Rename and change types of parameters
    private var mMush : Mushroom? = null

    private var _binding: FragmentDogamDetailBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mMush = it.getSerializable(TARGET_MUSH, Mushroom::class.java)
            } else {
                mMush = it.getSerializable(TARGET_MUSH) as Mushroom
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDogamDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mushroom = mMush
        binding.btnClose.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        val adapter = HistoryPicturesAdapter()
        adapter.submitList(mMush!!.myHistory)
        binding.myMushHistory.adapter = adapter
    }


}