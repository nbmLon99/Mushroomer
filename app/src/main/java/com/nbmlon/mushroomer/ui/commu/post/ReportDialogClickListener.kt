package com.nbmlon.mushroomer.ui.commu.post

import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest

fun interface ReportDialogClickListener {
    fun onDialogReportBtnClicked( domain : CommuPostUseCaseRequest.ReportRequestDomain)
}