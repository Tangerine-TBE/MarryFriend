package com.twx.module_dynamic.net.impl;

import com.twx.module_dynamic.bean.CheckTrendBean;
import com.twx.module_dynamic.bean.DeleteTrendBean;
import com.twx.module_dynamic.net.callback.IDoCheckTrendCallback;
import com.twx.module_dynamic.net.callback.IDoDeleteTrendCallback;
import com.twx.module_dynamic.net.module.UserData;
import com.twx.module_dynamic.net.present.IDoCheckTrendPresent;
import com.twx.module_dynamic.net.present.IDoDeleteTrendPresent;

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
public class doDeleteTrendPresentImpl implements IDoDeleteTrendPresent {

    public static doDeleteTrendPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoDeleteTrendCallback> mCallback = new ArrayList<>();

    public doDeleteTrendPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doDeleteTrendPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doDeleteTrendPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doDeleteTrend(Map<String, String> info) {
        handlerLoading();
        mUserData.doDeleteTrend(info, new Callback<DeleteTrendBean>() {

            private DeleteTrendBean mBody;

            @Override
            public void onResponse(Call<DeleteTrendBean> call, Response<DeleteTrendBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoDeleteTrendCallback callback : mCallback) {
                            callback.onDoDeleteTrendSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DeleteTrendBean> call, Throwable t) {
                for (IDoDeleteTrendCallback callback : mCallback) {
                    callback.onDoDeleteTrendError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoDeleteTrendCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoDeleteTrendCallback iDoDeleteTrendCallback) {
        if (!mCallback.contains(iDoDeleteTrendCallback)) {
            mCallback.add(iDoDeleteTrendCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoDeleteTrendCallback iDoDeleteTrendCallback) {
        mCallback.remove(iDoDeleteTrendCallback);
    }


}
