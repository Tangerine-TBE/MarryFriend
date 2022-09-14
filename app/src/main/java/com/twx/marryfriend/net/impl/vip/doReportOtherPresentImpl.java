package com.twx.marryfriend.net.impl.vip;

import com.twx.marryfriend.bean.vip.ReportOtherBean;
import com.twx.marryfriend.bean.vip.UploadFeedbackBean;
import com.twx.marryfriend.net.callback.vip.IDoReportOtherCallback;
import com.twx.marryfriend.net.callback.vip.IDoUploadFeedbackCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.vip.IDoReportOtherPresent;
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
public class doReportOtherPresentImpl implements IDoReportOtherPresent {

    public static doReportOtherPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoReportOtherCallback> mCallback = new ArrayList<>();

    public doReportOtherPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doReportOtherPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doReportOtherPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doReportOther(Map<String, String> info) {
        handlerLoading();
        mUserData.doReportOther(info, new Callback<ReportOtherBean>() {

            private ReportOtherBean mBody;

            @Override
            public void onResponse(Call<ReportOtherBean> call, Response<ReportOtherBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoReportOtherCallback callback : mCallback) {
                            callback.onDoReportOtherSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ReportOtherBean> call, Throwable t) {
                for (IDoReportOtherCallback callback : mCallback) {
                    callback.onDoReportOtherError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoReportOtherCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoReportOtherCallback iDoReportOtherCallback) {
        if (!mCallback.contains(iDoReportOtherCallback)) {
            mCallback.add(iDoReportOtherCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoReportOtherCallback iDoReportOtherCallback) {
        mCallback.remove(iDoReportOtherCallback);
    }
}
