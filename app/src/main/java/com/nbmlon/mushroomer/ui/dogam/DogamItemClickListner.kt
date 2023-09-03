package com.nbmlon.mushroomer.ui.dogam

import com.nbmlon.mushroomer.model.MushHistory
import com.nbmlon.mushroomer.model.Mushroom

fun interface DogamItemClickListner {
    fun onDogamItemClicked(clickedMushroom :Mushroom)
}

fun interface DogamHistoryClickListener{
    fun openPictureDialog(clickedMushHistory: MushHistory)
}