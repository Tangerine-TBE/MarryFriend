package com.twx.module_dynamic.net.impl;

import com.twx.module_dynamic.bean.MyTrendsListBean;
import com.twx.module_dynamic.net.callback.IGetMyTrendsListCallback;
import com.twx.module_dynamic.net.module.UserData;
import com.twx.module_dynamic.net.present.IGetMyTrendsListPresent;

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
public class getMyTrendsListPresentImpl implements IGetMyTrendsListPresent {

    public static getMyTrendsListPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetMyTrendsListCallback> mCallback = new ArrayList<>();

    public getMyTrendsListPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getMyTrendsListPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getMyTrendsListPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getMyTrendsList(Map<String, String> info, Integer page, Integer size) {
        handlerLoading();
        mUserData.getMyTrendsList(info, page, size, new Callback<MyTrendsListBean>() {

            private MyTrendsListBean mBody;

            @Override
            public void onResponse(Call<MyTrendsListBean> call, Response<MyTrendsListBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetMyTrendsListCallback callback : mCallback) {
                            callback.onGetMyTrendsListSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MyTrendsListBean> call, Throwable t) {
                for (IGetMyTrendsListCallback callback : mCallback) {
                    callback.onGetMyTrendsListCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetMyTrendsListCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetMyTrendsListCallback iGetMyTrendsListCallback) {
        if (!mCallback.contains(iGetMyTrendsListCallback)) {
            mCallback.add(iGetMyTrendsListCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetMyTrendsListCallback iGetMyTrendsListCallback) {
        mCallback.remove(iGetMyTrendsListCallback);
    }

}
