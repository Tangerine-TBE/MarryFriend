package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.PhotoListBean;
import com.twx.marryfriend.bean.UploadPhotoBean;
import com.twx.marryfriend.net.callback.IDoUploadPhotoCallback;
import com.twx.marryfriend.net.callback.IGetPhotoListCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IDoUploadPhotoPresent;
import com.twx.marryfriend.net.present.IGetPhotoListPresent;

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
public class getPhotoListPresentImpl implements IGetPhotoListPresent {

    public static getPhotoListPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetPhotoListCallback> mCallback = new ArrayList<>();

    public getPhotoListPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getPhotoListPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getPhotoListPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getPhotoList(Map<String, String> info) {
        handlerLoading();
        mUserData.getPhotoList(info ,new Callback<PhotoListBean>() {

            private PhotoListBean mBody;

            @Override
            public void onResponse(Call<PhotoListBean> call, Response<PhotoListBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetPhotoListCallback callback : mCallback) {
                            callback.onGetPhotoListSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PhotoListBean> call, Throwable t) {
                for (IGetPhotoListCallback callback : mCallback) {
                    callback.onGetPhotoListError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetPhotoListCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetPhotoListCallback iGetPhotoListCallback) {
        if (!mCallback.contains(iGetPhotoListCallback)) {
            mCallback.add(iGetPhotoListCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetPhotoListCallback iGetPhotoListCallback) {
        mCallback.remove(iGetPhotoListCallback);
    }

}
