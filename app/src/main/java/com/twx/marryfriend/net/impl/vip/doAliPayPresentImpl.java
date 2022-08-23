package com.twx.marryfriend.net.impl.vip;

import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.vip.AliPayBean;
import com.twx.marryfriend.net.callback.IDoAutoLoginCallback;
import com.twx.marryfriend.net.callback.vip.IDoAliPayCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoAutoLoginPresent;
import com.twx.marryfriend.net.present.vip.IDoAliPayPresent;

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
public class doAliPayPresentImpl implements IDoAliPayPresent {

    public static doAliPayPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoAliPayCallback> mCallback = new ArrayList<>();

    public doAliPayPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doAliPayPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doAliPayPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doAliPay(Map<String, String> info) {
        handlerLoading();
        mUserData.doAliPay(info, new Callback<AliPayBean>() {

            private AliPayBean mBody;

            @Override
            public void onResponse(Call<AliPayBean> call, Response<AliPayBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoAliPayCallback callback : mCallback) {
                            callback.onDoAliPaySuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AliPayBean> call, Throwable t) {
                for (IDoAliPayCallback callback : mCallback) {
                    callback.onDoAliPayError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoAliPayCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoAliPayCallback iDoAliPayCallback) {
        if (!mCallback.contains(iDoAliPayCallback)) {
            mCallback.add(iDoAliPayCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoAliPayCallback iDoAliPayCallback) {
        mCallback.remove(iDoAliPayCallback);
    }



}
