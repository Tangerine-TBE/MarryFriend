package com.twx.marryfriend.net.impl.vip;

import com.twx.marryfriend.bean.vip.AliPayBean;
import com.twx.marryfriend.bean.vip.PreviewOtherBean;
import com.twx.marryfriend.net.callback.vip.IDoAliPayCallback;
import com.twx.marryfriend.net.callback.vip.IDoPreviewOtherCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.vip.IDoAliPayPresent;
import com.twx.marryfriend.net.present.vip.IDoPreviewOtherPresent;

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
public class doPreviewOtherPresentImpl implements IDoPreviewOtherPresent {

    public static doPreviewOtherPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoPreviewOtherCallback> mCallback = new ArrayList<>();

    public doPreviewOtherPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doPreviewOtherPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doPreviewOtherPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doPreviewOther(Map<String, String> info) {
        handlerLoading();
        mUserData.doPreviewOther(info, new Callback<PreviewOtherBean>() {

            private PreviewOtherBean mBody;

            @Override
            public void onResponse(Call<PreviewOtherBean> call, Response<PreviewOtherBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoPreviewOtherCallback callback : mCallback) {
                            callback.onDoPreviewOtherSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PreviewOtherBean> call, Throwable t) {
                for (IDoPreviewOtherCallback callback : mCallback) {
                    callback.onDoPreviewOtherError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoPreviewOtherCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoPreviewOtherCallback iDoPreviewOtherCallback) {
        if (!mCallback.contains(iDoPreviewOtherCallback)) {
            mCallback.add(iDoPreviewOtherCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoPreviewOtherCallback iDoPreviewOtherCallback) {
        mCallback.remove(iDoPreviewOtherCallback);
    }

}
