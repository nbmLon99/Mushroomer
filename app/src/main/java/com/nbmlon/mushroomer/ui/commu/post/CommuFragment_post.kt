package com.nbmlon.mushroomer.ui.commu.post

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest
import com.nbmlon.mushroomer.databinding.DialogEdittextBinding
import com.nbmlon.mushroomer.databinding.FragmentCommuPostBinding
import com.nbmlon.mushroomer.domain.CommuPostUseCaseResponse
import com.nbmlon.mushroomer.domain.TargetType
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.board.CommuFragment_write
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

    private lateinit var targetPost: Post
    private var _binding: FragmentCommuPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ViewModelPost>()
    private lateinit var loading : Sweetalert

    private lateinit var commentAdapter : AdapterPostComment
    private var replyFor : Comment? = null

    //response 올 떄 실행시킬 함수 등록
    private var onResponseSuccess : (() -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                targetPost = it.getSerializable(TARGET_POST, Post::class.java) ?: viewModel.mPost.value!!
            }else{
                targetPost = it.getSerializable(TARGET_POST) as? Post ?: viewModel.mPost.value!!
            }
        }
        //targetPost 로딩 요청
        viewModel.request(
            CommuPostUseCaseRequest.LoadPostRequestDomain(targetPost.id)
        )

        //targetPost 로딩전까지 로딩스피너
        loading = Sweetalert(context, Sweetalert.PROGRESS_TYPE).apply { setCancelable(false) }
        loading.show()

        //targetPost 로드되면 UI 반영
        viewModel.mPost.observe(viewLifecycleOwner){
            loading.dismissWithAnimation()
            bindingPost(it)
        }

        //결과값 UI 반영하는 옵저버 등록
        viewModel.response.observe(viewLifecycleOwner, ::responseObserver)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommuPostBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun bindingPost(targetPost: Post){
        binding.apply {
            post = targetPost
            if(post.images?.size ?: 0 > 0)
                imageSlider.setSliderAdapter(ImageSliderAdapter(post.images!!.toList()))

            boardType.text = resources.getString(post?.boardType!!.boardNameResId)
            targetPost?.comments?.let{comments ->
                commentAdapter = AdapterPostComment(this@CommuFragment_post as PopupMenuClickListener)
                commentRV.adapter = commentAdapter.apply { submitList(comments) }
            }
            btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            btnPostMore.setOnClickListener { showContextMenu(btnPostMore) }
            replyCancel.setOnClickListener {
                replyFor = null
                setReplyTarget(replyFor)
            }
        }
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
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.FragmentContainer,
                                CommuFragment_write.getModifyFragment(targetPost = targetPost),
                                CommuFragment_write.TAG
                            )
                            .addToBackStack(null)
                            .commit()

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
    override fun onDialogReportBtnClicked(domain: CommuPostUseCaseRequest.ReportRequestDomain) {
        viewModel.request(domain)
        onResponseSuccess = {
            Sweetalert(this@CommuFragment_post.context, Sweetalert.BUTTON_NEUTRAL)
                .setTitleText("신고되었습니다!")
                .setCancelButton("확인"){it.dismissWithAnimation()}
                .show()
        }
        loading.show()
    }


    override fun onClickDel(target_post: Post?, target_comment: Comment?) {
        //포스트 삭제
        if (target_post != null && target_comment == null){
            viewModel.request(
                CommuPostUseCaseRequest.DeleteRequestDomain(
                    type = TargetType.POST ,
                    id = target_post.id
                )
            )
            onResponseSuccess = {
                Sweetalert(this@CommuFragment_post.context, Sweetalert.BUTTON_NEUTRAL)
                    .setTitleText("삭제되었습니다!")
                    .setCancelButton("확인"){
                        it.dismissWithAnimation()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                    .show()
            }

        }
        //댓글 삭제       
        else if ( target_post == null && target_comment != null ){
            viewModel.request(
                CommuPostUseCaseRequest.DeleteRequestDomain(
                    type = TargetType.COMMENT ,
                    id = target_comment.id
                )
            )
            onResponseSuccess = {
                Sweetalert(this@CommuFragment_post.context, Sweetalert.BUTTON_NEUTRAL)
                    .setTitleText("삭제되었습니다!")
                    .setCancelButton("확인"){
                        targetPost?.comments?.apply{
                            remove(target_comment)
                            commentAdapter.submitList(this)
                        }
                        it.dismissWithAnimation()
                    }
                    .show()
            }
        }
        loading.show()
    }

    override fun onClickModify(target_post: Post?, target_comment: Comment?, ) {
        //댓글 수정
        if ( target_post == null && target_comment != null ){
            Sweetalert(context, Sweetalert.NORMAL_TYPE).apply {
                val dialogBinding = DialogEdittextBinding.inflate(layoutInflater).apply {
                    editText.setText(target_comment?.content)
                }
                val editText = dialogBinding.editText
                setCustomView(dialogBinding.root)
                setCancelButton("수정"){
                    requestModify(target_comment,editText.text.toString())
                    it.dismissWithAnimation()
                }
                setNeutralButton(resources.getString(R.string.cancel)){
                    it.dismissWithAnimation()
                }
                show()
            }
        }
        loading.show()
    }
    private fun requestModify(target : Comment, modifiedContent : String){
        viewModel.request(
            CommuPostUseCaseRequest.ModifyCommentRequestDomain(
                target = target,
                modified = modifiedContent
            )
        )
        onResponseSuccess = {
            Sweetalert(this@CommuFragment_post.context, Sweetalert.BUTTON_NEUTRAL)
                .setTitleText("수정되었습니다!")
                .setCancelButton("확인"){
                    targetPost?.comments?.apply{
                        this[indexOf(target)].content = modifiedContent
                        commentAdapter.submitList(this)
                    }
                    it.dismissWithAnimation()
                }
                .show()
        }
    }

    // 
    override fun onClickWriteReply(target_comment: Comment) {
        replyFor = target_comment
        binding.setReplyTarget(replyFor)
    }


    /** 서버 요청에 대한 결과값 처리 **/
    private fun responseObserver(response : CommuPostUseCaseResponse){
        if(loading.isShowing)
            loading.dismissWithAnimation()
        when(response){
            is CommuPostUseCaseResponse.SuccessResponseDomain ->{
                if(response.success){
                    onResponseSuccess?.let{ it() }
                    onResponseSuccess = null
                }else{
                    Toast.makeText(this.context,"실패하였습니다.",Toast.LENGTH_SHORT).show()
                }
            }
            else ->{
                error("로드가 선택됨")
            }
        }
    }

    private fun onSuccessReport(){
        Sweetalert(this@CommuFragment_post.context,Sweetalert.BUTTON_NEUTRAL).apply {
            titleText = "신고 접수되었습니다!"
            setNeutralButton("확인"){this.dismissWithAnimation()}
        }
    }

    fun FragmentCommuPostBinding.setReplyTarget(target : Comment?){
        if (target != null){
            replyTargetFrame.visibility = View.VISIBLE
            replyTargetContent.text = target.content
        }
        else{
            replyTargetFrame.visibility = View.GONE
        }
    }

}



private const val TARGET_POST = "target_post_display"
