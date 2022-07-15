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
import com.twx.marryfriend.net.callback.IGetAccessTokenCallback
import com.twx.marryfriend.net.callback.IGetCityCallback
import com.twx.marryfriend.net.callback.IGetIndustryCallback
import com.twx.marryfriend.net.callback.IGetJobCallback
import com.twx.marryfriend.net.impl.getAccessTokenPresentImpl
import com.twx.marryfriend.net.impl.getCityPresentImpl
import com.twx.marryfriend.net.impl.getIndustryPresentImpl
import com.twx.marryfriend.net.impl.getJobPresentImpl
import java.util.*


class GetInfoActivity : MainBaseViewActivity(), IGetCityCallback, IGetIndustryCallback,
    IGetJobCallback, IGetAccessTokenCallback {

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

    }

    override fun initLoadData() {
        super.initLoadData()

        getAccessToken()

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

    // 获取  accessToken
    private fun getAccessToken() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.GRANT_TYPE] = "client_credentials"
        map[Contents.CLIENT_ID] = "jjKDyljlCOX3TcEcnXidYCcU"
        map[Contents.CLIENT_SECRET] = "GQcKzFuA87uEQZhIDnlcxTpkjT2oLxdX"
        getAccessTokenPresent.getAccessToken(map)

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

    override fun onGetAccessTokenSuccess(accessTokenBean: AccessTokenBean) {
        SPStaticUtils.put(Constant.ACCESS_TOKEN,accessTokenBean.access_token)
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

        Log.i("guo", GsonUtils.toJson(cityBean))

//        SPStaticUtils.put("city_province_sum", cityBean.data.size)

//        Thread {
//            for (i in 0.until(cityBean.data.size)) {
//
//                // 省份
//                SPStaticUtils.put("city_province_$i _name", cityBean.data[i].name)
//                SPStaticUtils.put("city_province_$i _code", cityBean.data[i].code)
//
//                // 市总数
//                SPStaticUtils.put("city_province_$i _city_sum", cityBean.data[i].cityList.size)
//
//                for (j in 0.until(cityBean.data[i].cityList.size)) {
//
//                    // 城市
//                    SPStaticUtils.put("city_province_$i _city_$j _name",
//                        cityBean.data[i].cityList[j].name)
//                    SPStaticUtils.put("city_province_$i _city_$j _code",
//                        cityBean.data[i].cityList[j].code)
//
//                    // 县总数
//                    SPStaticUtils.put("city_province_$i _city_$j _area_sum",
//                        cityBean.data[i].cityList[j].areaList.size)
//
//                    for (k in 0.until(cityBean.data[i].cityList[j].areaList.size)) {
//
//                        // 县
//                        SPStaticUtils.put("city_province_$i _city_$j _area_$k _name",
//                            cityBean.data[i].cityList[j].areaList[k].name)
//                        SPStaticUtils.put("city_province_$i _city_$j _area_$k _code",
//                            cityBean.data[i].cityList[j].areaList[k].code)
//
//                    }
//
//                }
//
//
//
//            }
//        }.start()
//
        SPStaticUtils.put(Constant.CITY_HAVE, true)

        isLoadCity = true

        isCompleteLoading()

    }

    override fun onGetCityCodeError() {

        Log.i("guo","error")

    }

}