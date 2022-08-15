package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.PlusDemandAddressBean;
import com.twx.marryfriend.net.callback.IDoPlusDemandAddressCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoPlusDemandAddressPresent;

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
public class doPlusDemandAddressPresentImpl implements IDoPlusDemandAddressPresent {

    public static doPlusDemandAddressPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoPlusDemandAddressCallback> mCallback = new ArrayList<>();

    public doPlusDemandAddressPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doPlusDemandAddressPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doPlusDemandAddressPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doPlusDemandAddress(Map<String, String> info) {
        handlerLoading();
        mUserData.plusDemandAddress(info, new Callback<PlusDemandAddressBean>() {

            private PlusDemandAddressBean mBody;

            @Override
            public void onResponse(Call<PlusDemandAddressBean> call, Response<PlusDemandAddressBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoPlusDemandAddressCallback callback : mCallback) {
                            callback.onDoPlusDemandAddressSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PlusDemandAddressBean> call, Throwable t) {
                for (IDoPlusDemandAddressCallback callback : mCallback) {
                    callback.onDoPlusDemandAddressError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoPlusDemandAddressCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoPlusDemandAddressCallback iDoPlusDemandAddressCallback) {
        if (!mCallback.contains(iDoPlusDemandAddressCallback)) {
            mCallback.add(iDoPlusDemandAddressCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoPlusDemandAddressCallback iDoPlusDemandAddressCallback) {
        mCallback.remove(iDoPlusDemandAddressCallback);
    }
}
