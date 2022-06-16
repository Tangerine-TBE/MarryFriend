package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.AccessTokenBean;
import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.IdentityVerifyBean;
import com.twx.marryfriend.net.callback.IDoAutoLoginCallback;
import com.twx.marryfriend.net.callback.IDoIdentityVerifyCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoAutoLoginPresent;
import com.twx.marryfriend.net.present.IDoIdentityVerifyPresent;

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
public class doIdentityVerifyPresentImpl implements IDoIdentityVerifyPresent {

    public static doIdentityVerifyPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoIdentityVerifyCallback> mCallback = new ArrayList<>();

    public doIdentityVerifyPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doIdentityVerifyPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doIdentityVerifyPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doIdentityVerify(Map<String, String> info) {
        handlerLoading();
        mUserData.doIdentityVerify(info, new Callback<IdentityVerifyBean>() {

            private IdentityVerifyBean mBody;

            @Override
            public void onResponse(Call<IdentityVerifyBean> call, Response<IdentityVerifyBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoIdentityVerifyCallback callback : mCallback) {
                            callback.onDoIdentityVerifySuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<IdentityVerifyBean> call, Throwable t) {
                for (IDoIdentityVerifyCallback callback : mCallback) {
                    callback.onDoIdentityVerifyError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoIdentityVerifyCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoIdentityVerifyCallback iDoIdentityVerifyCallback) {
        if (!mCallback.contains(iDoIdentityVerifyCallback)) {
            mCallback.add(iDoIdentityVerifyCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoIdentityVerifyCallback iDoIdentityVerifyCallback) {
        mCallback.remove(iDoIdentityVerifyCallback);
    }

}
