package com.twx.marryfriend.coin

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.alipay.sdk.app.PayTask
import com.google.gson.Gson
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.base.BaseConstant
import com.twx.marryfriend.bean.vip.CoinPriceBean
import com.twx.marryfriend.bean.vip.CoinPriceData
import com.twx.marryfriend.constant.Contents
import com.xyzz.myutils.NetworkUtil
import org.json.JSONObject
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CoinViewModel:ViewModel() {

    /**
     * 获取金币价格列表
     */
    suspend fun getCoinPrice()= suspendCoroutine<List<CoinPriceData>> {coroutine->
        val url="${Contents.USER_URL}/marryfriend/MemberCharge/jinbiList"
        val map= mapOf(
            "user_id" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "platform" to BaseConstant.channel,
            "type_kind" to "android"
        )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val gson=Gson()
                val result=gson.fromJson(response, CoinPriceBean::class.java).data
                coroutine.resume(result)
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })

    }

    suspend fun startAliPay(level:Int, money:String,fragmentActivity: FragmentActivity)= suspendCoroutine<Unit> { coroutine->
        val url="${Contents.USER_URL}/marryfriend/MemberCharge/alibabaPayment"
        val map= mapOf(
            "buy_order_number" to generateOrderNumber(level),
            "fee" to money,
            "body" to "金币",
            "user_system" to "1"
        )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                val payInfo=jsonObject.getJSONObject("data").getString("str")
                thread {
                    val alipay = PayTask(fragmentActivity)
                    val result = alipay.payV2(payInfo, true)

                    val code= result["resultStatus"]
                    val memo= result["memo"]
                    val ccccc=when(code){
                        "9000"->{
                            "支付成功"
                            coroutine.resume(Unit)
                            return@thread
                        }
                        "8000"->{
                            "正在处理中"
                        }
                        "4000"->{
                            "订单支付失败"
                        }
                        "5000"->{
                            "重复请求"
                        }
                        "6001"->{
                            "中途取消"
                        }
                        "6004"->{
                            "支付结果未知"
                        }
                        else->{
                            "未知"
                        }
                    }
                    val errorMsg=if (memo.isNullOrEmpty()){
                        ccccc
                    }else{
                        memo
                    }
                    coroutine.resumeWithException(Exception(errorMsg))
                }
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }

    private fun generateOrderNumber(level: Int):String{
        val channel=BaseConstant.channel.let {
            it.replace("yingyongbao","yyb")
                .replace("_","")
        }
        //WX,ALI
        return "JIN" + "_${level}" + "_${UserInfo.getUserId()}" + "_${channel}" + "_ALI" + "_${System.currentTimeMillis().toString().takeLast(6)}"
    }

    suspend fun getCoin()= suspendCoroutine<Int> { coroutine->
        val url="${Contents.USER_URL}/marryfriend/MemberCharge/refreshSelf"
        val map= mapOf(
            "user_id" to (UserInfo.getUserId()?:return@suspendCoroutine)
        )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                coroutine.resume(jsonObject.getJSONObject("data").getInt("jinbi_goldcoin"))
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }
}