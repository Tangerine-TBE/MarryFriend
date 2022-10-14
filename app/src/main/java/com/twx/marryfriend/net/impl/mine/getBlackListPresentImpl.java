package com.twx.marryfriend.net.impl.mine;

import com.twx.marryfriend.bean.vip.BlackListBean;
import com.twx.marryfriend.net.callback.mine.IGetBlackListCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.mine.IGetBlackListPresent;

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
public class getBlackListPresentImpl implements IGetBlackListPresent {

    public static getBlackListPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetBlackListCallback> mCallback = new ArrayList<>();

    public getBlackListPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getBlackListPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getBlackListPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getBlackList(Map<String, String> info) {
        handlerLoading();
        mUserData.getBlackList(info, new Callback<BlackListBean>() {

            private BlackListBean mBody;

            @Override
            public void onResponse(Call<BlackListBean> call, Response<BlackListBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetBlackListCallback callback : mCallback) {
                            callback.onGetBlackListSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BlackListBean> call, Throwable t) {
                for (IGetBlackListCallback callback : mCallback) {
                    callback.onGetBlackListCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetBlackListCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetBlackListCallback iGetBlackListCallback) {
        if (!mCallback.contains(iGetBlackListCallback)) {
            mCallback.add(iGetBlackListCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetBlackListCallback iGetBlackListCallback) {
        mCallback.remove(iGetBlackListCallback);
    }


}
