package com.twx.marryfriend.main

import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel() {
    var toDynamic:((Int)->Unit)?=null
}