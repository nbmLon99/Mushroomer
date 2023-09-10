package com.nbmlon.mushroomer.ui.commu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.FragmentCommuHistoryBinding
import com.nbmlon.mushroomer.model.Post
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
     내 댓글, 내 포스트 띄워주는 창
 */
class CommuFragment_history private constructor():  Fragment(), PostClickListener {
    companion object {
        const val TAG = "CommuFragment_history"
        @JvmStatic
        fun getInstance(bt: BoardType, isPost :Boolean) =
            CommuFragment_history().apply {
                arguments = Bundle().apply {
                    putInt(BOARD_TYPE_ORDINAL, bt.ordinal)
                    putBoolean(HISTORY_FOR_MY_POST, isPost)
                }
            }
    }


    private lateinit var mBoardType : BoardType
    private var forMyPost : Boolean = true
    var _binding : FragmentCommuHistoryBinding? = null
    val binding get() = _binding!!

    private lateinit var myPostAdpater : AdapterBoardPost


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mBoardType = BoardType.values()[it.getInt(BOARD_TYPE_ORDINAL)]
            forMyPost = it.getBoolean(HISTORY_FOR_MY_POST)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommuHistoryBinding.inflate(layoutInflater)
        val viewModel = ViewModelBoardHistory(forMyPost)
        myPostAdpater = AdapterBoardPost(mBoardType, this@CommuFragment_history::openPost)

        lifecycleScope.launch{
            viewModel.pagingDataFlow.collectLatest{myPostAdpater.submitData(it)}
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            boardTitle.text = resources.getString(mBoardType.boardNameResId)
            btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            historyRV.adapter = myPostAdpater
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun openPost(post: Post) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, CommuFragment_post.getInstance(post),CommuFragment_post.TAG)
            .addToBackStack(null)
            .commit()
    }
}

private const val HISTORY_FOR_MY_POST = "for_my_post"