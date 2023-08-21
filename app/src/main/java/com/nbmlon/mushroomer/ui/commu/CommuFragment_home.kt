package com.nbmlon.mushroomer.ui.commu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.databinding.FragmentCommuHomeBinding
import com.nbmlon.mushroomer.ui.camera.CameraFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import taimoor.sultani.sweetalert2.Sweetalert

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CommuFragment_home.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommuFragment_home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentCommuHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterQnA : AdapterHomePost
    private lateinit var adapterFree : AdapterHomePost
    private lateinit var adapterPic : AdapterHomePost

    private val viewModel : CommuViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
            openQnABoard.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer, CommuFragment_text.getInstance(BoardType.QnABoard.ordinal))
                    .addToBackStack(null)
                    .commit()
            }
            //자유 게시판 열기
            openFreeBoard.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer, CommuFragment_text.getInstance(BoardType.FreeBoard.ordinal))
                    .addToBackStack(null)
                    .commit()
            }
            //사진 게시판 열기
            openPicBoard.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer, CommuFragment_image())
                    .addToBackStack(null)
                    .commit()
            }


            //인기게시판 열기
            openHotBoard.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer, CommuFragment_hot())
                    .addToBackStack(null)
                    .commit()
            }
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CommunityFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CommuFragment_home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}