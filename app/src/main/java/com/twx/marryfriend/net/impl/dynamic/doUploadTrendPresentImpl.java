package com.twx.marryfriend.net.impl.dynamic;

import com.twx.marryfriend.bean.dynamic.UploadTrendBean;
import com.twx.marryfriend.net.callback.dynamic.IDoUploadTrendCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.dynamic.IDoUploadTrendPresent;

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
public class doUploadTrendPresentImpl implements IDoUploadTrendPresent {

    public static doUploadTrendPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoUploadTrendCallback> mCallback = new ArrayList<>();

    public doUploadTrendPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doUploadTrendPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doUploadTrendPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doUploadTrend(Map<String, String> info) {
        handlerLoading();
        mUserData.doUploadTrend(info, new Callback<UploadTrendBean>() {

            private UploadTrendBean mBody;

            @Override
            public void onResponse(Call<UploadTrendBean> call, Response<UploadTrendBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoUploadTrendCallback callback : mCallback) {
                            callback.onDoUploadTrendSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UploadTrendBean> call, Throwable t) {
                for (IDoUploadTrendCallback callback : mCallback) {
                    callback.onDoUploadTrendError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoUploadTrendCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoUploadTrendCallback iDoUploadTrendCallback) {
        if (!mCallback.contains(iDoUploadTrendCallback)) {
            mCallback.add(iDoUploadTrendCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoUploadTrendCallback iDoUploadTrendCallback) {
        mCallback.remove(iDoUploadTrendCallback);
    }

}
