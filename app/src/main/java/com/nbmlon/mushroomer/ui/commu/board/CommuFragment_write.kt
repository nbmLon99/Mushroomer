package com.nbmlon.mushroomer.ui.commu.board

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.api.EndConverter
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.databinding.FragmentCommuWriteBinding
import com.nbmlon.mushroomer.domain.CommuWriteUseCaseRequest
import com.nbmlon.mushroomer.domain.CommuWriteUseCaseResponse
import com.nbmlon.mushroomer.model.MushHistory
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.camera.ImageDeleteListner
import org.joda.time.DateTime
import taimoor.sultani.sweetalert2.Sweetalert

/**
 * 게시판 글 쓰기 화면
 * @param boardTypeIdx : 글쓰기를 요청한 게시판의 BoardType의 ord를 제공받음
 * @param mushHistory :  사진으로부터 바로 글 작성 요청한 경우
 */
class CommuFragment_write private constructor() : Fragment(), ImageDeleteListner {
    companion object {
        const val TAG = "CommuFragment_write"
        private val maxImageCount = 5 // 최대 이미지 개수 (예시: 5개)
        private val REQUIRED_PERMISSIONS = Manifest.permission.READ_EXTERNAL_STORAGE
        private const val MODIFY_TARGET_POST = "modify_target_post"
        private const val GALLERY_PERMISSION_REQUEST_CODE = 700
        private lateinit var loading : Sweetalert

        //기본 글쓰기 화면 열기
        @JvmStatic
        fun getInstance(boardTypeIdx: Int, mushHistory: MushHistory? = null) =
            CommuFragment_write().apply {
                arguments = Bundle().apply {
                    putInt(BOARD_TYPE_ORDINAL, boardTypeIdx)
                    mushHistory?.let { putSerializable(CALL_HISTORY_WRITING,it) }
                }
            }

        //수정 요구
        @JvmStatic
        fun getModifyFragment(targetPost: Post) =
            CommuFragment_write().apply {
                arguments = Bundle().apply {
                    putSerializable(MODIFY_TARGET_POST, targetPost)
                }
            }
    }
    // TODO: Rename and change types of parameters
    private var board_type_idx : Int? = null
    private lateinit var mBoardType : BoardType

    private var modifyTarget : Post? = null
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
                modifyTarget = it.getSerializable(MODIFY_TARGET_POST,Post::class.java)
            }else{
                mushHistoryForWriting = it.getSerializable(CALL_HISTORY_WRITING) as? MushHistory
                modifyTarget = it.getSerializable(MODIFY_TARGET_POST) as? Post
            }
        }
        getPermission()
        loading = Sweetalert(context,Sweetalert.PROGRESS_TYPE).apply {
            setCancelable(false)
        }
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
        val imageAdapter = PicturesAdapterForWriting(this@CommuFragment_write as ImageDeleteListner)
        setInitialImage()
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
            //수정할 값 반영
            modifyTarget?.let {
                title.setText(it.title)
                content.setText(it.content)
            }
            pictureRV.adapter = imageAdapter
            btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            btnUpload.setOnClickListener {
                val newPostWrited = Post(
                    id = -1,
                    title = title.text.toString(),
                    images = arrayListOf(),
                    content = content.text.toString(),
                    time = DateTime(),
                    writer = AppUser.user!!,
                    comments = arrayListOf(),
                    ThumbsUpCount = 0,
                    myThumbsUp = false,
                    boardType = mBoardType,
                    updated = false
                )
                if(imageAdapter.currentList.size <= 0 && mBoardType == BoardType.PicBoard){
                    Sweetalert(context, Sweetalert.BUTTON_CANCEL)
                        .setTitleText("사진 게시판에는 반드시 하나 이상의 사진 등록이 필요합니다!")
                        .setCancelButton("확인"){it.dismissWithAnimation()}
                        .show()
                }else {
                    loading.show()
                    if(modifyTarget == null){
                        viewModel.request(CommuWriteUseCaseRequest.UploadPostDomain(newPostWrited, EndConverter.urisToParts(this@CommuFragment_write.requireContext(), imageAdapter.currentList)))
                    }else{
                        viewModel.request(CommuWriteUseCaseRequest.ModifiyPostDomain(modifyTarget!!.id ,newPostWrited, EndConverter.urisToParts(this@CommuFragment_write.requireContext(), imageAdapter.currentList)))
                    }
                }
            }
            btnAddPic.setOnClickListener {
                galleryLauncher.launch(
                    Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    }
                )
            }
        }
    }

    private fun setInitialImage() {
        mushHistoryForWriting?.let { target->
            val uris = target.picPath.map { Uri.parse(it) }
            viewModel.addUris(ArrayList(uris))
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun getPermission(){
        //읽기 권한 있음
        if(ContextCompat.checkSelfPermission(
                requireContext(), REQUIRED_PERMISSIONS
            ) == PackageManager.PERMISSION_GRANTED
        )
        else{
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(REQUIRED_PERMISSIONS), GALLERY_PERMISSION_REQUEST_CODE
            )
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

                    // 이미지 개수 체크
                    if (viewModel.images.value?.size ?: 0 + count > maxImageCount) {
                        // 너무 많은 이미지가 선택되었음을 사용자에게 알림
                        // 예: Toast 메시지 표시
                        Toast.makeText(
                            requireActivity(),
                            "최대 ${maxImageCount}개까지 선택 가능합니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        for (i in 0 until maxImageCount) {
                            val imageUri = data.clipData!!.getItemAt(i).uri
                            imageUris.add(imageUri)
                        }
                    }else{
                        for (i in 0 until count) {
                            val imageUri = data.clipData!!.getItemAt(i).uri
                            imageUris.add(imageUri)
                        }
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
    private fun responseObserver(response : CommuWriteUseCaseResponse){
        if(loading.isShowing)
            loading.dismissWithAnimation()
        if(response.success){
            Sweetalert(context,Sweetalert.BUTTON_CANCEL).apply {
                titleText = "성공하였습니다!"
                setCancelButton("확인"){
                    it.dismissWithAnimation()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                show()
            }
        }else if(response.code == NETWORK_ERROR_CODE){
            Toast.makeText(this.context, getString(R.string.network_error_msg), Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this.context, getString(R.string.error_msg), Toast.LENGTH_SHORT).show()
        }
    }

    override fun deleteImage(idx: Int) {
        viewModel.delUri(idx)
    }

}

private const val CALL_HISTORY_WRITING = "call_history_writing"