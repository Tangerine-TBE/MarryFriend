package com.twx.module_dynamic.net.impl;

import com.twx.module_dynamic.bean.LikeCancelBean;
import com.twx.module_dynamic.bean.LikeClickBean;
import com.twx.module_dynamic.net.callback.IDoLikeCancelCallback;
import com.twx.module_dynamic.net.callback.IDoLikeClickCallback;
import com.twx.module_dynamic.net.module.UserData;
import com.twx.module_dynamic.net.present.IDoLikeCancelPresent;
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
public class doLikeCancelPresentImpl implements IDoLikeCancelPresent {

    public static doLikeCancelPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoLikeCancelCallback> mCallback = new ArrayList<>();

    public doLikeCancelPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doLikeCancelPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doLikeCancelPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doLikeCancel(Map<String, String> info) {
        handlerLoading();
        mUserData.doLikeCancel(info, new Callback<LikeCancelBean>() {

            private LikeCancelBean mBody;

            @Override
            public void onResponse(Call<LikeCancelBean> call, Response<LikeCancelBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoLikeCancelCallback callback : mCallback) {
                            callback.onDoLikeCancelSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LikeCancelBean> call, Throwable t) {
                for (IDoLikeCancelCallback callback : mCallback) {
                    callback.onLikeCancelError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoLikeCancelCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoLikeCancelCallback iDoLikeCancelCallback) {
        if (!mCallback.contains(iDoLikeCancelCallback)) {
            mCallback.add(iDoLikeCancelCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoLikeCancelCallback iDoLikeCancelCallback) {
        mCallback.remove(iDoLikeCancelCallback);
    }

}
