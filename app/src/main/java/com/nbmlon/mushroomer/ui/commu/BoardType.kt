package com.nbmlon.mushroomer.ui.commu

import com.nbmlon.mushroomer.R
import java.io.Serializable

const val BOARD_TYPE_ORDINAL = "board_type_ordinal"

enum class BoardType (val boardNameResId : Int) : Serializable{
    FreeBoard(R.string.FreeBoard),
    QnABoard(R.string.QnABoard),
    PicBoard(R.string.PicBoard),
    HotBoard(R.string.HotBoard),
    MyPosts(R.string.myPosts),
    MyComments(R.string.myComment)
}