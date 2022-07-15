package com.twx.marryfriend.recommend

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.aigestudio.wheelpicker.WheelPicker
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.exoplayer2.util.NalUnitUtil
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.CityBean
import com.twx.marryfriend.constant.Constant

class RecommendFragment : Fragment() {

    // 工作数据
    private var mJobFirstList: MutableList<String> = arrayListOf()
    private var mJobIdFirstList: MutableList<Int> = arrayListOf()
    private var mJobSecondList: MutableList<String> = arrayListOf()
    private var mJobIdSecondList: MutableList<Int> = arrayListOf()

    // 城市数据
    private lateinit var cityDate: CityBean

    private var mCityFirstList: MutableList<String> = arrayListOf()
    private var mCityIdFirstList: MutableList<String> = arrayListOf()
    private var mCitySecondList: MutableList<String> = arrayListOf()
    private var mCityIdSecondList: MutableList<String> = arrayListOf()
    private var mCityThirdList: MutableList<String> = arrayListOf()
    private var mCityIdThirdList: MutableList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_recommend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initPresent()
        initEvent()
    }

    private fun initView() {


    }

    private fun initData() {

        val size = SPStaticUtils.getInt(Constant.INDUSTRY_SUM, 0)
        for (i in 0.until(size)) {
            mJobFirstList.add(SPStaticUtils.getString("industry_item_name_$i", ""))
            mJobIdFirstList.add(SPStaticUtils.getInt("industry_item_id_$i", 0))
        }

        val jobSecondSize = SPStaticUtils.getInt("job_second_0 _sum", 0)
        for (i in 0.until(jobSecondSize)) {
            mJobSecondList.add(SPStaticUtils.getString("job_second_0 _name_$i", ""))
            mJobIdSecondList.add(SPStaticUtils.getInt("job_second_0 _id_$i", 0))
        }


        cityDate = GsonUtils.fromJson(SPStaticUtils.getString(Constant.CITY_JSON_DATE),
            CityBean::class.java)

        getJobCityFirstList()
        getJobCitySecondList(0)
        getJobCityThirdList(0, 0)

    }

    private fun initPresent() {

    }

    private fun initEvent() {

        showDialog()

    }

    private fun showDialog(){
        if (SPStaticUtils.getInt(Constant.ME_CAR, 0) != 0) {
            // 购车有数据，展示家乡数据
            if (SPStaticUtils.getString(Constant.ME_HOME_PROVINCE_NAME, "") != "") {
                // 家乡有数据，展示是否抽烟数据
                if (SPStaticUtils.getInt(Constant.ME_SMOKE, 0) != 0) {
                    if (SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0) != 0) {
                        // 是否有孩子有数据，展示是否想要孩子数据
                        if (SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0) != 0) {
                            // 是否想要孩子有数据，展示职业数据
                            if (SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME, "") != "") {
                                // 职业有数据，展示购房数据
                                if (SPStaticUtils.getInt(Constant.ME_HOUSE, 0) != 0) {
                                    // 全部数据都具有，不弹弹窗
                                } else {
                                    showHouseDialog()
                                }
                            } else {
                                showJobDialog()
                            }
                        } else {
                            showWantChildDialog()
                        }
                    } else {
                        showHaveChildDialog()
                    }
                } else {
                    showSmokeDialog()
                }
            } else {
                showHomeDialog()
            }
        } else {
            showCarDialog()
        }
    }


    private fun showCarDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(CarDialog(requireContext()))
            .show()
    }

    private fun showHomeDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(HomeDialog(requireContext()))
            .show()
    }

    private fun showSmokeDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(SmokeDialog(requireContext()))
            .show()
    }

    private fun showHaveChildDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(HaveChildDialog(requireContext()))
            .show()
    }

    private fun showWantChildDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(WantChildDialog(requireContext()))
            .show()
    }

    private fun showJobDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(JobDialog(requireContext()))
            .show()
    }

    private fun showHouseDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(HouseDialog(requireContext()))
            .show()
    }


    private fun getJobSecondList(i: Int) {
        mJobSecondList.clear()
        mJobIdSecondList.clear()
        val jobSecondSize = SPStaticUtils.getInt("job_second_$i _sum", 0)
        for (j in 0.until(jobSecondSize)) {
            mJobSecondList.add(SPStaticUtils.getString("job_second_$i _name_$j", null))
            mJobIdSecondList.add(SPStaticUtils.getInt("job_second_$i _id_$j", 0))
        }
    }

    // 省
    private fun getJobCityFirstList() {
        mCityFirstList.clear()
        mCityIdFirstList.clear()
        for (i in 0.until(cityDate.data.size)) {
            mCityFirstList.add(cityDate.data[i].name)
            mCityIdFirstList.add(cityDate.data[i].code)
        }
    }

    // 市
    private fun getJobCitySecondList(i: Int) {
        mCitySecondList.clear()
        mCityIdSecondList.clear()
        for (j in 0.until(cityDate.data[i].cityList.size)) {
            mCitySecondList.add(cityDate.data[i].cityList[j].name)
            mCityIdSecondList.add(cityDate.data[i].cityList[j].code)
        }
    }

    // 县
    private fun getJobCityThirdList(i: Int, j: Int) {
        mCityThirdList.clear()
        mCityIdThirdList.clear()
        if (cityDate.data[i].cityList[j].areaList.isNotEmpty()) {
            for (k in 0.until(cityDate.data[i].cityList[j].areaList.size)) {
                mCityThirdList.add(cityDate.data[i].cityList[j].areaList[k].name)
                mCityIdThirdList.add(cityDate.data[i].cityList[j].areaList[k].code)
            }
        } else {
            mCityThirdList.add("")
            mCityIdThirdList.add("")
        }
    }


    // 购车情况
    inner class CarDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_recommend_car

        override fun onCreate() {
            super.onCreate()

            val skip = findViewById<TextView>(R.id.ll_recommend_car_skip)

            tv_one = findViewById<TextView>(R.id.tv_recommend_car_one)
            tv_two = findViewById<TextView>(R.id.tv_recommend_car_two)

            tv_one.setOnClickListener {
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_CAR, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_CAR, 2)
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }
        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showDialog()
            }
        }
    }

    // 籍贯
    inner class HomeDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var mCityFirstPosition = 0
        private var mCitySecondPosition = 0

        override fun getImplLayoutId(): Int = R.layout.dialog_recommend_address

        override fun onCreate() {
            super.onCreate()

            val skip = findViewById<TextView>(R.id.tv_recommend_address_skip)

            val wheelOne = findViewById<WheelPicker>(R.id.wp_recommend_address_one)
            val wheelTwo = findViewById<WheelPicker>(R.id.wp_recommend_address_two)

            val confirm = findViewById<TextView>(R.id.tv_recommend_address_confirm)

            wheelOne.data = mCityFirstList
            wheelTwo.data = mCitySecondList

            mCityFirstPosition = SPStaticUtils.getInt(Constant.ME_HOME_PROVINCE_PICK, 0)
            mCitySecondPosition = SPStaticUtils.getInt(Constant.ME_HOME_CITY_PICK, 0)

            wheelOne.setSelectedItemPosition(mCityFirstPosition, false)
            getJobCitySecondList(mCityFirstPosition)
            wheelTwo.setSelectedItemPosition(mCitySecondPosition, false)

            // 是否为循环状态
            wheelOne.isCyclic = false
            // 当前选中的数据项文本颜色
            wheelOne.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheelOne.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheelOne.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheelOne.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheelOne.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheelOne.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheelOne.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheelOne.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheelOne.itemAlign = WheelPicker.ALIGN_CENTER

            // 是否为循环状态
            wheelTwo.isCyclic = false
            // 当前选中的数据项文本颜色
            wheelTwo.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheelTwo.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheelTwo.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheelTwo.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheelTwo.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheelTwo.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheelTwo.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheelTwo.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheelTwo.itemAlign = WheelPicker.ALIGN_CENTER


            wheelOne.setOnItemSelectedListener { picker, data, position ->
                mCityFirstPosition = position

                getJobCitySecondList(mCityFirstPosition)

                // 当二级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ， 不至于出现没有数据的情况
                if (mCitySecondPosition >= mCitySecondList.size) {
                    mCitySecondPosition = mCitySecondList.size - 1
                }

                wheelTwo.data = mCitySecondList

            }

            wheelTwo.setOnItemSelectedListener { picker, data, position ->
                mCitySecondPosition = position
            }

            confirm.setOnClickListener {

                val home =
                    "${mCityFirstList[mCityFirstPosition]}-${mCitySecondList[mCitySecondPosition]}"

                ToastUtils.showShort(home)

                SPStaticUtils.put(Constant.ME_HOME, home)

                SPStaticUtils.put(Constant.ME_HOME_PROVINCE_NAME,
                    mCityFirstList[mCityFirstPosition])
                SPStaticUtils.put(Constant.ME_HOME_PROVINCE_CODE,
                    mCityIdFirstList[mCityFirstPosition])
                SPStaticUtils.put(Constant.ME_HOME_PROVINCE_PICK, mCityFirstPosition)
                SPStaticUtils.put(Constant.ME_HOME_CITY_NAME, mCitySecondList[mCitySecondPosition])
                SPStaticUtils.put(Constant.ME_HOME_CITY_CODE,
                    mCityIdSecondList[mCitySecondPosition])
                SPStaticUtils.put(Constant.ME_HOME_CITY_PICK, mCitySecondPosition)

                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }
        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showDialog()
            }
        }
    }

    // 抽烟情况
    inner class SmokeDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_recommend_smoke

        override fun onCreate() {
            super.onCreate()

            val skip = findViewById<TextView>(R.id.ll_recommend_smoke_skip)

            tv_one = findViewById<TextView>(R.id.tv_recommend_smoke_one)
            tv_two = findViewById<TextView>(R.id.tv_recommend_smoke_two)
            tv_three = findViewById<TextView>(R.id.tv_recommend_smoke_three)
            tv_four = findViewById<TextView>(R.id.tv_recommend_smoke_four)

            tv_one.setOnClickListener {
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_SMOKE, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_SMOKE, 2)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_SMOKE, 3)
                isNeedJump = true
                dismiss()
            }

            tv_four.setOnClickListener {
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_SMOKE, 4)
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }
        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showDialog()
            }
        }
    }

    // 有没有孩子
    inner class HaveChildDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_recommend_havechild

        override fun onCreate() {
            super.onCreate()

            val skip = findViewById<TextView>(R.id.tv_recommend_havechild_skip)

            tv_one = findViewById<TextView>(R.id.tv_recommend_havechild_one)
            tv_two = findViewById<TextView>(R.id.tv_recommend_havechild_two)
            tv_three = findViewById<TextView>(R.id.tv_recommend_havechild_three)
            tv_four = findViewById<TextView>(R.id.tv_recommend_havechild_four)

            tv_one.setOnClickListener {
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HAVE_CHILD, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HAVE_CHILD, 2)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HAVE_CHILD, 3)
                isNeedJump = true
                dismiss()
            }

            tv_four.setOnClickListener {
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HAVE_CHILD, 4)
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showDialog()
            }
        }

    }

    // 是否想要孩子
    inner class WantChildDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_recommend_wantchild

        override fun onCreate() {
            super.onCreate()

            val skip = findViewById<TextView>(R.id.tv_recommend_wantchild_skip)

            tv_one = findViewById<TextView>(R.id.tv_recommend_wantchild_one)
            tv_two = findViewById<TextView>(R.id.tv_recommend_wantchild_two)
            tv_three = findViewById<TextView>(R.id.tv_recommend_wantchild_three)
            tv_four = findViewById<TextView>(R.id.tv_recommend_wantchild_four)

            tv_one.setOnClickListener {
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_WANT_CHILD, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_WANT_CHILD, 2)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_WANT_CHILD, 3)
                isNeedJump = true
                dismiss()
            }

            tv_four.setOnClickListener {
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_WANT_CHILD, 4)
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showDialog()
            }
        }
    }

    // 职业
    inner class JobDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var mFirstJobPosition = 0
        private var mSecondJobPosition = 0

        override fun getImplLayoutId(): Int = R.layout.dialog_recommend_job

        override fun onCreate() {
            super.onCreate()

            val skip = findViewById<TextView>(R.id.tv_recommend_job_skip)

            val wheelOne = findViewById<WheelPicker>(R.id.wp_recommend_job_one)
            val wheelTwo = findViewById<WheelPicker>(R.id.wp_recommend_job_two)
            val confirm = findViewById<TextView>(R.id.tv_recommend_job_confirm)

            wheelOne.data = mJobFirstList
            wheelTwo.data = mJobSecondList

            mFirstJobPosition = SPStaticUtils.getInt(Constant.ME_INDUSTRY_PICK, 0)
            mSecondJobPosition = SPStaticUtils.getInt(Constant.ME_OCCUPATION_PICK, 0)

            wheelOne.setSelectedItemPosition(SPStaticUtils.getInt(Constant.ME_INDUSTRY_PICK, 0),
                false)
            getJobSecondList(SPStaticUtils.getInt(Constant.ME_INDUSTRY_PICK, 0))
            wheelTwo.setSelectedItemPosition(SPStaticUtils.getInt(Constant.ME_OCCUPATION_PICK, 0),
                false)

            // 是否为循环状态
            wheelOne.isCyclic = false
            // 当前选中的数据项文本颜色
            wheelOne.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheelOne.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheelOne.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheelOne.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheelOne.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheelOne.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheelOne.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheelOne.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheelOne.itemAlign = WheelPicker.ALIGN_CENTER

            // 是否为循环状态
            wheelTwo.isCyclic = false
            // 当前选中的数据项文本颜色
            wheelTwo.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheelTwo.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheelTwo.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheelTwo.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheelTwo.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheelTwo.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheelTwo.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheelTwo.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheelTwo.itemAlign = WheelPicker.ALIGN_CENTER

            wheelOne.setOnItemSelectedListener { picker, data, position ->
                mFirstJobPosition = position

                getJobSecondList(mFirstJobPosition)

                // 当二级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ， 不至于出现没有数据的情况
                if (mSecondJobPosition >= mJobSecondList.size) {
                    mSecondJobPosition = mJobSecondList.size - 1
                }

                wheelTwo.data = mJobSecondList

            }

            wheelTwo.setOnItemSelectedListener { picker, data, position ->

                mSecondJobPosition = position

            }

            confirm.setOnClickListener {
                ToastUtils.showShort("${mJobFirstList[mFirstJobPosition]} - ${mJobIdFirstList[mFirstJobPosition]} ," + " ${mJobSecondList[mSecondJobPosition]} - ${mJobIdSecondList[mSecondJobPosition]}")
                SPStaticUtils.put(Constant.ME_INDUSTRY_NAME, mJobFirstList[mFirstJobPosition])
                SPStaticUtils.put(Constant.ME_INDUSTRY_CODE, mJobIdFirstList[mFirstJobPosition])
                SPStaticUtils.put(Constant.ME_INDUSTRY_PICK, mFirstJobPosition)

                SPStaticUtils.put(Constant.ME_OCCUPATION_NAME, mJobSecondList[mSecondJobPosition])
                SPStaticUtils.put(Constant.ME_OCCUPATION_CODE, mJobIdSecondList[mSecondJobPosition])
                SPStaticUtils.put(Constant.ME_OCCUPATION_PICK, mSecondJobPosition)
                isNeedJump = true
                dismiss()
            }


            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }
        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showDialog()
            }
        }

    }

    // 购房情况
    inner class HouseDialog(context: Context) : FullScreenPopupView(context) {

        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView
        private lateinit var tv_five: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_recommend_house

        override fun onCreate() {
            super.onCreate()

            val skip = findViewById<TextView>(R.id.tv_recommend_house_skip)

            tv_one = findViewById<TextView>(R.id.tv_recommend_house_one)
            tv_two = findViewById<TextView>(R.id.tv_recommend_house_two)
            tv_three = findViewById<TextView>(R.id.tv_recommend_house_three)
            tv_four = findViewById<TextView>(R.id.tv_recommend_house_four)
            tv_five = findViewById<TextView>(R.id.tv_recommend_house_five)

            tv_one.setOnClickListener {
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HOUSE, 1)
                dismiss()
            }

            tv_two.setOnClickListener {
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HOUSE, 2)
                dismiss()
            }

            tv_three.setOnClickListener {
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HOUSE, 3)
                dismiss()
            }

            tv_four.setOnClickListener {
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HOUSE, 4)
                dismiss()
            }

            tv_five.setOnClickListener {
                tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_five.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HOUSE, 5)
                dismiss()
            }

            skip.setOnClickListener {
                dismiss()
            }

        }

    }

}