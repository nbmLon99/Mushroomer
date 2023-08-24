package com.nbmlon.mushroomer.ui.commu



/** 게시판 열기 **/
class CommuFragmentBoard {
    companion object {
        const val TAG = "CommuFragmentBoard"

        @JvmStatic
        fun getInstance(param1: Int) =
            when(param1) {
                BoardType.FreeBoard.ordinal -> CommuFragmentBoard_text.getInstance(param1);
                BoardType.PicBoard.ordinal -> CommuFragmentBoard_image.getInstance(param1);
                BoardType.QnABoard.ordinal -> CommuFragmentBoard_text.getInstance(param1);
                BoardType.HotBoard.ordinal -> CommuFragmentBoard_hot()
                else ->  error("Invalid board type")
            }
    }
}