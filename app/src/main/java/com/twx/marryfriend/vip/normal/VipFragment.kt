package com.twx.marryfriend.vip.normal

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alipay.sdk.app.PayTask
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPStaticUtils
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
import com.twx.marryfriend.net.callback.vip.IDoVipRefreshSelfCallback
import com.twx.marryfriend.net.impl.vip.doAliPayPresentImpl
import com.twx.marryfriend.net.impl.vip.doVipRefreshSelfPresentImpl
import com.twx.marryfriend.utils.SpUtil
import com.twx.marryfriend.vip.VipActivity
import com.twx.marryfriend.vip.adapter.ToolAdapter
import com.twx.marryfriend.vip.adapter.VipBannerAdapter
import com.umeng.analytics.MobclickAgent
import com.youth.banner.Banner
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.indicator.RoundLinesIndicator
import kotlinx.android.synthetic.main.activity_coin.*
import kotlinx.android.synthetic.main.fragment_normal.*
import java.util.*


class VipFragment : Fragment(), IDoAliPayCallback, IDoVipRefreshSelfCallback {

    private val mode = "vip"

    var mVipPrice = "398"
    var mVipMode = "13"
    private var mPay = "ALI"


    var mVipPriceList: MutableList<String> = ArrayList()
    var mVipModeList: MutableList<String> = ArrayList()

    private lateinit var adapter: ToolAdapter

    private lateinit var mContext: Context

    // 第一次进来选中的弹窗
    private var item = 0

    private lateinit var doAliPayPresent: doAliPayPresentImpl
    private lateinit var doRefreshSelfPresent: doVipRefreshSelfPresentImpl

    fun newInstance(context: Context, item: Int): VipFragment {
        val fragment = VipFragment()
        fragment.mContext = context
        fragment.item = item
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_normal, container, false)
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

        doRefreshSelfPresent = doVipRefreshSelfPresentImpl.getsInstance()
        doRefreshSelfPresent.registerCallback(this)

        adapter = ToolAdapter(DataProvider.NormalVipData)

        val linearLayoutManager = LinearLayoutManager(mContext)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        rv_normal_container.layoutManager = GridLayoutManager(mContext, 4)
        rv_normal_container.adapter = adapter

        banner_normal_container.isAutoLoop(false)
        var banner = (banner_normal_container as Banner<Int, VipBannerAdapter>)
        banner.apply {
            addBannerLifecycleObserver(this@VipFragment)
            setBannerRound(20f)
            indicator = RoundLinesIndicator(mContext)
            setAdapter(VipBannerAdapter(DataProvider.NormalBannerData))
        }

        adapter.notifyDataSetChanged()

    }

    private fun initData() {

        // 收件箱
        // 消息发送
        // 高级搜索
        // 查看更多嘉宾，多加10个
        // 突出显示消息
        // 谁看过我
        // 谁喜欢我

        when (item) {
            1 -> {
                //消息列表点击收件箱_跳转到开通会员
                MobclickAgent.onEvent(mContext, "60019_inbox_goto_vip");
            }
            2 -> {
                //消息页面点击发送,开通会员
                MobclickAgent.onEvent(mContext, "60008_chat_send_goto_vip");
            }
            3 -> {
                //搜索高级资料,开通会员
                MobclickAgent.onEvent(mContext, "60000_search_goto_vip");
            }
            4 -> {
                //首页推荐次数用完,开通会员
                MobclickAgent.onEvent(mContext, "60001_recommend_more_goto_vip");
            }
            5 -> {
                //聊天页面,置顶功能,开通会员
                MobclickAgent.onEvent(mContext, "60010_chat_message_message_topping");
            }
            6 -> {
                //点击谁看过我_跳转到开通会员
                MobclickAgent.onEvent(mContext, "60020_who_has_seen_me_goto_vip");
            }
            7 -> {
                //查看谁喜欢我,开通会员
                MobclickAgent.onEvent(mContext, "60002_who_like_me_goto_vip");
            }

        }


        banner_normal_container.currentItem = item

        mVipPriceList.add("258")
        mVipPriceList.add("358")
        mVipPriceList.add("398")

        mVipModeList.add("11")
        mVipModeList.add("12")
        mVipModeList.add("13")

    }

    private fun initPresent() {

    }

    private fun initEvent() {

        rl_normal_12.setOnClickListener {
            hidePriceChoose()
            iv_normal_12_check.setImageResource(R.drawable.ic_vip_check)
            mVipPrice = mVipPriceList[2]
            mVipMode = mVipModeList[2]

        }

        rl_normal_3.setOnClickListener {
            hidePriceChoose()
            iv_normal_3_check.setImageResource(R.drawable.ic_vip_check)
            mVipPrice = mVipPriceList[1]
            mVipMode = mVipModeList[1]
        }

        rl_normal_1.setOnClickListener {
            hidePriceChoose()
            iv_normal_1_check.setImageResource(R.drawable.ic_vip_check)
            mVipPrice = mVipPriceList[0]
            mVipMode = mVipModeList[0]
        }

        rl_normal_wx.setOnClickListener {
            hidePayChoose()
            iv_normal_wx_check.setImageResource(R.drawable.ic_vip_check)
            mPay = "WX"
        }

        rl_normal_ali.setOnClickListener {
            hidePayChoose()
            iv_normal_ali_check.setImageResource(R.drawable.ic_vip_check)
            mPay = "ALI"
        }

        tv_normal_pay.setOnClickListener {
            if (mPay == "WX") {
                ToastUtils.showShort("暂不支持微信支付")
            } else {

                //点击开通佳偶会员
                MobclickAgent.onEvent(mContext, "60011_open_jiaou_member");

                doAliPay()
            }
        }
    }

    private fun doAliPay() {



            val map: MutableMap<String, String> = TreeMap()
            map[Contents.BUY_ORDER_NUMBER] = getOrder("VIP", mVipMode, mPay)
//        map[Contents.FEE] = mVipPrice
            map[Contents.FEE] = 0.01.toString()
            map[Contents.BODY] = "会员"
            map[Contents.USER_SYSTEM] = "1"
            doAliPayPresent.doAliPay(map)

            ll_vip_normal_loading?.visibility = View.VISIBLE


    }

    private fun doUpdate() {



            val map: MutableMap<String, String> = TreeMap()
            map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
            doRefreshSelfPresent.doVipRefreshSelf(map)



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
            val applicationInfo: ApplicationInfo =
                packageManager.getApplicationInfo(requireActivity().packageName,
                    PackageManager.GET_META_DATA)
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

        tv_normal_1_price?.text = "￥${vipPriceBean.data[0].now_price}"
        tv_normal_3_price?.text = "￥${vipPriceBean.data[1].now_price}"
        tv_normal_12_price?.text = "￥${vipPriceBean.data[2].now_price}"

        tv_normal_1_day?.text = vipPriceBean.data[0].mark
        tv_normal_3_day?.text = vipPriceBean.data[1].mark
        tv_normal_12_day?.text = vipPriceBean.data[2].mark

        tv_normal_1_describe?.text = vipPriceBean.data[0].describe
        tv_normal_3_describe?.text = vipPriceBean.data[1].describe
        tv_normal_12_describe?.text = vipPriceBean.data[2].describe

    }

    private fun hidePriceChoose() {
        iv_normal_12_check.setImageResource(R.drawable.ic_vip_check_non)
        iv_normal_3_check.setImageResource(R.drawable.ic_vip_check_non)
        iv_normal_1_check.setImageResource(R.drawable.ic_vip_check_non)
    }

    private fun hidePayChoose() {
        iv_normal_wx_check.setImageResource(R.drawable.ic_vip_check_non)
        iv_normal_ali_check.setImageResource(R.drawable.ic_vip_check_non)
    }

    private fun toBuy(orderInfo: String, activity: VipActivity) {
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

                    //开通佳偶会员成功
                    MobclickAgent.onEvent(mContext, "60012_open_jiaou_member_success");

                    ToastUtils.showShort("用户支付成功")
                    doUpdate()
                }
                "6001" -> {

                    //取消开通佳偶会员
                    MobclickAgent.onEvent(mContext, "60014_open_jiaou_member_cancel");

                    ll_vip_normal_loading?.visibility = View.GONE
                    ToastUtils.showShort("用户取消支付")
                }
                "6002" -> {

                    //开通佳偶会员失败
                    MobclickAgent.onEvent(mContext, "60013_open_jiaou_member_fail");

                    ll_vip_normal_loading?.visibility = View.GONE
                    ToastUtils.showShort("网络连接出错")
                }
                "4000" -> {

                    //开通佳偶会员失败
                    MobclickAgent.onEvent(mContext, "60013_open_jiaou_member_fail");

                    ll_vip_normal_loading?.visibility = View.GONE
                    ToastUtils.showShort("订单支付失败")
                }
                else -> {

                    //开通佳偶会员失败
                    MobclickAgent.onEvent(mContext, "60013_open_jiaou_member_fail");

                    ll_vip_normal_loading?.visibility = View.GONE
//                    ToastUtils.showShort("支付失败，请稍后再试")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(mContext)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(mContext)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }


    override fun onDoVipRefreshSelfSuccess(refreshSelfBean: RefreshSelfBean?) {

        ll_vip_normal_loading?.visibility = View.GONE

        if (refreshSelfBean != null) {
            if (refreshSelfBean.code == 200) {
                SpUtil.refreshUserInfo(refreshSelfBean)

                // 刷新视图
                if (isAdded) {
                    val activity = requireActivity() as VipActivity
                    activity.updateTopView(0)
                }

            } else {
                ToastUtils.showShort(refreshSelfBean.msg)
            }
        }
    }

    override fun onDoVipRefreshSelfError() {
        ll_vip_normal_loading?.visibility = View.GONE
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    override fun onDoAliPaySuccess(aliPayBean: AliPayBean?) {
        if (mode == "vip") {
            if (aliPayBean != null) {
                if (aliPayBean.code == "200") {
                    if (isAdded) {
                        toBuy(aliPayBean.data.str, requireActivity() as VipActivity)
                    }
                } else {
                    ll_vip_normal_loading?.visibility = View.GONE
                    ToastUtils.showShort("支付信息拉起失败，请稍后重试")
                }
            }
        }
    }

    override fun onDoAliPayError() {
        ll_vip_normal_loading?.visibility = View.GONE
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    override fun onDestroy() {
        super.onDestroy()

        doAliPayPresent.unregisterCallback(this)
        doRefreshSelfPresent.unregisterCallback(this)

    }

}