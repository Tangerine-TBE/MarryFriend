package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.FaceDetectBean;
import com.twx.marryfriend.bean.FaceVerifyBean;
import com.twx.marryfriend.net.callback.IDoLifeFaceDetectCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoLifeFaceDetectPresent;

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
public class doLifeFaceDetectPresentImpl implements IDoLifeFaceDetectPresent {

    public static doLifeFaceDetectPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoLifeFaceDetectCallback> mCallback = new ArrayList<>();

    public doLifeFaceDetectPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doLifeFaceDetectPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doLifeFaceDetectPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doLifeFaceDetect(Map<String, String> info) {
        handlerLoading();
        mUserData.doFaceDetect(info, new Callback<FaceDetectBean>() {

            private FaceDetectBean mBody;

            @Override
            public void onResponse(Call<FaceDetectBean> call, Response<FaceDetectBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoLifeFaceDetectCallback callback : mCallback) {
                            callback.onDoLifeFaceDetectSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<FaceDetectBean> call, Throwable t) {
                for (IDoLifeFaceDetectCallback callback : mCallback) {
                    callback.onDoLifeFaceDetectError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoLifeFaceDetectCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoLifeFaceDetectCallback iDoLifeFaceDetectCallback) {
        if (!mCallback.contains(iDoLifeFaceDetectCallback)) {
            mCallback.add(iDoLifeFaceDetectCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoLifeFaceDetectCallback iDoLifeFaceDetectCallback) {
        mCallback.remove(iDoLifeFaceDetectCallback);
    }

}
