package com.twx.marryfriend.net.impl.vip;

import com.twx.marryfriend.bean.vip.UpdatePushSetBean;
import com.twx.marryfriend.bean.vip.UpdateTokenBean;
import com.twx.marryfriend.net.callback.vip.IDoUpdatePushSetCallback;
import com.twx.marryfriend.net.callback.vip.IDoUpdateTokenCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.vip.IDoUpdatePushSetPresent;
import com.twx.marryfriend.net.present.vip.IDoUpdateTokenPresent;

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
public class doUpdatePushSetPresentImpl implements IDoUpdatePushSetPresent {

    public static doUpdatePushSetPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoUpdatePushSetCallback> mCallback = new ArrayList<>();

    public doUpdatePushSetPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doUpdatePushSetPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doUpdatePushSetPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doUpdatePushSet(Map<String, String> info) {
        handlerLoading();
        mUserData.doUpdatePushSet(info, new Callback<UpdatePushSetBean>() {

            private UpdatePushSetBean mBody;

            @Override
            public void onResponse(Call<UpdatePushSetBean> call, Response<UpdatePushSetBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoUpdatePushSetCallback callback : mCallback) {
                            callback.onDoUpdatePushSetSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdatePushSetBean> call, Throwable t) {
                for (IDoUpdatePushSetCallback callback : mCallback) {
                    callback.onDoUpdatePushSetError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoUpdatePushSetCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoUpdatePushSetCallback iDoUpdatePushSetCallback) {
        if (!mCallback.contains(iDoUpdatePushSetCallback)) {
            mCallback.add(iDoUpdatePushSetCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoUpdatePushSetCallback iDoUpdatePushSetCallback) {
        mCallback.remove(iDoUpdatePushSetCallback);
    }
}
