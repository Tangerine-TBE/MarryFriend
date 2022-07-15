package com.twx.module_dynamic.net.impl;

import com.twx.module_dynamic.bean.CommentOneBean;
import com.twx.module_dynamic.bean.TrendSaloonBean;
import com.twx.module_dynamic.net.callback.IGetCommentOneCallback;
import com.twx.module_dynamic.net.callback.IGetTrendSaloonCallback;
import com.twx.module_dynamic.net.module.UserData;
import com.twx.module_dynamic.net.present.IGetCommentOnePresent;
import com.twx.module_dynamic.net.present.IGetTrendSaloonPresent;

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
public class getTrendSaloonPresentImpl implements IGetTrendSaloonPresent {

    public static getTrendSaloonPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetTrendSaloonCallback> mCallback = new ArrayList<>();

    public getTrendSaloonPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getTrendSaloonPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getTrendSaloonPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getTrendSaloon(Map<String, String> info) {
        handlerLoading();
        mUserData.getTrendSaloon(info, new Callback<TrendSaloonBean>() {

            private TrendSaloonBean mBody;

            @Override
            public void onResponse(Call<TrendSaloonBean> call, Response<TrendSaloonBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetTrendSaloonCallback callback : mCallback) {
                            callback.onGetTrendSaloonSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TrendSaloonBean> call, Throwable t) {
                for (IGetTrendSaloonCallback callback : mCallback) {
                    callback.onGetTrendSaloonError();
                }
            }
        });
    }

    private void handlerLoading() {
        for (IGetTrendSaloonCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetTrendSaloonCallback iGetTrendSaloonCallback) {
        if (!mCallback.contains(iGetTrendSaloonCallback)) {
            mCallback.add(iGetTrendSaloonCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetTrendSaloonCallback iGetTrendSaloonCallback) {
        mCallback.remove(iGetTrendSaloonCallback);
    }

}
