package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.FiveInfoBean;
import com.twx.marryfriend.bean.VerifyCodeBean;
import com.twx.marryfriend.net.callback.IGetFiveInfoCallback;
import com.twx.marryfriend.net.callback.IGetVerifyCodeCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IGetFiveInfoPresent;
import com.twx.marryfriend.net.present.IGetVerifyCodePresent;

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
public class getFiveInfoPresentImpl implements IGetFiveInfoPresent {

    public static getFiveInfoPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetFiveInfoCallback> mCallback = new ArrayList<>();

    public getFiveInfoPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getFiveInfoPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getFiveInfoPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getFiveInfo(Map<String, String> info) {
        handlerLoading();
        mUserData.getFiveInfo(info, new Callback<FiveInfoBean>() {

            private FiveInfoBean mBody;

            @Override
            public void onResponse(Call<FiveInfoBean> call, Response<FiveInfoBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetFiveInfoCallback callback : mCallback) {
                            callback.onGetFiveInfoSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<FiveInfoBean> call, Throwable t) {
                for (IGetFiveInfoCallback callback : mCallback) {
                    callback.onGetFiveInfoError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetFiveInfoCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetFiveInfoCallback iGetFiveInfoCallback) {
        if (!mCallback.contains(iGetFiveInfoCallback)) {
            mCallback.add(iGetFiveInfoCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetFiveInfoCallback iGetFiveInfoCallback) {
        mCallback.remove(iGetFiveInfoCallback);
    }

}
