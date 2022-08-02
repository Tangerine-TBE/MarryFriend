package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.ViewHeadfaceBean;
import com.twx.marryfriend.net.callback.IDoAutoLoginCallback;
import com.twx.marryfriend.net.callback.IDoViewHeadFaceCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoAutoLoginPresent;
import com.twx.marryfriend.net.present.IDoViewHeadFacePresent;

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
public class doViewHeadFacePresentImpl implements IDoViewHeadFacePresent {

    public static doViewHeadFacePresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoViewHeadFaceCallback> mCallback = new ArrayList<>();

    public doViewHeadFacePresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doViewHeadFacePresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doViewHeadFacePresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doViewHeadFace(Map<String, String> info) {
        handlerLoading();
        mUserData.doViewHeadface(info, new Callback<ViewHeadfaceBean>() {

            private ViewHeadfaceBean mBody;

            @Override
            public void onResponse(Call<ViewHeadfaceBean> call, Response<ViewHeadfaceBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoViewHeadFaceCallback callback : mCallback) {
                            callback.onDoViewHeadFaceSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ViewHeadfaceBean> call, Throwable t) {
                for (IDoViewHeadFaceCallback callback : mCallback) {
                    callback.onDoViewHeadFaceError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoViewHeadFaceCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoViewHeadFaceCallback iDoViewHeadFaceCallback) {
        if (!mCallback.contains(iDoViewHeadFaceCallback)) {
            mCallback.add(iDoViewHeadFaceCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoViewHeadFaceCallback iDoViewHeadFaceCallback) {
        mCallback.remove(iDoViewHeadFaceCallback);
    }

}
