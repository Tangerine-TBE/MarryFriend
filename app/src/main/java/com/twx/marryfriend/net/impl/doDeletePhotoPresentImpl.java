package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.DeletePhotoBean;
import com.twx.marryfriend.net.callback.IDoAutoLoginCallback;
import com.twx.marryfriend.net.callback.IDoDeletePhotoCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoAutoLoginPresent;
import com.twx.marryfriend.net.present.IDoDeletePhotoPresent;

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
public class doDeletePhotoPresentImpl implements IDoDeletePhotoPresent {

    public static doDeletePhotoPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoDeletePhotoCallback> mCallback = new ArrayList<>();

    public doDeletePhotoPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doDeletePhotoPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doDeletePhotoPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doDeletePhoto(Map<String, String> info) {
        handlerLoading();
        mUserData.doDeletePhoto(info, new Callback<DeletePhotoBean>() {

            private DeletePhotoBean mBody;

            @Override
            public void onResponse(Call<DeletePhotoBean> call, Response<DeletePhotoBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoDeletePhotoCallback callback : mCallback) {
                            callback.onDoDeletePhotoSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DeletePhotoBean> call, Throwable t) {
                for (IDoDeletePhotoCallback callback : mCallback) {
                    callback.onDoDeletePhotoError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoDeletePhotoCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoDeletePhotoCallback iDoDeletePhotoCallback) {
        if (!mCallback.contains(iDoDeletePhotoCallback)) {
            mCallback.add(iDoDeletePhotoCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoDeletePhotoCallback iDoDeletePhotoCallback) {
        mCallback.remove(iDoDeletePhotoCallback);
    }

}
