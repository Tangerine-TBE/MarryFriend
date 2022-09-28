package com.twx.marryfriend.net.impl;

import android.util.Log;

import com.twx.marryfriend.bean.CityBean;
import com.twx.marryfriend.bean.GreetInfoBean;
import com.twx.marryfriend.net.callback.IGetCityCallback;
import com.twx.marryfriend.net.callback.IGetGreetInfoCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IGetCityPresent;
import com.twx.marryfriend.net.present.IGetGreetInfoPresent;

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
public class getGreetInfoPresentImpl implements IGetGreetInfoPresent {

    public static getGreetInfoPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetGreetInfoCallback> mCallback = new ArrayList<>();

    public getGreetInfoPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getGreetInfoPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getGreetInfoPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getGreetInfo(Map<String, String> info) {
        handlerLoading();
        mUserData.getGreetInfo(info, new Callback<GreetInfoBean>() {

            private GreetInfoBean mBody;

            @Override
            public void onResponse(Call<GreetInfoBean> call, Response<GreetInfoBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetGreetInfoCallback callback : mCallback) {
                            callback.onGetGreetInfoSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GreetInfoBean> call, Throwable t) {
                Log.i("guo", "erroe: " + t);
                for (IGetGreetInfoCallback callback : mCallback) {
                    callback.onGetGreetInfoCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetGreetInfoCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetGreetInfoCallback iGetGreetInfoCallback) {
        if (!mCallback.contains(iGetGreetInfoCallback)) {
            mCallback.add(iGetGreetInfoCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetGreetInfoCallback iGetGreetInfoCallback) {
        mCallback.remove(iGetGreetInfoCallback);
    }

}
