package com.nbmlon.mushroomer.ui.commu

import android.os.Bundle
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.FragmentCommuHomeBinding
import com.nbmlon.mushroomer.model.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import taimoor.sultani.sweetalert2.Sweetalert

class CommuFragment_home : Fragment(), PostClickListener {
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
        adapterFree =  AdapterHomePost(this@CommuFragment_home::openPost)
        adapterPic =  AdapterHomePost(this@CommuFragment_home::openPost)
        adapterQnA =  AdapterHomePost(this@CommuFragment_home::openPost)

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
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.loadRecentPosts()
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
                    .replace(R.id.FragmentContainer, CommuFragment_history(),CommuFragment_history.TAG)
                    .addToBackStack(null)
                    .commit()
            }
            //내 포스트 열기
            openMyPost.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer, CommuFragment_history(),CommuFragment_history.TAG)
                    .addToBackStack(null)
                    .commit()
            }
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }



    private fun openBoard(boardType: BoardType) {
        val boardTypeOrdinal = boardType.ordinal
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, CommuFragmentBoard.getInstance(boardTypeOrdinal),CommuFragmentBoard.TAG)
            .addToBackStack(null)
            .commit()
    }

    override fun openPost(post: Post) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, CommuFragment_post.getInstance(post),CommuFragment_post.TAG)
            .addToBackStack(null)
            .commit()
    }



    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        MenuInflater(requireContext()).inflate(R.menu.post_context_menu,menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return super.onContextItemSelected(item)
        when(item.itemId){

        }
    }
}