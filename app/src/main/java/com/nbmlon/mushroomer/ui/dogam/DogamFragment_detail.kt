package com.nbmlon.mushroomer.ui.dogam

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nbmlon.mushroomer.databinding.FragmentDogamDetailBinding
import com.nbmlon.mushroomer.model.Mushroom


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "mush"

/**
 * A simple [Fragment] subclass.
 * Use the [DogamFragment_detail.newInstance] factory method to
 * create an instance of this fragment.
 */
class DogamFragment_detail : Fragment() {
    // TODO: Rename and change types of parameters
    private var mMush : Mushroom? = null

    private var _binding: FragmentDogamDetailBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mMush = it.getSerializable(ARG_PARAM1, Mushroom::class.java)
            } else {
                mMush = it.getSerializable(ARG_PARAM1) as Mushroom
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
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DogamDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(mush: Mushroom) =
            DogamFragment_detail().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, mush)
                }
            }
    }
}