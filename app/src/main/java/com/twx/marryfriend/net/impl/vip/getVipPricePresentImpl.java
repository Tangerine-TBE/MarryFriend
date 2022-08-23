package com.twx.marryfriend.net.impl.vip;

import com.twx.marryfriend.bean.BanBean;
import com.twx.marryfriend.bean.vip.VipPriceBean;
import com.twx.marryfriend.net.callback.IGetBanCallback;
import com.twx.marryfriend.net.callback.vip.IGetVipPriceCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.IGetBanPresent;
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
public class getVipPricePresentImpl implements IGetVipPricePresent {

    public static getVipPricePresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetVipPriceCallback> mCallback = new ArrayList<>();

    public getVipPricePresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getVipPricePresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getVipPricePresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getVipPrice(Map<String, String> info) {
        handlerLoading();
        mUserData.getVipPrice(info, new Callback<VipPriceBean>() {

            private VipPriceBean mBody;

            @Override
            public void onResponse(Call<VipPriceBean> call, Response<VipPriceBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetVipPriceCallback callback : mCallback) {
                            callback.onGetVipPriceSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VipPriceBean> call, Throwable t) {
                for (IGetVipPriceCallback callback : mCallback) {
                    callback.onGetVipPriceCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetVipPriceCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetVipPriceCallback iGetVipPriceCallback) {
        if (!mCallback.contains(iGetVipPriceCallback)) {
            mCallback.add(iGetVipPriceCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetVipPriceCallback iGetVipPriceCallback) {
        mCallback.remove(iGetVipPriceCallback);
    }

}
