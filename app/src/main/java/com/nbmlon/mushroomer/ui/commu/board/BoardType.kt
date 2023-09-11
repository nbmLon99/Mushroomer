package com.nbmlon.mushroomer.ui.commu.board

import com.nbmlon.mushroomer.R
import java.io.Serializable

const val BOARD_TYPE_ORDINAL = "board_type_ordinal"

enum class BoardType (val boardNameResId : Int, val boradIdx : Int) : Serializable{
    FreeBoard(R.string.FreeBoard,0),
    QnABoard(R.string.QnABoard,1),
    PicBoard(R.string.PicBoard,2),
    HotBoard(R.string.HotBoard,3),
    MyPosts(R.string.myPosts,4),
    MyComments(R.string.myComment,5)
}