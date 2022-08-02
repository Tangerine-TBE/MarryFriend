package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.AccessTokenBean;
import com.twx.marryfriend.net.callback.IGetAccessTokenCallback;
import com.twx.marryfriend.net.callback.IGetIdAccessTokenCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IGetAccessTokenPresent;
import com.twx.marryfriend.net.present.IGetIdAccessTokenPresent;

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
public class getIdAccessTokenPresentImpl implements IGetIdAccessTokenPresent {

    public static getIdAccessTokenPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetIdAccessTokenCallback> mCallback = new ArrayList<>();

    public getIdAccessTokenPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getIdAccessTokenPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getIdAccessTokenPresentImpl();
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
                        for (IGetIdAccessTokenCallback callback : mCallback) {
                            callback.onGetIdAccessTokenSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AccessTokenBean> call, Throwable t) {
                for (IGetIdAccessTokenCallback callback : mCallback) {
                    callback.onGetIdAccessTokenFail();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetIdAccessTokenCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetIdAccessTokenCallback iGetIdAccessTokenCallback) {
        if (!mCallback.contains(iGetIdAccessTokenCallback)) {
            mCallback.add(iGetIdAccessTokenCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetIdAccessTokenCallback iGetIdAccessTokenCallback) {
        mCallback.remove(iGetIdAccessTokenCallback);
    }

}
