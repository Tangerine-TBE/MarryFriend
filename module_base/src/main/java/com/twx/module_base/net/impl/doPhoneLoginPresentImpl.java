package com.twx.module_base.net.impl;

import com.twx.module_base.net.bean.PhoneLoginBean;
import com.twx.module_base.net.callback.IDoPhoneLoginCallback;
import com.twx.module_base.net.module.UserData;
import com.twx.module_base.net.present.IDoPhoneLoginPresent;

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
public class doPhoneLoginPresentImpl implements IDoPhoneLoginPresent {

    public static doPhoneLoginPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoPhoneLoginCallback> mCallback = new ArrayList<>();

    public doPhoneLoginPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doPhoneLoginPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doPhoneLoginPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doPhoneLogin(Map<String, String> info) {
        handlerLoading();
        mUserData.doPhoneLogin(info, new Callback<PhoneLoginBean>() {

            private PhoneLoginBean mBody;

            @Override
            public void onResponse(Call<PhoneLoginBean> call, Response<PhoneLoginBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoPhoneLoginCallback callback : mCallback) {
                            callback.onDoPhoneLoginSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PhoneLoginBean> call, Throwable t) {
                for (IDoPhoneLoginCallback callback : mCallback) {
                    callback.onDoPhoneLoginError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoPhoneLoginCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoPhoneLoginCallback iDoPhoneLoginCallback) {
        if (!mCallback.contains(iDoPhoneLoginCallback)) {
            mCallback.add(iDoPhoneLoginCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoPhoneLoginCallback iDoPhoneLoginCallback) {
        mCallback.remove(iDoPhoneLoginCallback);
    }

}
