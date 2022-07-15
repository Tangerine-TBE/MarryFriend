package com.twx.marryfriend.recommend.widget

import com.kingja.loadsir.callback.Callback
import com.twx.marryfriend.R

class MutualLikeCallback: Callback() {
    override fun onCreateView(): Int {
        return R.layout.item_recommend_mutual_like
    }
}