package com.twx.marryfriend.net.impl.dynamic;

import com.twx.marryfriend.bean.dynamic.PlusFocusBean;
import com.twx.marryfriend.net.callback.dynamic.IDoPlusFocusCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.dynamic.IDoPlusFocusPresent;

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
public class doPlusFocusPresentImpl implements IDoPlusFocusPresent {

    public static doPlusFocusPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoPlusFocusCallback> mCallback = new ArrayList<>();

    public doPlusFocusPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doPlusFocusPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doPlusFocusPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doPlusFocusOther(Map<String, String> info) {
        handlerLoading();
        mUserData.doPlusFocusOther(info, new Callback<PlusFocusBean>() {

            private PlusFocusBean mBody;

            @Override
            public void onResponse(Call<PlusFocusBean> call, Response<PlusFocusBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoPlusFocusCallback callback : mCallback) {
                            callback.onDoPlusFocusSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PlusFocusBean> call, Throwable t) {
                for (IDoPlusFocusCallback callback : mCallback) {
                    callback.onDoPlusFocusError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoPlusFocusCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoPlusFocusCallback iDoPlusFocusCallback) {
        if (!mCallback.contains(iDoPlusFocusCallback)) {
            mCallback.add(iDoPlusFocusCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoPlusFocusCallback iDoPlusFocusCallback) {
        mCallback.remove(iDoPlusFocusCallback);
    }

}
