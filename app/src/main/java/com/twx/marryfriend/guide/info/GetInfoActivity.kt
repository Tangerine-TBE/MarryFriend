package com.twx.marryfriend.guide.info

import android.content.Context
import android.content.Intent
import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.*
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.guide.baseInfo.BaseInfoActivity
import com.twx.marryfriend.guide.detailInfo.DetailInfoActivity
import com.twx.marryfriend.main.MainActivity
import com.twx.marryfriend.mutual.MutualLikeDialogActivity
import com.twx.marryfriend.net.callback.*
import com.twx.marryfriend.net.impl.*
import java.util.*


class GetInfoActivity : MainBaseViewActivity(), IGetCityCallback, IGetIndustryCallback,
    IGetJobCallback, IGetAccessTokenCallback, IGetIdAccessTokenCallback,
    IGetLifeAccessTokenCallback, IGetFiveInfoCallback {

    /**
     * 此处进行数据处理，获取所有所需的数据，包括用户信息数据刷新
     **/

    // 跳转模式
    private var jumpMode = 0

    // 是否需要刷新
    private var needRefresh = false

    // 是否加载城市
    private var isLoadCity = false

    // 是否加载行业
    private var isLoadIndustry = false

    // 是否加载岗位
    private var isLoadJob = false

    // 是否加载用户信息
    private var isLoadUser = false

    // 城市获取
    private lateinit var getCityPresent: getCityPresentImpl
    private lateinit var getIndustryPresent: getIndustryPresentImpl
    private lateinit var getJobPresent: getJobPresentImpl

    private lateinit var getAccessTokenPresent: getAccessTokenPresentImpl
    private lateinit var getIdAccessTokenPresent: getIdAccessTokenPresentImpl
    private lateinit var getLifeAccessTokenPresent: getLifeAccessTokenPresentImpl

    private lateinit var getFiveInfoPresent: getFiveInfoPresentImpl


    /**
     * mode : 跳转模式
     * 0 : 注册流程，去注册界面
     * 1 : 登录流程，直接去首页
     * needRefresh : 是否需要刷新用户数据
     * false : 不做处理
     * true : 进行刷新
     * */
    companion object {
        private const val MODE = "mode"
        private const val REFRESH = "refresh"
        fun getIntent(context: Context, mode: Int, needRefresh: Boolean? = false): Intent {
            val intent = Intent(context, GetInfoActivity::class.java)
            intent.putExtra(MODE, mode)
            intent.putExtra(REFRESH, needRefresh)
            return intent
        }
    }

    override fun getLayoutView(): Int = R.layout.activity_get_info

    override fun initView() {
        super.initView()

        getCityPresent = getCityPresentImpl.getsInstance()
        getCityPresent.registerCallback(this)

        getIndustryPresent = getIndustryPresentImpl.getsInstance()
        getIndustryPresent.registerCallback(this)

        getJobPresent = getJobPresentImpl.getsInstance()
        getJobPresent.registerCallback(this)

        getAccessTokenPresent = getAccessTokenPresentImpl.getsInstance()
        getAccessTokenPresent.registerCallback(this)

        getIdAccessTokenPresent = getIdAccessTokenPresentImpl.getsInstance()
        getIdAccessTokenPresent.registerCallback(this)

        getLifeAccessTokenPresent = getLifeAccessTokenPresentImpl.getsInstance()
        getLifeAccessTokenPresent.registerCallback(this)

        getFiveInfoPresent = getFiveInfoPresentImpl.getsInstance()
        getFiveInfoPresent.registerCallback(this)

        jumpMode = intent.getIntExtra("mode", 0)
        needRefresh = intent.getBooleanExtra("refresh", false)

    }

    override fun initLoadData() {
        super.initLoadData()

        // 获取百度
        getAccessToken()
        getIdAccessToken()
        getLifeAccessToken()
        // 进行数据刷新
        if (needRefresh) {
            getFiveInfo()
            isLoadUser = false
        } else {
            isLoadUser = true
        }

    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        isCompleteLoading()

        if (!SPStaticUtils.getBoolean(Constant.JOB_HAVE, false)) {
            val jobMap: MutableMap<String, String> = TreeMap()
            getJobPresent.getJob(jobMap)
        }

        if (!SPStaticUtils.getBoolean(Constant.CITY_HAVE, false)) {
            val cityMap: MutableMap<String, String> = TreeMap()
            getCityPresent.getCity(cityMap)
        }

        if (!SPStaticUtils.getBoolean(Constant.INDUSTRY_HAVE, false)) {
            val industryMap: MutableMap<String, String> = TreeMap()
            getIndustryPresent.getIndustry(industryMap)
        }
    }

    // 获取 头像 审核的 accessToken
    private fun getAccessToken() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.GRANT_TYPE] = "client_credentials"
        map[Contents.CLIENT_ID] = "jjKDyljlCOX3TcEcnXidYCcU"
        map[Contents.CLIENT_SECRET] = "GQcKzFuA87uEQZhIDnlcxTpkjT2oLxdX"
        getAccessTokenPresent.getAccessToken(map)

    }

    // 获取身份证号与姓名匹配的    accessToken
    private fun getIdAccessToken() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.GRANT_TYPE] = "client_credentials"
        map[Contents.CLIENT_ID] = "YLnCFKMcNiSBGxETIg4DeMht"
        map[Contents.CLIENT_SECRET] = "ufkmEoIT4Oto1CP3oEFeKOSlU5OXtL8v"
        getIdAccessTokenPresent.getAccessToken(map)

    }

    // 获取 生活照 审核的 accessToken
    private fun getLifeAccessToken() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.GRANT_TYPE] = "client_credentials"
        map[Contents.CLIENT_ID] = "DfWMKUNlOSZzGIa6XzHKvTlZ"
        map[Contents.CLIENT_SECRET] = "F23G5Q3b0mWw2q5pyy6dyq4HEXSpdizu"
        getLifeAccessTokenPresent.getAccessToken(map)
    }

    // 获取五个（所有信息）
    private fun getFiveInfo() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
        getFiveInfoPresent.getFiveInfo(map)
    }

    private fun isCompleteLoading() {

        ThreadUtils.runOnUiThread {

            when (jumpMode) {
                0 -> {
                    // 走注册流程
                    if (SPStaticUtils.getBoolean(Constant.JOB_HAVE,
                            false) && SPStaticUtils.getBoolean(Constant.CITY_HAVE,
                            false) && SPStaticUtils.getBoolean(Constant.INDUSTRY_HAVE, false)
                    ) {
                        if (!SPStaticUtils.getBoolean(Constant.BASE_INFO_FINISH, false)) {
                            val intent = Intent(this, BaseInfoActivity::class.java)
                            startActivity(intent)
                            this.finish()
                        } else {
                            if (!SPStaticUtils.getBoolean(Constant.DETAIL_INFO_FINISH, false)) {
                                val intent = Intent(this, DetailInfoActivity::class.java)
                                startActivity(intent)
                                this.finish()
                            } else {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                this.finish()
                            }
                        }
                    }
                }
                1 -> {


                    SPStaticUtils.put(Constant.BASE_INFO_FINISH, true)
                    SPStaticUtils.put(Constant.DETAIL_INFO_FINISH, true)

                    // 直接去首页
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    this.finish()
                }
                else -> {
                    // 走注册流程
                    if (SPStaticUtils.getBoolean(Constant.JOB_HAVE,
                            false) && SPStaticUtils.getBoolean(Constant.CITY_HAVE,
                            false) && SPStaticUtils.getBoolean(Constant.INDUSTRY_HAVE, false)
                    ) {
                        if (!SPStaticUtils.getBoolean(Constant.BASE_INFO_FINISH, false)) {
                            val intent = Intent(this, BaseInfoActivity::class.java)
                            startActivity(intent)
                            this.finish()
                        } else {
                            if (!SPStaticUtils.getBoolean(Constant.DETAIL_INFO_FINISH, false)) {
                                val intent = Intent(this, DetailInfoActivity::class.java)
                                startActivity(intent)
                                this.finish()
                            } else {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                this.finish()
                            }
                        }
                    }
                }
            }

        }

    }


    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetFiveInfoSuccess(fiveInfoBean: FiveInfoBean?) {
        if (fiveInfoBean != null) {
            if (fiveInfoBean.code == 200) {


                // 自我介绍、语音介绍、心目中的ta

                SPStaticUtils.put(Constant.ME_INTRODUCE, fiveInfoBean.data.base.introduce_self)

                SPStaticUtils.put(Constant.ME_TA, fiveInfoBean.data.base.ta_in_my_mind)

                SPStaticUtils.put(Constant.ME_VOICE_LONG, fiveInfoBean.data.zhaohu.voice_long)

                SPStaticUtils.put(Constant.ME_VOICE, fiveInfoBean.data.zhaohu.voice_url)

                SPStaticUtils.put(Constant.ME_VOICE_NAME, "Greet")



                SPStaticUtils.put(Constant.ME_NAME, fiveInfoBean.data.base.nick)

                SPStaticUtils.put(Constant.ME_SEX, fiveInfoBean.data.base.user_sex)

                SPStaticUtils.put(Constant.ME_BIRTH, fiveInfoBean.data.base.birthday)


                SPStaticUtils.put(Constant.ME_HEIGHT, fiveInfoBean.data.base.height)

                SPStaticUtils.put(Constant.ME_INDUSTRY_NAME, fiveInfoBean.data.base.industry_str)
                SPStaticUtils.put(Constant.ME_OCCUPATION_NAME,
                    fiveInfoBean.data.base.occupation_str)

                SPStaticUtils.put(Constant.ME_INCOME, fiveInfoBean.data.base.salary_range)

                if (fiveInfoBean.data.base.work_province_str != "") {

                    if (fiveInfoBean.data.base.work_city_str != "") {
                        SPStaticUtils.put(Constant.ME_WORK,
                            "${fiveInfoBean.data.base.work_province_str}-${fiveInfoBean.data.base.work_city_str}")
                    } else {
                        SPStaticUtils.put(Constant.ME_WORK,
                            "${fiveInfoBean.data.base.work_province_str}")
                    }

                } else {

                    if (fiveInfoBean.data.base.work_city_str != "") {
                        SPStaticUtils.put(Constant.ME_WORK,
                            "${fiveInfoBean.data.base.work_city_str}")
                    } else {
                        SPStaticUtils.put(Constant.ME_WORK, "")
                    }

                }

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


                // 择偶条件

                SPStaticUtils.put(Constant.TA_AGE_MIN, fiveInfoBean.data.demand.age_min)
                SPStaticUtils.put(Constant.TA_AGE_MAX, fiveInfoBean.data.demand.age_max)

                SPStaticUtils.put(Constant.TA_HEIGHT_MIN, fiveInfoBean.data.demand.min_high)
                SPStaticUtils.put(Constant.TA_HEIGHT_MAX, fiveInfoBean.data.demand.max_high)


                val salary = fiveInfoBean.data.demand.salary_range

                val x = salary.replace("[", "")
                val y = x.replace("]", "")

                val salaryList = y.split(",")

                when (salaryList.size) {
                    0 -> {
                        SPStaticUtils.put(Constant.TA_INCOME_MAX, 9)
                    }
                    1 -> {
                        SPStaticUtils.put(Constant.TA_INCOME_MIN, salaryList[0].toInt())
                        SPStaticUtils.put(Constant.TA_INCOME_MAX, salaryList[0].toInt())
                    }
                    else -> {
                        SPStaticUtils.put(Constant.TA_INCOME_MIN, salaryList[0].toInt())
                        SPStaticUtils.put(Constant.TA_INCOME_MAX,
                            salaryList[salaryList.size - 1].toInt())

                    }
                }


                val edu1 = fiveInfoBean.data.demand.education.replace("[", "")
                val edu2 = edu1.replace("]", "")

                SPStaticUtils.put(Constant.TA_EDU, edu2)

                val marry1 = fiveInfoBean.data.demand.marry_status.replace("[", "")
                val marry2 = marry1.replace("]", "")

                SPStaticUtils.put(Constant.TA_MARRY_STATE, marry2)

                if (fiveInfoBean.data.demand.figure_nan == 0) {
                    SPStaticUtils.put(Constant.TA_BODY, fiveInfoBean.data.demand.figure_nv)
                } else {
                    SPStaticUtils.put(Constant.TA_BODY, fiveInfoBean.data.demand.figure_nan)
                }


                SPStaticUtils.put(Constant.TA_HAVE_CHILD, fiveInfoBean.data.demand.child_had)

                SPStaticUtils.put(Constant.TA_WANT_CHILD, fiveInfoBean.data.demand.want_child)

                SPStaticUtils.put(Constant.TA_SMOKE, fiveInfoBean.data.demand.is_smoking)

                SPStaticUtils.put(Constant.TA_DRINK, fiveInfoBean.data.demand.drink_wine)

                SPStaticUtils.put(Constant.TA_HAVE_PHOTO, fiveInfoBean.data.demand.is_headface)

                SPStaticUtils.put(Constant.TA_MARRY, fiveInfoBean.data.demand.marry_time)

                when (fiveInfoBean.data.headface.size) {
                    0 -> {
                        SPStaticUtils.put(Constant.ME_AVATAR, "")
                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                    }
                    1 -> {
                        when (fiveInfoBean.data.headface[0].status) {
                            0 -> {
                                SPStaticUtils.put(Constant.ME_AVATAR, "")
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                    fiveInfoBean.data.headface[0].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                            }
                            1 -> {
                                SPStaticUtils.put(Constant.ME_AVATAR,
                                    fiveInfoBean.data.headface[0].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                            }
                            2 -> {
                                SPStaticUtils.put(Constant.ME_AVATAR, "")
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                    fiveInfoBean.data.headface[0].image_url)
                            }
                        }
                    }
                    2 -> {
                        when (fiveInfoBean.data.headface[0].status) {
                            0 -> {
                                // 第一张为审核中
                                when (fiveInfoBean.data.headface[1].status) {
                                    0 -> {
                                        // 第二张为审核中

                                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                            fiveInfoBean.data.headface[0].image_url)
                                        SPStaticUtils.put(Constant.ME_AVATAR, "")
                                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                                    }
                                    1 -> {
                                        // 第二张为审核通过

                                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                            fiveInfoBean.data.headface[0].image_url)
                                        SPStaticUtils.put(Constant.ME_AVATAR,
                                            fiveInfoBean.data.headface[1].image_url)
                                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")

                                    }
                                    2 -> {
                                        // 第二张为审核拒绝

                                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                            fiveInfoBean.data.headface[0].image_url)
                                        SPStaticUtils.put(Constant.ME_AVATAR, "")
                                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                            fiveInfoBean.data.headface[1].image_url)

                                    }
                                }

                            }

                            1 -> {
                                // 第一张为审核通过

                                when (fiveInfoBean.data.headface[1].status) {
                                    0 -> {
                                        // 第二张为审核中

                                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                            fiveInfoBean.data.headface[1].image_url)
                                        SPStaticUtils.put(Constant.ME_AVATAR,
                                            fiveInfoBean.data.headface[0].image_url)
                                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                                    }
                                    1 -> {
                                        // 第二张为审核通过

                                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                                        SPStaticUtils.put(Constant.ME_AVATAR,
                                            fiveInfoBean.data.headface[0].image_url)
                                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                                    }
                                    2 -> {
                                        // 第二张为审核拒绝

                                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                                        SPStaticUtils.put(Constant.ME_AVATAR,
                                            fiveInfoBean.data.headface[0].image_url)
                                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                            fiveInfoBean.data.headface[1].image_url)
                                    }
                                }

                            }

                            2 -> {
                                // 第一张为审核拒绝
                                when (fiveInfoBean.data.headface[1].status) {
                                    0 -> {
                                        // 第二张为审核中

                                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                            fiveInfoBean.data.headface[1].image_url)
                                        SPStaticUtils.put(Constant.ME_AVATAR, "")
                                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                            fiveInfoBean.data.headface[0].image_url)
                                    }
                                    1 -> {
                                        // 第二张为审核通过

                                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                                        SPStaticUtils.put(Constant.ME_AVATAR,
                                            fiveInfoBean.data.headface[1].image_url)
                                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                            fiveInfoBean.data.headface[0].image_url)
                                    }
                                    2 -> {
                                        // 第二张为审核拒绝

                                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                                        SPStaticUtils.put(Constant.ME_AVATAR, "")
                                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                            fiveInfoBean.data.headface[0].image_url)
                                    }
                                }

                            }

                        }
                    }
                }

                if (fiveInfoBean.data.blacklist != null) {

                    val interdictionBean = InterdictionBean().also {
                        it.blacklist_status = fiveInfoBean.data.blacklist.blacklist_status
                        it.blacklist_permanent = fiveInfoBean.data.blacklist.blacklist_permanent
                        it.blacklist_close_time = fiveInfoBean.data.blacklist.blacklist_close_time
                    }

                    InterdictionBean.putInterdictionState(interdictionBean)

                }

            }
        }
    }

    override fun onGetFiveInfoError() {

    }

    override fun onGetLifeAccessTokenSuccess(accessTokenBean: AccessTokenBean?) {
        if (accessTokenBean != null) {
            SPStaticUtils.put(Constant.LIFE_ACCESS_TOKEN, accessTokenBean.access_token)
        }
    }

    override fun onGetLifeAccessTokenFail() {

    }

    override fun onGetIdAccessTokenSuccess(accessTokenBean: AccessTokenBean?) {
        if (accessTokenBean != null) {
            SPStaticUtils.put(Constant.ID_ACCESS_TOKEN, accessTokenBean.access_token)
        }
    }

    override fun onGetIdAccessTokenFail() {

    }

    override fun onGetAccessTokenSuccess(accessTokenBean: AccessTokenBean?) {
        if (accessTokenBean != null) {
            SPStaticUtils.put(Constant.ACCESS_TOKEN, accessTokenBean.access_token)
        }
    }

    override fun onGetAccessTokenFail() {

    }

    override fun onGetJobSuccess(jobBean: JobBean) {

        Thread {
            for (i in 0.until(jobBean.data.size)) {

                SPStaticUtils.put("job_second_$i _sum", jobBean.data[i].child.size)
                SPStaticUtils.put("job_second_$i _have", true)

                for (j in 0.until(jobBean.data[i].child.size)) {

                    SPStaticUtils.put("job_second_$i _id_$j", jobBean.data[i].child[j].id)
                    SPStaticUtils.put("job_second_$i _name_$j", jobBean.data[i].child[j].name)

                }

            }
        }.start()

        SPStaticUtils.put(Constant.JOB_HAVE, true)

        isLoadJob = true

        isCompleteLoading()


    }

    override fun onGetJobError() {


    }

    override fun onGetIndustrySuccess(industryBean: IndustryBean) {
        SPStaticUtils.put(Constant.INDUSTRY_SUM, industryBean.data.size)

        for (i in 0.until(industryBean.data.size)) {
            SPStaticUtils.put("industry_item_name_$i", industryBean.data[i].name)
            SPStaticUtils.put("industry_item_id_$i", industryBean.data[i].id)

            Log.i("guo", " sp : industry_item_name_$i ---- size : ${industryBean.data[i].name}")
            Log.i("guo", " sp : industry_item_id_$i ---- size : ${industryBean.data[i].id}")

        }


        SPStaticUtils.put(Constant.INDUSTRY_HAVE, true)

        isLoadIndustry = true

        isCompleteLoading()

    }

    override fun onGetIndustryError() {

    }

    override fun onGetCitySuccess(cityBean: CityBean) {

        // 省份总数
        SPStaticUtils.put(Constant.CITY_JSON_DATE, GsonUtils.toJson(cityBean))

        Log.i("guo",
            GsonUtils.fromJson(SPStaticUtils.getString(Constant.CITY_JSON_DATE,
                GsonUtils.toJson(cityBean)), CityBean::class.java).toString())

        SPStaticUtils.put(Constant.CITY_HAVE, true)

        isLoadCity = true

        isCompleteLoading()

    }

    override fun onGetCityCodeError() {

        Log.i("guo", "error")

    }

    override fun onDestroy() {
        super.onDestroy()

        getCityPresent.unregisterCallback(this)
        getIndustryPresent.unregisterCallback(this)
        getJobPresent.unregisterCallback(this)

        getAccessTokenPresent.unregisterCallback(this)
        getIdAccessTokenPresent.unregisterCallback(this)
        getLifeAccessTokenPresent.unregisterCallback(this)

        getFiveInfoPresent.unregisterCallback(this)

    }

}