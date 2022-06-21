package com.twx.module_dynamic

import android.os.Bundle
import com.twx.module_base.base.MainBaseViewActivity

class DynamicActivity : MainBaseViewActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic)
    }

    override fun getLayoutView(): Int = R.layout.activity_dynamic

    override fun initView() {
        super.initView()


    }

}