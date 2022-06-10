package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.SchoolBean;
import com.twx.marryfriend.bean.VerifyCodeBean;
import com.twx.marryfriend.net.callback.IGetSchoolCallback;
import com.twx.marryfriend.net.callback.IGetVerifyCodeCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IGetSchoolPresent;
import com.twx.marryfriend.net.present.IGetVerifyCodePresent;

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
public class getSchoolPresentImpl implements IGetSchoolPresent {

    public static getSchoolPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetSchoolCallback> mCallback = new ArrayList<>();

    public getSchoolPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getSchoolPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getSchoolPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getSchool(Map<String, String> info) {
        handlerLoading();
        mUserData.getSchool(info, new Callback<SchoolBean>() {

            private SchoolBean mBody;

            @Override
            public void onResponse(Call<SchoolBean> call, Response<SchoolBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetSchoolCallback callback : mCallback) {
                            callback.onGetSchoolSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SchoolBean> call, Throwable t) {
                for (IGetSchoolCallback callback : mCallback) {
                    callback.onGetSchoolCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetSchoolCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetSchoolCallback iGetSchoolCallback) {
        if (!mCallback.contains(iGetSchoolCallback)) {
            mCallback.add(iGetSchoolCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetSchoolCallback iGetSchoolCallback) {
        mCallback.remove(iGetSchoolCallback);
    }

}
