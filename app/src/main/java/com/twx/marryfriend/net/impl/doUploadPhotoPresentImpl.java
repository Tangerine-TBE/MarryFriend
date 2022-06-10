package com.twx.marryfriend.net.impl;

import androidx.annotation.AnyRes;

import com.twx.marryfriend.bean.BaseInfoUpdateBean;
import com.twx.marryfriend.bean.UploadPhotoBean;
import com.twx.marryfriend.net.callback.IDoUpdateBaseInfoCallback;
import com.twx.marryfriend.net.callback.IDoUploadPhotoCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoUpdateBaseInfoPresent;
import com.twx.marryfriend.net.present.IDoUploadPhotoPresent;

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
public class doUploadPhotoPresentImpl implements IDoUploadPhotoPresent {

    public static doUploadPhotoPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoUploadPhotoCallback> mCallback = new ArrayList<>();

    public doUploadPhotoPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doUploadPhotoPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doUploadPhotoPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doUploadPhoto(Map<String, String> info) {
        handlerLoading();
        mUserData.doUploadPhoto(info ,new Callback<UploadPhotoBean>() {

            private UploadPhotoBean mBody;

            @Override
            public void onResponse(Call<UploadPhotoBean> call, Response<UploadPhotoBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoUploadPhotoCallback callback : mCallback) {
                            callback.onDoUploadPhotoSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UploadPhotoBean> call, Throwable t) {
                for (IDoUploadPhotoCallback callback : mCallback) {
                    callback.onDoUploadPhotoError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoUploadPhotoCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoUploadPhotoCallback iDoUploadPhotoCallback) {
        if (!mCallback.contains(iDoUploadPhotoCallback)) {
            mCallback.add(iDoUploadPhotoCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoUploadPhotoCallback iDoUploadPhotoCallback) {
        mCallback.remove(iDoUploadPhotoCallback);
    }

}
