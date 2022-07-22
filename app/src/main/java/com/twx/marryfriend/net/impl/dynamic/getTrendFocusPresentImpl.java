package com.twx.marryfriend.net.impl.dynamic;

import com.twx.marryfriend.bean.dynamic.TrendFocusBean;
import com.twx.marryfriend.net.callback.dynamic.IGetTrendFocusCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.dynamic.IGetTrendFocusPresent;

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
public class getTrendFocusPresentImpl implements IGetTrendFocusPresent {

    public static getTrendFocusPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetTrendFocusCallback> mCallback = new ArrayList<>();

    public getTrendFocusPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getTrendFocusPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getTrendFocusPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getTrendFocus(Map<String, String> info) {
        handlerLoading();
        mUserData.getTrendFocus(info, new Callback<TrendFocusBean>() {

            private TrendFocusBean mBody;

            @Override
            public void onResponse(Call<TrendFocusBean> call, Response<TrendFocusBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetTrendFocusCallback callback : mCallback) {
                            callback.onGetTrendFocusSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TrendFocusBean> call, Throwable t) {
                for (IGetTrendFocusCallback callback : mCallback) {
                    callback.onGetTrendFocusError();
                }
            }
        });
    }

    private void handlerLoading() {
        for (IGetTrendFocusCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetTrendFocusCallback iGetTrendFocusCallback) {
        if (!mCallback.contains(iGetTrendFocusCallback)) {
            mCallback.add(iGetTrendFocusCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetTrendFocusCallback iGetTrendFocusCallback) {
        mCallback.remove(iGetTrendFocusCallback);
    }

}
