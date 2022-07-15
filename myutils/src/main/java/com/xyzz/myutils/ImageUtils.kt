package com.xyzz.myutils

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log

fun Bitmap.rsBlur(context: Context, radius: Int): Bitmap {
    val inputBmp: Bitmap = this
    //(1)
    val renderScript: RenderScript = RenderScript.create(context)
    Log.i("TAG", "scale size:" + inputBmp.getWidth().toString() + "*" + inputBmp.getHeight())

    // Allocate memory for Renderscript to work with
    //(2)
    val input: Allocation = Allocation.createFromBitmap(renderScript, inputBmp)
    val output: Allocation = Allocation.createTyped(renderScript, input.getType())
    //(3)
    // Load up an instance of the specific script that we want to use.
    val scriptIntrinsicBlur: ScriptIntrinsicBlur =
        ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
    //(4)
    scriptIntrinsicBlur.setInput(input)
    //(5)
    // Set the blur radius
    scriptIntrinsicBlur.setRadius(radius.toFloat())
    //(6)
    // Start the ScriptIntrinisicBlur
    scriptIntrinsicBlur.forEach(output)
    //(7)
    // Copy the output to the blurred bitmap
    output.copyTo(inputBmp)
    //(8)
    renderScript.destroy()
    return inputBmp
}