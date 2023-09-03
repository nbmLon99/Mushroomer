package com.nbmlon.mushroomer.ui.commu

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.databinding.FragmentCommuWriteBinding
import com.nbmlon.mushroomer.model.MushHistory
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.camera.ImageDeleteListner
import org.joda.time.DateTime


/**
 * 게시판 글 쓰기 화면
 * @param boardTypeIdx : 글쓰기를 요청한 게시판의 BoardType의 ord를 제공받음
 * @param mushHistory :  사진으로부터 바로 글 작성 요청한 경우
 */
class CommuFragment_write private constructor() : Fragment(), ImageDeleteListner {
    companion object {
        const val TAG = "CommuFragment_write"
        private val REQUIRED_PERMISSIONS = Manifest.permission.READ_EXTERNAL_STORAGE
        private const val GALLERY_PERMISSION_REQUEST_CODE = 700
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

    private val viewModel  by viewModels<ViewModelPostWriting>()
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result->
        onGalleryResult(result)
    }

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
        getPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommuWriteBinding.inflate(layoutInflater)
        getPermission()
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageAdapter =PicturesAdapterForWriting(this@CommuFragment_write as ImageDeleteListner)

        //이미지 반영
        viewModel.images.observe(viewLifecycleOwner){ items->
            if(items.size <= 0){
                binding.pictureRV.visibility = View.GONE
            }else{
                binding.pictureRV.visibility = View.VISIBLE
            }
            imageAdapter.submitList(items.toList())
        }


        binding.apply {
            pictureRV.adapter = imageAdapter
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
            btnAddPic.setOnClickListener {
//                if (ContextCompat.checkSelfPermission(
//                        requireContext(), REQUIRED_PERMISSIONS) != PackageManager.PERMISSION_GRANTED
//                ){
//                    //Toast.makeText(requireActivity(),"사진 등록을 위해서는 권한이 필요합니다.",Toast.LENGTH_SHORT).show()
//                }
                galleryLauncher.launch(
                    Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    }
                )
            }
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun getPermission(){
        //읽기 권한 있음
        if(ContextCompat.checkSelfPermission(
            requireContext(), REQUIRED_PERMISSIONS) == PackageManager.PERMISSION_GRANTED)
        else{
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(REQUIRED_PERMISSIONS), GALLERY_PERMISSION_REQUEST_CODE)
        }
    }

    /** 갤러리로부터 가져온 이미지 후처리 (ViewModel로 넘김) **/
    private fun onGalleryResult(result: androidx.activity.result.ActivityResult){
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                if (data.clipData != null) {
                    // 다중 이미지 선택 시
                    val count = data.clipData!!.itemCount
                    val imageUris = arrayListOf<Uri>()
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        imageUris.add(imageUri)
                    }
                    viewModel.addUris(imageUris)

                } else if (data.data != null) {
                    // 단일 이미지 선택 시
                    val imageUri = data.data!!
                    viewModel.addUris(arrayListOf(imageUri))
                }
            }
        }
    }

    override fun deleteImage(idx: Int) {
        viewModel.delUri(idx)
    }

}


private const val CALL_HISTORY_WRITING = "call_history_writing"