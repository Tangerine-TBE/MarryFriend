package com.twx.marryfriend.vip.normal

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.to.aboomy.pager2banner.IndicatorView
import com.twx.marryfriend.R
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.vip.adapter.BannerAdapter
import com.twx.marryfriend.vip.adapter.ToolAdapter
import kotlinx.android.synthetic.main.fragment_normal.*


class NormalFragment : Fragment() {

    private var mVipPrice = 398
    private var mVipMode = 12
    private var mPay = "wx"

    private var image: MutableList<Int> = ArrayList()

    private lateinit var adapter: ToolAdapter

    private lateinit var mContext: Context

    fun newInstance(context: Context): NormalFragment {
        val fragment = NormalFragment()
        fragment.mContext = context
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

        adapter = ToolAdapter(DataProvider.NormalVipData)

        rv_normal_container.layoutManager = GridLayoutManager(mContext, 4)
        rv_normal_container.adapter = adapter

        adapter.notifyDataSetChanged()

    }

    private fun initData() {

        image.add(R.mipmap.icon_super_1)
        image.add(R.mipmap.icon_super_2)
        image.add(R.mipmap.icon_super_3)


//        initBanner()

    }

    private fun initPresent() {

    }

    private fun initEvent() {

        rl_normal_12.setOnClickListener {
            hidePriceChoose()
            iv_normal_12_check.setImageResource(R.drawable.ic_vip_check)
            mVipPrice = 398
            mVipMode = 12

        }

        rl_normal_3.setOnClickListener {
            hidePriceChoose()
            iv_normal_3_check.setImageResource(R.drawable.ic_vip_check)
            mVipPrice = 358
            mVipMode = 3
        }

        rl_normal_1.setOnClickListener {
            hidePriceChoose()
            iv_normal_1_check.setImageResource(R.drawable.ic_vip_check)
            mVipPrice = 258
            mVipMode = 1
        }

        rl_normal_wx.setOnClickListener {
            hidePayChoose()
            iv_normal_wx_check.setImageResource(R.drawable.ic_vip_check)
            mPay = "wx"
        }

        rl_normal_ali.setOnClickListener {
            hidePayChoose()
            iv_normal_ali_check.setImageResource(R.drawable.ic_vip_check)
            mPay = "ali"
        }

        tv_normal_pay.setOnClickListener {
            ToastUtils.showShort("${mPay}渠道支付${mVipPrice}元购买${mVipMode}月会员")
        }

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


    private fun initBanner() {
        val image: List<Int> = image

        val indicator = IndicatorView(mContext)
            .setIndicatorColor(Color.DKGRAY)
            .setIndicatorSelectorColor(Color.WHITE)

        //创建adapter
        val adapter = BannerAdapter(image)

        //传入RecyclerView.Adapter 即可实现无限轮播
        banner_normal_container.setIndicator(indicator).adapter = adapter

    }

}