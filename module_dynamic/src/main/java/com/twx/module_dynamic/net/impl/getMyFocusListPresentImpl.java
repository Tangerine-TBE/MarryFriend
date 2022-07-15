package com.twx.module_dynamic.net.impl;

import com.twx.module_dynamic.bean.MyFocusBean;
import com.twx.module_dynamic.bean.MyTrendsListBean;
import com.twx.module_dynamic.net.callback.IGetMyFocusListCallback;
import com.twx.module_dynamic.net.callback.IGetMyTrendsListCallback;
import com.twx.module_dynamic.net.module.UserData;
import com.twx.module_dynamic.net.present.IGetMyFocusListPresent;
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
public class getMyFocusListPresentImpl implements IGetMyFocusListPresent {

    public static getMyFocusListPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetMyFocusListCallback> mCallback = new ArrayList<>();

    public getMyFocusListPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getMyFocusListPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getMyFocusListPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getMyFocus(Map<String, String> info) {
        handlerLoading();
        mUserData.getMyFocus(info, new Callback<MyFocusBean>() {

            private MyFocusBean mBody;

            @Override
            public void onResponse(Call<MyFocusBean> call, Response<MyFocusBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetMyFocusListCallback callback : mCallback) {
                            callback.onGetMyFocusListSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MyFocusBean> call, Throwable t) {
                for (IGetMyFocusListCallback callback : mCallback) {
                    callback.onGetMyFocusListError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetMyFocusListCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetMyFocusListCallback iGetMyFocusListCallback) {
        if (!mCallback.contains(iGetMyFocusListCallback)) {
            mCallback.add(iGetMyFocusListCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetMyFocusListCallback iGetMyFocusListCallback) {
        mCallback.remove(iGetMyFocusListCallback);
    }

}
