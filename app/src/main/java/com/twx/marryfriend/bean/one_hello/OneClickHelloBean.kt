package com.twx.marryfriend.bean.one_hello

data class OneClickHelloBean(
    var code: Int? = 0,
    var `data`: List<OneClickHelloItemBean>? = listOf(),
    var msg: String? = ""
)