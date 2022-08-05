package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.UploadAvatarBean;
import com.twx.marryfriend.bean.UploadPhotoBean;
import com.twx.marryfriend.net.callback.IDoUploadAvatarCallback;
import com.twx.marryfriend.net.callback.IDoUploadPhotoCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoUploadAvatarPresent;
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
public class doUploadAvatarPresentImpl implements IDoUploadAvatarPresent {

    public static doUploadAvatarPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoUploadAvatarCallback> mCallback = new ArrayList<>();

    public doUploadAvatarPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doUploadAvatarPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doUploadAvatarPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doUploadAvatar(Map<String, String> info) {
        handlerLoading();
        mUserData.doUploadAvatar(info, new Callback<UploadAvatarBean>() {

            private UploadAvatarBean mBody;

            @Override
            public void onResponse(Call<UploadAvatarBean> call, Response<UploadAvatarBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoUploadAvatarCallback callback : mCallback) {
                            callback.onDoUploadAvatarSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UploadAvatarBean> call, Throwable t) {
                for (IDoUploadAvatarCallback callback : mCallback) {
                    callback.onDoUploadAvatarError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoUploadAvatarCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoUploadAvatarCallback iDoUploadAvatarCallback) {
        if (!mCallback.contains(iDoUploadAvatarCallback)) {
            mCallback.add(iDoUploadAvatarCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoUploadAvatarCallback iDoUploadAvatarCallback) {
        mCallback.remove(iDoUploadAvatarCallback);
    }

}
