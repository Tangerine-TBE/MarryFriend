package com.twx.marryfriend.vip.svip

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.HandlerCompat.postDelayed
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.alipay.sdk.app.PayTask
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.vip.AliPayBean
import com.twx.marryfriend.bean.vip.PayResult
import com.twx.marryfriend.bean.vip.RefreshSelfBean
import com.twx.marryfriend.bean.vip.VipPriceBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.net.callback.vip.IDoAliPayCallback
import com.twx.marryfriend.net.callback.vip.IDoRefreshSelfCallback
import com.twx.marryfriend.net.callback.vip.IGetVipPriceCallback
import com.twx.marryfriend.net.impl.vip.doAliPayPresentImpl
import com.twx.marryfriend.net.impl.vip.doRefreshSelfPresentImpl
import com.twx.marryfriend.net.impl.vip.getVipPricePresentImpl
import com.twx.marryfriend.utils.SpUtil
import com.twx.marryfriend.utils.weight.CustomScrollView
import com.twx.marryfriend.vip.VipActivity
import com.twx.marryfriend.vip.adapter.ToolAdapter
import kotlinx.android.synthetic.main.fragment_normal.*
import kotlinx.android.synthetic.main.fragment_super.*
import java.util.*

class SVipFragment : Fragment(), IDoAliPayCallback, IDoRefreshSelfCallback {


    private val mode = "svip"

    var mVipPrice = "688"
    var mVipMode = "23"
    private var mPay = "WX"

    var mVipPriceList: MutableList<String> = ArrayList()
    var mVipModeList: MutableList<String> = ArrayList()

    private lateinit var mContext: Context

    private lateinit var adapter: ToolAdapter

    private lateinit var doAliPayPresent: doAliPayPresentImpl

    private lateinit var doRefreshSelfPresent: doRefreshSelfPresentImpl


    fun newInstance(context: Context): SVipFragment {
        val fragment = SVipFragment()
        fragment.mContext = context
        return fragment
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_super, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initPresent()
        initEvent()
    }

    private fun initView() {

        doAliPayPresent = doAliPayPresentImpl.getsInstance()
        doAliPayPresent.registerCallback(this)

        doRefreshSelfPresent = doRefreshSelfPresentImpl.getsInstance()
        doRefreshSelfPresent.registerCallback(this)

        adapter = ToolAdapter(DataProvider.SuperVipData)

        rv_super_container.layoutManager = GridLayoutManager(mContext, 4)
        rv_super_container.adapter = adapter

        adapter.notifyDataSetChanged()

    }

    private fun initData() {

        mVipPriceList.add("388")
        mVipPriceList.add("588")
        mVipPriceList.add("688")

        mVipModeList.add("21")
        mVipModeList.add("22")
        mVipModeList.add("23")

    }

    private fun initPresent() {

    }

    private fun initEvent() {

        rl_super_12.setOnClickListener {
            hidePriceChoose()
            iv_super_12_check.setImageResource(R.drawable.ic_vip_check)
            mVipPrice = mVipPriceList[2]
            mVipMode = mVipModeList[2]

        }

        rl_super_3.setOnClickListener {
            hidePriceChoose()
            iv_super_3_check.setImageResource(R.drawable.ic_vip_check)
            mVipPrice = mVipPriceList[1]
            mVipMode = mVipModeList[1]
        }

        rl_super_1.setOnClickListener {
            hidePriceChoose()
            iv_super_1_check.setImageResource(R.drawable.ic_vip_check)
            mVipPrice = mVipPriceList[0]
            mVipMode = mVipModeList[0]
        }

        rl_super_wx.setOnClickListener {
            hidePayChoose()
            iv_super_wx_check.setImageResource(R.drawable.ic_vip_check)
            mPay = "WX"
        }

        rl_super_ali.setOnClickListener {
            hidePayChoose()
            iv_super_ali_check.setImageResource(R.drawable.ic_vip_check)
            mPay = "ALI"
        }

        tv_super_pay.setOnClickListener {

            if (mPay == "WX") {
                ToastUtils.showShort("暂不支持微信支付")
            } else {

                doAliPay()
            }
        }

        // 滑动结束时事件，看要不要根据滑动情况，来更新按钮位置
        nset.setOnScrollEndListener(object : CustomScrollView.OnScrollEndListener {
            override fun onScrollEnd() {

            }
        })

    }

    private fun doAliPay() {
        mVipPrice = "0.01"
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.BUY_ORDER_NUMBER] = getOrder("VIP", mVipMode, mPay)
        map[Contents.FEE] = mVipPrice
        map[Contents.BODY] = "会员"
        map[Contents.USER_SYSTEM] = "1"
        doAliPayPresent.doAliPay(map)
        ll_vip_super_loading.visibility = View.VISIBLE
    }

    private fun doUpdate() {
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
            val packageManager: PackageManager = requireActivity().packageManager
            //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
            val applicationInfo: ApplicationInfo = packageManager.getApplicationInfo(
                requireActivity().packageName,
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

    fun updateView(vipPriceBean: VipPriceBean) {

        tv_super_1_price?.text = "￥${vipPriceBean.data[0].now_price}"
        tv_super_3_price?.text = "￥${vipPriceBean.data[1].now_price}"
        tv_super_12_price?.text = "￥${vipPriceBean.data[2].now_price}"

        tv_super_1_day?.text = vipPriceBean.data[0].mark
        tv_super_3_day?.text = vipPriceBean.data[1].mark
        tv_super_12_day?.text = vipPriceBean.data[2].mark

        tv_super_1_describe?.text = vipPriceBean.data[0].describe
        tv_super_3_describe?.text = vipPriceBean.data[1].describe
        tv_super_12_describe?.text = vipPriceBean.data[2].describe

    }

    private fun hidePriceChoose() {
        iv_super_12_check.setImageResource(R.drawable.ic_vip_check_non)
        iv_super_3_check.setImageResource(R.drawable.ic_vip_check_non)
        iv_super_1_check.setImageResource(R.drawable.ic_vip_check_non)
    }

    private fun hidePayChoose() {
        iv_super_wx_check.setImageResource(R.drawable.ic_vip_check_non)
        iv_super_ali_check.setImageResource(R.drawable.ic_vip_check_non)
    }

    private fun toBuy(orderInfo: String) {
        Thread {
            val alipay = PayTask(activity)
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
                    doUpdate()
                }
                "6001" -> {
                    ll_vip_super_loading.visibility = View.GONE
                    ToastUtils.showShort("用户取消支付")
                }
                "6002" -> {
                    ll_vip_super_loading.visibility = View.GONE
                    ToastUtils.showShort("网络连接出错")
                }
                "4000" -> {
                    ll_vip_super_loading.visibility = View.GONE
                    ToastUtils.showShort("订单支付失败")
                }
                else -> {
                    ll_vip_super_loading.visibility = View.GONE
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
        ll_vip_super_loading.visibility = View.GONE
        if (refreshSelfBean != null) {
            if (refreshSelfBean.code == 200) {
                SpUtil.refreshUserInfo(refreshSelfBean)

                // 刷新视图
                val activity = activity as VipActivity
                activity.updateTopView()

            } else {
                ToastUtils.showShort(refreshSelfBean.msg)
            }
        }
    }

    override fun onDoRefreshSelfError() {
        ll_vip_super_loading.visibility = View.GONE
    }

    override fun onDoAliPaySuccess(aliPayBean: AliPayBean?) {
        if (mode == "svip") {
            if (aliPayBean != null) {
                if (aliPayBean.code == "200") {
                    toBuy(aliPayBean.data.str)
                } else {
                    ll_vip_super_loading.visibility = View.GONE
                    ToastUtils.showShort("支付信息拉起失败，请稍后重试")
                }
            }
        }
    }

    override fun onDoAliPayError() {
        ll_vip_super_loading.visibility = View.GONE
    }


}