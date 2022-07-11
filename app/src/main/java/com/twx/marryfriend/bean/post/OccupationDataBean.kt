package com.twx.marryfriend.bean.post

data class OccupationDataBean(
    var child: List<OccupationBean>? = listOf(),
    var id: Int? = 0,
    var name: String? = "",
    var pid: Int? = 0
)