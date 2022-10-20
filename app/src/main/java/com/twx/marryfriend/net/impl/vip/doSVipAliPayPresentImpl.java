package com.twx.marryfriend.net.impl.vip;

import com.twx.marryfriend.bean.vip.AliPayBean;
import com.twx.marryfriend.net.callback.vip.IDoAliPayCallback;
import com.twx.marryfriend.net.callback.vip.IDoSVipAliPayCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.vip.IDoAliPayPresent;
import com.twx.marryfriend.net.present.vip.IDoSVipAliPayPresent;

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
public class doSVipAliPayPresentImpl implements IDoSVipAliPayPresent {

    public static doSVipAliPayPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoSVipAliPayCallback> mCallback = new ArrayList<>();

    public doSVipAliPayPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doSVipAliPayPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doSVipAliPayPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doSVipAliPay(Map<String, String> info) {
        handlerLoading();
        mUserData.doAliPay(info, new Callback<AliPayBean>() {

            private AliPayBean mBody;

            @Override
            public void onResponse(Call<AliPayBean> call, Response<AliPayBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoSVipAliPayCallback callback : mCallback) {
                            callback.onDoSVipAliPaySuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AliPayBean> call, Throwable t) {
                for (IDoSVipAliPayCallback callback : mCallback) {
                    callback.onDoSVipAliPayError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoSVipAliPayCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoSVipAliPayCallback iDoSVipAliPayCallback) {
        if (!mCallback.contains(iDoSVipAliPayCallback)) {
            mCallback.add(iDoSVipAliPayCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoSVipAliPayCallback iDoSVipAliPayCallback) {
        mCallback.remove(iDoSVipAliPayCallback);
    }

}
