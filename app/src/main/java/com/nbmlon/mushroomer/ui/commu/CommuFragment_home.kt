package com.nbmlon.mushroomer.ui.commu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.FragmentCommuHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import taimoor.sultani.sweetalert2.Sweetalert

class CommuFragment_home : Fragment() {
    companion object {
        const val TAG = "COMMUFRAGMENT_HOME"
    }

    private var _binding: FragmentCommuHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterQnA : AdapterHomePost
    private lateinit var adapterFree : AdapterHomePost
    private lateinit var adapterPic : AdapterHomePost

    private val viewModel : CommuViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommuHomeBinding.inflate(layoutInflater, container, false)
        adapterFree =  AdapterHomePost()
        adapterPic =  AdapterHomePost()
        adapterQnA =  AdapterHomePost()

        val loading = Sweetalert(requireActivity(),Sweetalert.PROGRESS_TYPE)
        loading.apply {
            setTitleText(R.string.loading)
            setCancelable(false)
            show()
        }

        viewModel.recentPostsForDisplay.observe(viewLifecycleOwner){item ->
            adapterFree.submitList(item.newFreePosts)
            adapterPic.submitList(item.newPicPosts)
            adapterQnA.submitList(item.newQnAPosts)
            loading.dismissWithAnimation()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            QnABoardRV.adapter = adapterQnA
            freeBoardRV.adapter = adapterFree
            picBoardRV.adapter = adapterPic

            //QnA 게시판 열기
            openQnABoard.setOnClickListener     { openBoard(BoardType.QnABoard) }
            //자유 게시판 열기
            openFreeBoard.setOnClickListener    { openBoard(BoardType.FreeBoard) }
            //사진 게시판 열기
            openPicBoard.setOnClickListener     { openBoard(BoardType.PicBoard) }
            //인기게시판 열기
            openHotBoard.setOnClickListener     { openBoard(BoardType.HotBoard) }

            //내 댓글 열기
            openMyComment.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer, CommuFragment_history())
                    .addToBackStack(null)
                    .commit()
            }
            //내 포스트 열기
            openMyPost.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer, CommuFragment_history())
                    .addToBackStack(null)
                    .commit()
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.loadRecentPosts()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }



    private fun openBoard(boardType: BoardType) {
        val boardTypeOrdinal = boardType.ordinal
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, CommuFragmentBoard.getInstance(boardTypeOrdinal))
            .addToBackStack(null)
            .commit()
    }
}