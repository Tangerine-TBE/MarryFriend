package com.twx.marryfriend.net.impl.vip;

import com.twx.marryfriend.bean.vip.RefreshSelfBean;
import com.twx.marryfriend.net.callback.vip.IDoRefreshSelfCallback;
import com.twx.marryfriend.net.callback.vip.IDoVipRefreshSelfCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.vip.IDoRefreshSelfPresent;
import com.twx.marryfriend.net.present.vip.IDoVipRefreshSelfPresent;

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
public class doVipRefreshSelfPresentImpl implements IDoVipRefreshSelfPresent {

    public static doVipRefreshSelfPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoVipRefreshSelfCallback> mCallback = new ArrayList<>();

    public doVipRefreshSelfPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doVipRefreshSelfPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doVipRefreshSelfPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doVipRefreshSelf(Map<String, String> info) {
        handlerLoading();
        mUserData.doRefreshSelf(info, new Callback<RefreshSelfBean>() {

            private RefreshSelfBean mBody;

            @Override
            public void onResponse(Call<RefreshSelfBean> call, Response<RefreshSelfBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoVipRefreshSelfCallback callback : mCallback) {
                            callback.onDoVipRefreshSelfSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RefreshSelfBean> call, Throwable t) {
                for (IDoVipRefreshSelfCallback callback : mCallback) {
                    callback.onDoVipRefreshSelfError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoVipRefreshSelfCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoVipRefreshSelfCallback iDoVipRefreshSelfCallback) {
        if (!mCallback.contains(iDoVipRefreshSelfCallback)) {
            mCallback.add(iDoVipRefreshSelfCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoVipRefreshSelfCallback iDoVipRefreshSelfCallback) {
        mCallback.remove(iDoVipRefreshSelfCallback);
    }

}
