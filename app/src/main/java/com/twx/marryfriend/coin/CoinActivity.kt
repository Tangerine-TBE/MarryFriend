package com.twx.marryfriend.coin

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import kotlinx.android.synthetic.main.activity_coin.*

class CoinActivity : MainBaseViewActivity() {

    private var mPrice = 10
    private var mMode = 1
    private var mPay = "wx"

    override fun getLayoutView(): Int = R.layout.activity_coin

    override fun initView() {
        super.initView()
    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_coin_finish.setOnClickListener {
            finish()
        }

        rl_coin_one.setOnClickListener {
            clearChoose()
            ll_coin_one.setBackgroundResource(R.drawable.shape_bg_coin_choose)
            tv_coin_one_coin.setTextColor(Color.parseColor("#FF4444"))

            mPrice = 10
            mMode = 1
        }

        rl_coin_two.setOnClickListener {
            clearChoose()
            ll_coin_two.setBackgroundResource(R.drawable.shape_bg_coin_choose)
            tv_coin_two_coin.setTextColor(Color.parseColor("#FF4444"))

            mPrice = 30
            mMode = 2
        }

        rl_coin_three.setOnClickListener {
            clearChoose()
            ll_coin_three.setBackgroundResource(R.drawable.shape_bg_coin_choose)
            tv_coin_three_coin.setTextColor(Color.parseColor("#FF4444"))

            mPrice = 100
            mMode = 3
        }

        rl_coin_four.setOnClickListener {
            clearChoose()
            ll_coin_four.setBackgroundResource(R.drawable.shape_bg_coin_choose)
            tv_coin_four_coin.setTextColor(Color.parseColor("#FF4444"))

            mPrice = 300
            mMode = 4
        }

        rl_coin_five.setOnClickListener {
            clearChoose()
            ll_coin_five.setBackgroundResource(R.drawable.shape_bg_coin_choose)
            tv_coin_five_coin.setTextColor(Color.parseColor("#FF4444"))

            mPrice = 1000
            mMode = 5
        }

        rl_coin_wx.setOnClickListener {
            mPay = "wx"
            iv_coin_wx_check.setImageResource(R.drawable.ic_vip_check)
            iv_coin_ali_check.setImageResource(R.drawable.ic_vip_check_non)
        }

        rl_coin_ali.setOnClickListener {
            mPay = "ali"
            iv_coin_ali_check.setImageResource(R.drawable.ic_vip_check)
            iv_coin_wx_check.setImageResource(R.drawable.ic_vip_check_non)
        }

        tv_coin_pay.setOnClickListener {
            ToastUtils.showShort("使用${mPay}方式支付${mPrice}元购买套餐${mMode}")
        }

    }


    private fun clearChoose() {

        ll_coin_one.setBackgroundResource(R.drawable.shape_bg_coin_choose_non)
        tv_coin_one_coin.setTextColor(Color.parseColor("#404040"))

        ll_coin_two.setBackgroundResource(R.drawable.shape_bg_coin_choose_non)
        tv_coin_two_coin.setTextColor(Color.parseColor("#404040"))

        ll_coin_three.setBackgroundResource(R.drawable.shape_bg_coin_choose_non)
        tv_coin_three_coin.setTextColor(Color.parseColor("#404040"))

        ll_coin_four.setBackgroundResource(R.drawable.shape_bg_coin_choose_non)
        tv_coin_four_coin.setTextColor(Color.parseColor("#404040"))

        ll_coin_five.setBackgroundResource(R.drawable.shape_bg_coin_choose_non)
        tv_coin_five_coin.setTextColor(Color.parseColor("#404040"))

    }

}