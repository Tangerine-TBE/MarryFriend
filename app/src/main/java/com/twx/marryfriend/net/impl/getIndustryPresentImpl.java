package com.twx.marryfriend.net.impl;

import com.twx.marryfriend.bean.IndustryBean;
import com.twx.marryfriend.net.callback.IGetIndustryCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IGetIndustryPresent;

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
public class getIndustryPresentImpl implements IGetIndustryPresent {

    public static getIndustryPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetIndustryCallback> mCallback = new ArrayList<>();

    public getIndustryPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getIndustryPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getIndustryPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getIndustry(Map<String, String> info) {
        handlerLoading();
        mUserData.getIndustry(info, new Callback<IndustryBean>() {

            private IndustryBean mBody;

            @Override
            public void onResponse(Call<IndustryBean> call, Response<IndustryBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetIndustryCallback callback : mCallback) {
                            callback.onGetIndustrySuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<IndustryBean> call, Throwable t) {
                for (IGetIndustryCallback callback : mCallback) {
                    callback.onGetIndustryError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetIndustryCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetIndustryCallback iGetIndustryCallback) {
        if (!mCallback.contains(iGetIndustryCallback)) {
            mCallback.add(iGetIndustryCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetIndustryCallback iGetIndustryCallback) {
        mCallback.remove(iGetIndustryCallback);
    }

}
