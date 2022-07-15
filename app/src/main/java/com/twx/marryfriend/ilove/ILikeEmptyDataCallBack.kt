package com.twx.marryfriend.ilove

import com.kingja.loadsir.callback.Callback
import com.twx.marryfriend.R

class ILikeEmptyDataCallBack: Callback() {
    override fun onCreateView(): Int {
        return R.layout.item_search_empty_data
    }
}