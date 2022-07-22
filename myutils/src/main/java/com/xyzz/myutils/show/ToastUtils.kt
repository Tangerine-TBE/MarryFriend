package com.xyzz.myutils.show

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

private var toast: Toast?=null

fun Any.toast(context: Context?, text: String){
    context?:return
    toast?.cancel()
    toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
    toast?.show()
}
fun Fragment.toast(text: String?){
    context?:return
    toast?.cancel()
    toast = Toast.makeText(context, text?:"null", Toast.LENGTH_SHORT)
    toast?.show()
}

fun Context.toast(text: String?){
    toast?.cancel()
    toast = Toast.makeText(this, text?:"null", Toast.LENGTH_SHORT)
    toast?.show()
}

fun Context.longToast(text: String){
    toast?.cancel()
    toast = Toast.makeText(this, text, Toast.LENGTH_LONG)
    toast?.show()
}