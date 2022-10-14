package com.twx.marryfriend.net.impl.dynamic;

import com.twx.marryfriend.bean.dynamic.MyTrendsListBean;
import com.twx.marryfriend.bean.dynamic.OtherTrendsListBean;
import com.twx.marryfriend.net.callback.dynamic.IGetMyTrendsListCallback;
import com.twx.marryfriend.net.callback.dynamic.IGetOtherTrendsListCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.dynamic.IGetMyTrendsListPresent;
import com.twx.marryfriend.net.present.dynamic.IGetOtherTrendsListPresent;

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
public class getOtherTrendsListPresentImpl implements IGetOtherTrendsListPresent {

    public static getOtherTrendsListPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetOtherTrendsListCallback> mCallback = new ArrayList<>();

    public getOtherTrendsListPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getOtherTrendsListPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getOtherTrendsListPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getOtherTrendsList(Map<String, String> info) {
        handlerLoading();
        mUserData.getOtherTrendsList(info, new Callback<OtherTrendsListBean>() {

            private OtherTrendsListBean mBody;

            @Override
            public void onResponse(Call<OtherTrendsListBean> call, Response<OtherTrendsListBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetOtherTrendsListCallback callback : mCallback) {
                            callback.onGetOtherTrendsListSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<OtherTrendsListBean> call, Throwable t) {
                for (IGetOtherTrendsListCallback callback : mCallback) {
                    callback.onGetOtherTrendsListCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetOtherTrendsListCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetOtherTrendsListCallback iGetOtherTrendsListCallback) {
        if (!mCallback.contains(iGetOtherTrendsListCallback)) {
            mCallback.add(iGetOtherTrendsListCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetOtherTrendsListCallback iGetOtherTrendsListCallback) {
        mCallback.remove(iGetOtherTrendsListCallback);
    }

}
