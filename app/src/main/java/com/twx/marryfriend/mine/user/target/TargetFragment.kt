package com.twx.marryfriend.mine.user.target

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aigestudio.wheelpicker.WheelPicker
import com.blankj.utilcode.util.*
import com.luck.picture.lib.decoration.WrapContentLinearLayoutManager
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.CityBean
import com.twx.marryfriend.bean.UpdateDemandInfoBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.mine.user.target.adapter.JobAddressAdapter
import com.twx.marryfriend.mine.user.target.adapter.TargetBaseAdapter
import com.twx.marryfriend.mine.user.target.adapter.TargetMoreAdapter
import com.twx.marryfriend.net.callback.IDoUpdateDemandInfoCallback
import com.twx.marryfriend.net.impl.doUpdateDemandInfoPresentImpl
import kotlinx.android.synthetic.main.fragment_target.*
import java.util.*

class TargetFragment : Fragment(), IDoUpdateDemandInfoCallback {

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
    private var jobAddressInfoList: MutableList<String> = arrayListOf()

    private lateinit var baseAdapter: TargetBaseAdapter
    private lateinit var moreAdapter: TargetMoreAdapter

    private lateinit var jobAddressAdapter: JobAddressAdapter

    private lateinit var updateDemandInfoPresent: doUpdateDemandInfoPresentImpl

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
        jobAddressAdapter = JobAddressAdapter(jobAddressInfoList)

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

        updateDemandInfoPresent = doUpdateDemandInfoPresentImpl.getsInstance()
        updateDemandInfoPresent.registerCallback(this)

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

        mIncomeList.add("不限")
        mIncomeList.add("5000元")
        mIncomeList.add("10000元")
        mIncomeList.add("20000元")
        mIncomeList.add("40000元")
        mIncomeList.add("70000元")

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

    // 点击选项之后显示下一个弹窗
    private fun showNextDialog(position: Int) {
        when (position) {
            0 -> {
                if (SPStaticUtils.getInt(Constant.TA_HEIGHT_MAX, 0) == 0) {
                    // 身高
                    showHeightDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.TA_INCOME_MIN, 7) == 7) {
                        // 月收入
                        showIncomeDialog()
                    } else {
                        if (SPStaticUtils.getString(Constant.TA_EDU, "") == "") {
                            // 学历
                            showEduDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.TA_MARRY_STATE, 4) == 4) {
                                // 婚况
                                showMarryStateDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.TA_BODY, 10) == 10) {
                                    // 体型
                                    showBodyDialog()
                                } else {
                                    if (SPStaticUtils.getInt(Constant.TA_WORK_PROVINCE_PICK,
                                            34) == 34
                                    ) {
                                        // 工作地区
                                        showJobDialog()
                                    } else {
                                        if (SPStaticUtils.getInt(Constant.TA_HAVE_CHILD, 5) == 5) {
                                            // 有没有孩子
                                            showHaveChildDialog()
                                        } else {
                                            if (SPStaticUtils.getInt(Constant.TA_WANT_CHILD,
                                                    5) == 5
                                            ) {
                                                // 是否想要孩子
                                                showWantChildDialog()
                                            } else {
                                                if (SPStaticUtils.getInt(Constant.TA_SMOKE,
                                                        5) == 5
                                                ) {
                                                    // 是否吸烟
                                                    showSmokeDialog()
                                                } else {
                                                    if (SPStaticUtils.getInt(Constant.TA_DRINK,
                                                            5) == 5
                                                    ) {
                                                        // 是否喝酒
                                                        showDrinkDialog()
                                                    } else {
                                                        if (SPStaticUtils.getInt(Constant.TA_HAVE_PHOTO,
                                                                3) == 3
                                                        ) {
                                                            // 有无照片
                                                            showPhotoDialog()
                                                        } else {
                                                            if (SPStaticUtils.getInt(Constant.TA_MARRY,
                                                                    5) == 5
                                                            ) {
                                                                // 何时结婚
                                                                showMarryDialog()
                                                            } else {
                                                                updateDateUI()
                                                                update()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                if (SPStaticUtils.getInt(Constant.TA_INCOME_MIN, 7) == 7) {
                    // 月收入
                    showIncomeDialog()
                } else {
                    if (SPStaticUtils.getString(Constant.TA_EDU, "") == "") {
                        // 学历
                        showEduDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.TA_MARRY_STATE, 4) == 4) {
                            // 婚况
                            showMarryStateDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.TA_BODY, 10) == 10) {
                                // 体型
                                showBodyDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.TA_WORK_PROVINCE_PICK,
                                        34) == 34
                                ) {
                                    // 工作地区
                                    showJobDialog()
                                } else {
                                    if (SPStaticUtils.getInt(Constant.TA_HAVE_CHILD, 5) == 5) {
                                        // 有没有孩子
                                        showHaveChildDialog()
                                    } else {
                                        if (SPStaticUtils.getInt(Constant.TA_WANT_CHILD, 5) == 5) {
                                            // 是否想要孩子
                                            showWantChildDialog()
                                        } else {
                                            if (SPStaticUtils.getInt(Constant.TA_SMOKE, 5) == 5) {
                                                // 是否吸烟
                                                showSmokeDialog()
                                            } else {
                                                if (SPStaticUtils.getInt(Constant.TA_DRINK,
                                                        5) == 5
                                                ) {
                                                    // 是否喝酒
                                                    showDrinkDialog()
                                                } else {
                                                    if (SPStaticUtils.getInt(Constant.TA_HAVE_PHOTO,
                                                            3) == 3
                                                    ) {
                                                        // 有无照片
                                                        showPhotoDialog()
                                                    } else {
                                                        if (SPStaticUtils.getInt(Constant.TA_MARRY,
                                                                5) == 5
                                                        ) {
                                                            // 何时结婚
                                                            showMarryDialog()
                                                        } else {
                                                            updateDateUI()
                                                            update()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                if (SPStaticUtils.getString(Constant.TA_EDU, "") == "") {
                    // 学历
                    showEduDialog()
                } else {
                    if (SPStaticUtils.getString(Constant.TA_MARRY_STATE, "") == "") {
                        // 婚况
                        showMarryStateDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.TA_BODY, 10) == 10) {
                            // 体型
                            showBodyDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.TA_WORK_PROVINCE_PICK, 34) == 34) {
                                // 工作地区
                                showJobDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.TA_HAVE_CHILD, 5) == 5) {
                                    // 有没有孩子
                                    showHaveChildDialog()
                                } else {
                                    if (SPStaticUtils.getInt(Constant.TA_WANT_CHILD, 5) == 5) {
                                        // 是否想要孩子
                                        showWantChildDialog()
                                    } else {
                                        if (SPStaticUtils.getInt(Constant.TA_SMOKE, 5) == 5) {
                                            // 是否吸烟
                                            showSmokeDialog()
                                        } else {
                                            if (SPStaticUtils.getInt(Constant.TA_DRINK, 5) == 5) {
                                                // 是否喝酒
                                                showDrinkDialog()
                                            } else {
                                                if (SPStaticUtils.getInt(Constant.TA_HAVE_PHOTO,
                                                        3) == 3
                                                ) {
                                                    // 有无照片
                                                    showPhotoDialog()
                                                } else {
                                                    if (SPStaticUtils.getInt(Constant.TA_MARRY,
                                                            5) == 5
                                                    ) {
                                                        // 何时结婚
                                                        showMarryDialog()
                                                    } else {
                                                        updateDateUI()
                                                        update()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            3 -> {
                if (SPStaticUtils.getString(Constant.TA_MARRY_STATE, "") == "") {
                    // 婚况
                    showMarryStateDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.TA_BODY, 10) == 10) {
                        // 体型
                        showBodyDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.TA_WORK_PROVINCE_PICK, 34) == 34) {
                            // 工作地区
                            showJobDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.TA_HAVE_CHILD, 5) == 5) {
                                // 有没有孩子
                                showHaveChildDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.TA_WANT_CHILD, 5) == 5) {
                                    // 是否想要孩子
                                    showWantChildDialog()
                                } else {
                                    if (SPStaticUtils.getInt(Constant.TA_SMOKE, 5) == 5) {
                                        // 是否吸烟
                                        showSmokeDialog()
                                    } else {
                                        if (SPStaticUtils.getInt(Constant.TA_DRINK, 5) == 5) {
                                            // 是否喝酒
                                            showDrinkDialog()
                                        } else {
                                            if (SPStaticUtils.getInt(Constant.TA_HAVE_PHOTO,
                                                    3) == 3
                                            ) {
                                                // 有无照片
                                                showPhotoDialog()
                                            } else {
                                                if (SPStaticUtils.getInt(Constant.TA_MARRY,
                                                        5) == 5
                                                ) {
                                                    // 何时结婚
                                                    showMarryDialog()
                                                } else {
                                                    updateDateUI()
                                                    update()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            4 -> {
                if (SPStaticUtils.getInt(Constant.TA_BODY, 10) == 10) {
                    // 体型
                    showBodyDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.TA_WORK_PROVINCE_PICK, 34) == 34) {
                        // 工作地区
                        showJobDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.TA_HAVE_CHILD, 5) == 5) {
                            // 有没有孩子
                            showHaveChildDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.TA_WANT_CHILD, 5) == 5) {
                                // 是否想要孩子
                                showWantChildDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.TA_SMOKE, 5) == 5) {
                                    // 是否吸烟
                                    showSmokeDialog()
                                } else {
                                    if (SPStaticUtils.getInt(Constant.TA_DRINK, 5) == 5) {
                                        // 是否喝酒
                                        showDrinkDialog()
                                    } else {
                                        if (SPStaticUtils.getInt(Constant.TA_HAVE_PHOTO, 3) == 3) {
                                            // 有无照片
                                            showPhotoDialog()
                                        } else {
                                            if (SPStaticUtils.getInt(Constant.TA_MARRY, 5) == 5) {
                                                // 何时结婚
                                                showMarryDialog()
                                            } else {
                                                updateDateUI()
                                                update()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            5 -> {
                if (SPStaticUtils.getInt(Constant.TA_WORK_PROVINCE_PICK, 34) == 34) {
                    // 工作地区
                    showJobDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.TA_HAVE_CHILD, 5) == 5) {
                        // 有没有孩子
                        showHaveChildDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.TA_WANT_CHILD, 5) == 5) {
                            // 是否想要孩子
                            showWantChildDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.TA_SMOKE, 5) == 5) {
                                // 是否吸烟
                                showSmokeDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.TA_DRINK, 5) == 5) {
                                    // 是否喝酒
                                    showDrinkDialog()
                                } else {
                                    if (SPStaticUtils.getInt(Constant.TA_HAVE_PHOTO, 3) == 3) {
                                        // 有无照片
                                        showPhotoDialog()
                                    } else {
                                        if (SPStaticUtils.getInt(Constant.TA_MARRY, 5) == 5) {
                                            // 何时结婚
                                            showMarryDialog()
                                        } else {
                                            updateDateUI()
                                            update()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            6 -> {
                if (SPStaticUtils.getInt(Constant.TA_HAVE_CHILD, 5) == 5) {
                    // 有没有孩子
                    showHaveChildDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.TA_WANT_CHILD, 5) == 5) {
                        // 是否想要孩子
                        showWantChildDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.TA_SMOKE, 5) == 5) {
                            // 是否吸烟
                            showSmokeDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.TA_DRINK, 5) == 5) {
                                // 是否喝酒
                                showDrinkDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.TA_HAVE_PHOTO, 3) == 3) {
                                    // 有无照片
                                    showPhotoDialog()
                                } else {
                                    if (SPStaticUtils.getInt(Constant.TA_MARRY, 5) == 5) {
                                        // 何时结婚
                                        showMarryDialog()
                                    } else {
                                        updateDateUI()
                                        update()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            7 -> {
                if (SPStaticUtils.getInt(Constant.TA_WANT_CHILD, 5) == 5) {
                    // 是否想要孩子
                    showWantChildDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.TA_SMOKE, 5) == 5) {
                        // 是否吸烟
                        showSmokeDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.TA_DRINK, 5) == 5) {
                            // 是否喝酒
                            showDrinkDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.TA_HAVE_PHOTO, 3) == 3) {
                                // 有无照片
                                showPhotoDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.TA_MARRY, 5) == 5) {
                                    // 何时结婚
                                    showMarryDialog()
                                } else {
                                    updateDateUI()
                                    update()
                                }
                            }
                        }
                    }
                }
            }
            8 -> {
                if (SPStaticUtils.getInt(Constant.TA_SMOKE, 5) == 5) {
                    // 是否吸烟
                    showSmokeDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.TA_DRINK, 5) == 5) {
                        // 是否喝酒
                        showDrinkDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.TA_HAVE_PHOTO, 3) == 3) {
                            // 有无照片
                            showPhotoDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.TA_MARRY, 5) == 5) {
                                // 何时结婚
                                showMarryDialog()
                            } else {
                                updateDateUI()
                                update()
                            }
                        }
                    }
                }
            }
            9 -> {
                if (SPStaticUtils.getInt(Constant.TA_DRINK, 5) == 5) {
                    // 是否喝酒
                    showDrinkDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.TA_HAVE_PHOTO, 3) == 3) {
                        // 有无照片
                        showPhotoDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.TA_MARRY, 5) == 5) {
                            // 何时结婚
                            showMarryDialog()
                        } else {
                            updateDateUI()
                            update()
                        }
                    }
                }
            }
            10 -> {
                if (SPStaticUtils.getInt(Constant.TA_HAVE_PHOTO, 3) == 3) {
                    // 有无照片
                    showPhotoDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.TA_MARRY, 5) == 5) {
                        // 何时结婚
                        showMarryDialog()
                    } else {
                        updateDateUI()
                        update()
                    }
                }
            }
            11 -> {
                if (SPStaticUtils.getInt(Constant.TA_MARRY, 5) == 5) {
                    // 何时结婚
                    showMarryDialog()
                } else {
                    updateDateUI()
                    update()
                }
            }
        }
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

        when (SPStaticUtils.getInt(Constant.TA_INCOME_MIN, 7)) {
            7 -> income = "未填写"
            0 -> income = "不限"
            else -> income =
                "${
                    mIncomeList[SPStaticUtils.getInt(Constant.TA_INCOME_MIN,
                        7)]
                }~" + "${mIncomeList[SPStaticUtils.getInt(Constant.TA_INCOME_MAX, 7)]}"
        }


        val x = SPStaticUtils.getString(Constant.TA_EDU, "")
        val list = x.split(",")

        if (x == "0") {
            edu = "不限"
        } else {
            edu = ""
            if (list.contains("1")) {
                edu += "大专以下/"
            }
            if (list.contains("2")) {
                edu += "大专/"
            }
            if (list.contains("3")) {
                edu += "本科/"
            }
            if (list.contains("4")) {
                edu += "硕士/"
            }
            if (list.contains("5")) {
                edu += "博士/"
            }
            if (edu != "") {
                edu = edu.substring(0, edu.length - 1)
            }
        }

//        when (SPStaticUtils.getInt(Constant.TA_EDU, 0)) {
//            0 -> edu = "不限"
//            1 -> edu = "大专以下"
//            2 -> edu = "大专"
//            3 -> edu = "本科"
//            4 -> edu = "硕士"
//            5 -> edu = "博士"
//            6 -> edu = "博士以上"
//            7 -> edu = "未填写"
//        }


        val y = SPStaticUtils.getString(Constant.TA_MARRY_STATE, "")
        val listY = y.split(",")

        if (y == "0") {
            marryState = "不限"
        } else {
            marryState = ""
            if (listY.contains("1")) {
                marryState += "未婚/"
            }
            if (listY.contains("2")) {
                marryState += "离异/"
            }
            if (listY.contains("3")) {
                marryState += "丧偶/"
            }
            if (marryState != "") {
                marryState = marryState.substring(0, marryState.length - 1)
            }

        }

//        when (SPStaticUtils.getInt(Constant.TA_MARRY_STATE, 4)) {
//            0 -> marryState = "不限"
//            1 -> marryState = "未婚"
//            2 -> marryState = "离异"
//            3 -> marryState = "丧偶"
//            4 -> marryState = "未填写"
//        }

        body = when (SPStaticUtils.getInt(Constant.TA_BODY, 10)) {
            10 -> "未填写"
            else -> mBodyList[SPStaticUtils.getInt(Constant.TA_BODY, 10)]
        }

        workPlace = when (SPStaticUtils.getString(Constant.TA_WORK_PLACE, "")) {
            "" -> "未填写"
            else -> SPStaticUtils.getString(Constant.TA_WORK_PLACE, "")
                .substring(1, SPStaticUtils.getString(Constant.TA_WORK_PLACE, "").length)
        }

//        when (SPStaticUtils.getInt(Constant.TA_HAVE_CHILD, 5)) {
//            0 -> haveChild = "不限"
//            1 -> haveChild = "没有孩子"
//            2 -> haveChild = "有孩子且住在一起"
//            3 -> haveChild = "有孩子偶尔会住在一起"
//            4 -> haveChild = "有孩子但不在身边"
//            5 -> haveChild = "未填写"
//        }

        val z = SPStaticUtils.getString(Constant.TA_HAVE_CHILD, "")
        val listZ = z.split(",")

        if (z == "0") {
            haveChild = "不限"
        } else {
            haveChild = ""
            if (listZ.contains("1")) {
                haveChild += "没有孩子/"
            }
            if (listZ.contains("2")) {
                haveChild += "有孩子且住在一起/"
            }
            if (listZ.contains("3")) {
                haveChild += "有孩子偶尔会住在一起/"
            }
            if (listZ.contains("4")) {
                haveChild += "有孩子但不在身边/"
            }
            if (haveChild != "") {
                haveChild = haveChild.substring(0, haveChild.length - 1)
            }
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
        jobAddressInfoList.clear()

        for (i in 0.until(DataProvider.targetBaseData.size)) {
            baseInfoList.add("")
        }

        for (j in 0.until(DataProvider.targetMoreData.size)) {
            moreInfoList.add("")
        }

        if (SPStaticUtils.getString(Constant.TA_WORK_PLACE, "") != "") {

            val x: MutableList<String> = SPStaticUtils.getString(Constant.TA_WORK_PLACE, "").split(",") as MutableList<String>
            x.removeAt(0)

            for (i in 0.until(x.size)) {
                jobAddressInfoList.add(x[i])
            }
        }

    }

    private fun update() {
//        val demandInfoMap: MutableMap<String, String> = TreeMap()
//        demandInfoMap[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
//        demandInfoMap[Contents.DEMAND_UPDATE] = getDemandInfo()
//        updateDemandInfoPresent.doUpdateDemandInfo(demandInfoMap)

    }

    // 需要上传的择偶条件信息
    private fun getDemandInfo(): String {

        val sex = SPStaticUtils.getInt(Constant.ME_SEX, 0)
        val ageMin = SPStaticUtils.getInt(Constant.TA_AGE_MIN, 0)
        val ageMax = SPStaticUtils.getInt(Constant.TA_AGE_MAX, 0)
        val heightMin = SPStaticUtils.getInt(Constant.TA_HEIGHT_MIN, 0)
        val heightMax = SPStaticUtils.getInt(Constant.TA_HEIGHT_MAX, 0)
        val income = SPStaticUtils.getInt(Constant.TA_INCOME_MIN, 0)
        val incomeMax = SPStaticUtils.getInt(Constant.TA_INCOME_MAX, 0)
        val edu = SPStaticUtils.getString(Constant.TA_EDU, "")
        val marryState = SPStaticUtils.getInt(Constant.TA_MARRY_STATE, 0)
        val body = SPStaticUtils.getInt(Constant.TA_BODY, 0)
        val childHave = SPStaticUtils.getInt(Constant.TA_HAVE_CHILD, 0)
        val childWant = SPStaticUtils.getInt(Constant.TA_WANT_CHILD, 0)
        val smoke = SPStaticUtils.getInt(Constant.TA_SMOKE, 0)
        val drink = SPStaticUtils.getInt(Constant.TA_DRINK, 0)
        val havePhoto = SPStaticUtils.getInt(Constant.TA_HAVE_PHOTO, 0)
        val marryTime = SPStaticUtils.getInt(Constant.TA_MARRY, 0)
        val car = SPStaticUtils.getInt(Constant.TA_CAR, 0)
        val house = SPStaticUtils.getInt(Constant.TA_HOUSE, 0)
        val city = SPStaticUtils.getString(Constant.TA_WORK_CITY_NAME, "")
        val cityCode = SPStaticUtils.getString(Constant.TA_WORK_CITY_CODE, "")

        val demandInfo =
            " {\"user_sex\": $sex, " +
                    "\"age_min\":       $ageMin," +
                    "\"age_max\":       $ageMax," +
                    "\"min_high\":      $heightMin," +
                    "\"max_high\":      $heightMax," +
                    "\"figure_nan\":    $body," +
                    "\"figure_nv\":     $body," +
                    "\"salary_range\":  $income," +
                    "\"education\":     $edu," +
                    "\"marry_status\":  $marryState," +
                    "\"child_had\":     $childHave," +
                    "\"want_child\":    $childWant," +
                    "\"is_smoking\":    $smoke," +
                    "\"drink_wine\":    $drink," +
                    "\"is_headface\":   $havePhoto," +
                    "\"marry_time\":    $marryTime," +
                    "\"buy_car\":       $car," +
                    "\"buy_house\":     $house," +
                    "\"work_place_str\":\"$city\"," +
                    "\"work_place_code\": $cityCode}"

        return demandInfo

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
    private fun showIncomeDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(IncomeDialog(requireContext()))
            .show()
    }

    // 学历弹窗
    private fun showEduDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(EduNewDialog(requireContext()))
            .show()
    }

    // 婚况弹窗
    private fun showMarryStateDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(MarryStateNewDialog(requireContext()))
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
            .asCustom(HaveChildNewDialog(requireContext()))
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


            mMinAgePosition = SPStaticUtils.getInt(Constant.TA_AGE_MIN, 18) - 18
            mMaxAgePosition = SPStaticUtils.getInt(Constant.TA_AGE_MAX, 18) - 18

            wheelOne.setSelectedItemPosition(mMinAgePosition, false)
            wheelTwo.setSelectedItemPosition(mMaxAgePosition, false)

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

                if (mMinAgePosition > mMaxAgePosition) {
                    mMaxAgePosition = mMinAgePosition
                    wheelTwo.selectedItemPosition = mMaxAgePosition
                }

            }

            wheelTwo.setOnItemSelectedListener { picker, data, position ->

                mMaxAgePosition = position

                if (mMaxAgePosition < mMinAgePosition) {
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
                showNextDialog(0)
            } else {
                updateDateUI()
                update()
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

            mMinHeightPosition = SPStaticUtils.getInt(Constant.TA_HEIGHT_MIN, 140) - 140
            mMaxHeightPosition = SPStaticUtils.getInt(Constant.TA_HEIGHT_MAX, 140) - 140

            wheelOne.setSelectedItemPosition(mMinHeightPosition, false)
            wheelTwo.setSelectedItemPosition(mMaxHeightPosition, false)

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
                if (mMinHeightPosition > mMaxHeightPosition) {
                    mMaxHeightPosition = mMinHeightPosition
                    wheelTwo.selectedItemPosition = mMaxHeightPosition
                }
            }

            wheelTwo.setOnItemSelectedListener { picker, data, position ->

                mMaxHeightPosition = position

                if (mMaxHeightPosition < mMinHeightPosition) {
                    mMinHeightPosition = mMaxHeightPosition
                    wheelOne.selectedItemPosition = mMinHeightPosition
                }

            }

            confirm.setOnClickListener {

                Log.i("guo", "mMinHeightPosition : $mMinHeightPosition")
                Log.i("guo", "mMinHeightPosition : $mMinHeightPosition")

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
                showNextDialog(1)
            } else {
                updateDateUI()
                update()
            }
        }

    }

    // 月收入
    inner class IncomeDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var mIncomeMin = 0  // 最小月收入值
        private var mIncomeMax = 0  // 最大月收入值

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_income

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_target_income_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_income_skip)

            val wheelOne = findViewById<WheelPicker>(R.id.wp_user_target_income_container_one)
            val wheelTwo = findViewById<WheelPicker>(R.id.wp_user_target_income_container_two)
            val confirm = findViewById<TextView>(R.id.tv_user_target_income_confirm)

            wheelOne.data = mIncomeList
            wheelTwo.data = mIncomeList

            mIncomeMin = SPStaticUtils.getInt(Constant.TA_INCOME_MIN, 0)
            mIncomeMax = SPStaticUtils.getInt(Constant.TA_INCOME_MAX, 0)

            wheelOne.setSelectedItemPosition(mIncomeMin, false)
            wheelTwo.setSelectedItemPosition(mIncomeMax, false)

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
                mIncomeMin = position

                if (mIncomeMin > mIncomeMax) {
                    mIncomeMax = mIncomeMin
                    wheelTwo.selectedItemPosition = mIncomeMax
                }

            }

            wheelTwo.setOnItemSelectedListener { picker, data, position ->
                mIncomeMax = position

                if (mIncomeMax < mIncomeMin) {
                    mIncomeMin = mIncomeMax
                    wheelOne.selectedItemPosition = mIncomeMin
                }

            }

            confirm.setOnClickListener {
                ToastUtils.showShort(mIncomeMin)
                SPStaticUtils.put(Constant.TA_INCOME_MIN, mIncomeMin)
                SPStaticUtils.put(Constant.TA_INCOME_MAX, mIncomeMax)
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
                showNextDialog(2)
            } else {
                updateDateUI()
                update()
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
                showNextDialog(3)
            } else {
                updateDateUI()
                update()
            }

        }

    }

    // 学历（多选）
    inner class EduNewDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_unlimited: TextView
        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView
        private lateinit var tv_five: TextView
        private lateinit var confirm: TextView

        private var chooseLimit = false
        private var chooseOne = false
        private var chooseTwo = false
        private var chooseThree = false
        private var chooseFour = false
        private var chooseFive = false

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
            confirm = findViewById<TextView>(R.id.tv_user_target_edu_confirm)

            clearChoose()
            chooseLimit = false
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_unlimited.setOnClickListener {
                if (chooseLimit) {
                    tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                    tv_unlimited.setTextColor(Color.parseColor("#101010"))

                } else {
                    clearChoose()
                    tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_unlimited.setTextColor(Color.parseColor("#FF4444"))

                }
                chooseLimit = !chooseLimit
                injustLimit()
                isNeedJump = true
            }

            tv_one.setOnClickListener {
                if (chooseOne) {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                    tv_one.setTextColor(Color.parseColor("#101010"))
                } else {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))

                }
                chooseOne = !chooseOne
                chooseLimit = false
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                tv_unlimited.setTextColor(Color.parseColor("#101010"))
                injustLimit()
                isNeedJump = true
            }

            tv_two.setOnClickListener {

                if (chooseTwo) {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                    tv_two.setTextColor(Color.parseColor("#101010"))
                } else {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                chooseTwo = !chooseTwo
                chooseLimit = false
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                tv_unlimited.setTextColor(Color.parseColor("#101010"))
                injustLimit()
                isNeedJump = true
            }

            tv_three.setOnClickListener {

                if (chooseThree) {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                    tv_three.setTextColor(Color.parseColor("#101010"))
                } else {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
                chooseThree = !chooseThree
                chooseLimit = false
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                tv_unlimited.setTextColor(Color.parseColor("#101010"))
                injustLimit()
                isNeedJump = true
            }

            tv_four.setOnClickListener {
                if (chooseFour) {
                    tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                    tv_four.setTextColor(Color.parseColor("#101010"))
                } else {
                    tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_four.setTextColor(Color.parseColor("#FF4444"))
                }
                chooseFour = !chooseFour
                chooseLimit = false
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                tv_unlimited.setTextColor(Color.parseColor("#101010"))
                injustLimit()
                isNeedJump = true
            }

            tv_five.setOnClickListener {

                if (chooseFive) {
                    tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                    tv_five.setTextColor(Color.parseColor("#101010"))
                } else {
                    tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_five.setTextColor(Color.parseColor("#FF4444"))
                }
                chooseFive = !chooseFive
                chooseLimit = false
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                tv_unlimited.setTextColor(Color.parseColor("#101010"))
                injustLimit()
                isNeedJump = true
            }

            confirm.setOnClickListener {

                var text = ""

                Log.i("guo", " limit : $chooseLimit")
                Log.i("guo", " one : $chooseOne")
                Log.i("guo", " two : $chooseTwo")
                Log.i("guo", " three : $chooseThree")
                Log.i("guo", " four : $chooseFour")
                Log.i("guo", " five : $chooseFive")
                if (chooseLimit) {
                    text = "0"
                } else {
                    val x = arrayListOf<String>()
                    if (chooseOne) x.add("1")
                    if (chooseTwo) x.add("2")
                    if (chooseThree) x.add("3")
                    if (chooseFour) x.add("4")
                    if (chooseFive) x.add("5")

                    when (x.size) {
                        1 -> text = "${x[0]}"
                        2 -> text = "${x[0]},${x[1]}"
                        3 -> text = "${x[0]},${x[1]},${x[2]}"
                        4 -> text = "${x[0]},${x[1]},${x[2]},${x[3]}"
                    }

                }

                Log.i("guo", text)

                SPStaticUtils.put(Constant.TA_EDU, text)

                isNeedJump = true
                dismiss()

            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun clearChoose() {
            tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_unlimited.setTextColor(Color.parseColor("#101010"))

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))
            chooseOne = false

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))
            chooseTwo = false

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))
            chooseThree = false

            tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_four.setTextColor(Color.parseColor("#101010"))
            chooseFour = false

            tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_five.setTextColor(Color.parseColor("#101010"))
            chooseFive = false

        }

        private fun initChoose() {
            val x = SPStaticUtils.getString(Constant.TA_EDU, "")
            val list = x.split(",")

            if (x == "0") {
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                chooseLimit = true
            }
            if (list.contains("1")) {
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                chooseOne = true
            }
            if (list.contains("2")) {
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                chooseTwo = true
            }
            if (list.contains("3")) {
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                chooseThree = true
            }
            if (list.contains("4")) {
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                chooseFour = true
            }
            if (list.contains("5")) {
                tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_five.setTextColor(Color.parseColor("#FF4444"))
                chooseFive = true
            }

        }

        private fun injustLimit() {
            if (chooseOne && chooseTwo && chooseThree && chooseFour && chooseFive) {
                clearChoose()
                chooseLimit = true
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
            }
        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(3)
            } else {
                updateDateUI()
                update()
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
            when (SPStaticUtils.getInt(Constant.TA_MARRY_STATE, 4)) {
                4 -> {
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
                showNextDialog(4)
            } else {
                updateDateUI()
                update()
            }

        }

    }

    // 婚况(多选)
    inner class MarryStateNewDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_unlimited: TextView
        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView

        private var chooseLimit = false
        private var chooseOne = false
        private var chooseTwo = false
        private var chooseThree = false

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_marrystate

        override fun onCreate() {
            super.onCreate()
            val close = findViewById<ImageView>(R.id.iv_user_target_marrystate_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_marrystate_skip)

            val confirm = findViewById<TextView>(R.id.tv_user_target_marrystate_confirm)

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
                if (chooseLimit) {
                    tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                    tv_unlimited.setTextColor(Color.parseColor("#101010"))

                } else {
                    clearChoose()
                    tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_unlimited.setTextColor(Color.parseColor("#FF4444"))

                }
                chooseLimit = !chooseLimit
                injustLimit()
                isNeedJump = true
            }

            tv_one.setOnClickListener {
                if (chooseOne) {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                    tv_one.setTextColor(Color.parseColor("#101010"))
                } else {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))

                }
                chooseOne = !chooseOne
                chooseLimit = false
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                tv_unlimited.setTextColor(Color.parseColor("#101010"))
                injustLimit()
                isNeedJump = true

            }

            tv_two.setOnClickListener {

                if (chooseTwo) {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                    tv_two.setTextColor(Color.parseColor("#101010"))
                } else {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                chooseTwo = !chooseTwo
                chooseLimit = false
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                tv_unlimited.setTextColor(Color.parseColor("#101010"))
                injustLimit()
                isNeedJump = true

            }

            tv_three.setOnClickListener {

                if (chooseThree) {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                    tv_three.setTextColor(Color.parseColor("#101010"))
                } else {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
                chooseThree = !chooseThree
                chooseLimit = false
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                tv_unlimited.setTextColor(Color.parseColor("#101010"))
                injustLimit()
                isNeedJump = true

            }

            confirm.setOnClickListener {
                var text = ""

                Log.i("guo", " limit : $chooseLimit")
                Log.i("guo", " one : $chooseOne")
                Log.i("guo", " two : $chooseTwo")
                Log.i("guo", " three : $chooseThree")

                if (chooseLimit) {
                    text = "0"
                } else {
                    val x = arrayListOf<String>()
                    if (chooseOne) x.add("1")
                    if (chooseTwo) x.add("2")
                    if (chooseThree) x.add("3")

                    when (x.size) {
                        1 -> text = "${x[0]}"
                        2 -> text = "${x[0]},${x[1]}"
                    }

                }

                Log.i("guo", text)

                SPStaticUtils.put(Constant.TA_MARRY_STATE, text)

                isNeedJump = true
                dismiss()

            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun injustLimit() {
            if (chooseOne && chooseTwo && chooseThree) {
                clearChoose()
                chooseLimit = true
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
            }
        }

        private fun initChoose() {

            val x = SPStaticUtils.getString(Constant.TA_MARRY_STATE, "")
            val list = x.split(",")

            if (x == "0") {
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                chooseLimit = true
            }
            if (list.contains("1")) {
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                chooseOne = true
            }
            if (list.contains("2")) {
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                chooseTwo = true
            }
            if (list.contains("3")) {
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                chooseThree = true
            }

        }

        private fun clearChoose() {
            tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_unlimited.setTextColor(Color.parseColor("#101010"))

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))
            chooseOne = false

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))
            chooseTwo = false

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))
            chooseThree = false

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(4)
            } else {
                updateDateUI()
                update()
            }

        }

    }

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

            mBody = SPStaticUtils.getInt(Constant.TA_BODY, 0)

            wheel.setSelectedItemPosition(mBody, false)

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
                showNextDialog(5)
            } else {
                updateDateUI()
                update()
            }
        }

    }

    // 工作区域
    inner class JobDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var isConfirm = true  // 确定键此时的模式

        private var mCityFirstPosition = 0
        private var mCitySecondPosition = 0

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_jobaddress

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_target_jobaddress_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_jobaddress_skip)

            val wheel = findViewById<LinearLayout>(R.id.ll_user_target_jobaddress_wheel)
            val wheelOne = findViewById<WheelPicker>(R.id.wp_user_target_jobaddress_one)
            val wheelTwo = findViewById<WheelPicker>(R.id.wp_user_target_jobaddress_two)
            val confirm = findViewById<TextView>(R.id.tv_user_target_jobaddress_confirm)

            val info = findViewById<RelativeLayout>(R.id.rl_user_target_jobaddress_content)
            val content = findViewById<RecyclerView>(R.id.rv_user_target_jobaddress_content)
            val add = findViewById<TextView>(R.id.tv_user_target_jobaddress_add)

            val sum = findViewById<TextView>(R.id.tv_user_target_jobaddress_sum)


            wheelOne.data = mCityFirstList
            wheelTwo.data = mCitySecondList

            mCityFirstPosition = SPStaticUtils.getInt(Constant.TA_WORK_PROVINCE_PICK, 0)
            mCitySecondPosition = SPStaticUtils.getInt(Constant.TA_WORK_CITY_PICK, 0)

            wheelOne.setSelectedItemPosition(mCityFirstPosition, false)
            getJobCitySecondList(mCityFirstPosition)
            wheelTwo.setSelectedItemPosition(mCitySecondPosition, false)

            if (jobAddressInfoList.size == 0) {
                if (SPStaticUtils.getString(Constant.ME_WORK_CITY_NAME, "") != "") {
                    jobAddressInfoList.add(SPStaticUtils.getString(Constant.ME_WORK_CITY_NAME, ""))
                } else {
                    if (SPStaticUtils.getString(Constant.ME_HOME_CITY_NAME, "") != "") {
                        jobAddressInfoList.add(SPStaticUtils.getString(Constant.ME_HOME_CITY_NAME,
                            ""))
                    } else {
                        wheel.visibility = View.VISIBLE
                        info.visibility = View.GONE
                        isConfirm = false
                    }
                }
            }

            if (jobAddressInfoList.size == 5) {
                add.visibility = View.GONE
            }

            content.adapter = jobAddressAdapter
            val layoutManager = LinearLayoutManager(context)
            content.layoutManager = layoutManager


            if (SPStaticUtils.getString(Constant.TA_WORK_PLACE, "") != "") {

                val x: MutableList<String> = SPStaticUtils.getString(Constant.TA_WORK_PLACE, "")
                    .split(",") as MutableList<String>
                x.removeAt(0)

                jobAddressInfoList.clear()

                for (i in 0.until(x.size)) {
                    jobAddressInfoList.add(x[i])
                }
            }

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

            jobAddressAdapter.notifyDataSetChanged()
            sum.text = jobAddressInfoList.size.toString()

//            jobAddressAdapter.setOnItemClickListener(object :
//                JobAddressAdapter.OnItemClickListener {
//                override fun onItemClick(v: View?, position: Int) {
//                    ToastUtils.showShort("第${position}个数据需要进行修改")
//                }
//            })

            jobAddressAdapter.setOnItemCloseClickListener(object :
                JobAddressAdapter.OnItemCloseClickListener {
                override fun onItemCloseClick(v: View?, position: Int) {
                    ToastUtils.showShort("第${position}个数据需要删除")
                    jobAddressInfoList.removeAt(position)
                    jobAddressAdapter.notifyDataSetChanged()
                    sum.text = jobAddressInfoList.size.toString()

                    if (jobAddressInfoList.size < 5) {
                        add.visibility = View.VISIBLE
                    }

                    if (jobAddressInfoList.isEmpty()) {
                        wheel.visibility = View.VISIBLE
                        info.visibility = View.GONE
                        isConfirm = false
                    }
                }
            })


            add.setOnClickListener {

                if (jobAddressInfoList.size < 5) {
                    wheel.visibility = View.VISIBLE
                    info.visibility = View.GONE

                    isConfirm = false
                } else {
                    ToastUtils.showShort("最多只可选择5个期望工作地")
                }
            }

            confirm.setOnClickListener {

                // 两种情况，
                // 一种是“确定” ： 将数组中的数据存储至sp中，显示下一个弹窗
                // 一种是“添加” ： 将滚轮中的数据储存至数组中

                if (isConfirm) {
                    // “确定” ： 将数组中的数据存储至sp中，显示下一个弹窗

                    var workPlace = ""

                    for (i in 0.until(jobAddressInfoList.size)) {
                        workPlace += ",${jobAddressInfoList[i]}"
                    }

                    Log.i("guo", "liset : $workPlace")

                    SPStaticUtils.put(Constant.TA_WORK_PLACE, workPlace)

                    isNeedJump = true
                    dismiss()

                } else {
                    // “添加” ： 将滚轮中的数据储存至数组中

                    jobAddressInfoList.add(mCitySecondList[mCitySecondPosition])

                    Log.i("guo", mCitySecondList[mCitySecondPosition])

                    jobAddressAdapter.notifyDataSetChanged()
                    sum.text = jobAddressInfoList.size.toString()

                    wheel.visibility = View.GONE
                    info.visibility = View.VISIBLE

                    isConfirm = true

                    if (jobAddressInfoList.size == 5) {
                        add.visibility = View.GONE
                    }

                }

//                val workPlace = "${mCityFirstList[mCityFirstPosition]}-${mCitySecondList[mCitySecondPosition]}"
//
//                ToastUtils.showShort(workPlace)
//
//                SPStaticUtils.put(Constant.TA_WORK_PLACE, workPlace)
//
//                SPStaticUtils.put(Constant.TA_WORK_PROVINCE_NAME, mCityFirstList[mCityFirstPosition])
//                SPStaticUtils.put(Constant.TA_WORK_PROVINCE_CODE, mCityIdFirstList[mCityFirstPosition])
//                SPStaticUtils.put(Constant.TA_WORK_PROVINCE_PICK, mCityFirstPosition)
//                SPStaticUtils.put(Constant.TA_WORK_CITY_NAME, mCitySecondList[mCitySecondPosition])
//                SPStaticUtils.put(Constant.TA_WORK_CITY_CODE, mCityIdSecondList[mCitySecondPosition])
//                SPStaticUtils.put(Constant.TA_WORK_CITY_PICK, mCitySecondPosition)

//                isNeedJump = true
//                dismiss()
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
                showNextDialog(6)
            } else {
                updateDateUI()
                update()
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
                showNextDialog(7)
            } else {
                updateDateUI()
                update()
            }
        }

    }

    // 有不有孩子(多选)
    inner class HaveChildNewDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_unlimited: TextView
        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView


        private var chooseLimit = false
        private var chooseOne = false
        private var chooseTwo = false
        private var chooseThree = false
        private var chooseFour = false

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_havechild

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_target_havechild_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_havechild_skip)

            val confirm = findViewById<TextView>(R.id.tv_user_target_havechild_confirm)

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
                if (chooseLimit) {
                    tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                    tv_unlimited.setTextColor(Color.parseColor("#101010"))

                } else {
                    clearChoose()
                    tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                }
                chooseLimit = !chooseLimit
                injustLimit()
                isNeedJump = true
            }

            tv_one.setOnClickListener {
                if (chooseOne) {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                    tv_one.setTextColor(Color.parseColor("#101010"))
                } else {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                chooseOne = !chooseOne
                chooseLimit = false
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                tv_unlimited.setTextColor(Color.parseColor("#101010"))
                injustLimit()
                isNeedJump = true
            }

            tv_two.setOnClickListener {
                if (chooseTwo) {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                    tv_two.setTextColor(Color.parseColor("#101010"))
                } else {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                chooseTwo = !chooseTwo
                chooseLimit = false
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                tv_unlimited.setTextColor(Color.parseColor("#101010"))
                injustLimit()
                isNeedJump = true
            }

            tv_three.setOnClickListener {
                if (chooseThree) {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                    tv_three.setTextColor(Color.parseColor("#101010"))
                } else {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
                chooseThree = !chooseThree
                chooseLimit = false
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                tv_unlimited.setTextColor(Color.parseColor("#101010"))
                injustLimit()
                isNeedJump = true

            }

            tv_four.setOnClickListener {
                if (chooseFour) {
                    tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                    tv_four.setTextColor(Color.parseColor("#101010"))
                } else {
                    tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_four.setTextColor(Color.parseColor("#FF4444"))
                }
                chooseFour = !chooseFour
                chooseLimit = false
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
                tv_unlimited.setTextColor(Color.parseColor("#101010"))
                injustLimit()
                isNeedJump = true
            }

            confirm.setOnClickListener {
                var text = ""

                Log.i("guo", " limit : $chooseLimit")
                Log.i("guo", " one : $chooseOne")
                Log.i("guo", " two : $chooseTwo")
                Log.i("guo", " three : $chooseThree")
                Log.i("guo", " four : $chooseFour")

                if (chooseLimit) {
                    text = "0"
                } else {
                    val x = arrayListOf<String>()
                    if (chooseOne) x.add("1")
                    if (chooseTwo) x.add("2")
                    if (chooseThree) x.add("3")
                    if (chooseFour) x.add("3")

                    when (x.size) {
                        1 -> text = "${x[0]}"
                        2 -> text = "${x[0]},${x[1]}"
                        3 -> text = "${x[0]},${x[1]},${x[2]}"
                    }
                }

                SPStaticUtils.put(Constant.TA_HAVE_CHILD, text)

                isNeedJump = true
                dismiss()

            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun injustLimit() {
            if (chooseOne && chooseTwo && chooseThree && chooseFour) {
                clearChoose()
                chooseLimit = true
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
            }
        }

        private fun initChoose() {

            val x = SPStaticUtils.getString(Constant.TA_HAVE_CHILD, "")
            val list = x.split(",")

            if (x == "0") {
                tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_unlimited.setTextColor(Color.parseColor("#FF4444"))
                chooseLimit = true
            }
            if (list.contains("1")) {
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                chooseOne = true
            }
            if (list.contains("2")) {
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                chooseTwo = true
            }
            if (list.contains("3")) {
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                chooseThree = true
            }
            if (list.contains("4")) {
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                chooseFour = true
            }

        }

        private fun clearChoose() {

            tv_unlimited.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_unlimited.setTextColor(Color.parseColor("#101010"))

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))
            chooseOne = false

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))
            chooseTwo = false

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))
            chooseThree = false

            tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_four.setTextColor(Color.parseColor("#101010"))
            chooseFour = false

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(7)
            } else {
                updateDateUI()
                update()
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
                showNextDialog(8)
            } else {
                updateDateUI()
                update()
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
                showNextDialog(9)
            } else {
                updateDateUI()
                update()
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
                showNextDialog(10)
            } else {
                updateDateUI()
                update()
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
                3 -> {
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
                showNextDialog(11)
            } else {
                updateDateUI()
                update()
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
            when (SPStaticUtils.getInt(Constant.TA_MARRY, 5)) {
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
            update()
        }

    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUpdateDemandInfoSuccess(updateDemandInfoBean: UpdateDemandInfoBean?) {

    }

    override fun onDoUpdateDemandInfoError() {

    }

}