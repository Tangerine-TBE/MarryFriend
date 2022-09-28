package com.twx.marryfriend.net.impl.dynamic;

import com.twx.marryfriend.bean.dynamic.LikeCancelBean;
import com.twx.marryfriend.net.callback.dynamic.IDoFocusLikeCancelCallback;
import com.twx.marryfriend.net.callback.dynamic.IDoLikeCancelCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.dynamic.IDoFocusLikeCancelPresent;
import com.twx.marryfriend.net.present.dynamic.IDoLikeCancelPresent;

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
public class doFocusLikeCancelPresentImpl implements IDoFocusLikeCancelPresent {

    public static doFocusLikeCancelPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoFocusLikeCancelCallback> mCallback = new ArrayList<>();

    public doFocusLikeCancelPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doFocusLikeCancelPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doFocusLikeCancelPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doFocusLikeCancel(Map<String, String> info) {
        handlerLoading();
        mUserData.doLikeCancel(info, new Callback<LikeCancelBean>() {

            private LikeCancelBean mBody;

            @Override
            public void onResponse(Call<LikeCancelBean> call, Response<LikeCancelBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoFocusLikeCancelCallback callback : mCallback) {
                            callback.onDoFocusLikeCancelSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LikeCancelBean> call, Throwable t) {
                for (IDoFocusLikeCancelCallback callback : mCallback) {
                    callback.onFocusLikeCancelError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoFocusLikeCancelCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoFocusLikeCancelCallback iDoFocusLikeCancelCallback) {
        if (!mCallback.contains(iDoFocusLikeCancelCallback)) {
            mCallback.add(iDoFocusLikeCancelCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoFocusLikeCancelCallback iDoFocusLikeCancelCallback) {
        mCallback.remove(iDoFocusLikeCancelCallback);
    }

}
