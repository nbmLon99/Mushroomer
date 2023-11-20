package com.nbmlon.mushroomer.ui.dogam

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.databinding.FragmentDogamDetailBinding
import com.nbmlon.mushroomer.domain.DogamUseCaseReqeust
import com.nbmlon.mushroomer.domain.DogamUseCaseResponse
import com.nbmlon.mushroomer.model.MushHistory
import com.nbmlon.mushroomer.model.Mushroom
import com.nbmlon.mushroomer.ui.dialog_picture.PictureDialogFragment
import com.nbmlon.mushroomer.ui.dialog_picture.PictureDialogFrom
import taimoor.sultani.sweetalert2.Sweetalert

private const val TARGET_MUSH = "mush"


/**
 * 특정 버섯 표시하는 도감 디테일
 */
class DogamFragment_detail private constructor(): Fragment(), DogamHistoryClickListener{

    companion object {
        const val TAG ="DogamFragment_Detail"
        @JvmStatic
        fun newInstance(mush: Mushroom) =
            DogamFragment_detail().apply {
                arguments = Bundle().apply {
                    putSerializable(TARGET_MUSH, mush)
                }
            }
    }
    // TODO: Rename and change types of parameters
    private var mMush : Mushroom? = null

    private var _binding: FragmentDogamDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DogamDetailViewModel>()
    private lateinit var loading : Sweetalert

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mMush = it.getSerializable(TARGET_MUSH, Mushroom::class.java)
            } else {
                mMush = it.getSerializable(TARGET_MUSH) as Mushroom
            }
        }
        viewModel.getMush(
            DogamUseCaseReqeust.SpecificDogamRequestDomain(mMush!!.dogamNo)
        )
        viewModel.response.observe(viewLifecycleOwner, ::bindResponse)
        loading = Sweetalert(context, Sweetalert.PROGRESS_TYPE).apply {
            setCancelable(false)
            show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDogamDetailBinding.inflate(inflater, container, false)

        return binding.root
    }
    
    override fun openPictureDialog(clickedMushHistory: MushHistory) {
        PictureDialogFragment.getInstance(target= clickedMushHistory,callFrom= PictureDialogFrom.DogamFrag)
            .show(requireActivity().supportFragmentManager, PictureDialogFragment.TAG)
    }


    private fun bindResponse(response : DogamUseCaseResponse.SpecificDogamResponse){
        val alert = Sweetalert(context, Sweetalert.BUTTON_CANCEL).apply {
            setCancelButton(getString(R.string.confirm)){
                it.dismiss()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        loading.dismiss()
        if(response.success){
            binding.apply {
                mushroom = mMush
                btnClose.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
                myMushHistory.adapter = HistoryPicturesAdapter( this@DogamFragment_detail::openPictureDialog ).apply {
                    submitList(mMush!!.myHistory)
                }
                if(!mMush!!.gotcha)
                    myMushPicturesFrame.visibility = View.GONE
            }
        }else if (response.code == NETWORK_ERROR_CODE){
            alert.setTitleText(getString(R.string.network_error_msg)).show()
        }else{
            alert.setTitleText(getString(R.string.error_msg)).show()
        }
    }
}