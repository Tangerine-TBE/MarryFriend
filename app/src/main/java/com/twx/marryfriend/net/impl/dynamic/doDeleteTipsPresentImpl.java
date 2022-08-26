package com.twx.marryfriend.net.impl.dynamic;

import com.twx.marryfriend.bean.dynamic.CancelFocusBean;
import com.twx.marryfriend.bean.dynamic.DeleteTipsBean;
import com.twx.marryfriend.net.callback.dynamic.IDoCancelFocusCallback;
import com.twx.marryfriend.net.callback.dynamic.IDoDeleteTipsCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.dynamic.IDoCancelFocusPresent;
import com.twx.marryfriend.net.present.dynamic.IDoDeleteTipsPresent;

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
public class doDeleteTipsPresentImpl implements IDoDeleteTipsPresent {

    public static doDeleteTipsPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoDeleteTipsCallback> mCallback = new ArrayList<>();

    public doDeleteTipsPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doDeleteTipsPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doDeleteTipsPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doDeleteTips(Map<String, String> info) {
        handlerLoading();
        mUserData.doDeleteTips(info, new Callback<DeleteTipsBean>() {

            private DeleteTipsBean mBody;

            @Override
            public void onResponse(Call<DeleteTipsBean> call, Response<DeleteTipsBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoDeleteTipsCallback callback : mCallback) {
                            callback.onDoDeleteTipsSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DeleteTipsBean> call, Throwable t) {
                for (IDoDeleteTipsCallback callback : mCallback) {
                    callback.onDoDeleteTipsError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoDeleteTipsCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoDeleteTipsCallback iDoDeleteTipsCallback) {
        if (!mCallback.contains(iDoDeleteTipsCallback)) {
            mCallback.add(iDoDeleteTipsCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoDeleteTipsCallback iDoDeleteTipsCallback) {
        mCallback.remove(iDoDeleteTipsCallback);
    }

}
