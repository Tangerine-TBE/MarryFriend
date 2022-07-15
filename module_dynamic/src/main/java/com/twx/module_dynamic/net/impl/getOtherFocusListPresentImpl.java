package com.twx.module_dynamic.net.impl;

import com.twx.module_dynamic.bean.MyFocusBean;
import com.twx.module_dynamic.bean.OtherFocusBean;
import com.twx.module_dynamic.net.callback.IGetMyFocusListCallback;
import com.twx.module_dynamic.net.callback.IGetOtherFocusListCallback;
import com.twx.module_dynamic.net.module.UserData;
import com.twx.module_dynamic.net.present.IGetMyFocusListPresent;
import com.twx.module_dynamic.net.present.IGetOtherFocusListPresent;

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
public class getOtherFocusListPresentImpl implements IGetOtherFocusListPresent {

    public static getOtherFocusListPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetOtherFocusListCallback> mCallback = new ArrayList<>();

    public getOtherFocusListPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getOtherFocusListPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getOtherFocusListPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getOtherFocus(Map<String, String> info) {
        handlerLoading();
        mUserData.getOtherFocus(info, new Callback<OtherFocusBean>() {

            private OtherFocusBean mBody;

            @Override
            public void onResponse(Call<OtherFocusBean> call, Response<OtherFocusBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetOtherFocusListCallback callback : mCallback) {
                            callback.onGetOtherFocusListSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<OtherFocusBean> call, Throwable t) {
                for (IGetOtherFocusListCallback callback : mCallback) {
                    callback.onGetOtherFocusListError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetOtherFocusListCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetOtherFocusListCallback iGetOtherFocusListCallback) {
        if (!mCallback.contains(iGetOtherFocusListCallback)) {
            mCallback.add(iGetOtherFocusListCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetOtherFocusListCallback iGetOtherFocusListCallback) {
        mCallback.remove(iGetOtherFocusListCallback);
    }

}
