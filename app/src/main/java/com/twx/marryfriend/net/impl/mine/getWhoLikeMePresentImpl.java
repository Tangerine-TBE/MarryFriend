package com.twx.marryfriend.net.impl.mine;

import android.util.Log;

import com.twx.marryfriend.bean.mine.WhoFocusMeBean;
import com.twx.marryfriend.bean.mine.WhoLikeMeBean;
import com.twx.marryfriend.net.callback.mine.IGetWhoFocusMeCallback;
import com.twx.marryfriend.net.callback.mine.IGetWhoLikeMeCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.mine.IGetWhoFocusMePresent;
import com.twx.marryfriend.net.present.mine.IGetWhoLikeMePresent;

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
public class getWhoLikeMePresentImpl implements IGetWhoLikeMePresent {

    public static getWhoLikeMePresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetWhoLikeMeCallback> mCallback = new ArrayList<>();

    public getWhoLikeMePresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getWhoLikeMePresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getWhoLikeMePresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getWhoLikeMe(Map<String, String> info, Integer page) {
        handlerLoading();
        mUserData.getWhoLikeMe(info, page, new Callback<WhoLikeMeBean>() {

            private WhoLikeMeBean mBody;

            @Override
            public void onResponse(Call<WhoLikeMeBean> call, Response<WhoLikeMeBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetWhoLikeMeCallback callback : mCallback) {
                            callback.onGetWhoLikeMeSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WhoLikeMeBean> call, Throwable t) {
                Log.i("guo", "erroe: " + t);
                for (IGetWhoLikeMeCallback callback : mCallback) {
                    callback.onGetWhoLikeMeCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetWhoLikeMeCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetWhoLikeMeCallback iGetWhoLikeMeCallback) {
        if (!mCallback.contains(iGetWhoLikeMeCallback)) {
            mCallback.add(iGetWhoLikeMeCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetWhoLikeMeCallback iGetWhoLikeMeCallback) {
        mCallback.remove(iGetWhoLikeMeCallback);
    }




}
