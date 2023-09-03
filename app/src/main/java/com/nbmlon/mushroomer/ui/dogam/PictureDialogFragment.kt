package com.nbmlon.mushroomer.ui.dogam

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nbmlon.mushroomer.R
import com.nbmlon.mushroomer.data.posts.CommuHomeRepository
import com.nbmlon.mushroomer.databinding.FragmentPictureDialogBinding
import com.nbmlon.mushroomer.model.MushHistory
import com.nbmlon.mushroomer.ui.commu.BoardType
import com.nbmlon.mushroomer.ui.commu.CommuFragmentBoard_img
import com.nbmlon.mushroomer.ui.commu.CommuFragment_home
import com.nbmlon.mushroomer.ui.commu.CommuFragment_write
import com.nbmlon.mushroomer.ui.map.MapFragment

private const val TARGET_MUSH_HISTORY = "target_mush_history"
private const val DIALOG_CALL_FROM = "dialog_call_from"


/**
 * 작업 요함
 */
class PictureDialogFragment private constructor(): DialogFragment() {
    companion object {
        const val TAG = "PictureDialogFragment"

        @JvmStatic
        fun getInstance(target : MushHistory, callFrom : PictureDialogFrom) =
            PictureDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TARGET_MUSH_HISTORY, target)
                    putInt(DIALOG_CALL_FROM, callFrom.ordinal)
                }
            }
    }
    private var _binding : FragmentPictureDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMushHistory : MushHistory
    private lateinit var callFrom : PictureDialogFrom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mMushHistory = it.getSerializable(TARGET_MUSH_HISTORY, MushHistory::class.java)!!
            } else{
                mMushHistory = it.getSerializable(TARGET_MUSH_HISTORY) as MushHistory
            }
            callFrom = PictureDialogFrom.values()[it.getInt(DIALOG_CALL_FROM)]
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPictureDialogBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnGoAnother.setOnClickListener { goAnother(); this@PictureDialogFragment.dismiss() }
            btnGoBoard.setOnClickListener { goPicBoard(); this@PictureDialogFragment.dismiss() }
            btnClose.setOnClickListener { this@PictureDialogFragment.dismiss() }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    private fun goAnother(){
        when (callFrom){
            PictureDialogFrom.DogamFrag->{
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer,MapFragment.getFocusedFor(mMushHistory))
                    .commit()
            };
            PictureDialogFrom.MapFrag->{
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer,DogamFragment.openDetail(mMushHistory))
                    .commit()
            }
        }
    }

    private fun goPicBoard(){
        val picBoard_idx = BoardType.PicBoard.ordinal
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, CommuFragment_home())
            .add(CommuFragmentBoard_img.getInstance(picBoard_idx), CommuFragmentBoard_img.TAG)
            .add(CommuFragment_write.getInstance(picBoard_idx,mushHistory = mMushHistory), CommuFragment_write.TAG)
            .commit()
    }

}

enum class PictureDialogFrom{
    MapFrag,
    DogamFrag
}