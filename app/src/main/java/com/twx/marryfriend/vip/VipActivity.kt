package com.twx.marryfriend.vip

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.fragment.app.FragmentTransaction
import com.blankj.utilcode.util.SPStaticUtils
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.vip.normal.NormalFragment
import com.twx.marryfriend.vip.svip.SuperFragment
import kotlinx.android.synthetic.main.activity_vip.*

class VipActivity : MainBaseViewActivity() {

    private var normal: NormalFragment? = null
    private var svip: SuperFragment? = null

    companion object {
        private const val VIP_MODE = "vip_mode"
        fun getIntent(context: Context, mode: Int): Intent? {
            val intent = Intent(context, VipActivity::class.java)
            intent.putExtra(VIP_MODE, mode)
            return intent
        }
    }

    override fun getLayoutView(): Int = R.layout.activity_vip

    override fun initView() {
        super.initView()

        when (intent.getIntExtra("vip_mode", 0)) {
            0 -> {
                initNormalFragment()
                chooseMode(0)
            }
            1 -> {
                initSuperFragment()
                chooseMode(1)
            }
        }

        if (SPStaticUtils.getInt(Constant.ME_SEX, 1) == 1) {
            Glide.with(this)
                .load(SPStaticUtils.getString(Constant.ME_AVATAR))
                .error(R.mipmap.icon_mine_male_default)
                .placeholder(R.mipmap.icon_mine_male_default)
                .into(riv_vip_avatar)
        } else {
            Glide.with(this)
                .load(SPStaticUtils.getString(Constant.ME_AVATAR))
                .error(R.mipmap.icon_mine_female_default)
                .placeholder(R.mipmap.icon_mine_female_default)
                .into(riv_vip_avatar)
        }

        tv_vip_name.text = SPStaticUtils.getString(Constant.ME_NAME, "default")

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_vip_finish.setOnClickListener {
            finish()
        }

        tv_vip_normal.setOnClickListener {
            initNormalFragment()
            chooseMode(0)
        }

        tv_vip_super.setOnClickListener {
            initSuperFragment()
            chooseMode(1)
        }

    }

    private fun chooseMode(mode: Int) {
        if (mode == 0) {
            tv_vip_normal.setPadding(0, 30, 0, 30)
            tv_vip_super.setPadding(0, 20, 0, 20)
            tv_vip_normal.setTextColor(Color.parseColor("#DF43FC"))
            tv_vip_super.setTextColor(Color.parseColor("#717171"))
        } else {
            tv_vip_super.setPadding(0, 30, 0, 30)
            tv_vip_normal.setPadding(0, 20, 0, 20)
            tv_vip_super.setTextColor(Color.parseColor("#DF43FC"))
            tv_vip_normal.setTextColor(Color.parseColor("#717171"))
        }
    }


    private fun initNormalFragment() {

        ll_vip_top.setBackgroundResource(R.mipmap.pic_normal_top)

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (normal == null) {
            normal = NormalFragment().newInstance(this)
            transaction.add(R.id.fl_vip_container, normal!!)
        }
        hideFragment(transaction)
        transaction.show(normal!!)
        transaction.commit()
    }

    private fun initSuperFragment() {

        ll_vip_top.setBackgroundResource(R.mipmap.pic_super_top)

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (svip == null) {
            svip = SuperFragment().newInstance(this)
            transaction.add(R.id.fl_vip_container, svip!!)
        }
        hideFragment(transaction)
        transaction.show(svip!!)
        transaction.commit()
    }


    private fun hideFragment(transaction: FragmentTransaction) {
        if (normal != null) {
            transaction.hide(normal!!)
        }
        if (svip != null) {
            transaction.hide(svip!!)
        }
    }

}