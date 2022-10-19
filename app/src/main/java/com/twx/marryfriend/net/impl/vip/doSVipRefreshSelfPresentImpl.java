package com.twx.marryfriend.net.impl.vip;

import com.twx.marryfriend.bean.vip.RefreshSelfBean;
import com.twx.marryfriend.net.callback.vip.IDoSVipRefreshSelfCallback;
import com.twx.marryfriend.net.callback.vip.IDoVipRefreshSelfCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.vip.IDoSVipRefreshSelfPresent;
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
public class doSVipRefreshSelfPresentImpl implements IDoSVipRefreshSelfPresent {

    public static doSVipRefreshSelfPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoSVipRefreshSelfCallback> mCallback = new ArrayList<>();

    public doSVipRefreshSelfPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doSVipRefreshSelfPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doSVipRefreshSelfPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doSVipRefreshSelf(Map<String, String> info) {
        handlerLoading();
        mUserData.doSVipRefreshSelf(info, new Callback<RefreshSelfBean>() {

            private RefreshSelfBean mBody;

            @Override
            public void onResponse(Call<RefreshSelfBean> call, Response<RefreshSelfBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoSVipRefreshSelfCallback callback : mCallback) {
                            callback.onDoSVipRefreshSelfSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RefreshSelfBean> call, Throwable t) {
                for (IDoSVipRefreshSelfCallback callback : mCallback) {
                    callback.onDoSVipRefreshSelfError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoSVipRefreshSelfCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoSVipRefreshSelfCallback iDoSVipRefreshSelfCallback) {
        if (!mCallback.contains(iDoSVipRefreshSelfCallback)) {
            mCallback.add(iDoSVipRefreshSelfCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoSVipRefreshSelfCallback iDoSVipRefreshSelfCallback) {
        mCallback.remove(iDoSVipRefreshSelfCallback);
    }

}
