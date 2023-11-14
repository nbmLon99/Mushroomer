package com.nbmlon.mushroomer.ui.commu.board

import com.nbmlon.mushroomer.R
import java.io.Serializable

const val BOARD_TYPE_ORDINAL = "board_type_ordinal"

enum class BoardType (val boardNameResId : Int, val boradIdx : Int, val serverName : String) : Serializable{
    FreeBoard(R.string.FreeBoard,0, "Free"),
    QnABoard(R.string.QnABoard,1, "QA"),
    PicBoard(R.string.PicBoard,2, "Pic"),
    HotBoard(R.string.HotBoard,3,"Hot"),
    MyPosts(R.string.myPosts,4 , TODO()),
    MyComments(R.string.myComment,5, TODO())
}