package com.nbmlon.mushroomer.ui.commu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.FragmentCommuHotBinding
import com.nbmlon.mushroomer.databinding.FragmentCommuSearchBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val SEARCH_BOARD_TYPE = "search_board_type"

/**
 * 게시글 검색
 */
class CommuFragment_search : Fragment() {
    // TODO: Rename and change types of parameters
    private var search_board_type: Int? = null
    private var _binding: FragmentCommuSearchBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            search_board_type = it.getInt(SEARCH_BOARD_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCommuSearchBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun getInstance(param1: Int) =
            CommuFragment_search().apply {
                arguments = Bundle().apply {
                    putInt(SEARCH_BOARD_TYPE, param1)
                }
            }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}