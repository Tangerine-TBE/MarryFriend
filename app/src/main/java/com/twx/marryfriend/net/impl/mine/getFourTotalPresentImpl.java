package com.twx.marryfriend.net.impl.mine;

import android.util.Log;

import com.twx.marryfriend.bean.mine.FourTotalBean;
import com.twx.marryfriend.bean.mine.MeDiscussWhoBean;
import com.twx.marryfriend.net.callback.mine.IGetFourTotalCallback;
import com.twx.marryfriend.net.callback.mine.IGetMeDiscussWhoCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.mine.IGetFourTotalPresent;
import com.twx.marryfriend.net.present.mine.IGetMeDiscussWhoPresent;

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
public class getFourTotalPresentImpl implements IGetFourTotalPresent {

    public static getFourTotalPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetFourTotalCallback> mCallback = new ArrayList<>();

    public getFourTotalPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getFourTotalPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getFourTotalPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getFourTotal(Map<String, String> info) {
        handlerLoading();
        mUserData.getFourTotal(info, new Callback<FourTotalBean>() {

            private FourTotalBean mBody;

            @Override
            public void onResponse(Call<FourTotalBean> call, Response<FourTotalBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetFourTotalCallback callback : mCallback) {
                            callback.onGetFourTotalSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<FourTotalBean> call, Throwable t) {
                Log.i("guo", "erroe: " + t);
                for (IGetFourTotalCallback callback : mCallback) {
                    callback.onGetFourTotalError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetFourTotalCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetFourTotalCallback iGetFourTotalCallback) {
        if (!mCallback.contains(iGetFourTotalCallback)) {
            mCallback.add(iGetFourTotalCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetFourTotalCallback iGetFourTotalCallback) {
        mCallback.remove(iGetFourTotalCallback);
    }


}
