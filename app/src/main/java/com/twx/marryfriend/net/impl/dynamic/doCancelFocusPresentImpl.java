package com.twx.marryfriend.net.impl.dynamic;

import com.twx.marryfriend.bean.dynamic.CancelFocusBean;
import com.twx.marryfriend.net.callback.dynamic.IDoCancelFocusCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.dynamic.IDoCancelFocusPresent;

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
public class doCancelFocusPresentImpl implements IDoCancelFocusPresent {

    public static doCancelFocusPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoCancelFocusCallback> mCallback = new ArrayList<>();

    public doCancelFocusPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doCancelFocusPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doCancelFocusPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doCancelFocusOther(Map<String, String> info) {
        handlerLoading();
        mUserData.doCancelFocusOther(info, new Callback<CancelFocusBean>() {

            private CancelFocusBean mBody;

            @Override
            public void onResponse(Call<CancelFocusBean> call, Response<CancelFocusBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoCancelFocusCallback callback : mCallback) {
                            callback.onDoCancelFocusSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CancelFocusBean> call, Throwable t) {
                for (IDoCancelFocusCallback callback : mCallback) {
                    callback.onDoCancelFocusError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoCancelFocusCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoCancelFocusCallback iDoCancelFocusCallback) {
        if (!mCallback.contains(iDoCancelFocusCallback)) {
            mCallback.add(iDoCancelFocusCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoCancelFocusCallback iDoCancelFocusCallback) {
        mCallback.remove(iDoCancelFocusCallback);
    }
}
