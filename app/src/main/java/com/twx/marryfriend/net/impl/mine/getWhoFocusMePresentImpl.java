package com.twx.marryfriend.net.impl.mine;

import android.util.Log;

import com.twx.marryfriend.bean.mine.WhoFocusMeBean;
import com.twx.marryfriend.bean.mine.WhoSeeMeBean;
import com.twx.marryfriend.net.callback.mine.IGetWhoFocusMeCallback;
import com.twx.marryfriend.net.callback.mine.IGetWhoSeeMeCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.mine.IGetWhoFocusMePresent;
import com.twx.marryfriend.net.present.mine.IGetWhoSeeMePresent;

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
public class getWhoFocusMePresentImpl implements IGetWhoFocusMePresent {

    public static getWhoFocusMePresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetWhoFocusMeCallback> mCallback = new ArrayList<>();

    public getWhoFocusMePresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getWhoFocusMePresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getWhoFocusMePresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getWhoFocusMe(Map<String, String> info, Integer page) {
        handlerLoading();
        mUserData.getWhoFocusMe(info, page, new Callback<WhoFocusMeBean>() {

            private WhoFocusMeBean mBody;

            @Override
            public void onResponse(Call<WhoFocusMeBean> call, Response<WhoFocusMeBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetWhoFocusMeCallback callback : mCallback) {
                            callback.onGetWhoFocusMeSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WhoFocusMeBean> call, Throwable t) {
                Log.i("guo", "erroe: " + t);
                for (IGetWhoFocusMeCallback callback : mCallback) {
                    callback.onGetWhoFocusMeCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetWhoFocusMeCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetWhoFocusMeCallback iGetWhoFocusMeCallback) {
        if (!mCallback.contains(iGetWhoFocusMeCallback)) {
            mCallback.add(iGetWhoFocusMeCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetWhoFocusMeCallback iGetWhoFocusMeCallback) {
        mCallback.remove(iGetWhoFocusMeCallback);
    }

}
