package com.twx.marryfriend.guide.info

import android.content.Intent
import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.AccessTokenBean
import com.twx.marryfriend.bean.CityBean
import com.twx.marryfriend.bean.IndustryBean
import com.twx.marryfriend.bean.JobBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.guide.baseInfo.BaseInfoActivity
import com.twx.marryfriend.guide.detailInfo.DetailInfoActivity
import com.twx.marryfriend.main.MainActivity
import com.twx.marryfriend.net.callback.*
import com.twx.marryfriend.net.impl.*
import java.util.*


class GetInfoActivity : MainBaseViewActivity(), IGetCityCallback, IGetIndustryCallback,
    IGetJobCallback, IGetAccessTokenCallback, IGetIdAccessTokenCallback,
    IGetLifeAccessTokenCallback {

    // 是否加载城市
    private var isLoadCity = false

    // 是否加载行业
    private var isLoadIndustry = false

    // 是否加载岗位
    private var isLoadJob = false

    // 城市获取
    private lateinit var getCityPresent: getCityPresentImpl
    private lateinit var getIndustryPresent: getIndustryPresentImpl
    private lateinit var getJobPresent: getJobPresentImpl

    private lateinit var getAccessTokenPresent: getAccessTokenPresentImpl
    private lateinit var getIdAccessTokenPresent: getIdAccessTokenPresentImpl
    private lateinit var getLifeAccessTokenPresent: getLifeAccessTokenPresentImpl

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

    }

    override fun initLoadData() {
        super.initLoadData()

        // 获取百度
        getAccessToken()
        getIdAccessToken()
        getLifeAccessToken()

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

    private fun isCompleteLoading() {

        ThreadUtils.runOnUiThread {

            if (SPStaticUtils.getBoolean(Constant.JOB_HAVE, false) &&
                SPStaticUtils.getBoolean(Constant.CITY_HAVE, false) &&
                SPStaticUtils.getBoolean(Constant.INDUSTRY_HAVE, false)
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
                        ToastUtils.showShort("资料填写完成，前往首页")
                    }
                }
            }

        }

    }

    override fun onLoading() {

    }

    override fun onError() {

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
                GsonUtils.toJson(cityBean)), CityBean::class.java)
                .toString())

        SPStaticUtils.put(Constant.CITY_HAVE, true)

        isLoadCity = true

        isCompleteLoading()

    }

    override fun onGetCityCodeError() {

        Log.i("guo", "error")

    }

}