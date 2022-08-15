package com.twx.marryfriend.net.impl;

import android.util.Log;

import com.twx.marryfriend.bean.MeSeeWhoBean;
import com.twx.marryfriend.bean.WhoSeeMeBean;
import com.twx.marryfriend.net.callback.IGetMeSeeWhoCallback;
import com.twx.marryfriend.net.callback.IGetWhoSeeMeCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IGetMeSeeWhoPresent;
import com.twx.marryfriend.net.present.IGetWhoSeeMePresent;

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
public class getMeSeeWhoPresentImpl implements IGetMeSeeWhoPresent {

    public static getMeSeeWhoPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetMeSeeWhoCallback> mCallback = new ArrayList<>();

    public getMeSeeWhoPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getMeSeeWhoPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getMeSeeWhoPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getMeSeeWho(Map<String, String> info) {
        handlerLoading();
        mUserData.getMeSeeWho(info, new Callback<MeSeeWhoBean>() {

            private MeSeeWhoBean mBody;

            @Override
            public void onResponse(Call<MeSeeWhoBean> call, Response<MeSeeWhoBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetMeSeeWhoCallback callback : mCallback) {
                            callback.onGetMeSeeWhoSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MeSeeWhoBean> call, Throwable t) {
                Log.i("guo", "erroe: " + t);
                for (IGetMeSeeWhoCallback callback : mCallback) {
                    callback.onGetMeSeeWhoCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetMeSeeWhoCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetMeSeeWhoCallback iGetMeSeeWhoCallback) {
        if (!mCallback.contains(iGetMeSeeWhoCallback)) {
            mCallback.add(iGetMeSeeWhoCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetMeSeeWhoCallback iGetMeSeeWhoCallback) {
        mCallback.remove(iGetMeSeeWhoCallback);
    }


}
