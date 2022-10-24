package com.twx.marryfriend.net.impl;

import com.blankj.utilcode.util.ToastUtils;
import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.TextVerifyBean;
import com.twx.marryfriend.net.callback.IDoAutoLoginCallback;
import com.twx.marryfriend.net.callback.IDoTextVerifyCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoAutoLoginPresent;
import com.twx.marryfriend.net.present.IDoTextVerifyPresent;

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
public class doTextVerifyPresentImpl implements IDoTextVerifyPresent {

    public static doTextVerifyPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoTextVerifyCallback> mCallback = new ArrayList<>();

    public doTextVerifyPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doTextVerifyPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doTextVerifyPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doTextVerify(Map<String, String> info) {
        handlerLoading();
        mUserData.doTextVerify(info, new Callback<TextVerifyBean>() {

            private TextVerifyBean mBody;

            @Override
            public void onResponse(Call<TextVerifyBean> call, Response<TextVerifyBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoTextVerifyCallback callback : mCallback) {
                            callback.onDoTextVerifySuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TextVerifyBean> call, Throwable t) {

                ToastUtils.showShort("网络请求失败，请检查网络");

                for (IDoTextVerifyCallback callback : mCallback) {
                    callback.onDoTextVerifyError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoTextVerifyCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoTextVerifyCallback iDoTextVerifyCallback) {
        if (!mCallback.contains(iDoTextVerifyCallback)) {
            mCallback.add(iDoTextVerifyCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoTextVerifyCallback iDoTextVerifyCallback) {
        mCallback.remove(iDoTextVerifyCallback);
    }

}
