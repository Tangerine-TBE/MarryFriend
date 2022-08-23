package com.twx.marryfriend

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.core.content.FileProvider
import java.io.File

fun DisplayMetrics.dp2Px(dp:Float):Float{
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,this)
}

fun File.toUri(context: Context):Uri{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
        val authority =
            context.packageName.toString() + ".fileProvider"
        return FileProvider.getUriForFile(context, authority, this)
    }else{
        return Uri.fromFile(this)
    }
}
