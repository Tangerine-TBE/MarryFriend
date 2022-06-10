package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.UpdateMoreInfoBean;
import com.twx.marryfriend.bean.UpdateVerifyInfoBean;
import com.twx.marryfriend.net.callback.IDoUpdateMoreInfoCallback;
import com.twx.marryfriend.net.callback.IDoUpdateVerifyInfoCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoUpdateMoreInfoPresent;
import com.twx.marryfriend.net.present.IDoUpdateVerifyInfoPresent;

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
public class doUpdateVerifyInfoPresentImpl implements IDoUpdateVerifyInfoPresent {

    public static doUpdateVerifyInfoPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoUpdateVerifyInfoCallback> mCallback = new ArrayList<>();

    public doUpdateVerifyInfoPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doUpdateVerifyInfoPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doUpdateVerifyInfoPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doUpdateVerifyInfo(Map<String, String> info) {
        handlerLoading();
        mUserData.doUpdateVerifyInfo(info, new Callback<UpdateVerifyInfoBean>() {

            private UpdateVerifyInfoBean mBody;

            @Override
            public void onResponse(Call<UpdateVerifyInfoBean> call, Response<UpdateVerifyInfoBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoUpdateVerifyInfoCallback callback : mCallback) {
                            callback.onDoUpdateVerifyInfoSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateVerifyInfoBean> call, Throwable t) {
                for (IDoUpdateVerifyInfoCallback callback : mCallback) {
                    callback.onDoUpdateVerifyInfoError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoUpdateVerifyInfoCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoUpdateVerifyInfoCallback iDoUpdateVerifyInfoCallback) {
        if (!mCallback.contains(iDoUpdateVerifyInfoCallback)) {
            mCallback.add(iDoUpdateVerifyInfoCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoUpdateVerifyInfoCallback iDoUpdateVerifyInfoCallback) {
        mCallback.remove(iDoUpdateVerifyInfoCallback);
    }

}
