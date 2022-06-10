package com.twx.marryfriend.net.impl;

import android.util.Log;

import com.twx.marryfriend.bean.JobBean;
import com.twx.marryfriend.net.callback.IGetJobCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IGetJobPresent;

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
public class getJobPresentImpl implements IGetJobPresent {

    public static getJobPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetJobCallback> mCallback = new ArrayList<>();

    public getJobPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getJobPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getJobPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getJob(Map<String, String> info) {
        handlerLoading();
        mUserData.getJob(info, new Callback<JobBean>() {

            private JobBean mBody;

            @Override
            public void onResponse(Call<JobBean> call, Response<JobBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    Log.i("guo","success");
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetJobCallback callback : mCallback) {
                            callback.onGetJobSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JobBean> call, Throwable t) {
                Log.i("guo",t.toString());
                for (IGetJobCallback callback : mCallback) {
                    callback.onGetJobError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetJobCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetJobCallback iGetJobCallback) {
        if (!mCallback.contains(iGetJobCallback)) {
            mCallback.add(iGetJobCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetJobCallback iGetJobCallback) {
        mCallback.remove(iGetJobCallback);
    }

}
