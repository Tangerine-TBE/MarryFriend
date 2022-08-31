package com.twx.marryfriend.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil

/**
 * @author: Administrator
 * @date: 2022/7/13
 */
object BitmapUtil {

    /***
     * 添加水印
     * @param srcBitmap源图像
     * @param watermark水印图像
     * @return
     */
    fun createWaterMarkBitmap(srcBitmap: Bitmap?, watermark: Bitmap): Bitmap? {
        if (srcBitmap == null) {
            return null
        }
        val srcWidth: Int = srcBitmap.width //获取源图像的宽
        val srcHeight: Int = srcBitmap.height //获取源图像的高
        val waterWidth: Int = watermark.width //获取水印图像的宽
        val waterHeight: Int = watermark.height //获取水印图像的高
        val bitmap: Bitmap =
            Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888) //创建一个空白位图
        val canvas = Canvas(bitmap) //根据位图创建画布
        canvas.drawBitmap(srcBitmap, 0F, 0F, null) //先画出源图像
        canvas.drawBitmap(watermark,
            (srcWidth - waterWidth - 10).toFloat(),
            (srcHeight - waterHeight - 10).toFloat(),
            null) //然后在画水印图像
        return bitmap //返回水印图片
    }

    fun generateBitmap(text: String, textSizePx: Float, textColor: Int): Bitmap? {
        val textPaint = TextPaint()
        textPaint.textSize = textSizePx;
        textPaint.color = textColor;
        val width = ceil(textPaint.measureText(text))
        val fontMetrics: Paint.FontMetrics = textPaint.fontMetrics
        val height = ceil(abs(fontMetrics.bottom) + abs(fontMetrics.top))
        val bitmap = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888);
        val canvas = Canvas(bitmap)
        canvas.drawText(text, 0f, abs(fontMetrics.ascent), textPaint)
        return bitmap
    }

}