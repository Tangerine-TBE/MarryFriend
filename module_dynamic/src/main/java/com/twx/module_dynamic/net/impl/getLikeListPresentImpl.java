package com.twx.module_dynamic.net.impl;

import com.twx.module_dynamic.bean.LikeListBean;
import com.twx.module_dynamic.bean.MyTrendsListBean;
import com.twx.module_dynamic.net.callback.IGetLikeListCallback;
import com.twx.module_dynamic.net.callback.IGetMyTrendsListCallback;
import com.twx.module_dynamic.net.module.UserData;
import com.twx.module_dynamic.net.present.IGetLikeListPresent;
import com.twx.module_dynamic.net.present.IGetMyTrendsListPresent;

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
public class getLikeListPresentImpl implements IGetLikeListPresent {

    public static getLikeListPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetLikeListCallback> mCallback = new ArrayList<>();

    public getLikeListPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getLikeListPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getLikeListPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getLikeList(Map<String, String> info, Integer page, Integer size) {
        handlerLoading();
        mUserData.getLikeList(info,page,size, new Callback<LikeListBean>() {

            private LikeListBean mBody;

            @Override
            public void onResponse(Call<LikeListBean> call, Response<LikeListBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetLikeListCallback callback : mCallback) {
                            callback.onGetLikeListSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LikeListBean> call, Throwable t) {
                for (IGetLikeListCallback callback : mCallback) {
                    callback.onGetLikeListCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetLikeListCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetLikeListCallback iGetLikeListCallback) {
        if (!mCallback.contains(iGetLikeListCallback)) {
            mCallback.add(iGetLikeListCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetLikeListCallback iGetLikeListCallback) {
        mCallback.remove(iGetLikeListCallback);
    }

}
