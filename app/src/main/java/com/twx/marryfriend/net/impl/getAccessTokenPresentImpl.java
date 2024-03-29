package com.twx.marryfriend.net.impl;

import com.blankj.utilcode.util.ToastUtils;
import com.twx.marryfriend.bean.AccessTokenBean;
import com.twx.marryfriend.bean.BanBean;
import com.twx.marryfriend.net.callback.IGetAccessTokenCallback;
import com.twx.marryfriend.net.callback.IGetBanCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IGetAccessTokenPresent;
import com.twx.marryfriend.net.present.IGetBanPresent;

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
public class getAccessTokenPresentImpl implements IGetAccessTokenPresent {

    public static getAccessTokenPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetAccessTokenCallback> mCallback = new ArrayList<>();

    public getAccessTokenPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getAccessTokenPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getAccessTokenPresentImpl();
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
                        for (IGetAccessTokenCallback callback : mCallback) {
                            callback.onGetAccessTokenSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AccessTokenBean> call, Throwable t) {

                ToastUtils.showShort("网络请求失败，请检查网络");

                for (IGetAccessTokenCallback callback : mCallback) {
                    callback.onGetAccessTokenFail();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetAccessTokenCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetAccessTokenCallback iGetAccessTokenCallback) {
        if (!mCallback.contains(iGetAccessTokenCallback)) {
            mCallback.add(iGetAccessTokenCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetAccessTokenCallback iGetAccessTokenCallback) {
        mCallback.remove(iGetAccessTokenCallback);
    }

}
