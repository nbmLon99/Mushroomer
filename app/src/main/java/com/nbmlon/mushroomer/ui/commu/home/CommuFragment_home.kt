package com.nbmlon.mushroomer.ui.commu.home

import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.api.ResponseCodeConstants.UNDEFINED_ERROR_CODE
import com.nbmlon.mushroomer.databinding.FragmentCommuHomeBinding
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.board.BoardType
import com.nbmlon.mushroomer.ui.commu.board.CommuBoardFragment
import com.nbmlon.mushroomer.ui.commu.board.CommuFragment_history
import com.nbmlon.mushroomer.ui.commu.board.PostClickListener
import com.nbmlon.mushroomer.ui.commu.board.getBoardFragment
import com.nbmlon.mushroomer.ui.commu.post.CommuFragment_post
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

    private val viewModel : ViewModelCommuHome by viewModels()
    private var loading : Sweetalert? = null


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

        loading = Sweetalert(requireActivity(),Sweetalert.PROGRESS_TYPE).apply {
            setTitleText(R.string.loading)
            setCancelable(false)
            show()
        }

        viewModel.recentPostsForDisplay.observe(viewLifecycleOwner){response ->
            if(response.success){
                adapterFree.submitList(response.freeBoardPosts)
                adapterPic.submitList(response.qnaBoardPosts)
                adapterQnA.submitList(response.picBoardPosts)
            }else if(response.code == NETWORK_ERROR_CODE){
                Toast.makeText(requireActivity(),getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show()
            }else if(response.code == UNDEFINED_ERROR_CODE){
                Toast.makeText(requireActivity(),getString(R.string.error_msg), Toast.LENGTH_SHORT).show()
            }
            loading?.dismissWithAnimation()
            loading = null
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
                    .replace(R.id.FragmentContainer, CommuFragment_history.getInstance(BoardType.MyComments, false),
                        CommuFragment_history.TAG)
                    .addToBackStack(null)
                    .commit()
            }
            //내 포스트 열기
            openMyPost.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer, CommuFragment_history.getInstance(BoardType.MyPosts, true),
                        CommuFragment_history.TAG)
                    .addToBackStack(null)
                    .commit()
            }
        }

    }

    override fun onDestroyView() {
        _binding = null
        loading?.let { it.dismissWithAnimation() }
        super.onDestroyView()
    }



    private fun openBoard(boardType: BoardType) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, getBoardFragment(boardType) , CommuBoardFragment.TAG)
            .addToBackStack(null)
            .commit()
    }

    override fun openPost(post: Post) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer,
                CommuFragment_post.getInstance(post),
                CommuFragment_post.TAG
            )
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
