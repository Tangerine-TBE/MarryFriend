package com.twx.marryfriend.net.impl;

import android.util.Log;

import com.twx.marryfriend.bean.CityBean;
import com.twx.marryfriend.net.callback.IGetCityCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IGetCityPresent;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public class getCityPresentImpl implements IGetCityPresent {

    public static getCityPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetCityCallback> mCallback = new ArrayList<>();

    public getCityPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getCityPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getCityPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getCity(Map<String, String> info) {
        handlerLoading();
        mUserData.getCity(info, new Callback<CityBean>() {

            private CityBean mBody;

            @Override
            public void onResponse(Call<CityBean> call, Response<CityBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetCityCallback callback : mCallback) {
                            callback.onGetCitySuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CityBean> call, Throwable t) {
                Log.i("guo","erroe: " + t);
                for (IGetCityCallback callback : mCallback) {
                    callback.onGetCityCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetCityCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetCityCallback iGetCityCallback) {
        if (!mCallback.contains(iGetCityCallback)) {
            mCallback.add(iGetCityCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetCityCallback iGetCityCallback) {
        mCallback.remove(iGetCityCallback);
    }

}
