package com.twx.marryfriend.net.impl;

import com.blankj.utilcode.util.ToastUtils;
import com.twx.marryfriend.bean.AccessTokenBean;
import com.twx.marryfriend.net.callback.IGetIdAccessTokenCallback;
import com.twx.marryfriend.net.callback.IGetLifeAccessTokenCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IGetIdAccessTokenPresent;
import com.twx.marryfriend.net.present.IGetLifeAccessTokenPresent;

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
public class getLifeAccessTokenPresentImpl implements IGetLifeAccessTokenPresent {

    public static getLifeAccessTokenPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetLifeAccessTokenCallback> mCallback = new ArrayList<>();

    public getLifeAccessTokenPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getLifeAccessTokenPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getLifeAccessTokenPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getAccessToken(Map<String, String> info) {
        handlerLoading();
        mUserData.getAccessToken(info, new Callback<AccessTokenBean>() {

            private AccessTokenBean mBody;

            @Override
            public void onResponse(Call<AccessTokenBean> call, Response<AccessTokenBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetLifeAccessTokenCallback callback : mCallback) {
                            callback.onGetLifeAccessTokenSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AccessTokenBean> call, Throwable t) {

                ToastUtils.showShort("网络请求失败，请检查网络");

                for (IGetLifeAccessTokenCallback callback : mCallback) {
                    callback.onGetLifeAccessTokenFail();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetLifeAccessTokenCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetLifeAccessTokenCallback iGetLifeAccessTokenCallback) {
        if (!mCallback.contains(iGetLifeAccessTokenCallback)) {
            mCallback.add(iGetLifeAccessTokenCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetLifeAccessTokenCallback iGetLifeAccessTokenCallback) {
        mCallback.remove(iGetLifeAccessTokenCallback);
    }


}
