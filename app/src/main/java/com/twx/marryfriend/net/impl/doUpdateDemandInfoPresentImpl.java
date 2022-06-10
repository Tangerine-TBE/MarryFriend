package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.UpdateDemandInfoBean;
import com.twx.marryfriend.net.callback.IDoUpdateDemandInfoCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoUpdateDemandInfoPresent;

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
public class doUpdateDemandInfoPresentImpl implements IDoUpdateDemandInfoPresent {

    public static doUpdateDemandInfoPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoUpdateDemandInfoCallback> mCallback = new ArrayList<>();

    public doUpdateDemandInfoPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doUpdateDemandInfoPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doUpdateDemandInfoPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doUpdateDemandInfo(Map<String, String> info) {
        handlerLoading();
        mUserData.doUpdateDemandInfo(info, new Callback<UpdateDemandInfoBean>() {

            private UpdateDemandInfoBean mBody;

            @Override
            public void onResponse(Call<UpdateDemandInfoBean> call, Response<UpdateDemandInfoBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoUpdateDemandInfoCallback callback : mCallback) {
                            callback.onDoUpdateDemandInfoSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateDemandInfoBean> call, Throwable t) {
                for (IDoUpdateDemandInfoCallback callback : mCallback) {
                    callback.onDoUpdateDemandInfoError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoUpdateDemandInfoCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoUpdateDemandInfoCallback iDoUpdateDemandInfoCallback) {
        if (!mCallback.contains(iDoUpdateDemandInfoCallback)) {
            mCallback.add(iDoUpdateDemandInfoCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoUpdateDemandInfoCallback iDoUpdateDemandInfoCallback) {
        mCallback.remove(iDoUpdateDemandInfoCallback);
    }

}
