package com.twx.marryfriend.net.impl;

import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.FaceDetectBean;
import com.twx.marryfriend.net.callback.IDoAutoLoginCallback;
import com.twx.marryfriend.net.callback.IDoFaceDetectCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoAutoLoginPresent;
import com.twx.marryfriend.net.present.IDoFaceDetectPresent;

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
public class doFaceDetectPresentImpl implements IDoFaceDetectPresent {

    public static doFaceDetectPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoFaceDetectCallback> mCallback = new ArrayList<>();

    public doFaceDetectPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doFaceDetectPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doFaceDetectPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doFaceDetect(Map<String, String> info) {
        handlerLoading();
        mUserData.doFaceDetect(info, new Callback<FaceDetectBean>() {

            private FaceDetectBean mBody;

            @Override
            public void onResponse(Call<FaceDetectBean> call, Response<FaceDetectBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoFaceDetectCallback callback : mCallback) {
                            callback.onDoFaceDetectSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<FaceDetectBean> call, Throwable t) {

                ToastUtils.showShort("网络请求失败，请检查网络");

                Log.i("guo","error");
                Log.i("guo",t.toString());

                for (IDoFaceDetectCallback callback : mCallback) {
                    callback.onDoFaceDetectError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoFaceDetectCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoFaceDetectCallback iDoFaceDetectCallback) {
        if (!mCallback.contains(iDoFaceDetectCallback)) {
            mCallback.add(iDoFaceDetectCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoFaceDetectCallback iDoFaceDetectCallback) {
        mCallback.remove(iDoFaceDetectCallback);
    }

}
