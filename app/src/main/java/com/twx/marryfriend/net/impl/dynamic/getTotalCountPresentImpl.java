package com.twx.marryfriend.net.impl.dynamic;

import com.twx.marryfriend.bean.dynamic.TotalCountBean;
import com.twx.marryfriend.net.callback.dynamic.IGetTotalCountCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.dynamic.IGetTotalCountPresent;

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
public class getTotalCountPresentImpl implements IGetTotalCountPresent {

    public static getTotalCountPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetTotalCountCallback> mCallback = new ArrayList<>();

    public getTotalCountPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getTotalCountPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getTotalCountPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getTotalCount(Map<String, String> info) {
        handlerLoading();
        mUserData.getTotalCount(info, new Callback<TotalCountBean>() {

            private TotalCountBean mBody;

            @Override
            public void onResponse(Call<TotalCountBean> call, Response<TotalCountBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetTotalCountCallback callback : mCallback) {
                            callback.onGetTotalCountSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TotalCountBean> call, Throwable t) {
                for (IGetTotalCountCallback callback : mCallback) {
                    callback.onGetTotalCountError();
                }
            }
        });
    }

    private void handlerLoading() {
        for (IGetTotalCountCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetTotalCountCallback iGetTotalCountCallback) {
        if (!mCallback.contains(iGetTotalCountCallback)) {
            mCallback.add(iGetTotalCountCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetTotalCountCallback iGetTotalCountCallback) {
        mCallback.remove(iGetTotalCountCallback);
    }

}