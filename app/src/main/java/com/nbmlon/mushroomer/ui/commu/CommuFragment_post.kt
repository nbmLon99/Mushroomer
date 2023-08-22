package com.nbmlon.mushroomer.ui.commu

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nbmlon.mushroomer.databinding.FragmentCommuPostBinding
import com.nbmlon.mushroomer.model.Post

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val TARGET_POST = "target_post_display"

class CommuFragment_post : Fragment() {
    // TODO: Rename and change types of parameters
    private var targetPost: Post? = null

    private var _binding: FragmentCommuPostBinding? = null
    private val binding get() = _binding!!
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
        _binding = FragmentCommuPostBinding.inflate(LayoutInflater.from(context))
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            post = targetPost
            boardType.text = resources.getString(post.boardType.boardNameResId) 
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(param1: Post) =
            CommuFragment_post().apply {
                arguments = Bundle().apply {
                    putSerializable(TARGET_POST, param1)
                }
            }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}

