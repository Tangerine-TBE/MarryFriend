package com.twx.marryfriend.recommend

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.getCityData
import com.xyzz.myutils.LifecycleCallbacks
import com.xyzz.myutils.MyUtils
import com.xyzz.myutils.NetworkUtil
import com.xyzz.myutils.SPUtil
import com.xyzz.myutils.show.iLog
import java.text.NumberFormat
import kotlin.math.*

object LocationUtils {
    private const val PRE_UP_TIME = "pre_up_time"
    private var isFirst = true
    private const val CITY_CODE_KEY = "location_k"
    fun putLocation(location: MyLocation) {
        val gson = GsonBuilder()
            .serializeNulls()
            .create()
        SPUtil.instance.putString(CITY_CODE_KEY, gson.toJson(location))
    }

    fun getLocation(): MyLocation? {
        return locationLiveData.value ?: try {
            Gson().fromJson(SPUtil.instance.getString(CITY_CODE_KEY), MyLocation::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private val context by lazy {
        MyUtils.application
    }

    data class MyLocation constructor(
        val longitude: Double,
        val latitude: Double,
        val address: String,
    ) {
        var province: String? = null
        var provinceCode: Int? = null
        var city: String? = null
        var cityCode: Int? = null
    }

    private val locationLiveData by lazy {
        MutableLiveData<MyLocation?>()
    }

    fun observeLocation(owner: LifecycleOwner, observer: (MyLocation?) -> Unit) {
        locationLiveData.observe(owner, observer)
    }

    fun frontBackstageLiveData(owner: LifecycleOwner) {
        LifecycleCallbacks.instance.frontBackstageLiveData.observe(owner) {
            if (isFirst || it == true) {
                upLocation()
                isFirst = false
            }
        }
    }

    private fun upLocation() {
        val myLocation = locationLiveData.value
        if (myLocation != null) {
            val preTime = SPUtil.instance.getLong(PRE_UP_TIME, 0L)
            if (preTime + 6 * 60 * 60 * 1000L < System.currentTimeMillis()) {
                val numberFormat = NumberFormat.getNumberInstance()
                numberFormat.maximumFractionDigits = 5
                NetworkUtil.sendPostSecret("${Contents.USER_URL}/marryfriend/LoginRegister/positionUp",
                    mapOf("user_id" to (UserInfo.getUserId() ?: return),
                        "jingdu" to numberFormat.format(myLocation.longitude),
                        "weidu" to numberFormat.format(myLocation.latitude),
                        "address" to myLocation.address), {
                        iLog(it, "上传位置")
                        SPUtil.instance.putLong(PRE_UP_TIME, System.currentTimeMillis())
                    }, {
                        iLog(it, "上传位置")
                    })
            }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startLocation() {
        val mLocationClient = LocationClient(context)
        mLocationClient.registerLocationListener(object :
            BDAbstractLocationListener() {
            override fun onReceiveLocation(location: BDLocation) {
                if (location.addrStr == null) {
                    locationLiveData.value = null
                    return
                }
                locationLiveData.value = MyLocation(location.longitude,
                    location.latitude,
                    location.addrStr ?: "").also { myLocation ->
                    myLocation.city = location.city
                    var provinceCode: Int? = null
                    var cityCode: Int? = null
                    getCityData()?.data?.find {
                        val result =
                            it.name.removeSuffix("省") == location.province.removeSuffix("省")
                        provinceCode = it.id
                        result
                    }?.child?.find {
                        val result = it.name.removeSuffix("市") == location.city.removeSuffix("市")
                        cityCode = it.id
                        result
                    }
                    myLocation.provinceCode = provinceCode
                    myLocation.cityCode = cityCode

                    putLocation(myLocation)
                }
            }
        })
        val option = LocationClientOption()
        option.setIsNeedAddress(true)
        option.setNeedNewVersionRgc(true)
        option.isOnceLocation = true
//        option.setScanSpan(300_000)
        mLocationClient.locOption = option
        mLocationClient.start()
    }

    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radLat1 = lat1 * Math.PI / 180
        val radLat2 = lat2 * Math.PI / 180
        val a = radLat1 - radLat2
        val b = lon1 * Math.PI / 180 - lon2 * Math.PI / 180
        var s = 2 * asin(
            sqrt(
                sin(a / 2).pow(2.0)
                        + (cos(radLat1) * cos(radLat2)
                        * sin(b / 2).pow(2.0))
            )
        )
        s *= 6378137.0 // 取WGS84标准参考椭球中的地球长半径(单位:m)
        s = ((s * 10000).roundToInt() / 10000).toDouble()
        return s
    }
}