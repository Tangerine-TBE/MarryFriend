package com.twx.marryfriend.net.impl.vip;

import com.twx.marryfriend.bean.vip.AliPayBean;
import com.twx.marryfriend.bean.vip.RefreshSelfBean;
import com.twx.marryfriend.net.callback.vip.IDoAliPayCallback;
import com.twx.marryfriend.net.callback.vip.IDoRefreshSelfCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.vip.IDoAliPayPresent;
import com.twx.marryfriend.net.present.vip.IDoRefreshSelfPresent;

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
public class doRefreshSelfPresentImpl implements IDoRefreshSelfPresent {

    public static doRefreshSelfPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoRefreshSelfCallback> mCallback = new ArrayList<>();

    public doRefreshSelfPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doRefreshSelfPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doRefreshSelfPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doRefreshSelf(Map<String, String> info) {
        handlerLoading();
        mUserData.doRefreshSelf(info, new Callback<RefreshSelfBean>() {

            private RefreshSelfBean mBody;

            @Override
            public void onResponse(Call<RefreshSelfBean> call, Response<RefreshSelfBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoRefreshSelfCallback callback : mCallback) {
                            callback.onDoRefreshSelfSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RefreshSelfBean> call, Throwable t) {
                for (IDoRefreshSelfCallback callback : mCallback) {
                    callback.onDoRefreshSelfError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoRefreshSelfCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoRefreshSelfCallback iDoRefreshSelfCallback) {
        if (!mCallback.contains(iDoRefreshSelfCallback)) {
            mCallback.add(iDoRefreshSelfCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoRefreshSelfCallback iDoRefreshSelfCallback) {
        mCallback.remove(iDoRefreshSelfCallback);
    }

}
