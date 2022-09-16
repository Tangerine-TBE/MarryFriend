package com.twx.marryfriend.mine.user

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blankj.utilcode.util.SPStaticUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.DemandAddressBean
import com.twx.marryfriend.bean.FiveInfoBean
import com.twx.marryfriend.bean.PhotoListBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.mine.user.data.DataFragment
import com.twx.marryfriend.mine.user.target.TargetFragment
import com.twx.marryfriend.net.callback.IDoGetDemandAddressCallback
import com.twx.marryfriend.net.callback.IGetFiveInfoCallback
import com.twx.marryfriend.net.callback.IGetPhotoListCallback
import com.twx.marryfriend.net.impl.doGetDemandAddressPresentImpl
import com.twx.marryfriend.net.impl.getFiveInfoPresentImpl
import com.twx.marryfriend.net.impl.getPhotoListPresentImpl
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.fragment_data.*
import java.io.File
import java.util.*

class UserActivity : MainBaseViewActivity(), IGetPhotoListCallback, IDoGetDemandAddressCallback,
    IGetFiveInfoCallback {

    private lateinit var dateFragment: DataFragment
    private lateinit var targetFragment: TargetFragment

    private lateinit var getPhotoListPresent: getPhotoListPresentImpl
    private lateinit var doGetDemandAddressPresent: doGetDemandAddressPresentImpl
    private lateinit var getFiveInfoPresent: getFiveInfoPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_user

    override fun initView() {
        super.initView()

        getPhotoListPresent = getPhotoListPresentImpl.getsInstance()
        getPhotoListPresent.registerCallback(this)

        doGetDemandAddressPresent = doGetDemandAddressPresentImpl.getsInstance()
        doGetDemandAddressPresent.registerCallback(this)

        getFiveInfoPresent = getFiveInfoPresentImpl.getsInstance()
        getFiveInfoPresent.registerCallback(this)

        dateFragment = DataFragment()
        targetFragment = TargetFragment()

        getPhoto()

        getDemandAddress()

        getFiveInfo()

        //添加适配器
        vp_user_container.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> dateFragment
                    1 -> targetFragment
                    else -> dateFragment
                }
            }
        }

        TabLayoutMediator(tb_user_indicator, vp_user_container) { tab, position ->
            when (position) {
                0 -> tab.text = "我的资料"
                1 -> tab.text = "择偶条件"
                else -> tab.text = "我的资料"
            }
        }.attach()

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_user_finish.setOnClickListener {
            val intent = intent
            setResult(RESULT_OK, intent)
            finish()
        }

    }

    // 获取生活照
    private fun getPhoto() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getPhotoListPresent.getPhotoList(map)

    }

    // 获取择偶省市要求列表
    private fun getDemandAddress() {
        val demandInfoMap: MutableMap<String, String> = TreeMap()
        demandInfoMap[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
        doGetDemandAddressPresent.doGetDemandAddress(demandInfoMap)
    }

    // 获取五个（所有信息）
    private fun getFiveInfo() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
        getFiveInfoPresent.getFiveInfo(map)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == FragmentActivity.RESULT_OK) {
            when (requestCode) {

                UCrop.REQUEST_CROP -> {
                    if (data != null) {
                        dateFragment.handlePhotoCropResult(data)
                    };
                };
                UCrop.RESULT_ERROR -> {
                    if (data != null) {
                        dateFragment.handlePhotoCropError(data)
                    }
                }

            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = intent
            setResult(RESULT_OK, intent)
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetFiveInfoSuccess(fiveInfoBean: FiveInfoBean?) {
        if (fiveInfoBean != null) {
            if (fiveInfoBean.code == 200) {

                SPStaticUtils.put(Constant.ME_NAME, fiveInfoBean.data.base.nick)

                SPStaticUtils.put(Constant.ME_SEX, fiveInfoBean.data.base.user_sex)

                SPStaticUtils.put(Constant.ME_BIRTH, fiveInfoBean.data.base.birthday)


                SPStaticUtils.put(Constant.ME_HEIGHT, fiveInfoBean.data.base.height)

                SPStaticUtils.put(Constant.ME_INDUSTRY_NAME, fiveInfoBean.data.base.industry_str)
                SPStaticUtils.put(Constant.ME_OCCUPATION_NAME,
                    fiveInfoBean.data.base.occupation_str)

                SPStaticUtils.put(Constant.ME_INCOME, fiveInfoBean.data.base.salary_range)

                SPStaticUtils.put(Constant.ME_WORK,
                    "${fiveInfoBean.data.base.work_province_str}-${fiveInfoBean.data.base.work_city_str}")

                SPStaticUtils.put(Constant.ME_EDU, fiveInfoBean.data.base.education)

                SPStaticUtils.put(Constant.ME_MARRY_STATE, fiveInfoBean.data.base.marry_had)

                SPStaticUtils.put(Constant.ME_LOVE_TARGET, fiveInfoBean.data.more.love_target)

                SPStaticUtils.put(Constant.ME_HAVE_CHILD, fiveInfoBean.data.more.child_had)

                SPStaticUtils.put(Constant.ME_WANT_CHILD, fiveInfoBean.data.more.want_child)

                SPStaticUtils.put(Constant.ME_HOUSE, fiveInfoBean.data.more.buy_house)

                SPStaticUtils.put(Constant.ME_CAR, fiveInfoBean.data.more.buy_car)

                SPStaticUtils.put(Constant.ME_HOME_PROVINCE_NAME,
                    fiveInfoBean.data.base.hometown_province_str)
                SPStaticUtils.put(Constant.ME_HOME_CITY_NAME,
                    fiveInfoBean.data.base.hometown_city_str)

                SPStaticUtils.put(Constant.ME_WEIGHT, fiveInfoBean.data.more.weight)

                SPStaticUtils.put(Constant.ME_BODY, fiveInfoBean.data.more.figure_nan.toInt())


            }
        }
    }

    override fun onGetFiveInfoError() {

    }

    override fun onDoGetDemandAddressSuccess(demandAddressBean: DemandAddressBean?) {

        if (demandAddressBean != null) {
            if (demandAddressBean.code == 200) {

                var workPlace = ""

                var workProvinceName = ""
                var workProvinceCode = ""
                var workCityName = ""
                var workCityCode = ""

                for (i in 0.until(demandAddressBean.data.size)) {

                    workPlace += ",${demandAddressBean.data[i].work_city_str}"
                    workProvinceName += ",${demandAddressBean.data[i].work_province_str}"
                    workProvinceCode += ",${demandAddressBean.data[i].work_province_code}"
                    workCityName += ",${demandAddressBean.data[i].work_city_str}"
                    workCityCode += ",${demandAddressBean.data[i].work_city_code}"

                }
                SPStaticUtils.put(Constant.TA_WORK_PLACE, workPlace)
                SPStaticUtils.put(Constant.WANT_WORK_PROVINCE_NAME, workProvinceName)
                SPStaticUtils.put(Constant.WANT_WORK_PROVINCE_CODE, workProvinceCode)
                SPStaticUtils.put(Constant.WANT_WORK_CITY_NAME, workCityName)
                SPStaticUtils.put(Constant.WANT_WORK_CITY_CODE, workCityCode)

            }
        }

    }

    override fun onDoGetDemandAddressError() {

    }

    override fun onGetPhotoListSuccess(photoListBean: PhotoListBean?) {

        if (photoListBean != null) {
            if (photoListBean.code == 200) {

                when (photoListBean.data.size) {
                    0 -> {
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT, "0")

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT, "0")

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, "0")

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "0")

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")

                    }
                    1 -> {
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE,
                            photoListBean.data[0].image_url)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT,
                            photoListBean.data[0].content)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID,
                            photoListBean.data[0].id.toString())
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT,
                            photoListBean.data[0].status.toString())

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT, "0")

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, "0")

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "0")

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")
                    }
                    2 -> {
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE,
                            photoListBean.data[0].image_url)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT,
                            photoListBean.data[0].content)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID,
                            photoListBean.data[0].id.toString())
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT,
                            photoListBean.data[0].status.toString())

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO,
                            photoListBean.data[1].image_url)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT,
                            photoListBean.data[1].content)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID,
                            photoListBean.data[1].id.toString())
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT,
                            photoListBean.data[1].status.toString())

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, "0")

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "0")

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")
                    }
                    3 -> {
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE,
                            photoListBean.data[0].image_url)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT,
                            photoListBean.data[0].content)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID,
                            photoListBean.data[0].id.toString())
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT,
                            photoListBean.data[0].status.toString())

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO,
                            photoListBean.data[1].image_url)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT,
                            photoListBean.data[1].content)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID,
                            photoListBean.data[1].id.toString())
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT,
                            photoListBean.data[1].status.toString())

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE,
                            photoListBean.data[2].image_url)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT,
                            photoListBean.data[2].content)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID,
                            photoListBean.data[2].id.toString())
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT,
                            photoListBean.data[2].status.toString())

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "0")

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")
                    }
                    4 -> {
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE,
                            photoListBean.data[0].image_url)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT,
                            photoListBean.data[0].content)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID,
                            photoListBean.data[0].id.toString())
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT,
                            photoListBean.data[0].status.toString())

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO,
                            photoListBean.data[1].image_url)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT,
                            photoListBean.data[1].content)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID,
                            photoListBean.data[1].id.toString())
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT,
                            photoListBean.data[1].status.toString())

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE,
                            photoListBean.data[2].image_url)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT,
                            photoListBean.data[2].content)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID,
                            photoListBean.data[2].id.toString())
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT,
                            photoListBean.data[2].status.toString())

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR,
                            photoListBean.data[3].image_url)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT,
                            photoListBean.data[3].content)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID,
                            photoListBean.data[3].id.toString())
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT,
                            photoListBean.data[3].status.toString())

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")
                    }
                    5 -> {
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE,
                            photoListBean.data[0].image_url)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT,
                            photoListBean.data[0].content)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID,
                            photoListBean.data[0].id.toString())
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT,
                            photoListBean.data[0].status.toString())

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO,
                            photoListBean.data[1].image_url)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT,
                            photoListBean.data[1].content)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID,
                            photoListBean.data[1].id.toString())
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT,
                            photoListBean.data[1].status.toString())

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE,
                            photoListBean.data[2].image_url)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT,
                            photoListBean.data[2].content)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID,
                            photoListBean.data[2].id.toString())
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT,
                            photoListBean.data[2].status.toString())

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR,
                            photoListBean.data[3].image_url)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT,
                            photoListBean.data[3].content)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID,
                            photoListBean.data[3].id.toString())
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT,
                            photoListBean.data[3].status.toString())

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE,
                            photoListBean.data[4].image_url)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT,
                            photoListBean.data[4].content)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID,
                            photoListBean.data[4].id.toString())
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT,
                            photoListBean.data[4].status.toString())
                    }
                }
            }
        }

    }

    override fun onGetPhotoListError() {

    }

    override fun onDestroy() {
        super.onDestroy()

        getPhotoListPresent.unregisterCallback(this)
        doGetDemandAddressPresent.unregisterCallback(this)
        getFiveInfoPresent.unregisterCallback(this)

    }

}