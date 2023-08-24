package com.nbmlon.mushroomer.ui.commu

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.FragmentCommuPostBinding
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val TARGET_POST = "target_post_display"

class CommuFragment_post private constructor(): Fragment(), PopupMenuClickListener{
    companion object {
        const val TAG= "CommuFragment_post"
        @JvmStatic
        fun getInstance(param1: Post) =
            CommuFragment_post().apply {
                arguments = Bundle().apply {
                    putSerializable(TARGET_POST, param1)
                }
            }
    }

    private var targetPost: Post? = null
    private var _binding: FragmentCommuPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CommuViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                targetPost = it.getSerializable(TARGET_POST, Post::class.java)
            }else{
                targetPost = it.getSerializable(TARGET_POST) as Post
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommuPostBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            post = targetPost
            boardType.text = resources.getString(post?.boardType!!.boardNameResId)
            targetPost?.comments?.let{comments ->
                commentRV.adapter = AdapterPostComment(this@CommuFragment_post as PopupMenuClickListener).apply { submitList(comments) }
            }
            btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            btnPostMore.setOnClickListener {  }
        }
    }



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    private fun showContextMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.post_context_menu, popupMenu.menu)
        popupMenu.apply {
            menu?.findItem(R.id.menuForOwner)?.isVisible = targetPost?.writer == AppUser.user
            menu?.findItem(R.id.menuForComment)?.isVisible = false

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.report -> {
                        onClickReport(targetPost,null)
                        true
                    }
                    R.id.modify_post_or_comment -> {
                        TODO("수정 요구 필요")
                        onClickModify(targetPost,null)
                        true
                    }
                    R.id.delete_post_or_comment -> {
                        TODO("재확인 필요")
                        onClickDel(targetPost,null)
                        true
                    }
                    R.id.write_reply -> {
                        error("포스트에서 답글 추가 요청")
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    /** PopupMenu Listener For Comment **/
    override fun onClickReport(target_post: Post?, target_comment: Comment?) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, CommuFragment_report.newInstance(target_post,target_comment))
            .addToBackStack(null)
            .commit()
    }

    override fun onClickDel(target_post: Post?, target_comment: Comment?) {
        //포스트 삭제
        if (target_post != null && target_comment == null){

        }
        //댓글 삭제       
        else if ( target_post == null && target_comment != null ){

        }
        TODO("서버 삭제 구현")
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.requestDelete(null,target_comment)
        }
    }

    override fun onClickModify(target_post: Post?, target_comment: Comment?) {
        //포스트 수정
        if (target_post != null && target_comment == null){

        }
        //댓글 수정     
        else if ( target_post == null && target_comment != null ){

        }
        TODO("서버 수정 구현")
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.requestModify(null, target_comment)
        }

    }

    override fun onClickWriteReply(target_comment: Comment) {
        TODO("서버 추가 구현")
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.requestUpload(null, target_comment)
        }
    }

}

