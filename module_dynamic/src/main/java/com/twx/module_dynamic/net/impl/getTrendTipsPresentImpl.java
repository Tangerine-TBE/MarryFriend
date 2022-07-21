package com.twx.module_dynamic.net.impl;

import com.twx.module_dynamic.bean.TrendSaloonBean;
import com.twx.module_dynamic.bean.TrendTipBean;
import com.twx.module_dynamic.net.callback.IGetTrendSaloonCallback;
import com.twx.module_dynamic.net.callback.IGetTrendTipsCallback;
import com.twx.module_dynamic.net.module.UserData;
import com.twx.module_dynamic.net.present.IGetTrendSaloonPresent;
import com.twx.module_dynamic.net.present.IGetTrendTipsPresent;

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
public class getTrendTipsPresentImpl implements IGetTrendTipsPresent {

    public static getTrendTipsPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetTrendTipsCallback> mCallback = new ArrayList<>();

    public getTrendTipsPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getTrendTipsPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getTrendTipsPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getTrendTips(Map<String, String> info,Integer page) {
        handlerLoading();
        mUserData.getTrendTips(info,page, new Callback<TrendTipBean>() {

            private TrendTipBean mBody;

            @Override
            public void onResponse(Call<TrendTipBean> call, Response<TrendTipBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetTrendTipsCallback callback : mCallback) {
                            callback.onGetTrendTipsSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TrendTipBean> call, Throwable t) {
                for (IGetTrendTipsCallback callback : mCallback) {
                    callback.onGetTrendTipsError();
                }
            }
        });
    }

    private void handlerLoading() {
        for (IGetTrendTipsCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetTrendTipsCallback iGetTrendTipsCallback) {
        if (!mCallback.contains(iGetTrendTipsCallback)) {
            mCallback.add(iGetTrendTipsCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetTrendTipsCallback iGetTrendTipsCallback) {
        mCallback.remove(iGetTrendTipsCallback);
    }


}
