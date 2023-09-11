package com.nbmlon.mushroomer.ui.commu.post

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.api.dto.CommuPostRequestDTO
import com.nbmlon.mushroomer.databinding.FragmentCommuPostBinding
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.dialog_picture.ImageSliderAdapter
import taimoor.sultani.sweetalert2.Sweetalert

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class CommuFragment_post private constructor(): Fragment(), PopupMenuClickListener, ReportDialogClickListener {
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
    private val viewModel by viewModels<ViewModelPost>()
    private lateinit var loading : Sweetalert
    private var onResponseSuccess : (() -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                targetPost = it.getSerializable(TARGET_POST, Post::class.java)
            }else{
                targetPost = it.getSerializable(TARGET_POST) as Post
            }
        }
        loading = Sweetalert(context, Sweetalert.PROGRESS_TYPE).apply { setCancelable(false) }
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
            if(post.images?.size ?: 0 > 0)
                imageSlider.setSliderAdapter(ImageSliderAdapter(post.images!!.toList()))

            boardType.text = resources.getString(post?.boardType!!.boardNameResId)
            targetPost?.comments?.let{comments ->
                commentRV.adapter = AdapterPostComment(this@CommuFragment_post as PopupMenuClickListener).apply { submitList(comments) }
            }
            btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            btnPostMore.setOnClickListener { showContextMenu(btnPostMore) }
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
            val isMine = targetPost?.writer == AppUser.user
            menu?.findItem(R.id.report)?.isVisible = !isMine
//            menu?.setGroupVisible(R.id.menuForOwner,true)
            menu?.setGroupVisible(R.id.menuForOwner, isMine)
            menu?.setGroupVisible(R.id.menuForComment,false)

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.report -> {
                        openReportDialog(targetPost,null)
                        true
                    }
                    R.id.modify_post_or_comment -> {
                        onClickModify(targetPost,null)
                        true
                    }
                    R.id.delete_post_or_comment -> {
                        Sweetalert(context, Sweetalert.NORMAL_TYPE)
                            .setTitleText("정말로 삭제하시겠습니까?")
                            .setCancelButton("네"){
                                onClickDel(targetPost,null)
                                it.dismissWithAnimation()
                            }
                            .setNeutralButton("아니요"){
                                it.dismissWithAnimation()
                            }
                            .show()
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
    override fun openReportDialog(target_post: Post?, target_comment: Comment?) {
        CommuFragment_report.newInstance(target_post,target_comment).show(
            requireActivity().supportFragmentManager, TAG
        )
    }
    override fun onDialogReportBtnClicked(type: TargetType, dto: CommuPostRequestDTO.ReportDTO) {
        viewModel.request(
            CommuPostRequest.ForReport(
                targetType = type,
                dto = dto)
        )
        loading.show()
    }


    override fun onClickDel(target_post: Post?, target_comment: Comment?) {
        //포스트 삭제
        if (target_post != null && target_comment == null){
            viewModel.request(
                CommuPostRequest.ForDelete(
                    targetType = TargetType.POST,
                    dto = dto)
            )
        }
        //댓글 삭제       
        else if ( target_post == null && target_comment != null ){
            viewModel.request(
                CommuPostRequest.ForDelete(
                    targetType = TargetType.COMMENT,
                    dto = dto)
            )
        }
        loading.show()
    }

    override fun onClickModify(target_post: Post?, target_comment: Comment?) {
        //포스트 수정
        if (target_post != null && target_comment == null){
            viewModel.request(
                CommuPostRequest.ForReport(
                    targetType = TargetType.POST,
                    dto = dto)
            )
        }
        //댓글 수정     
        else if ( target_post == null && target_comment != null ){
            viewModel.request(
                CommuPostRequest.ForModify(
                    targetType = TargetType.COMMENT,
                    dto = target_comment)
            )
        }
        loading.show()
    }

    // 
    override fun onClickWriteReply(target_comment: Comment) {
        //TODO : 클릭 -> 댓글 작성 버튼 위에 답글 띄우고 댓글 업로드 버튼을 그걸로 바꿔야함
        viewModel.request(
            CommuPostRequest.ForWriteReply(
                targetType = TargetType.COMMENT,
                dto = target_comment)
        )
    }


    /** 서버 요청에 대한 결과값 처리 **/
    private fun responseObserver(response : CommuPostResponse){
        if(loading.isShowing)
            loading.dismissWithAnimation()
        when(response){
            is CommuPostResponse.SuccessResponse ->{
                if(response.dto.success)
                    onResponseSuccess?.let{ it() }
            }
        }
    }

    private fun onSuccessReport(){
        Sweetalert(this@CommuFragment_post.context,Sweetalert.BUTTON_NEUTRAL).apply {
            titleText = "신고 접수되었습니다!"
            setNeutralButton("확인"){this.dismissWithAnimation()}
        }
    }


}



private const val TARGET_POST = "target_post_display"
