package com.twx.marryfriend.vip.svip

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.vip.adapter.ToolAdapter
import com.twx.marryfriend.vip.normal.NormalFragment
import kotlinx.android.synthetic.main.fragment_normal.*
import kotlinx.android.synthetic.main.fragment_super.*

class SuperFragment : Fragment() {


    private var mVipPrice = 688
    private var mVipMode = 12
    private var mPay = "wx"


    private lateinit var mContext: Context

    private lateinit var adapter: ToolAdapter


    fun newInstance(context: Context): SuperFragment {
        val fragment = SuperFragment()
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

        adapter = ToolAdapter(DataProvider.SuperVipData)

        rv_super_container.layoutManager = GridLayoutManager(mContext, 4)
        rv_super_container.adapter = adapter

        adapter.notifyDataSetChanged()

    }

    private fun initData() {

    }

    private fun initPresent() {

    }

    private fun initEvent() {

        rl_super_12.setOnClickListener {
            hidePriceChoose()
            iv_super_12_check.setImageResource(R.drawable.ic_vip_check)
            mVipPrice = 688
            mVipMode = 12

        }

        rl_super_3.setOnClickListener {
            hidePriceChoose()
            iv_super_3_check.setImageResource(R.drawable.ic_vip_check)
            mVipPrice = 588
            mVipMode = 3
        }

        rl_super_1.setOnClickListener {
            hidePriceChoose()
            iv_super_1_check.setImageResource(R.drawable.ic_vip_check)
            mVipPrice = 388
            mVipMode = 1
        }

        rl_super_wx.setOnClickListener {
            hidePayChoose()
            iv_super_wx_check.setImageResource(R.drawable.ic_vip_check)
            mPay = "wx"
        }

        rl_super_ali.setOnClickListener {
            hidePayChoose()
            iv_super_ali_check.setImageResource(R.drawable.ic_vip_check)
            mPay = "ali"
        }

        tv_super_pay.setOnClickListener {
            ToastUtils.showShort("${mPay}渠道支付${mVipPrice}元购买${mVipMode}月会员")
        }

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

}