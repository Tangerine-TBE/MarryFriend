package com.twx.marryfriend.net.impl.vip;

import com.twx.marryfriend.bean.vip.AliPayBean;
import com.twx.marryfriend.bean.vip.UpdateTokenBean;
import com.twx.marryfriend.net.callback.vip.IDoAliPayCallback;
import com.twx.marryfriend.net.callback.vip.IDoUpdateTokenCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.vip.IDoAliPayPresent;
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
public class doUpdateTokenPresentImpl implements IDoUpdateTokenPresent {

    public static doUpdateTokenPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoUpdateTokenCallback> mCallback = new ArrayList<>();

    public doUpdateTokenPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doUpdateTokenPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doUpdateTokenPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doUpdateToken(Map<String, String> info) {
        handlerLoading();
        mUserData.doUpdateToken(info, new Callback<UpdateTokenBean>() {

            private UpdateTokenBean mBody;

            @Override
            public void onResponse(Call<UpdateTokenBean> call, Response<UpdateTokenBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoUpdateTokenCallback callback : mCallback) {
                            callback.onDoUpdateTokenSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateTokenBean> call, Throwable t) {
                for (IDoUpdateTokenCallback callback : mCallback) {
                    callback.onDoUpdateTokenError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoUpdateTokenCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoUpdateTokenCallback iDoUpdateTokenCallback) {
        if (!mCallback.contains(iDoUpdateTokenCallback)) {
            mCallback.add(iDoUpdateTokenCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoUpdateTokenCallback iDoUpdateTokenCallback) {
        mCallback.remove(iDoUpdateTokenCallback);
    }


}
