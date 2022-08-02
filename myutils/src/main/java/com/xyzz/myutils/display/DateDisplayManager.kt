package com.xyzz.myutils.display

object DateDisplayManager {
    fun getLoveDateImpl(textDate: String, pattern: String): IDateDisplay{
        return LoveDateDisplayImpl().textDateInit(textDate, pattern)
    }

    fun getMessageDataImpl():IDateDisplay=MessageDateDisplayImpl()
}