package com.nbmlon.mushroomer.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nbmlon.mushroomer.databinding.FragmentMapBinding
import com.nbmlon.mushroomer.model.MushHistory
import com.nbmlon.mushroomer.ui.dogam.PictureDialogFragment
import com.nbmlon.mushroomer.ui.dogam.PictureDialogFrom

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 *
 */
class MapFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding : FragmentMapBinding? = null
    private val binding get() = _binding!!


    companion object {
        const val TAG = "MapFragment"

        @JvmStatic
        fun getFocusedFor(mushHistory: MushHistory) =
            MapFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnTest.setOnClickListener { testPictureDialog() }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun testPictureDialog(){
        PictureDialogFragment.getInstance(MushHistory.getDummy(),PictureDialogFrom.MapFrag)
            .show(requireActivity().supportFragmentManager, PictureDialogFragment.TAG)
    }
}