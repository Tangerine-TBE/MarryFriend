package com.twx.module_dynamic.net.impl;

import com.twx.module_dynamic.bean.SearchBean;
import com.twx.module_dynamic.net.callback.IBaiduSearchCallback;
import com.twx.module_dynamic.net.module.UserData;
import com.twx.module_dynamic.net.present.IBaiduSearchPresent;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author: Administrator
 * @date: 2021/12/22
 */
public class BaiduSearchPresentImpl implements IBaiduSearchPresent {
    private static BaiduSearchPresentImpl sInstance;
    private final UserData mUserDate;

    private List<IBaiduSearchCallback> mCallback = new ArrayList<>();

    public static BaiduSearchPresentImpl getInstance() {
        if (sInstance == null) {
            sInstance = new BaiduSearchPresentImpl();
        }
        return sInstance;
    }

    public BaiduSearchPresentImpl() {
        this.mUserDate = UserData.getInstance();
    }


    @Override
    public void doBaiduSearch(Map<String, String> info) {
        handlerLoading();
        mUserDate.doSearch(info, new Callback<SearchBean>() {
            private SearchBean mBody;

            @Override
            public void onResponse(Call<SearchBean> call, Response<SearchBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IBaiduSearchCallback callback : mCallback) {
                            callback.onBaiduSearchSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchBean> call, Throwable t) {
                for (IBaiduSearchCallback callback : mCallback) {
                    callback.onBaiduSearchError();
                }
            }
        });
    }

    private void handlerLoading() {
        for (IBaiduSearchCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IBaiduSearchCallback iBaiduSearchCallback) {
        if (!mCallback.contains(iBaiduSearchCallback)) {
            mCallback.add(iBaiduSearchCallback);
        }
    }

    @Override
    public void unregisterCallback(IBaiduSearchCallback iBaiduSearchCallback) {

    }
}
