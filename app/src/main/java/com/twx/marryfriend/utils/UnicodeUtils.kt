package com.twx.marryfriend.utils

/**
 * @author: Administrator
 * @date: 2022/5/20
 */
object UnicodeUtils {

    /** Unicode码转为汉字  */
    fun decode(unicodeStr: String?): String {
        if (unicodeStr == null) {
            return ""
        }
        val retBuf = StringBuffer()
        val maxLoop = unicodeStr.length
        var i = 0
        while (i < maxLoop) {
            if (unicodeStr[i] == '\\') {
                if (i < maxLoop - 5 && (unicodeStr[i + 1] == 'u' || unicodeStr[i + 1] == 'U')) {
                    try {
                        retBuf.append(unicodeStr.substring(i + 2, i + 6).toInt(16).toChar())
                        i += 5
                    } catch (localNumberFormatException: NumberFormatException) {
                        retBuf.append(unicodeStr[i])
                    }
                } else {
                    retBuf.append(unicodeStr[i])
                }
            } else {
                retBuf.append(unicodeStr[i])
            }
            i++
        }
        return retBuf.toString()
    }


    /** 取出string字符串中的换行 */
    fun newLineText(text: String): String {
        return text.replace("\r|\n".toRegex(), " ")
    }

    // 取出第一个汉字，然后添加星号
    fun hideName(name: String): String {
        var newName = name.substring(0, 1)
        for (i in 0.until(name.length - 1)) {
            newName = "$newName*"
        }
        return newName
    }

    // 取出前6个数字，然后添加星号
    fun hideId(name: String): String {
        var newId = name.substring(0, 6)
        for (i in 0.until(name.length - 6)) {
            newId = "$newId*"
        }
        return newId
    }


}