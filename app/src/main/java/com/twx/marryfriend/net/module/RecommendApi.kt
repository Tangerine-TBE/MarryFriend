package com.twx.marryfriend.net.module

import com.twx.marryfriend.bean.VerifyCodeBean
import com.twx.marryfriend.net.utils.RetrofitManager
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface RecommendApi {
    companion object{
        val instance by lazy {
            RetrofitManager.getInstance().retrofitUser.create(RecommendApi::class.java)
        }
    }

    // 推荐列表，初始贫民每天5条、认证后贫民每天10条。会员20条
//    @POST()
//    fun getCommendList(@Url path: String, @QueryMap params: Map<String, String>): Call<VerifyCodeBean?>
}