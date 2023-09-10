package com.nbmlon.mushroomer.ui.commu

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.databinding.FragmentCommuReportBinding
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val TARGET_POST = "target_post"
private const val TARGET_COMMENT = "target_comment"

/**
 * 게시글 / 댓글 신고 Fragment
 */
class CommuFragment_report private constructor() : Fragment() {
    // TODO: Rename and change types of parameters
    private var targetPost: Post? = null
    private var targetComment: Comment? = null
    private var _binding: FragmentCommuReportBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ViewModelBoard>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                targetPost = it.getSerializable(TARGET_POST , Post::class.java)
                targetComment = it.getSerializable(TARGET_COMMENT, Comment::class.java)
            }else{
                targetPost = it.getSerializable(TARGET_POST ) as Post?
                targetComment = it.getSerializable(TARGET_COMMENT) as Comment?
            }
        }
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
                CoroutineScope(Dispatchers.Main).launch {  viewModel.requestReport(targetPost, targetComment) }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance( postToReport : Post?, commentToReport: Comment?) =
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