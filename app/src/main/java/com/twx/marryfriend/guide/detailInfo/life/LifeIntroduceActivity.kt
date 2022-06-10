package com.twx.marryfriend.guide.detailInfo.life

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import kotlinx.android.synthetic.main.activity_life_introduce.*

class LifeIntroduceActivity : MainBaseViewActivity() {

    private var picPath = ""
    private var introduce = ""

    override fun getLayoutView(): Int = R.layout.activity_life_introduce

    override fun initView() {
        super.initView()

        val requestOptions = RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE);

        picPath = intent.getStringExtra("path").toString()

        Glide.with(this)
            .load(picPath)
            .apply(requestOptions)
            .into(iv_life_introduce_container)

        Log.i("guo", picPath)
    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_life_introduce_finish.setOnClickListener {
            finish()
        }


        tv_life_introduce_confirm.setOnClickListener {

            introduce = et_life_introduce_introduce.text.toString().trim { it <= ' ' }

            val intent = intent
            intent.putExtra("introduce", introduce)
            setResult(RESULT_OK, intent)
            finish()

        }

    }

}