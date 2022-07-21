package com.twx.module_base.utils

import androidx.emoji.text.EmojiCompat
import java.util.*

/**
 * @author: Administrator
 * @date: 2022/7/14
 */
object EmojiUtils {

    /** 添加表情包 */
    fun getEmojiList(): MutableList<String> {
        val emojiList: MutableList<String> = arrayListOf()
        emojiList.add("1F600")
        emojiList.add("1F601")
        emojiList.add("1F605")
        emojiList.add("1F923")
        emojiList.add("1F602")
        emojiList.add("1F642")
        emojiList.add("1F643")
        emojiList.add("1FAE0")
        emojiList.add("1F609")
        emojiList.add("1F60A")
        emojiList.add("1F60D")
        emojiList.add("1F929")
        emojiList.add("1F618")
        emojiList.add("263A")
        emojiList.add("1F61B")
        emojiList.add("1F911")
        emojiList.add("1F917")
        emojiList.add("1F92D")
        emojiList.add("1F92B")
        emojiList.add("1FAE3")
        emojiList.add("1F610")
        emojiList.add("1F60F")
        emojiList.add("1F612")
        emojiList.add("1F644")
        emojiList.add("1F62C")
        emojiList.add("1F60C")
        emojiList.add("1F614")
        emojiList.add("1F62A")
        emojiList.add("1F634")
        emojiList.add("1F637")
        emojiList.add("1F912")
        emojiList.add("1F92E")
        emojiList.add("1F635")
        emojiList.add("1F633")
        emojiList.add("1F97A")
        emojiList.add("1F625")
        emojiList.add("1F62D")
        emojiList.add("1F631")
        emojiList.add("1F60E")
        emojiList.add("1F913")
        emojiList.add("1F92C")
        emojiList.add("1F4A9")
        emojiList.add("1F47B")
        emojiList.add("2764")
        emojiList.add("1F4A3")
        emojiList.add("1F44B")
        emojiList.add("1F44C")
        emojiList.add("1F44D")
        emojiList.add("1F91D")
        emojiList.add("1F437")
        emojiList.add("1F339")
        emojiList.add("1F940")
        emojiList.add("1F382")
        emojiList.add("1F37A")
        emojiList.add("1F42E")
        return emojiList
    }

    /** 判断当前 EmojiCompat 是否初始化成功 */
    fun isEmojiCompatInit(): Boolean {
        return EmojiCompat.get().loadState == EmojiCompat.LOAD_STATE_SUCCEEDED
    }

    /** 获取可兼容的 emoji 字符串 */
    fun getCompatEmojiString(code: String): CharSequence? {
        //将当前 code 转换为 16 进制数
        val hex = code.toInt(16)
        //将当前 16 进制数转换成字符数组
        val chars = Character.toChars(hex)
        //将当前字符数组转换成 TextView 可加载的 String 字符串
        return String(chars)
    }

}