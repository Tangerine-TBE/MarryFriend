package com.twx.module_base.net.impl;

import com.twx.module_base.net.bean.UpdateGreetInfoBean;
import com.twx.module_base.net.callback.IDoUpdateGreetInfoCallback;
import com.twx.module_base.net.module.UserData;
import com.twx.module_base.net.present.IDoUpdateGreetInfoPresent;

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
public class doUpdateGreetInfoPresentImpl implements IDoUpdateGreetInfoPresent {

    public static doUpdateGreetInfoPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoUpdateGreetInfoCallback> mCallback = new ArrayList<>();

    public doUpdateGreetInfoPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doUpdateGreetInfoPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doUpdateGreetInfoPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doUpdateGreetInfo(Map<String, String> info) {
        handlerLoading();
        mUserData.doUpdateGreetInfo(info, new Callback<UpdateGreetInfoBean>() {

            private UpdateGreetInfoBean mBody;

            @Override
            public void onResponse(Call<UpdateGreetInfoBean> call, Response<UpdateGreetInfoBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoUpdateGreetInfoCallback callback : mCallback) {
                            callback.onDoUpdateGreetInfoSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateGreetInfoBean> call, Throwable t) {
                for (IDoUpdateGreetInfoCallback callback : mCallback) {
                    callback.onDoUpdateGreetInfoError();
                }
            }
        });
    }

    private void handlerLoading() {
        for (IDoUpdateGreetInfoCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoUpdateGreetInfoCallback iDoUpdateGreetInfoCallback) {
        if (!mCallback.contains(iDoUpdateGreetInfoCallback)) {
            mCallback.add(iDoUpdateGreetInfoCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoUpdateGreetInfoCallback iDoUpdateGreetInfoCallback) {
        mCallback.remove(iDoUpdateGreetInfoCallback);
    }

}
