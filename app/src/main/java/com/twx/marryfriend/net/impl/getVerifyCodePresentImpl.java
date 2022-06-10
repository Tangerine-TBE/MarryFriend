package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.VerifyCodeBean;
import com.twx.marryfriend.net.callback.IGetVerifyCodeCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IGetVerifyCodePresent;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
public class getVerifyCodePresentImpl implements IGetVerifyCodePresent {

    public static getVerifyCodePresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetVerifyCodeCallback> mCallback = new ArrayList<>();

    public getVerifyCodePresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getVerifyCodePresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getVerifyCodePresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getVerifyCode(Map<String, String> info) {
        handlerLoading();
        mUserData.getVerifyCode(info, new Callback<VerifyCodeBean>() {

            private VerifyCodeBean mBody;

            @Override
            public void onResponse(Call<VerifyCodeBean> call, Response<VerifyCodeBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetVerifyCodeCallback callback : mCallback) {
                            callback.onGetVerifyCodeSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VerifyCodeBean> call, Throwable t) {
                for (IGetVerifyCodeCallback callback : mCallback) {
                    callback.onGetVerifyCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetVerifyCodeCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetVerifyCodeCallback iGetVerifyCodeCallback) {
        if (!mCallback.contains(iGetVerifyCodeCallback)) {
            mCallback.add(iGetVerifyCodeCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetVerifyCodeCallback iGetVerifyCodeCallback) {
        mCallback.remove(iGetVerifyCodeCallback);
    }

}
