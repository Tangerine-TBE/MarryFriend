package com.twx.marryfriend.net.impl.vip;

import com.twx.marryfriend.bean.vip.VipPriceBean;
import com.twx.marryfriend.net.callback.vip.IGetSVipPriceCallback;
import com.twx.marryfriend.net.callback.vip.IGetVipPriceCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.vip.IGetSVipPricePresent;
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
public class getSVipPricePresentImpl implements IGetSVipPricePresent {

    public static getSVipPricePresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetSVipPriceCallback> mCallback = new ArrayList<>();

    public getSVipPricePresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getSVipPricePresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getSVipPricePresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getSVipPrice(Map<String, String> info) {
        handlerLoading();
        mUserData.getVipPrice(info, new Callback<VipPriceBean>() {

            private VipPriceBean mBody;

            @Override
            public void onResponse(Call<VipPriceBean> call, Response<VipPriceBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetSVipPriceCallback callback : mCallback) {
                            callback.onGetSVipPriceSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VipPriceBean> call, Throwable t) {
                for (IGetSVipPriceCallback callback : mCallback) {
                    callback.onGetSVipPriceCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetSVipPriceCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetSVipPriceCallback iGetSVipPriceCallback) {
        if (!mCallback.contains(iGetSVipPriceCallback)) {
            mCallback.add(iGetSVipPriceCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetSVipPriceCallback iGetSVipPriceCallback) {
        mCallback.remove(iGetSVipPriceCallback);
    }


}
