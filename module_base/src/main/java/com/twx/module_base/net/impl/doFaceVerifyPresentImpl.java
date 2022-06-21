package com.twx.module_base.net.impl;

import com.twx.module_base.net.bean.FaceVerifyBean;
import com.twx.module_base.net.callback.IDoFaceVerifyCallback;
import com.twx.module_base.net.module.UserData;
import com.twx.module_base.net.present.IDoFaceVerifyPresent;

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
public class doFaceVerifyPresentImpl implements IDoFaceVerifyPresent {

    public static doFaceVerifyPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoFaceVerifyCallback> mCallback = new ArrayList<>();

    public doFaceVerifyPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doFaceVerifyPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doFaceVerifyPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doFaceVerify(Map<String, String> info) {
        handlerLoading();
        mUserData.doFaceVerify(info, new Callback<FaceVerifyBean>() {

            private FaceVerifyBean mBody;

            @Override
            public void onResponse(Call<FaceVerifyBean> call, Response<FaceVerifyBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoFaceVerifyCallback callback : mCallback) {
                            callback.onDoFaceVerifySuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<FaceVerifyBean> call, Throwable t) {
                for (IDoFaceVerifyCallback callback : mCallback) {
                    callback.onDoFaceVerifyError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoFaceVerifyCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoFaceVerifyCallback iDoFaceVerifyCallback) {
        if (!mCallback.contains(iDoFaceVerifyCallback)) {
            mCallback.add(iDoFaceVerifyCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoFaceVerifyCallback iDoFaceVerifyCallback) {
        mCallback.remove(iDoFaceVerifyCallback);
    }

}
