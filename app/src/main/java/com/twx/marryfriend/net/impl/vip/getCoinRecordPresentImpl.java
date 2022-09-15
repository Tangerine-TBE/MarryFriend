package com.twx.marryfriend.net.impl.vip;

import com.twx.marryfriend.bean.vip.CoinRecordBean;
import com.twx.marryfriend.net.callback.vip.IGetCoinRecordCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.vip.IGetCoinRecordPresent;

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
public class getCoinRecordPresentImpl implements IGetCoinRecordPresent {

    public static getCoinRecordPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetCoinRecordCallback> mCallback = new ArrayList<>();

    public getCoinRecordPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getCoinRecordPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getCoinRecordPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getCoinRecord(Map<String, String> info, Integer page) {
        handlerLoading();
        mUserData.getCoinRecord(info, page, new Callback<CoinRecordBean>() {

            private CoinRecordBean mBody;

            @Override
            public void onResponse(Call<CoinRecordBean> call, Response<CoinRecordBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetCoinRecordCallback callback : mCallback) {
                            callback.onGetCoinRecordSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CoinRecordBean> call, Throwable t) {
                for (IGetCoinRecordCallback callback : mCallback) {
                    callback.onGetCoinRecordCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetCoinRecordCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetCoinRecordCallback iGetCoinRecordCallback) {
        if (!mCallback.contains(iGetCoinRecordCallback)) {
            mCallback.add(iGetCoinRecordCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetCoinRecordCallback iGetCoinRecordCallback) {
        mCallback.remove(iGetCoinRecordCallback);
    }

}
