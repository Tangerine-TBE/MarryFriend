package com.twx.marryfriend.search

import com.kingja.loadsir.callback.Callback
import com.twx.marryfriend.R

class EmptyDataCallBack: Callback() {
    override fun onCreateView(): Int {
        return R.layout.item_search_empty_data
    }
}