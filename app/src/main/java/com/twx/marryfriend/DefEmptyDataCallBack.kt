package com.twx.marryfriend

import com.kingja.loadsir.callback.Callback
import com.twx.marryfriend.R

class DefEmptyDataCallBack: Callback() {
    override fun onCreateView(): Int {
        return R.layout.item_def_empty_data
    }
}