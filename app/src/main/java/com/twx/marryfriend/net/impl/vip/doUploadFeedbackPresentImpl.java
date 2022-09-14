package com.twx.marryfriend.net.impl.vip;

import com.twx.marryfriend.bean.vip.RefreshSelfBean;
import com.twx.marryfriend.bean.vip.UploadFeedbackBean;
import com.twx.marryfriend.net.callback.vip.IDoRefreshSelfCallback;
import com.twx.marryfriend.net.callback.vip.IDoUploadFeedbackCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.vip.IDoRefreshSelfPresent;
import com.twx.marryfriend.net.present.vip.IDoUploadFeedbackPresent;

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
public class doUploadFeedbackPresentImpl implements IDoUploadFeedbackPresent {

    public static doUploadFeedbackPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoUploadFeedbackCallback> mCallback = new ArrayList<>();

    public doUploadFeedbackPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doUploadFeedbackPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doUploadFeedbackPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doUploadFeedback(Map<String, String> info) {
        handlerLoading();
        mUserData.doUploadFeedback(info, new Callback<UploadFeedbackBean>() {

            private UploadFeedbackBean mBody;

            @Override
            public void onResponse(Call<UploadFeedbackBean> call, Response<UploadFeedbackBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoUploadFeedbackCallback callback : mCallback) {
                            callback.onDoUploadFeedbackSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UploadFeedbackBean> call, Throwable t) {
                for (IDoUploadFeedbackCallback callback : mCallback) {
                    callback.onDoUploadFeedbackError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoUploadFeedbackCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoUploadFeedbackCallback iDoUploadFeedbackCallback) {
        if (!mCallback.contains(iDoUploadFeedbackCallback)) {
            mCallback.add(iDoUploadFeedbackCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoUploadFeedbackCallback iDoUploadFeedbackCallback) {
        mCallback.remove(iDoUploadFeedbackCallback);
    }

}
