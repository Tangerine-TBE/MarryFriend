package com.twx.marryfriend.net.impl;

import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.twx.marryfriend.bean.FaceDetectBean;
import com.twx.marryfriend.net.callback.IDoFaceDetectCallback;
import com.twx.marryfriend.net.callback.vip.IDoDataFaceDetectCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoFaceDetectPresent;
import com.twx.marryfriend.net.present.vip.IDoDataFaceDetectPresent;

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
public class doDataFaceDetectPresentImpl implements IDoDataFaceDetectPresent {

    public static doDataFaceDetectPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoDataFaceDetectCallback> mCallback = new ArrayList<>();

    public doDataFaceDetectPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doDataFaceDetectPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doDataFaceDetectPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doDataFaceDetect(Map<String, String> info) {
        handlerLoading();
        mUserData.doFaceDetect(info, new Callback<FaceDetectBean>() {

            private FaceDetectBean mBody;

            @Override
            public void onResponse(Call<FaceDetectBean> call, Response<FaceDetectBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoDataFaceDetectCallback callback : mCallback) {
                            callback.onDoDataFaceDetectSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<FaceDetectBean> call, Throwable t) {

                ToastUtils.showShort("网络请求失败，请检查网络");

                Log.i("guo", "error");
                Log.i("guo", t.toString());

                for (IDoDataFaceDetectCallback callback : mCallback) {
                    callback.onDoDataFaceDetectError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoDataFaceDetectCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoDataFaceDetectCallback iDoDataFaceDetectCallback) {
        if (!mCallback.contains(iDoDataFaceDetectCallback)) {
            mCallback.add(iDoDataFaceDetectCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoDataFaceDetectCallback iDoDataFaceDetectCallback) {
        mCallback.remove(iDoDataFaceDetectCallback);
    }

}
