package com.nbmlon.mushroomer.ui.commu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nbmlon.mushroomer.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CommuFragment_post.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommuFragment_post : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_commu_post, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PostFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CommuFragment_post().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


/**
 *
 * val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
val radioButton1 = findViewById<RadioButton>(R.id.radio_button_1)
val radioButton2 = findViewById<RadioButton>(R.id.radio_button_2)

radioGroup.setOnCheckedChangeListener { _, checkedId ->
when (checkedId) {
R.id.radio_button_1 -> {
radioButton1.setTypeface(null, Typeface.BOLD)
radioButton1.paintFlags = radioButton1.paintFlags or Paint.UNDERLINE_TEXT_FLAG

radioButton2.setTypeface(null, Typeface.NORMAL)
radioButton2.paintFlags = radioButton2.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
}
R.id.radio_button_2 -> {
radioButton2.setTypeface(null, Typeface.BOLD)
radioButton2.paintFlags = radioButton2.paintFlags or Paint.UNDERLINE_TEXT_FLAG

radioButton1.setTypeface(null, Typeface.NORMAL)
radioButton1.paintFlags = radioButton1.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
}
}
}
 */