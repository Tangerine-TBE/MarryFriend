package com.twx.module_dynamic.net.impl;

import com.twx.module_dynamic.bean.CheckTrendBean;
import com.twx.module_dynamic.bean.UploadTrendBean;
import com.twx.module_dynamic.net.callback.IDoCheckTrendCallback;
import com.twx.module_dynamic.net.callback.IDoUploadTrendCallback;
import com.twx.module_dynamic.net.module.UserData;
import com.twx.module_dynamic.net.present.IDoCheckTrendPresent;
import com.twx.module_dynamic.net.present.IDoUploadTrendPresent;

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
public class doCheckTrendPresentImpl implements IDoCheckTrendPresent {

    public static doCheckTrendPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoCheckTrendCallback> mCallback = new ArrayList<>();

    public doCheckTrendPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doCheckTrendPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doCheckTrendPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doCheckTrend(Map<String, String> info) {
        handlerLoading();
        mUserData.doCheckTrend(info, new Callback<CheckTrendBean>() {

            private CheckTrendBean mBody;

            @Override
            public void onResponse(Call<CheckTrendBean> call, Response<CheckTrendBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoCheckTrendCallback callback : mCallback) {
                            callback.onDoCheckTrendSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckTrendBean> call, Throwable t) {
                for (IDoCheckTrendCallback callback : mCallback) {
                    callback.onDoCheckTrendError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoCheckTrendCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoCheckTrendCallback iDoCheckTrendCallback) {
        if (!mCallback.contains(iDoCheckTrendCallback)) {
            mCallback.add(iDoCheckTrendCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoCheckTrendCallback iDoCheckTrendCallback) {
        mCallback.remove(iDoCheckTrendCallback);
    }

}
