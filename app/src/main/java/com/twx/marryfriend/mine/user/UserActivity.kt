package com.twx.marryfriend.mine.user

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blankj.utilcode.util.SPStaticUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.PhotoListBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.mine.user.data.DataFragment
import com.twx.marryfriend.mine.user.target.TargetFragment
import com.twx.marryfriend.net.callback.IGetPhotoListCallback
import com.twx.marryfriend.net.impl.getPhotoListPresentImpl
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.fragment_data.*
import java.io.File
import java.util.*

class UserActivity : MainBaseViewActivity(), IGetPhotoListCallback {

    private lateinit var dateFragment: DataFragment
    private lateinit var targetFragment: TargetFragment

    private lateinit var getPhotoListPresent: getPhotoListPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_user

    override fun initView() {
        super.initView()

        getPhotoListPresent = getPhotoListPresentImpl.getsInstance()
        getPhotoListPresent.registerCallback(this)

        dateFragment = DataFragment()
        targetFragment = TargetFragment()

        getPhoto()

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

    private fun getPhoto() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getPhotoListPresent.getPhotoList(map)

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

    override fun onLoading() {

    }

    override fun onError() {

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

}