package com.twx.module_base.net.impl;

import com.twx.module_base.net.bean.BaseInfoUpdateBean;
import com.twx.module_base.net.callback.IDoUpdateBaseInfoCallback;
import com.twx.module_base.net.module.UserData;
import com.twx.module_base.net.present.IDoUpdateBaseInfoPresent;

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
public class doUpdateBaseInfoPresentImpl implements IDoUpdateBaseInfoPresent {

    public static doUpdateBaseInfoPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoUpdateBaseInfoCallback> mCallback = new ArrayList<>();

    public doUpdateBaseInfoPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doUpdateBaseInfoPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doUpdateBaseInfoPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doUpdateBaseInfo(Map<String, String> info) {
        handlerLoading();
        mUserData.doUpdateBaseInfo(info, new Callback<BaseInfoUpdateBean>() {

            private BaseInfoUpdateBean mBody;

            @Override
            public void onResponse(Call<BaseInfoUpdateBean> call, Response<BaseInfoUpdateBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoUpdateBaseInfoCallback callback : mCallback) {
                            callback.onDoUpdateBaseInfoSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseInfoUpdateBean> call, Throwable t) {
                for (IDoUpdateBaseInfoCallback callback : mCallback) {
                    callback.onDoUpdateBaseInfoError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoUpdateBaseInfoCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoUpdateBaseInfoCallback iDoUpdateBaseInfoCallback) {
        if (!mCallback.contains(iDoUpdateBaseInfoCallback)) {
            mCallback.add(iDoUpdateBaseInfoCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoUpdateBaseInfoCallback iDoUpdateBaseInfoCallback) {
        mCallback.remove(iDoUpdateBaseInfoCallback);
    }

}
