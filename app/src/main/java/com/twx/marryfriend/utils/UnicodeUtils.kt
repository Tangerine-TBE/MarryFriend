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

}