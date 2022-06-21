package com.twx.module_base.net.impl;

import com.twx.module_base.net.bean.UpdateProportionInfoBean;
import com.twx.module_base.net.callback.IDoUpdateProportionCallback;
import com.twx.module_base.net.module.UserData;
import com.twx.module_base.net.present.IDoUpdateProportionPresent;

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
public class doUpdateProportionPresentImpl implements IDoUpdateProportionPresent {

    public static doUpdateProportionPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoUpdateProportionCallback> mCallback = new ArrayList<>();

    public doUpdateProportionPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doUpdateProportionPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doUpdateProportionPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doUpdateProportion(Map<String, String> info) {
        handlerLoading();
        mUserData.doUpdateProportion(info, new Callback<UpdateProportionInfoBean>() {

            private UpdateProportionInfoBean mBody;

            @Override
            public void onResponse(Call<UpdateProportionInfoBean> call, Response<UpdateProportionInfoBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoUpdateProportionCallback callback : mCallback) {
                            callback.onDoUpdateProportionSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateProportionInfoBean> call, Throwable t) {
                for (IDoUpdateProportionCallback callback : mCallback) {
                    callback.onDoUpdateProportionError();
                }
            }
        });
    }

    private void handlerLoading() {
        for (IDoUpdateProportionCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoUpdateProportionCallback iDoUpdateProportionCallback) {
        if (!mCallback.contains(iDoUpdateProportionCallback)) {
            mCallback.add(iDoUpdateProportionCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoUpdateProportionCallback iDoUpdateProportionCallback) {
        mCallback.remove(iDoUpdateProportionCallback);
    }

}
