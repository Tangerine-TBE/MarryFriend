package com.twx.marryfriend.net.impl.vip;

import com.twx.marryfriend.bean.vip.CoinPriceBean;
import com.twx.marryfriend.bean.vip.GetPushSetBean;
import com.twx.marryfriend.bean.vip.UpdatePushSetBean;
import com.twx.marryfriend.net.callback.vip.IGetCoinPriceCallback;
import com.twx.marryfriend.net.callback.vip.IGetPushSetCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.vip.IGetCoinPricePresent;
import com.twx.marryfriend.net.present.vip.IGetPushSetPresent;

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
public class getPushSetPresentImpl implements IGetPushSetPresent {

    public static getPushSetPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetPushSetCallback> mCallback = new ArrayList<>();

    public getPushSetPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getPushSetPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getPushSetPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getPushSet(Map<String, String> info) {
        handlerLoading();
        mUserData.getPushSet(info, new Callback<GetPushSetBean>() {

            private GetPushSetBean mBody;

            @Override
            public void onResponse(Call<GetPushSetBean> call, Response<GetPushSetBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetPushSetCallback callback : mCallback) {
                            callback.onGetPushSetSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GetPushSetBean> call, Throwable t) {
                for (IGetPushSetCallback callback : mCallback) {
                    callback.onGetPushSetError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetPushSetCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetPushSetCallback iGetPushSetCallback) {
        if (!mCallback.contains(iGetPushSetCallback)) {
            mCallback.add(iGetPushSetCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetPushSetCallback iGetPushSetCallback) {
        mCallback.remove(iGetPushSetCallback);
    }

}
