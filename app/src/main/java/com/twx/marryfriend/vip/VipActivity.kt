package com.twx.marryfriend.vip

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.vip.PreviewOtherBean
import com.twx.marryfriend.bean.vip.RefreshSelfBean
import com.twx.marryfriend.bean.vip.VipPriceBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.net.callback.vip.*
import com.twx.marryfriend.net.impl.vip.*
import com.twx.marryfriend.utils.SpUtil
import com.twx.marryfriend.utils.weight.FragmentPagerAdapter
import com.twx.marryfriend.utils.weight.XCollapsingToolbarLayout
import com.twx.marryfriend.vip.adapter.DialogAdapter
import com.twx.marryfriend.vip.normal.VipFragment
import com.twx.marryfriend.vip.svip.SVipFragment
import kotlinx.android.synthetic.main.activity_coin.*
import kotlinx.android.synthetic.main.activity_vip.*
import kotlinx.android.synthetic.main.fragment_super.*
import java.util.*

class VipActivity : MainBaseViewActivity(), XCollapsingToolbarLayout.OnScrimsListener,
    OnPageChangeListener, IGetVipPriceCallback, IGetSVipPriceCallback, IDoPreviewOtherCallback,
    IDoRefreshSelfCallback {

    private lateinit var normal: VipFragment
    private lateinit var svip: SVipFragment

    // 是否加载普通会员价格信息
    private var isLoadVip = false

    // 是否加载超级会员价格信息
    private var isLoadSVip = false

    // 查看预览用户的id
    private var targetId = 0

    // 初次进入时选择的会员界面    0 普通会员 ； 1 超级会员
    private var mode = 0

    private lateinit var getVipPricePresent: getVipPricePresentImpl
    private lateinit var getSVipPricePresent: getSVipPricePresentImpl
    private lateinit var doPreviewOtherPresent: doPreviewOtherPresentImpl
    private lateinit var doRefreshSelfPresent: doRefreshSelfPresentImpl

    private lateinit var mPagerAdapter: FragmentPagerAdapter<Fragment>

    private lateinit var mDialogPhotoAdapter: DialogAdapter
    private lateinit var mDialogDynamicAdapter: DialogAdapter

    /**
     *
     * mode : 0 普通会员 ； 1 超级会员
     *id : 查看预览用户的id，不预览时为0
     *
     * */
    companion object {
        private const val VIP_MODE = "vip_mode"
        private const val TARGET_ID = "target_id"
        fun getIntent(context: Context, mode: Int, id: Int? = 0): Intent? {
            val intent = Intent(context, VipActivity::class.java)
            intent.putExtra(VIP_MODE, mode)
            intent.putExtra(TARGET_ID, id)
            return intent
        }
    }

    override fun getLayoutView(): Int = R.layout.activity_vip

    override fun initView() {
        super.initView()

        getVipPricePresent = getVipPricePresentImpl.getsInstance()
        getVipPricePresent.registerCallback(this)

        getSVipPricePresent = getSVipPricePresentImpl.getsInstance()
        getSVipPricePresent.registerCallback(this)

        doPreviewOtherPresent = doPreviewOtherPresentImpl.getsInstance()
        doPreviewOtherPresent.registerCallback(this)

        doRefreshSelfPresent = doRefreshSelfPresentImpl.getsInstance()
        doRefreshSelfPresent.registerCallback(this)

        normal = VipFragment().newInstance(this)
        svip = SVipFragment().newInstance(this)

        if (SPStaticUtils.getInt(Constant.ME_SEX, 1) == 1) {
            Glide.with(this)
                .load(SPStaticUtils.getString(Constant.ME_AVATAR))
                .error(R.drawable.ic_mine_male_default)
                .placeholder(R.drawable.ic_mine_male_default)
                .into(riv_vip_avatar)
        } else {
            Glide.with(this)
                .load(SPStaticUtils.getString(Constant.ME_AVATAR))
                .error(R.drawable.ic_mine_female_default)
                .placeholder(R.drawable.ic_mine_female_default)
                .into(riv_vip_avatar)
        }

        tv_vip_name.text = SPStaticUtils.getString(Constant.ME_NAME, "default")

        mPagerAdapter = FragmentPagerAdapter(this)
        mPagerAdapter.addFragment(normal, "")
        mPagerAdapter.addFragment(svip, "")

        nvp_vip_container.adapter = mPagerAdapter
        nvp_vip_container.addOnPageChangeListener(this)


        xctl.setOnScrimsListener(this)

        mode = intent.getIntExtra("vip_mode", 0)

        when (mode) {
            0 -> {
                if (!isLoadVip) {
                    getVipPrice()
                }
                chooseMode(0)
            }
            1 -> {
                if (!isLoadSVip) {
                    getSVipPrice()
                }
                chooseMode(1)
            }
        }

        targetId = intent.getIntExtra("target_id", 0)

    }

    override fun initLoadData() {
        super.initLoadData()
        if (targetId != 0) {
            rl_vip_dialog_container.visibility = View.VISIBLE
            doPreviewOther()
        }
        updateCoin()
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
            if (!isLoadVip) {
                getVipPrice()
            }
            chooseMode(0)
        }

        tv_vip_super.setOnClickListener {
            if (!isLoadSVip) {
                getSVipPrice()
            }
            chooseMode(1)
        }


        iv_vip_top_switch_vip.setOnClickListener {
            if (!isLoadVip) {
                getVipPrice()
            }
            chooseMode(0)
        }

        iv_vip_top_switch_svip.setOnClickListener {
            if (!isLoadSVip) {
                getSVipPrice()
            }
            chooseMode(1)
        }


        rl_vip_dialog_container.setOnClickListener {

        }

        iv_vip_dialog_close.setOnClickListener {
            rl_vip_dialog_container.visibility = View.GONE
        }

        tv_vip_dialog_dynamic_buy.setOnClickListener {
            rl_vip_dialog_container.visibility = View.GONE
        }

    }

    private fun updateCoin() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        doRefreshSelfPresent.doRefreshSelf(map)
    }

    private fun getVipPrice() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.PLATFORM] = SPStaticUtils.getString(Constant.CHANNEL, "_360")
        map[Contents.TYPE_KIND] = "android"
        map[Contents.VIP_LEVEL] = "1"
        getVipPricePresent.getVipPrice(map)
    }

    private fun getSVipPrice() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.PLATFORM] = SPStaticUtils.getString(Constant.CHANNEL, "_360")
        map[Contents.TYPE_KIND] = "android"
        map[Contents.VIP_LEVEL] = "2"
        getSVipPricePresent.getSVipPrice(map)
    }

    private fun doPreviewOther() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.HOST_UID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.GUEST_UID] = targetId.toString()
        doPreviewOtherPresent.doPreviewOther(map)
    }


    private fun chooseMode(mode: Int) {
        if (mode == 0) {

            iv_vip_top_switch_vip.setImageResource(R.mipmap.pic_vip_check)
            iv_vip_top_switch_svip.setImageResource(R.mipmap.pic_svip_uncheck)

            ll_vip_top_background.setBackgroundResource(R.mipmap.pic_normal_top)

            tv_vip_normal.setPadding(0, 30, 0, 30)
            tv_vip_super.setPadding(0, 20, 0, 20)
            tv_vip_normal.setTextColor(Color.parseColor("#FF4444"))
            tv_vip_super.setTextColor(Color.parseColor("#101010"))

            updateTopView(0)

            nvp_vip_container.currentItem = 0

        } else {

            iv_vip_top_switch_vip.setImageResource(R.mipmap.pic_vip_uncheck)
            iv_vip_top_switch_svip.setImageResource(R.mipmap.pic_svip_check)

            ll_vip_top_background.setBackgroundResource(R.mipmap.pic_super_top)

            tv_vip_super.setPadding(0, 30, 0, 30)
            tv_vip_normal.setPadding(0, 20, 0, 20)
            tv_vip_super.setTextColor(Color.parseColor("#FF4444"))
            tv_vip_normal.setTextColor(Color.parseColor("#101010"))

            updateTopView(1)

            nvp_vip_container.currentItem = 1
        }
    }


    /**
     * 更新最上方视图数据
     * mode :
     *   0 ：刷新普通会员的信息
     *   1 ：刷新超级会员的信息
     * */
    fun updateTopView(mode: Int) {

        when (mode) {
            0 -> {
                // 刷新普通会员的信息

                Log.i("guo", "刷新普通会员的信息")

                if (SPStaticUtils.getInt(Constant.USER_VIP_LEVEL, 0) != 1) {
                    tv_vip_level.text = "您还不是会员"
                    tv_vip_time.visibility = View.GONE
                } else {
                    tv_vip_level.text = "您已经是普通会员"
                    tv_vip_time.visibility = View.VISIBLE
                    tv_vip_time.text = SPStaticUtils.getString(Constant.CLOSE_TIME_LOW)
                }

            }

            1 -> {
                // 刷新超级会员的信息

                Log.i("guo", "刷新超级会员的信息")

                if (SPStaticUtils.getInt(Constant.USER_VIP_LEVEL, 0) != 2) {
                    tv_vip_level.text = "您还不是会员"
                    tv_vip_time.visibility = View.GONE
                } else {
                    tv_vip_level.text = "您已经是超级会员"
                    tv_vip_time.visibility = View.VISIBLE
                    tv_vip_time.text = SPStaticUtils.getString(Constant.CLOSE_TIME_HIGH)
                }
            }

        }

    }


    private fun updateDialog(previewOtherBean: PreviewOtherBean) {

        val base = previewOtherBean.data.base_info
        val trends = previewOtherBean.data.trends_info
        val photo = previewOtherBean.data.photos_info

        if (base.user_sex == 1) {
            Glide.with(applicationContext)
                .load(base.image_url)
                .error(R.drawable.ic_mine_male_default)
                .placeholder(R.drawable.ic_mine_male_default)
                .into(riv_vip_dialog_avatar)
        } else {
            Glide.with(applicationContext)
                .load(base.image_url)
                .error(R.drawable.ic_mine_female_default)
                .placeholder(R.drawable.ic_mine_female_default)
                .into(riv_vip_dialog_avatar)
        }

        tv_vip_dialog_nick.text = base.nick

        tv_vip_dialog_detail.text =
            "${base.work_city_str} ${base.age}岁" +
                    " ${base.occupation_str} ${DataProvider.IncomeData[base.salary_range]} ${base.height}cm"


        if (base.identity_status == 1) {
            iv_vip_dialog_identity.visibility = View.VISIBLE
        } else {
            iv_vip_dialog_identity.visibility = View.GONE
        }

        if (base.real_face == 1) {
            iv_vip_dialog_avatar_true.visibility = View.VISIBLE
        } else {
            iv_vip_dialog_avatar_true.visibility = View.GONE
        }

        tv_vip_dialog_photo_sum.text = "她上传了${previewOtherBean.data.photos_count}张照片"

        val photoList = arrayListOf<String>()

        for (i in 0.until(previewOtherBean.data.photos_count)) {
            photoList.add(photo[i].image_url)
        }

        mDialogPhotoAdapter = DialogAdapter(photoList)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        rl_vip_dialog_photo_container.layoutManager = linearLayoutManager
        rl_vip_dialog_photo_container.adapter = mDialogPhotoAdapter

        tv_vip_dialog_dynamic_sum.text = "她上传了${previewOtherBean.data.trends_count}条动态"



        when (trends.trends_type) {
            1 -> {
                tv_vip_dialog_dynamic_text.visibility = View.GONE
                rv_vip_dialog_dynamic_container.visibility = View.VISIBLE
                fl_vip_dialog_dynamic_video.visibility = View.GONE

                val dynamicList = arrayListOf<String>()

                for (i in 0.until(previewOtherBean.data.photos_count)) {
                    dynamicList.add(photo[i].image_url)
                }

                mDialogDynamicAdapter = DialogAdapter(photoList)
                val dynamicLinearLayoutManager = LinearLayoutManager(this)
                dynamicLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

                rl_vip_dialog_photo_container.layoutManager = dynamicLinearLayoutManager
                rl_vip_dialog_photo_container.adapter = mDialogDynamicAdapter

            }
            2 -> {
                tv_vip_dialog_dynamic_text.visibility = View.GONE
                rv_vip_dialog_dynamic_container.visibility = View.GONE
                fl_vip_dialog_dynamic_video.visibility = View.VISIBLE

                Glide.with(applicationContext)
                    .load(trends.video_url)
                    .error(R.drawable.ic_pic_default)
                    .placeholder(R.drawable.ic_pic_default)
                    .into(iv_vip_dialog_dynamic_video)
            }
            3 -> {
                tv_vip_dialog_dynamic_text.visibility = View.VISIBLE
                rv_vip_dialog_dynamic_container.visibility = View.GONE
                fl_vip_dialog_dynamic_video.visibility = View.GONE

                tv_vip_dialog_dynamic_text.text = trends.text_content
            }
        }

        if (base.introduce_self != "") {
            tv_vip_dialog_dynamic_introduce.text = base.introduce_self
        } else {
            tv_vip_dialog_dynamic_introduce0.visibility = View.GONE
            tv_vip_dialog_dynamic_introduce.visibility = View.GONE
        }


    }


    override fun onScrimsStateChange(layout: XCollapsingToolbarLayout?, shown: Boolean) {

        if (shown) {

            ll_vip_top.setBackgroundResource(R.drawable.shape_bg_vip_top)

            tv_vip_top_title.visibility = View.GONE
            ll_vip_top_switch.visibility = View.VISIBLE

        } else {

            ll_vip_top.setBackgroundResource(R.color.transparent)

            tv_vip_top_title.visibility = View.VISIBLE
            ll_vip_top_switch.visibility = View.GONE

        }

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoRefreshSelfSuccess(refreshSelfBean: RefreshSelfBean?) {
        if (refreshSelfBean != null) {
            if (refreshSelfBean.code == 200) {

                SpUtil.refreshUserInfo(refreshSelfBean)

                updateTopView(mode)

            }
        }
    }

    override fun onDoRefreshSelfError() {

    }

    override fun onDoPreviewOtherSuccess(previewOtherBean: PreviewOtherBean?) {
        if (previewOtherBean != null) {
            if (previewOtherBean.code == 200) {
                updateDialog(previewOtherBean)
            } else {
                ToastUtils.showShort(previewOtherBean.msg)
            }
        }
    }

    override fun onDoPreviewOtherError() {

    }

    override fun onGetSVipPriceSuccess(vipPriceBean: VipPriceBean?) {
        if (vipPriceBean != null) {
            if (vipPriceBean.code == 200) {
                isLoadSVip = true

                for (i in 0.until(vipPriceBean.data.size)) {
                    svip.mVipPriceList.add(vipPriceBean.data[i].now_price)
                    svip.mVipModeList.add(vipPriceBean.data[i].level.toString())
                }

                svip.mVipPrice = vipPriceBean.data[2].now_price
                svip.mVipMode = vipPriceBean.data[2].level.toString()

                svip.updateView(vipPriceBean)
            }
        }
    }

    override fun onGetSVipPriceCodeError() {

    }

    override fun onGetVipPriceSuccess(vipPriceBean: VipPriceBean?) {
        if (vipPriceBean != null) {
            if (vipPriceBean.code == 200) {
                isLoadVip = true

                for (i in 0.until(vipPriceBean.data.size)) {
                    normal.mVipPriceList.add(vipPriceBean.data[i].now_price)
                    normal.mVipModeList.add(vipPriceBean.data[i].level.toString())
                }

                normal.mVipPrice = vipPriceBean.data[2].now_price
                normal.mVipMode = vipPriceBean.data[2].level.toString()

                normal.updateView(vipPriceBean)
            }
        }
    }

    override fun onGetVipPriceCodeError() {

    }

}