package com.twx.module_dynamic.net.impl;

import com.twx.module_dynamic.bean.DeleteTrendBean;
import com.twx.module_dynamic.bean.LikeClickBean;
import com.twx.module_dynamic.net.callback.IDoDeleteTrendCallback;
import com.twx.module_dynamic.net.callback.IDoLikeClickCallback;
import com.twx.module_dynamic.net.module.UserData;
import com.twx.module_dynamic.net.present.IDoDeleteTrendPresent;
import com.twx.module_dynamic.net.present.IDoLikeClickPresent;

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
public class doLikeClickPresentImpl implements IDoLikeClickPresent {

    public static doLikeClickPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoLikeClickCallback> mCallback = new ArrayList<>();

    public doLikeClickPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doLikeClickPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doLikeClickPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doLikeClick(Map<String, String> info) {
        handlerLoading();
        mUserData.doLikeClick(info, new Callback<LikeClickBean>() {

            private LikeClickBean mBody;

            @Override
            public void onResponse(Call<LikeClickBean> call, Response<LikeClickBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoLikeClickCallback callback : mCallback) {
                            callback.onDoLikeClickSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LikeClickBean> call, Throwable t) {
                for (IDoLikeClickCallback callback : mCallback) {
                    callback.onDoLikeClickError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoLikeClickCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoLikeClickCallback iDoLikeClickCallback) {
        if (!mCallback.contains(iDoLikeClickCallback)) {
            mCallback.add(iDoLikeClickCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoLikeClickCallback iDoLikeClickCallback) {
        mCallback.remove(iDoLikeClickCallback);
    }

}
