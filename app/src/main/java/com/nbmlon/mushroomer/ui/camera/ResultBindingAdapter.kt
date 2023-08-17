package com.nbmlon.mushroomer.ui.camera

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("setNew")
fun bindIsNew(view: TextView, isNew: Boolean) {
    if (isNew) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

