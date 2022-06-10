package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.PhoneLoginBean;
import com.twx.marryfriend.net.callback.IDoAutoLoginCallback;
import com.twx.marryfriend.net.callback.IDoPhoneLoginCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoAutoLoginPresent;
import com.twx.marryfriend.net.present.IDoPhoneLoginPresent;

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
public class doAutoLoginPresentImpl implements IDoAutoLoginPresent {

    public static doAutoLoginPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoAutoLoginCallback> mCallback = new ArrayList<>();

    public doAutoLoginPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doAutoLoginPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doAutoLoginPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doAutoLogin(Map<String, String> info) {
        handlerLoading();
        mUserData.doAutoLogin(info, new Callback<AutoLoginBean>() {

            private AutoLoginBean mBody;

            @Override
            public void onResponse(Call<AutoLoginBean> call, Response<AutoLoginBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoAutoLoginCallback callback : mCallback) {
                            callback.onDoAutoLoginSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AutoLoginBean> call, Throwable t) {
                for (IDoAutoLoginCallback callback : mCallback) {
                    callback.onDoAutoLoginError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoAutoLoginCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoAutoLoginCallback iDoAutoLoginCallback) {
        if (!mCallback.contains(iDoAutoLoginCallback)) {
            mCallback.add(iDoAutoLoginCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoAutoLoginCallback iDoAutoLoginCallback) {
        mCallback.remove(iDoAutoLoginCallback);
    }

}
