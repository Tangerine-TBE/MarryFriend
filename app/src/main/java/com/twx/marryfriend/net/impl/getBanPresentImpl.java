package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.BanBean;
import com.twx.marryfriend.bean.SchoolBean;
import com.twx.marryfriend.net.callback.IGetBanCallback;
import com.twx.marryfriend.net.callback.IGetSchoolCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IGetBanPresent;
import com.twx.marryfriend.net.present.IGetSchoolPresent;

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
public class getBanPresentImpl implements IGetBanPresent {

    public static getBanPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetBanCallback> mCallback = new ArrayList<>();

    public getBanPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getBanPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getBanPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getBan(Map<String, String> info) {
        handlerLoading();
        mUserData.getBan(info, new Callback<BanBean>() {

            private BanBean mBody;

            @Override
            public void onResponse(Call<BanBean> call, Response<BanBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetBanCallback callback : mCallback) {
                            callback.onGetBanSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BanBean> call, Throwable t) {
                for (IGetBanCallback callback : mCallback) {
                    callback.onGetBanCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetBanCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetBanCallback iGetBanCallback) {
        if (!mCallback.contains(iGetBanCallback)) {
            mCallback.add(iGetBanCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetBanCallback iGetBanCallback) {
        mCallback.remove(iGetBanCallback);
    }

}
