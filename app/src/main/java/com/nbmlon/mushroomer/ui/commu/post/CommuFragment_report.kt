package com.nbmlon.mushroomer.ui.commu.post

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest
import com.nbmlon.mushroomer.databinding.FragmentCommuReportBinding
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.board.ViewModelBoard

/**
 * 게시글 / 댓글 신고 Fragment
 */

//  TODO : 다이얼로그 프래그먼트로 바꿔야함
class CommuFragment_report private constructor() : DialogFragment() {
    private var targetPost: Post? = null
    private var targetComment: Comment? = null
    private var _binding: FragmentCommuReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var cl : ReportDialogClickListener
    private lateinit var targetType : TargetType
    private val viewModel by viewModels<ViewModelBoard>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                targetPost = it.getSerializable(TARGET_POST, Post::class.java)
                targetComment = it.getSerializable(TARGET_COMMENT, Comment::class.java)
            }else{
                targetPost = it.getSerializable(TARGET_POST) as Post?
                targetComment = it.getSerializable(TARGET_COMMENT) as Comment?
            }
        }

        targetType = if (targetPost != null) TargetType.POST else TargetType.COMMENT
        if (parentFragment is ReportDialogClickListener)
            cl = parentFragment as ReportDialogClickListener
    }

    override fun onStart() {
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommuReportBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            post = targetPost
            comment = targetComment
            btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            btnReport.setOnClickListener {
                cl.onDialogReportBtnClicked(
                    CommuPostUseCaseRequest.ReportRequestDomain(
                        type = targetType,
                        id = (post?.id ?: comment?.id)!!
                    )
                )
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(postToReport : Post?, commentToReport: Comment?) =
            CommuFragment_report().apply {
                arguments = Bundle().apply {
                    putSerializable(TARGET_POST, postToReport)
                    putSerializable(TARGET_COMMENT, commentToReport)
                }
            }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}

private const val TARGET_POST = "target_post"
private const val TARGET_COMMENT = "target_comment"

