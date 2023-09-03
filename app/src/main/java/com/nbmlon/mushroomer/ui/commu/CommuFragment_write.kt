package com.nbmlon.mushroomer.ui.commu

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.databinding.FragmentCommuWriteBinding
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.MushHistory
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.model.User
import org.joda.time.DateTime


/**
 * 게시판 글 쓰기 화면
 * @param boardTypeIdx : 글쓰기를 요청한 게시판의 BoardType의 ord를 제공받음
 * @param mushHistory :  사진으로부터 바로 글 작성 요청한 경우
 */
class CommuFragment_write private constructor() : Fragment() {
    companion object {
        const val TAG = "CommuFragment_write"
        @JvmStatic
        fun getInstance(boardTypeIdx: Int, mushHistory: MushHistory? = null) =
            CommuFragment_write().apply {
                arguments = Bundle().apply {
                    putInt(BOARD_TYPE_ORDINAL, boardTypeIdx)
                    mushHistory?.let { putSerializable(CALL_HISTORY_WRITING,it) }
                }
            }
    }
    // TODO: Rename and change types of parameters
    private var board_type_idx : Int? = null
    private lateinit var mBoardType : BoardType

    private var mushHistoryForWriting : MushHistory? = null

    private var _binding: FragmentCommuWriteBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            board_type_idx = it.getInt(BOARD_TYPE_ORDINAL)
            mBoardType = BoardType.values()[board_type_idx!!]

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mushHistoryForWriting = it.getSerializable(CALL_HISTORY_WRITING, MushHistory::class.java)
            }else{
                mushHistoryForWriting = it.getSerializable(CALL_HISTORY_WRITING) as MushHistory
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommuWriteBinding.inflate(layoutInflater)


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            btnUpload.setOnClickListener {
                val newPostWrited = Post(
                    title = "입력값",
                    images = arrayListOf(),
                    content = "내용",
                    time = DateTime(),
                    writer = AppUser.user!!,
                    comments = arrayListOf(),
                    ThumbsUpCount = 0,
                    myThumbsUp = false,
                    boardType = mBoardType,
                    updated = false
                )
                if(newPostWrited.images!!.size <= 0 && mBoardType == BoardType.PicBoard ){
                    TODO("사진 하나이상등록하라고 alert")
                }else{
                    TODO("등록")
                }
            }
            btnAddPic.setOnClickListener { TODO("사진 추가") }
        }
    }



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}


private const val CALL_HISTORY_WRITING = "call_history_writing"