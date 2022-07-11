package com.twx.marryfriend.recommend

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.xyzz.myutils.MyUtils
import kotlin.math.*

object LocationUtils {
    private val context by lazy {
        MyUtils.application
    }
    data class MyLocation(val longitude:Double,val latitude:Double)
    private val locationLiveData by lazy {
        MutableLiveData<MyLocation>()
    }

    fun observeLocation(owner: LifecycleOwner,observer:(MyLocation)->Unit){
        locationLiveData.observe(owner,observer)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startLocation(){
        val mLocationClient = LocationClient(context)
        mLocationClient.registerLocationListener(object :
            BDAbstractLocationListener() {
            override fun onReceiveLocation(location: BDLocation) {
                locationLiveData.value=MyLocation(location.longitude,location.latitude)
            }
        })
        val option = LocationClientOption()
        option.setIsNeedAddress(true)
        option.setNeedNewVersionRgc(true)
        option.isOnceLocation=true
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