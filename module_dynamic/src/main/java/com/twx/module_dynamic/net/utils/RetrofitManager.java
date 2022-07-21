package com.twx.module_dynamic.net.utils;

import com.blankj.utilcode.util.AppUtils;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
import com.twx.module_base.constant.Contents;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private static RetrofitManager sInstance;

    public Retrofit getRetrofitUser() { return mRetrofitUser; }
    private final Retrofit mRetrofitUser;

    public Retrofit getBaiduRetrofitUser() { return mBaiduRetrofit; }
    private final Retrofit mBaiduRetrofit;

    public Retrofit getGaodeMapRetrofitUser() { return mGaodeMapRetrofit; }
    private final Retrofit mGaodeMapRetrofit;

    public static RetrofitManager getInstance() {
        if (sInstance == null) {
            sInstance = new RetrofitManager();
        }
        return sInstance;
    }

    private RetrofitManager() {

        mRetrofitUser = new Retrofit.Builder()
                .baseUrl(Contents.USER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient().build())
                .build();

        mBaiduRetrofit = new Retrofit.Builder()
                .baseUrl(Contents.BAIDU_MAP_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient().build())
                .build();

        mGaodeMapRetrofit = new Retrofit.Builder()
                .baseUrl(Contents.GAODE_MAP_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient().build())
                .build();

    }

    private OkHttpClient.Builder getClient(){
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(20, TimeUnit.SECONDS);
        //add log record
        if (AppUtils.isAppDebug()) {
            //打印网络请求日志
            LoggingInterceptor httpLoggingInterceptor = new LoggingInterceptor.Builder()
                    .loggable(BuildConfig.DEBUG)
                    .setLevel(Level.BASIC)
                    .log(Platform.INFO)
                    .request("请求")
                    .response("响应")
                    .build();
            httpClientBuilder.addInterceptor(httpLoggingInterceptor);
        }
        return httpClientBuilder;
    }
}
