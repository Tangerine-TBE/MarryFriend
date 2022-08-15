package com.xyzz.myutils

import android.graphics.Color
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans


fun TextView.setExpandableText(content: CharSequence, maxLine: Int, expandText: String, shrinkText: String, isExpand: Boolean = false) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            val availableWith = width - compoundPaddingLeft - compoundPaddingRight
            val layout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(content, 0, content.length, paint, availableWith).build()
            } else {
                @Suppress("DEPRECATION")
                (StaticLayout(content, 0, content.length, paint, availableWith, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, false))
            }
            if (layout.lineCount > maxLine) {
                val lastLineStart = layout.getLineStart(maxLine - 1)
                val ellipsize = (content.subSequence(0, lastLineStart).toString() +
                        TextUtils.ellipsize(
                            content.subSequence(lastLineStart, content.length), paint, availableWith - paint.measureText(expandText), TextUtils.TruncateAt.END
                        ))

                setText(isExpand, ellipsize)
                movementMethod = LinkMovementMethod.getInstance()
//                transparentHighlightColor()
            } else {
                text = content
            }
        }

        private fun setText(isExpand: Boolean, ellipsize: CharSequence) {
            text = buildSpannedString {
                append(if (isExpand) content else ellipsize)
                appendClickable(if (isExpand) shrinkText else expandText, color = Color.parseColor("#FF43A0FC"),isUnderlineText = false) {
                    setText(!isExpand, ellipsize)
                }
            }
        }
    })
}

fun SpannableStringBuilder.appendClickable(
    text: CharSequence?,
    @ColorInt color: Int? = null,
    isUnderlineText: Boolean = true,
    onClick: (View) -> Unit
): SpannableStringBuilder = inSpans(ClickableSpan(color, isUnderlineText, onClick)) { append(text) }

fun ClickableSpan(
    @ColorInt color: Int? = null,
    isUnderlineText: Boolean = true,
    onClick: (View) -> Unit,
): ClickableSpan = object : ClickableSpan() {
    override fun onClick(widget: View) = onClick(widget)

    override fun updateDrawState(ds: TextPaint) {
        ds.color = color ?: ds.linkColor
        ds.isUnderlineText = isUnderlineText
    }
}