package com.twx.marryfriend.coin

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import com.alipay.sdk.app.PayTask
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.vip.*
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.net.callback.vip.IDoAliPayCallback
import com.twx.marryfriend.net.callback.vip.IDoRefreshSelfCallback
import com.twx.marryfriend.net.callback.vip.IGetCoinPriceCallback
import com.twx.marryfriend.net.impl.vip.doAliPayPresentImpl
import com.twx.marryfriend.net.impl.vip.doRefreshSelfPresentImpl
import com.twx.marryfriend.net.impl.vip.getCoinPricePresentImpl
import com.twx.marryfriend.utils.SpUtil.refreshUserInfo
import kotlinx.android.synthetic.main.activity_coin.*
import java.util.*

class CoinActivity : MainBaseViewActivity(), IGetCoinPriceCallback, IDoAliPayCallback,
    IDoRefreshSelfCallback {

    private val mode = "coin"

    private var mPrice = "10"
    private var mMode = "1"
    private var mPay = "ALI"

    private var mPriceList: MutableList<String> = arrayListOf()
    private var mModeList: MutableList<String> = arrayListOf()

    private lateinit var getCoinPricePresent: getCoinPricePresentImpl
    private lateinit var doAliPayPresent: doAliPayPresentImpl
    private lateinit var doRefreshSelfPresent: doRefreshSelfPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_coin

    override fun initView() {
        super.initView()

        getCoinPricePresent = getCoinPricePresentImpl.getsInstance()
        getCoinPricePresent.registerCallback(this)

        doAliPayPresent = doAliPayPresentImpl.getsInstance()
        doAliPayPresent.registerCallback(this)

        doRefreshSelfPresent = doRefreshSelfPresentImpl.getsInstance()
        doRefreshSelfPresent.registerCallback(this)

        mPriceList.add("10")
        mPriceList.add("30")
        mPriceList.add("98")
        mPriceList.add("285")
        mPriceList.add("900")

        mModeList.add("1")
        mModeList.add("2")
        mModeList.add("3")
        mModeList.add("4")
        mModeList.add("5")

        getCoinPrice()


    }

    override fun initLoadData() {
        super.initLoadData()

        updateCoin()

    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_coin_finish.setOnClickListener {
            finish()
        }

        iv_coin_record.setOnClickListener {
            startActivity(Intent(this, CoinRecordActivity::class.java))
        }

        rl_coin_one.setOnClickListener {
            clearChoose()
            ll_coin_one.setBackgroundResource(R.drawable.shape_bg_coin_choose)
            tv_coin_one_coin.setTextColor(Color.parseColor("#FF4444"))

            mPrice = mPriceList[0]
            mMode = mModeList[0]
        }

        rl_coin_two.setOnClickListener {
            clearChoose()
            ll_coin_two.setBackgroundResource(R.drawable.shape_bg_coin_choose)
            tv_coin_two_coin.setTextColor(Color.parseColor("#FF4444"))

            mPrice = mPriceList[1]
            mMode = mModeList[1]
        }

        rl_coin_three.setOnClickListener {
            clearChoose()
            ll_coin_three.setBackgroundResource(R.drawable.shape_bg_coin_choose)
            tv_coin_three_coin.setTextColor(Color.parseColor("#FF4444"))

            mPrice = mPriceList[2]
            mMode = mModeList[2]
        }

        rl_coin_four.setOnClickListener {
            clearChoose()
            ll_coin_four.setBackgroundResource(R.drawable.shape_bg_coin_choose)
            tv_coin_four_coin.setTextColor(Color.parseColor("#FF4444"))

            mPrice = mPriceList[3]
            mMode = mModeList[3]
        }

        rl_coin_five.setOnClickListener {
            clearChoose()
            ll_coin_five.setBackgroundResource(R.drawable.shape_bg_coin_choose)
            tv_coin_five_coin.setTextColor(Color.parseColor("#FF4444"))

            mPrice = mPriceList[4]
            mMode = mModeList[4]
        }

        rl_coin_wx.setOnClickListener {
            mPay = "WX"
            iv_coin_wx_check.setImageResource(R.drawable.ic_vip_check)
            iv_coin_ali_check.setImageResource(R.drawable.ic_vip_check_non)
        }

        rl_coin_ali.setOnClickListener {
            mPay = "ALI"
            iv_coin_ali_check.setImageResource(R.drawable.ic_vip_check)
            iv_coin_wx_check.setImageResource(R.drawable.ic_vip_check_non)
        }

        tv_coin_pay.setOnClickListener {
            ToastUtils.showShort("使用${mPay}方式支付${mPrice}元购买套餐${mMode}")
            doAliPay()
        }

    }

    private fun getCoinPrice() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.PLATFORM] = SPStaticUtils.getString(Constant.CHANNEL, "_360")
        map[Contents.TYPE_KIND] = "android"
        getCoinPricePresent.getCoinPrice(map)

    }

    private fun doAliPay() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.BUY_ORDER_NUMBER] = getOrder("JIN", mMode, mPay)
        map[Contents.FEE] = mPrice
        map[Contents.BODY] = "金币"
        map[Contents.USER_SYSTEM] = "1"
        doAliPayPresent.doAliPay(map)

    }

    private fun updateCoin() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        doRefreshSelfPresent.doRefreshSelf(map)
    }


    // 获取订单号信息
    // VIP_5_4321_360_ALI_432176，商品类型_商品id_userid_渠道_支付方式_时间戳后六位_应用编码 如果是应用宝，缩写成_yyb

    private fun getOrder(commodityType: String, type: String, payType: String): String {
        //获取时间戳
        val currentTimeMillis = (System.currentTimeMillis() / 1000).toString()
        val x = currentTimeMillis.substring(currentTimeMillis.length - 6, currentTimeMillis.length)
        var str = getChannelName()
        if (str == "null") {
            str = "_360"
        }
        var mUserId: String = SPStaticUtils.getString(Constant.USER_ID);
        return commodityType + "_" + type + "_" + mUserId + str + "_" + payType + "_" + x
    }

    /**
     * 获取渠道名
     * @param context 此处习惯性的设置为activity，实际上context就可以
     * @return 如果没有获取成功，那么返回值为空
     */
    private fun getChannelName(): String {
        var channelName: String = "_360"

        try {
            val packageManager: PackageManager = this.packageManager
            //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
            val applicationInfo: ApplicationInfo = packageManager.getApplicationInfo(
                this.packageName,
                PackageManager.GET_META_DATA
            )
            if (applicationInfo.metaData != null) {
                channelName =
                    java.lang.String.valueOf(applicationInfo.metaData.get("UMENG_CHANNEL"))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return channelName
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

    private fun updateView(coinPriceBean: CoinPriceBean) {

        tv_coin_one_coin.text = coinPriceBean.data[0].describe
        tv_coin_two_coin.text = coinPriceBean.data[1].describe
        tv_coin_three_coin.text = coinPriceBean.data[2].describe
        tv_coin_four_coin.text = coinPriceBean.data[3].describe
        tv_coin_five_coin.text = coinPriceBean.data[4].describe

        tv_coin_one_price.text = "￥${coinPriceBean.data[0].now_price} "
        tv_coin_two_price.text = "￥${coinPriceBean.data[1].now_price} "
        tv_coin_three_price.text = "￥${coinPriceBean.data[2].now_price} "
        tv_coin_four_price.text = "￥${coinPriceBean.data[3].now_price} "
        tv_coin_five_price.text = "￥${coinPriceBean.data[4].now_price} "

        if (coinPriceBean.data[0].now_price == coinPriceBean.data[0].original_price) {
            tv_coin_one_more.visibility = View.GONE
        } else {
            tv_coin_one_more.visibility = View.VISIBLE
            tv_coin_one_more.text =
                "${(100 * coinPriceBean.data[0].now_price.toInt()) / coinPriceBean.data[0].original_price.toInt()}折"
        }

        if (coinPriceBean.data[1].now_price == coinPriceBean.data[1].original_price) {
            tv_coin_two_more.visibility = View.GONE
        } else {
            tv_coin_two_more.visibility = View.VISIBLE
            tv_coin_two_more.text =
                "${(100 * coinPriceBean.data[1].now_price.toInt()) / coinPriceBean.data[1].original_price.toInt()}折"
        }

        if (coinPriceBean.data[2].now_price == coinPriceBean.data[2].original_price) {
            tv_coin_three_more.visibility = View.GONE
        } else {
            tv_coin_three_more.visibility = View.VISIBLE
            tv_coin_three_more.text =
                "${(100 * coinPriceBean.data[2].now_price.toInt()) / coinPriceBean.data[2].original_price.toInt()}折"
        }

        if (coinPriceBean.data[3].now_price == coinPriceBean.data[3].original_price) {
            tv_coin_four_more.visibility = View.GONE
        } else {
            tv_coin_four_more.visibility = View.VISIBLE
            tv_coin_four_more.text =
                "${(100 * coinPriceBean.data[3].now_price.toInt()) / coinPriceBean.data[3].original_price.toInt()}折"
        }

        if (coinPriceBean.data[4].now_price == coinPriceBean.data[4].original_price) {
            tv_coin_five_more.visibility = View.GONE
        } else {
            tv_coin_five_more.visibility = View.VISIBLE
            tv_coin_five_more.text =
                "${(100 * coinPriceBean.data[4].now_price.toInt()) / coinPriceBean.data[4].original_price.toInt()}折"
        }


    }

    private fun toBuy(orderInfo: String) {
        Thread {
            val alipay = PayTask(this)
            val result: Map<String, String> = alipay.payV2(orderInfo, true)
            val msg = Message()
            msg.obj = result
            mHandler.sendMessage(msg);
        }.start()
    }

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
//            val result = Result(msg.obj)
            val result = PayResult(msg.obj as MutableMap<String, String>?)
            val status = result.resultStatus
            when (result.resultStatus) {
                "9000" -> {

                    ToastUtils.showShort("用户支付成功")
                    // 刷新数据
                    updateCoin()
                }
                "6001" -> {

                    ToastUtils.showShort("用户取消支付")
                }
                "6002" -> {

                    ToastUtils.showShort("网络连接出错")
                }
                "4000" -> {

                    ToastUtils.showShort("订单支付失败")
                }
                else -> {

//                    ToastUtils.showShort("支付失败，请稍后再试")
                }
            }
        }
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoRefreshSelfSuccess(refreshSelfBean: RefreshSelfBean?) {
        if (refreshSelfBean != null) {
            if (refreshSelfBean.code == 200) {
                tv_coin_sum.text = "${refreshSelfBean.data.jinbi_goldcoin}币"
                refreshUserInfo(refreshSelfBean)
            }
        }
    }

    override fun onDoRefreshSelfError() {

    }

    override fun onDoAliPaySuccess(aliPayBean: AliPayBean?) {

        if (mode == "coin") {

            if (aliPayBean != null) {
                if (aliPayBean.code == "200") {
                    toBuy(aliPayBean.data.str)
                } else {
                    ToastUtils.showShort("支付信息拉起失败，请稍后重试")
                }
            }

        }


    }

    override fun onDoAliPayError() {

    }


    override fun onGetCoinPriceSuccess(coinPriceBean: CoinPriceBean?) {
        if (coinPriceBean != null) {
            if (coinPriceBean.code == 200) {

                mPriceList.clear()

                for (i in 0.until(coinPriceBean.data.size)) {
                    mPriceList.add(coinPriceBean.data[i].now_price)
                    mModeList.add(coinPriceBean.data[i].level.toString())
                }
                mPrice = coinPriceBean.data[0].now_price
                mMode = coinPriceBean.data[0].level.toString()

                updateView(coinPriceBean)

            }
        }
    }

    override fun onGetCoinPriceCodeError() {

    }

    override fun onDestroy() {
        super.onDestroy()

        getCoinPricePresent.unregisterCallback(this)
        doAliPayPresent.unregisterCallback(this)
        doRefreshSelfPresent.unregisterCallback(this)

    }

}