package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.DemandAddressBean;
import com.twx.marryfriend.bean.PlusDemandAddressBean;
import com.twx.marryfriend.net.callback.IDoGetDemandAddressCallback;
import com.twx.marryfriend.net.callback.IDoPlusDemandAddressCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoGetDemandAddressPresent;
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
public class doGetDemandAddressPresentImpl implements IDoGetDemandAddressPresent {

    public static doGetDemandAddressPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoGetDemandAddressCallback> mCallback = new ArrayList<>();

    public doGetDemandAddressPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doGetDemandAddressPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doGetDemandAddressPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doGetDemandAddress(Map<String, String> info) {
        handlerLoading();
        mUserData.getDemandAddress(info, new Callback<DemandAddressBean>() {

            private DemandAddressBean mBody;

            @Override
            public void onResponse(Call<DemandAddressBean> call, Response<DemandAddressBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoGetDemandAddressCallback callback : mCallback) {
                            callback.onDoGetDemandAddressSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DemandAddressBean> call, Throwable t) {
                for (IDoGetDemandAddressCallback callback : mCallback) {
                    callback.onDoGetDemandAddressError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoGetDemandAddressCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoGetDemandAddressCallback iDoGetDemandAddressCallback) {
        if (!mCallback.contains(iDoGetDemandAddressCallback)) {
            mCallback.add(iDoGetDemandAddressCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoGetDemandAddressCallback iDoGetDemandAddressCallback) {
        mCallback.remove(iDoGetDemandAddressCallback);
    }


}
