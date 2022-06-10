package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.BaseInfoUpdateBean;
import com.twx.marryfriend.bean.UpdateMoreInfoBean;
import com.twx.marryfriend.net.callback.IDoUpdateBaseInfoCallback;
import com.twx.marryfriend.net.callback.IDoUpdateMoreInfoCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoUpdateMoreInfoPresent;

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
public class doUpdateMoreInfoPresentImpl implements IDoUpdateMoreInfoPresent {

    public static doUpdateMoreInfoPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoUpdateMoreInfoCallback> mCallback = new ArrayList<>();

    public doUpdateMoreInfoPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doUpdateMoreInfoPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doUpdateMoreInfoPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doUpdateMoreInfo(Map<String, String> info) {
        handlerLoading();
        mUserData.doUpdateMoreInfo(info, new Callback<UpdateMoreInfoBean>() {

            private UpdateMoreInfoBean mBody;

            @Override
            public void onResponse(Call<UpdateMoreInfoBean> call, Response<UpdateMoreInfoBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoUpdateMoreInfoCallback callback : mCallback) {
                            callback.onDoUpdateMoreInfoSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateMoreInfoBean> call, Throwable t) {
                for (IDoUpdateMoreInfoCallback callback : mCallback) {
                    callback.onDoUpdateMoreInfoError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoUpdateMoreInfoCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoUpdateMoreInfoCallback iDoUpdateMoreInfoCallback) {
        if (!mCallback.contains(iDoUpdateMoreInfoCallback)) {
            mCallback.add(iDoUpdateMoreInfoCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoUpdateMoreInfoCallback iDoUpdateMoreInfoCallback) {
        mCallback.remove(iDoUpdateMoreInfoCallback);
    }

}
