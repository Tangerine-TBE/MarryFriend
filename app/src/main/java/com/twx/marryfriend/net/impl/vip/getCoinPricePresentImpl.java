package com.twx.marryfriend.net.impl.vip;

import com.twx.marryfriend.bean.vip.CoinPriceBean;
import com.twx.marryfriend.bean.vip.VipPriceBean;
import com.twx.marryfriend.net.callback.vip.IGetCoinPriceCallback;
import com.twx.marryfriend.net.callback.vip.IGetVipPriceCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.vip.IGetCoinPricePresent;
import com.twx.marryfriend.net.present.vip.IGetVipPricePresent;

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
public class getCoinPricePresentImpl implements IGetCoinPricePresent {

    public static getCoinPricePresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetCoinPriceCallback> mCallback = new ArrayList<>();

    public getCoinPricePresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getCoinPricePresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getCoinPricePresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getCoinPrice(Map<String, String> info) {
        handlerLoading();
        mUserData.getCoinPrice(info, new Callback<CoinPriceBean>() {

            private CoinPriceBean mBody;

            @Override
            public void onResponse(Call<CoinPriceBean> call, Response<CoinPriceBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetCoinPriceCallback callback : mCallback) {
                            callback.onGetCoinPriceSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CoinPriceBean> call, Throwable t) {
                for (IGetCoinPriceCallback callback : mCallback) {
                    callback.onGetCoinPriceCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetCoinPriceCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetCoinPriceCallback iGetCoinPriceCallback) {
        if (!mCallback.contains(iGetCoinPriceCallback)) {
            mCallback.add(iGetCoinPriceCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetCoinPriceCallback iGetCoinPriceCallback) {
        mCallback.remove(iGetCoinPriceCallback);
    }

}
