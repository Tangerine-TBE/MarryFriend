package com.twx.marryfriend.net.impl.dynamic;

import com.twx.marryfriend.bean.dynamic.LikeClickBean;
import com.twx.marryfriend.net.callback.dynamic.IDoFocusLikeClickCallback;
import com.twx.marryfriend.net.callback.dynamic.IDoLikeClickCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.dynamic.IDoFocusLikeClickPresent;
import com.twx.marryfriend.net.present.dynamic.IDoLikeClickPresent;

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
public class doFocusLikeClickPresentImpl implements IDoFocusLikeClickPresent {

    public static doFocusLikeClickPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoFocusLikeClickCallback> mCallback = new ArrayList<>();

    public doFocusLikeClickPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doFocusLikeClickPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doFocusLikeClickPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doFocusLikeClick(Map<String, String> info) {
        handlerLoading();
        mUserData.doLikeClick(info, new Callback<LikeClickBean>() {

            private LikeClickBean mBody;

            @Override
            public void onResponse(Call<LikeClickBean> call, Response<LikeClickBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoFocusLikeClickCallback callback : mCallback) {
                            callback.onDoFocusLikeClickSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LikeClickBean> call, Throwable t) {
                for (IDoFocusLikeClickCallback callback : mCallback) {
                    callback.onDoFocusLikeClickError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoFocusLikeClickCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoFocusLikeClickCallback iDoFocusLikeClickCallback) {
        if (!mCallback.contains(iDoFocusLikeClickCallback)) {
            mCallback.add(iDoFocusLikeClickCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoFocusLikeClickCallback iDoFocusLikeClickCallback) {
        mCallback.remove(iDoFocusLikeClickCallback);
    }


}
