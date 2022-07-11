package com.twx.marryfriend

import android.app.Activity
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.SPStaticUtils
import com.twx.marryfriend.constant.Constant
import com.xyzz.myutils.loadingdialog.ILoadingDialog
import com.xyzz.myutils.loadingdialog.LoadingDialogImpl
import com.xyzz.myutils.loadingdialog.LoadingDialogManager

fun DisplayMetrics.dp2Px(dp:Float):Float{
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,this)
}
