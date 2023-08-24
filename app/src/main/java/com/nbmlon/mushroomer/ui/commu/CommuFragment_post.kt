package com.nbmlon.mushroomer.ui.commu

import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.R
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
        registerForContextMenu(binding.btnMore)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            post = targetPost
            boardType.text = resources.getString(post?.boardType!!.boardNameResId)
            targetPost?.comments?.let{comments ->
                commentRV.adapter = AdapterPostComment().apply { submitList(comments) }
            }
            btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }
    }

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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = MenuInflater(requireContext())
        inflater.inflate(R.menu.post_context_menu, menu) // R.menu.context_menu는 콘텍스트 메뉴 리소스

        menu?.findItem(R.id.menuForOwner)?.isVisible = targetPost?.writer == AppUser.user

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return super.onContextItemSelected(item)
        when (item?.itemId) {
            // 신고버튼
            R.id.report -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer, CommuFragment_report.newInstance(targetPost,null))
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.modify_post -> {
                return true
            }
            R.id.delete_post -> {
                return true
            }
            // 다른 메뉴 아이템에 대한 처리 추가
        }
        return false
    }

}

