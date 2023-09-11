package com.nbmlon.mushroomer.ui.commu.post

import com.nbmlon.mushroomer.api.dto.CommuPostRequestDTO

fun interface ReportDialogClickListener {
    fun onDialogReportBtnClicked(type : TargetType, dto : CommuPostRequestDTO.ReportDTO)
}