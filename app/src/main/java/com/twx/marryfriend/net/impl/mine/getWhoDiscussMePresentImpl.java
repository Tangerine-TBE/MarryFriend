package com.twx.marryfriend.net.impl.mine;

import android.util.Log;

import com.twx.marryfriend.bean.mine.WhoDiscussMeBean;
import com.twx.marryfriend.bean.mine.WhoFocusMeBean;
import com.twx.marryfriend.net.callback.mine.IGetWhoDiscussMeCallback;
import com.twx.marryfriend.net.callback.mine.IGetWhoFocusMeCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.mine.IGetWhoDiscussMePresent;
import com.twx.marryfriend.net.present.mine.IGetWhoFocusMePresent;

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
public class getWhoDiscussMePresentImpl implements IGetWhoDiscussMePresent {

    public static getWhoDiscussMePresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetWhoDiscussMeCallback> mCallback = new ArrayList<>();

    public getWhoDiscussMePresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getWhoDiscussMePresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getWhoDiscussMePresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getWhoDiscussMe(Map<String, String> info, Integer page) {
        handlerLoading();
        mUserData.getWhoDiscussMe(info, page, new Callback<WhoDiscussMeBean>() {

            private WhoDiscussMeBean mBody;

            @Override
            public void onResponse(Call<WhoDiscussMeBean> call, Response<WhoDiscussMeBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetWhoDiscussMeCallback callback : mCallback) {
                            callback.onGetWhoDiscussMeSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WhoDiscussMeBean> call, Throwable t) {
                Log.i("guo", "erroe: " + t);
                for (IGetWhoDiscussMeCallback callback : mCallback) {
                    callback.onGetWhoDiscussMeCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetWhoDiscussMeCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetWhoDiscussMeCallback iGetWhoDiscussMeCallback) {
        if (!mCallback.contains(iGetWhoDiscussMeCallback)) {
            mCallback.add(iGetWhoDiscussMeCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetWhoDiscussMeCallback iGetWhoDiscussMeCallback) {
        mCallback.remove(iGetWhoDiscussMeCallback);
    }

}
