package com.twx.marryfriend.mine.user.target

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aigestudio.wheelpicker.WheelPicker
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.CityBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.mine.user.target.adapter.TargetBaseAdapter
import com.twx.marryfriend.mine.user.target.adapter.TargetMoreAdapter
import kotlinx.android.synthetic.main.fragment_target.*

class TargetFragment : Fragment() {

    private var mAgeMinList: MutableList<Int> = arrayListOf()
    private var mAgeMaxList: MutableList<Int> = arrayListOf()

    private var mHeightMinList: MutableList<Int> = arrayListOf()
    private var mHeightMaxList: MutableList<Int> = arrayListOf()

    private var mIncomeList: MutableList<String> = arrayListOf()

    private var mBodyList: MutableList<String> = arrayListOf()

    // 城市数据
    private lateinit var cityDate: CityBean

    private var mCityFirstList: MutableList<String> = arrayListOf()
    private var mCityIdFirstList: MutableList<String> = arrayListOf()
    private var mCitySecondList: MutableList<String> = arrayListOf()
    private var mCityIdSecondList: MutableList<String> = arrayListOf()
    private var mCityThirdList: MutableList<String> = arrayListOf()
    private var mCityIdThirdList: MutableList<String> = arrayListOf()

    private var baseInfoList: MutableList<String> = arrayListOf()
    private var moreInfoList: MutableList<String> = arrayListOf()

    private lateinit var baseAdapter: TargetBaseAdapter
    private lateinit var moreAdapter: TargetMoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_target, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initPresent()
        initEvent()
    }

    private fun initView() {

        initInfo()

        baseAdapter = TargetBaseAdapter(DataProvider.targetBaseData, baseInfoList)
        moreAdapter = TargetMoreAdapter(DataProvider.targetMoreData, moreInfoList)

        val baseScrollManager: LinearLayoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        val moreScrollManager: LinearLayoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        rv_user_target_base.layoutManager = baseScrollManager
        rv_user_target_base.adapter = baseAdapter

        rv_user_target_more.layoutManager = moreScrollManager
        rv_user_target_more.adapter = moreAdapter

    }

    private fun initData() {

        cityDate = GsonUtils.fromJson(SPStaticUtils.getString(Constant.CITY_JSON_DATE),
            CityBean::class.java)

        for (i in 0..42) {
            mAgeMinList.add(18 + i)
            mAgeMaxList.add(18 + i)
        }

        for (i in 0..80) {
            mHeightMinList.add(140 + i)
            mHeightMaxList.add(140 + i)
        }

        mIncomeList.add("保密")
        mIncomeList.add("五千及以下")
        mIncomeList.add("五千~一万")
        mIncomeList.add("一万~两万")
        mIncomeList.add("两万~四万")
        mIncomeList.add("四万~七万")
        mIncomeList.add("七万及以上")

        // 先判断性别
        if (SPStaticUtils.getInt(Constant.ME_SEX, 2) == 2) {
            mBodyList.add("保密")
            mBodyList.add("一般")
            mBodyList.add("瘦长")
            mBodyList.add("运动员型")
            mBodyList.add("比较魁梧")
            mBodyList.add("壮实")
        } else {
            mBodyList.add("保密")
            mBodyList.add("一般")
            mBodyList.add("瘦长")
            mBodyList.add("苗条")
            mBodyList.add("高大美丽")
            mBodyList.add("丰满")
            mBodyList.add("富线条美")
        }

        getJobCityFirstList()
        getJobCitySecondList(0)
        getJobCityThirdList(0, 0)

    }

    private fun initPresent() {

        updateDateUI()

    }

    private fun initEvent() {

        baseAdapter.setOnItemClickListener(object : TargetBaseAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                when (position) {
                    0 -> {
                        showAgeDialog()
                    }
                    1 -> {
                        showHeightDialog()
                    }
                    2 -> {
                        showIncomeDialog()
                    }
                    3 -> {
                        showEduDialog()
                    }
                    4 -> {
                        showMarryStateDialog()
                    }
                }
            }
        })

        moreAdapter.setOnItemClickListener(object : TargetMoreAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                when (position) {
                    0 -> {
                        // 体型
                        showBodyDialog()
                    }
                    1 -> {
                        // 工作地区
                        showJobDialog()
                    }
                    2 -> {
                        // 有没有孩子
                        showHaveChildDialog()
                    }
                    3 -> {
                        // 是否想要孩子
                        showWantChildDialog()
                    }
                    4 -> {
                        // 是否吸烟
                        showSmokeDialog()
                    }
                    5 -> {
                        // 是否喝酒
                        showDrinkDialog()
                    }
                    6 -> {
                        // 有无照片
                        showPhotoDialog()
                    }
                    7 -> {
                        // 何时结婚
                        showMarryDialog()
                    }
                }
            }
        })

    }

    // 获取所有数据更新视图
    private fun updateDateUI() {

        var age = ""
        var height = ""
        var income = ""
        var edu = ""
        var marryState = ""

        var body = ""
        var workPlace = ""
        var haveChild = ""
        var wantChild = ""
        var smoke = ""
        var drink = ""
        var photo = ""
        var marry = ""

        age = when (SPStaticUtils.getInt(Constant.TA_AGE_MIN, 0)) {
            0 -> "未填写"
            else -> "${
                SPStaticUtils.getInt(Constant.TA_AGE_MIN,
                    0)
            }~${SPStaticUtils.getInt(Constant.TA_AGE_MAX, 0)}"
        }

        height = when (SPStaticUtils.getInt(Constant.TA_HEIGHT_MIN, 0)) {
            0 -> "未填写"
            else -> "${SPStaticUtils.getInt(Constant.TA_HEIGHT_MIN, 0)}~${
                SPStaticUtils.getInt(Constant.TA_HEIGHT_MAX,
                    0)
            }"
        }

        when (SPStaticUtils.getInt(Constant.TA_INCOME, 7)) {
            0 -> income = "保密"
            1 -> income = "五千及以下"
            2 -> income = "五千~一万"
            3 -> income = "一万~两万"
            4 -> income = "两万~四万"
            5 -> income = "四万~七万"
            6 -> income = "七万及以上"
            7 -> income = "未填写"
        }

        when (SPStaticUtils.getInt(Constant.TA_EDU, 0)) {
            0 -> edu = "不限"
            1 -> edu = "大专以下"
            2 -> edu = "大专"
            3 -> edu = "本科"
            4 -> edu = "硕士"
            5 -> edu = "博士"
            6 -> edu = "博士以上"
            7 -> edu = "未填写"
        }

        when (SPStaticUtils.getInt(Constant.TA_MARRY_STATE, 4)) {
            0 -> marryState = "不限"
            1 -> marryState = "未婚"
            2 -> marryState = "离异"
            3 -> marryState = "丧偶"
            4 -> marryState = "未填写"
        }

        body = when (SPStaticUtils.getInt(Constant.TA_BODY, 10)) {
            10 -> "未填写"
            else -> mBodyList[SPStaticUtils.getInt(Constant.TA_BODY, 10)]
        }

        workPlace = when (SPStaticUtils.getString(Constant.TA_WORK_PLACE, "")) {
            "" -> "未填写"
            else -> SPStaticUtils.getString(Constant.TA_WORK_PLACE, "")
        }

        when (SPStaticUtils.getInt(Constant.TA_HAVE_CHILD, 5)) {
            0 -> haveChild = "不限"
            1 -> haveChild = "没有孩子"
            2 -> haveChild = "有孩子且住在一起"
            3 -> haveChild = "有孩子偶尔会住在一起"
            4 -> haveChild = "有孩子但不在身边"
            5 -> haveChild = "未填写"
        }

        when (SPStaticUtils.getInt(Constant.TA_WANT_CHILD, 5)) {
            0 -> wantChild = "不限"
            1 -> wantChild = "视情况而定"
            2 -> wantChild = "想要孩子"
            3 -> wantChild = "不想要孩子"
            4 -> wantChild = "以后再告诉你"
            5 -> wantChild = "未填写"
        }

        when (SPStaticUtils.getInt(Constant.TA_SMOKE, 5)) {
            0 -> smoke = "不限"
            1 -> smoke = "可以随意吸烟"
            2 -> smoke = "偶尔抽烟"
            3 -> smoke = "禁止抽烟"
            4 -> smoke = "社交场合可以抽"
            5 -> smoke = "未填写"
        }

        when (SPStaticUtils.getInt(Constant.TA_DRINK, 5)) {
            0 -> drink = "不限"
            1 -> drink = "可以随意喝酒"
            2 -> drink = "偶尔喝酒"
            3 -> drink = "禁止喝酒"
            4 -> drink = "社交场合可以喝"
            5 -> drink = "未填写"
        }

        when (SPStaticUtils.getInt(Constant.TA_HAVE_PHOTO, 3)) {
            0 -> photo = "不限"
            1 -> photo = "有"
            2 -> photo = "没有"
            3 -> photo = "未填写"
        }

        when (SPStaticUtils.getInt(Constant.TA_MARRY, 5)) {
            0 -> marry = "认同闪婚"
            1 -> marry = "一年内"
            2 -> marry = "两年内"
            3 -> marry = "三年内"
            4 -> marry = "实际成熟就结婚"
            5 -> marry = "未填写"
        }

        baseInfoList.clear()
        moreInfoList.clear()

        baseInfoList.add(age)
        baseInfoList.add(height)
        baseInfoList.add(income)
        baseInfoList.add(edu)
        baseInfoList.add(marryState)

        moreInfoList.add(body)
        moreInfoList.add(workPlace)
        moreInfoList.add(haveChild)
        moreInfoList.add(wantChild)
        moreInfoList.add(smoke)
        moreInfoList.add(drink)
        moreInfoList.add(photo)
        moreInfoList.add(marry)

        baseAdapter.notifyDataSetChanged()
        moreAdapter.notifyDataSetChanged()

    }

    // 加载数据
    private fun initInfo() {
        baseInfoList.clear()
        moreInfoList.clear()

        for (i in 0.until(DataProvider.targetBaseData.size)) {
            baseInfoList.add("")
        }

        for (j in 0.until(DataProvider.targetMoreData.size)) {
            moreInfoList.add("")
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

    // 年龄弹窗
    private fun showAgeDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(AgeDialog(requireContext()))
            .show()
    }

    // 身高弹窗
    private fun showHeightDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(HeightDialog(requireContext()))
            .show()
    }

    // 月收入弹窗
    private fun showEduDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(EduDialog(requireContext()))
            .show()
    }

    // 婚况弹窗
    private fun showMarryStateDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(MarryStateDialog(requireContext()))
            .show()
    }

    // 学历弹窗
    private fun showIncomeDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(IncomeDialog(requireContext()))
            .show()
    }

    // 体型弹窗
    private fun showBodyDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(BodyDialog(requireContext()))
            .show()
    }

    // 工作地区
    private fun showJobDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(JobDialog(requireContext()))
            .show()
    }

    // 有没有孩子弹窗
    private fun showHaveChildDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(HaveChildDialog(requireContext()))
            .show()
    }

    // 是否想要孩子
    private fun showWantChildDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(WantChildDialog(requireContext()))
            .show()
    }

    // 是否吸烟
    private fun showSmokeDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(SmokeDialog(requireContext()))
            .show()
    }

    // 是否饮酒
    private fun showDrinkDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(DrinkDialog(requireContext()))
            .show()
    }

    // 有无照片
    private fun showPhotoDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(PhotoDialog(requireContext()))
            .show()
    }

    // 何时结婚
    private fun showMarryDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(MarryDialog(requireContext()))
            .show()
    }


    // ---------------------------------- 点击弹窗 ----------------------------------

    // 年龄
    inner class AgeDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var mMinAgePosition = 0
        private var mMaxAgePosition = 0

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_age

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_target_age_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_age_skip)

            val wheelOne = findViewById<WheelPicker>(R.id.wp_user_target_age_one)
            val wheelTwo = findViewById<WheelPicker>(R.id.wp_user_target_age_two)
            val confirm = findViewById<TextView>(R.id.tv_user_target_age_confirm)

            wheelOne.data = mAgeMinList
            wheelTwo.data = mAgeMaxList

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
            // 设置数据项文本尺寸大小
//            wheelTwo.itemTextSize = ConvertUtils.dp2px(10F)
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
                mMinAgePosition = position

                if (mMinAgePosition > mMaxAgePosition){
                    mMaxAgePosition = mMinAgePosition
                    wheelTwo.selectedItemPosition = mMaxAgePosition
                }

            }

            wheelTwo.setOnItemSelectedListener { picker, data, position ->

                mMaxAgePosition = position

                if (mMaxAgePosition < mMinAgePosition){
                    mMinAgePosition = mMaxAgePosition
                    wheelOne.selectedItemPosition = mMinAgePosition
                }

            }

            confirm.setOnClickListener {
                ToastUtils.showShort("${mAgeMinList[mMinAgePosition]} - ${mAgeMaxList[mMaxAgePosition]} ")
                SPStaticUtils.put(Constant.TA_AGE_MIN, mAgeMinList[mMinAgePosition])
                SPStaticUtils.put(Constant.TA_AGE_MAX, mAgeMaxList[mMaxAgePosition])
                isNeedJump = true
                dismiss()
            }

            close.setOnClickListener {
                isNeedJump = false
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
                showHeightDialog()
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 身高
    inner class HeightDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var mMinHeightPosition = 0
        private var mMaxHeightPosition = 0

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_height

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_target_height_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_height_skip)

            val wheelOne = findViewById<WheelPicker>(R.id.wp_user_target_height_one)
            val wheelTwo = findViewById<WheelPicker>(R.id.wp_user_target_height_two)
            val confirm = findViewById<TextView>(R.id.tv_user_target_height_confirm)

            wheelOne.data = mHeightMinList
            wheelTwo.data = mHeightMaxList

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
            // 设置数据项文本尺寸大小
//            wheelTwo.itemTextSize = ConvertUtils.dp2px(10F)
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
                mMinHeightPosition = position

            }

            wheelTwo.setOnItemSelectedListener { picker, data, position ->

                mMaxHeightPosition = position

            }

            confirm.setOnClickListener {
                ToastUtils.showShort("${mHeightMinList[mMinHeightPosition]} - ${mHeightMaxList[mMaxHeightPosition]} ")
                SPStaticUtils.put(Constant.TA_HEIGHT_MIN, mHeightMinList[mMinHeightPosition])
                SPStaticUtils.put(Constant.TA_HEIGHT_MAX, mHeightMaxList[mMaxHeightPosition])
                isNeedJump = true
                dismiss()
            }

            close.setOnClickListener {
                isNeedJump = false
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
                showIncomeDialog()
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 月收入
    inner class IncomeDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var mIncome = 0

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_income

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_target_income_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_income_skip)

            val wheel = findViewById<WheelPicker>(R.id.wp_user_target_income_container)
            val confirm = findViewById<TextView>(R.id.tv_user_target_income_confirm)

            wheel.data = mIncomeList

            // 是否为循环状态
            wheel.isCyclic = false
            // 当前选中的数据项文本颜色
            wheel.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheel.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheel.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheel.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheel.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheel.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheel.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheel.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheel.itemAlign = WheelPicker.ALIGN_CENTER

            wheel.setOnItemSelectedListener { picker, data, position ->
                mIncome = position
            }

            confirm.setOnClickListener {
                ToastUtils.showShort(mIncome)
                SPStaticUtils.put(Constant.TA_INCOME, mIncome)
                isNeedJump = true
                dismiss()
            }

            close.setOnClickListener {
                isNeedJump = false
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
                showEduDialog()
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 学历
    inner class EduDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_unlimited: TextView
        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView
        private lateinit var tv_five: TextView


        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_edu

        override fun onCreate() {
            super.onCreate()
            val close = findViewById<ImageView>(R.id.iv_user_target_edu_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_edu_skip)

            tv_unlimited = findViewById<TextView>(R.id.tv_user_target_edu_unlimited)
            tv_one = findViewById<TextView>(R.id.tv_user_target_edu_one)
            tv_two = findViewById<TextView>(R.id.tv_user_target_edu_two)
            tv_three = findViewById<TextView>(R.id.tv_user_target_edu_three)
            tv_four = findViewById<TextView>(R.id.tv_user_target_edu_four)
            tv_five = findViewById<TextView>(R.id.tv_user_target_edu_five)

            clearChoose()
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_unlimited.setOnClickListener {
                clearChoose()
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_EDU, 0)
                isNeedJump = true
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_EDU, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_EDU, 2)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                clearChoose()
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_EDU, 3)
                isNeedJump = true
                dismiss()
            }

            tv_four.setOnClickListener {
                clearChoose()
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_EDU, 4)
                isNeedJump = true
                dismiss()
            }

            tv_five.setOnClickListener {
                clearChoose()
                tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_five.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_EDU, 5)
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.TA_EDU, 6)) {
                6 -> {
                }
                0 -> {
                    tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                }
                1 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                3 -> {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
                4 -> {
                    tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_four.setTextColor(Color.parseColor("#FF4444"))
                }
                5 -> {
                    tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_five.setTextColor(Color.parseColor("#FF4444"))
                }

            }

        }

        private fun clearChoose() {
            tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_unlimited.setTextColor(Color.parseColor("#101010"))

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))

            tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_four.setTextColor(Color.parseColor("#101010"))

            tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_five.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showMarryStateDialog()
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }

        }

    }

    // 婚况
    inner class MarryStateDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_unlimited: TextView
        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_marrystate

        override fun onCreate() {
            super.onCreate()
            val close = findViewById<ImageView>(R.id.iv_user_target_marrystate_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_marrystate_skip)

            tv_unlimited = findViewById<TextView>(R.id.tv_user_target_marrystate_unlimited)
            tv_one = findViewById<TextView>(R.id.tv_user_target_marrystate_one)
            tv_two = findViewById<TextView>(R.id.tv_user_target_marrystate_two)
            tv_three = findViewById<TextView>(R.id.tv_user_target_marrystate_three)

            clearChoose()
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_unlimited.setOnClickListener {
                clearChoose()
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_MARRY_STATE, 0)
                isNeedJump = true
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_MARRY_STATE, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_MARRY_STATE, 2)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                clearChoose()
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_MARRY_STATE, 3)
                isNeedJump = true
                dismiss()
            }


            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.TA_MARRY_STATE, 6)) {
                6 -> {
                }
                0 -> {
                    tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                }
                1 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                3 -> {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
            }

        }

        private fun clearChoose() {
            tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_unlimited.setTextColor(Color.parseColor("#101010"))

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showBodyDialog()
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }

        }

    }


    //

    // 体型
    inner class BodyDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var mBody = 0

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_body

        override fun onCreate() {
            super.onCreate()
            val close = findViewById<ImageView>(R.id.iv_user_target_body_close)
            val skip = findViewById<TextView>(R.id.rl_user_target_body_skip)

            val wheel = findViewById<WheelPicker>(R.id.wp_user_target_body_container)
            val confirm = findViewById<TextView>(R.id.tv_user_target_body_confirm)

            wheel.data = mBodyList

            // 是否为循环状态
            wheel.isCyclic = false
            // 当前选中的数据项文本颜色
            wheel.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheel.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheel.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheel.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheel.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheel.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheel.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheel.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheel.itemAlign = WheelPicker.ALIGN_CENTER

            wheel.setOnItemSelectedListener { picker, data, position ->
                mBody = position
            }

            confirm.setOnClickListener {
                ToastUtils.showShort(mBody)
                SPStaticUtils.put(Constant.TA_BODY, mBody)
                isNeedJump = true
                dismiss()
            }

            close.setOnClickListener {
                isNeedJump = false
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
                showJobDialog()
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 工作区域
    inner class JobDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var mCityFirstPosition = 0
        private var mCitySecondPosition = 0
        private var mCityThirdPosition = 0

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_jobaddress

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_data_home_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_home_skip)


            val wheelOne = findViewById<WheelPicker>(R.id.wp_user_data_home_one)
            val wheelTwo = findViewById<WheelPicker>(R.id.wp_user_data_home_two)
            val wheelThree = findViewById<WheelPicker>(R.id.wp_user_data_home_three)
            val confirm = findViewById<TextView>(R.id.tv_user_data_home_confirm)

            wheelOne.data = mCityFirstList
            wheelTwo.data = mCitySecondList
            wheelThree.data = mCityThirdList

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

            // 是否为循环状态
            wheelThree.isCyclic = false
            // 当前选中的数据项文本颜色
            wheelThree.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheelThree.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheelThree.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheelThree.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheelThree.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheelThree.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheelThree.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheelThree.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheelThree.itemAlign = WheelPicker.ALIGN_CENTER


            wheelOne.setOnItemSelectedListener { picker, data, position ->
                mCityFirstPosition = position

                getJobCitySecondList(mCityFirstPosition)

                // 当二级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ， 不至于出现没有数据的情况
                if (mCitySecondPosition >= mCitySecondList.size) {
                    mCitySecondPosition = mCitySecondList.size - 1
                }

                getJobCityThirdList(mCityFirstPosition, mCitySecondPosition)


                // 当三级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ，
                if (mCityThirdPosition >= mCityThirdList.size) {
                    mCityThirdPosition = mCityThirdList.size - 1
                }

                wheelTwo.data = mCitySecondList
                wheelThree.data = mCityThirdList

            }

            wheelTwo.setOnItemSelectedListener { picker, data, position ->

                mCitySecondPosition = position

                getJobCityThirdList(mCityFirstPosition, mCitySecondPosition)

                // 当三级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位
                if (mCityThirdPosition >= mCityThirdList.size) {
                    mCityThirdPosition = mCityThirdList.size - 1
                }

                wheelThree.data = mCityThirdList

            }

            wheelThree.setOnItemSelectedListener { picker, data, position ->

                mCityThirdPosition = position

            }


            confirm.setOnClickListener {

                val workPlace =
                    "${mCityFirstList[mCityFirstPosition]}-${mCitySecondList[mCitySecondPosition]}-${mCityThirdList[mCityThirdPosition]}"

                ToastUtils.showShort(workPlace)

                SPStaticUtils.put(Constant.TA_WORK_PLACE, workPlace)

                isNeedJump = true
                dismiss()
            }


            close.setOnClickListener {
                isNeedJump = false
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
                showHaveChildDialog()
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 有不有孩子
    inner class HaveChildDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_unlimited: TextView
        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_havechild

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_target_havechild_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_havechild_skip)

            tv_unlimited = findViewById<TextView>(R.id.tv_user_target_havechild_unlimited)
            tv_one = findViewById<TextView>(R.id.tv_user_target_havechild_one)
            tv_two = findViewById<TextView>(R.id.tv_user_target_havechild_two)
            tv_three = findViewById<TextView>(R.id.tv_user_target_havechild_three)
            tv_four = findViewById<TextView>(R.id.tv_user_target_havechild_four)

            clearChoose()
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_unlimited.setOnClickListener {
                clearChoose()
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_HAVE_CHILD, 0)
                isNeedJump = true
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_HAVE_CHILD, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_HAVE_CHILD, 2)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                clearChoose()
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_HAVE_CHILD, 3)
                isNeedJump = true
                dismiss()
            }

            tv_four.setOnClickListener {
                clearChoose()
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_HAVE_CHILD, 4)
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.TA_HAVE_CHILD, 5)) {
                5 -> {
                }
                0 -> {
                    tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                }
                1 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                3 -> {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
                4 -> {
                    tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_four.setTextColor(Color.parseColor("#FF4444"))
                }
            }
        }

        private fun clearChoose() {

            tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_unlimited.setTextColor(Color.parseColor("#101010"))

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))

            tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_four.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showWantChildDialog()
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 想不想要孩子
    inner class WantChildDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_unlimited: TextView
        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_wantchild

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_target_wantchild_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_wantchild_skip)

            tv_unlimited = findViewById<TextView>(R.id.tv_user_target_wantchild_unlimited)
            tv_one = findViewById<TextView>(R.id.tv_user_target_wantchild_one)
            tv_two = findViewById<TextView>(R.id.tv_user_target_wantchild_two)
            tv_three = findViewById<TextView>(R.id.tv_user_target_wantchild_three)
            tv_four = findViewById<TextView>(R.id.tv_user_target_wantchild_four)

            clearChoose()
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_unlimited.setOnClickListener {
                clearChoose()
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_WANT_CHILD, 0)
                isNeedJump = true
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_WANT_CHILD, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_WANT_CHILD, 2)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                clearChoose()
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_WANT_CHILD, 3)
                isNeedJump = true
                dismiss()
            }

            tv_four.setOnClickListener {
                clearChoose()
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_WANT_CHILD, 4)
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.TA_WANT_CHILD, 5)) {
                5 -> {
                }
                0 -> {
                    tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                }
                1 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                3 -> {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
                4 -> {
                    tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_four.setTextColor(Color.parseColor("#FF4444"))
                }
            }
        }

        private fun clearChoose() {

            tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_unlimited.setTextColor(Color.parseColor("#101010"))

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))

            tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_four.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showSmokeDialog()
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 是否抽烟
    inner class SmokeDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_unlimited: TextView
        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_smoke

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_target_smoke_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_smoke_skip)

            tv_unlimited = findViewById<TextView>(R.id.tv_user_target_smoke_unlimited)
            tv_one = findViewById<TextView>(R.id.tv_user_target_smoke_one)
            tv_two = findViewById<TextView>(R.id.tv_user_target_smoke_two)
            tv_three = findViewById<TextView>(R.id.tv_user_target_smoke_three)
            tv_four = findViewById<TextView>(R.id.tv_user_target_smoke_four)

            clearChoose()
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_unlimited.setOnClickListener {
                clearChoose()
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_SMOKE, 0)
                isNeedJump = true
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_SMOKE, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_SMOKE, 2)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                clearChoose()
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_SMOKE, 3)
                isNeedJump = true
                dismiss()
            }

            tv_four.setOnClickListener {
                clearChoose()
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_SMOKE, 4)
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.TA_SMOKE, 5)) {
                5 -> {
                }
                0 -> {
                    tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                }
                1 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                3 -> {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
                4 -> {
                    tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_four.setTextColor(Color.parseColor("#FF4444"))
                }
            }
        }

        private fun clearChoose() {

            tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_unlimited.setTextColor(Color.parseColor("#101010"))

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))

            tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_four.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showDrinkDialog()
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 是否喝酒
    inner class DrinkDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_unlimited: TextView
        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_drink

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_target_drink_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_drink_skip)

            tv_unlimited = findViewById<TextView>(R.id.tv_user_target_drink_unlimited)
            tv_one = findViewById<TextView>(R.id.tv_user_target_drink_one)
            tv_two = findViewById<TextView>(R.id.tv_user_target_drink_two)
            tv_three = findViewById<TextView>(R.id.tv_user_target_drink_three)
            tv_four = findViewById<TextView>(R.id.tv_user_target_drink_four)

            clearChoose()
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_unlimited.setOnClickListener {
                clearChoose()
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_DRINK, 0)
                isNeedJump = true
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_DRINK, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_DRINK, 2)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                clearChoose()
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_DRINK, 3)
                isNeedJump = true
                dismiss()
            }

            tv_four.setOnClickListener {
                clearChoose()
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_DRINK, 4)
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.TA_DRINK, 5)) {
                5 -> {
                }
                0 -> {
                    tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                }
                1 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                3 -> {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
                4 -> {
                    tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_four.setTextColor(Color.parseColor("#FF4444"))
                }
            }
        }

        private fun clearChoose() {

            tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_unlimited.setTextColor(Color.parseColor("#101010"))

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))

            tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_four.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showPhotoDialog()
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }
    }

    // 有无照片
    inner class PhotoDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_unlimited: TextView
        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_photo

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_target_photo_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_photo_skip)

            tv_unlimited = findViewById<TextView>(R.id.tv_user_target_photo_unlimited)
            tv_one = findViewById<TextView>(R.id.tv_user_target_photo_one)
            tv_two = findViewById<TextView>(R.id.tv_user_target_photo_two)

            clearChoose()
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_unlimited.setOnClickListener {
                clearChoose()
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_HAVE_PHOTO, 0)
                isNeedJump = true
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_HAVE_PHOTO, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_HAVE_PHOTO, 2)
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.TA_HAVE_PHOTO, 5)) {
                5 -> {
                }
                0 -> {
                    tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                }
                1 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
            }
        }

        private fun clearChoose() {

            tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_unlimited.setTextColor(Color.parseColor("#101010"))

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showMarryDialog()
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 何时结婚
    inner class MarryDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView
        private lateinit var tv_five: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_marry

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_target_marry_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_marry_skip)

            tv_one = findViewById<TextView>(R.id.tv_user_target_marry_one)
            tv_two = findViewById<TextView>(R.id.tv_user_target_marry_two)
            tv_three = findViewById<TextView>(R.id.tv_user_target_marry_three)
            tv_four = findViewById<TextView>(R.id.tv_user_target_marry_four)
            tv_five = findViewById<TextView>(R.id.tv_user_target_marry_five)

            clearChoose()
            initChoose()

            close.setOnClickListener {
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_MARRY, 0)
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_MARRY, 1)
                dismiss()
            }

            tv_three.setOnClickListener {
                clearChoose()
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_MARRY, 2)
                dismiss()
            }

            tv_four.setOnClickListener {
                clearChoose()
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_MARRY, 3)
                dismiss()
            }

            tv_five.setOnClickListener {
                clearChoose()
                tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_five.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.TA_MARRY, 4)
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.TA_DRINK, 5)) {
                5 -> {
                }
                0 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                1 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
                3 -> {
                    tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_four.setTextColor(Color.parseColor("#FF4444"))
                }
                4 -> {
                    tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_five.setTextColor(Color.parseColor("#FF4444"))
                }
            }
        }

        private fun clearChoose() {

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))

            tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_four.setTextColor(Color.parseColor("#101010"))

            tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_five.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            updateDateUI()
            ToastUtils.showShort("刷新界面")
        }

    }

}