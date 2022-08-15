package com.twx.marryfriend.net.impl;

import android.util.Log;

import com.twx.marryfriend.bean.CityBean;
import com.twx.marryfriend.bean.WhoSeeMeBean;
import com.twx.marryfriend.net.callback.IGetCityCallback;
import com.twx.marryfriend.net.callback.IGetWhoSeeMeCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IGetCityPresent;
import com.twx.marryfriend.net.present.IGetWhoSeeMePresent;

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
public class getWhoSeeMePresentImpl implements IGetWhoSeeMePresent {

    public static getWhoSeeMePresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetWhoSeeMeCallback> mCallback = new ArrayList<>();

    public getWhoSeeMePresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getWhoSeeMePresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getWhoSeeMePresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getWhoSeeMe(Map<String, String> info) {
        handlerLoading();
        mUserData.getWhoSeeMe(info, new Callback<WhoSeeMeBean>() {

            private WhoSeeMeBean mBody;

            @Override
            public void onResponse(Call<WhoSeeMeBean> call, Response<WhoSeeMeBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetWhoSeeMeCallback callback : mCallback) {
                            callback.onGetWhoSeeMeSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WhoSeeMeBean> call, Throwable t) {
                Log.i("guo", "erroe: " + t);
                for (IGetWhoSeeMeCallback callback : mCallback) {
                    callback.onGetWhoSeeMeCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetWhoSeeMeCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetWhoSeeMeCallback iGetWhoSeeMeCallback) {
        if (!mCallback.contains(iGetWhoSeeMeCallback)) {
            mCallback.add(iGetWhoSeeMeCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetWhoSeeMeCallback iGetWhoSeeMeCallback) {
        mCallback.remove(iGetWhoSeeMeCallback);
    }


}
