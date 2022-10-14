package com.twx.marryfriend.net.impl.dynamic;


import com.twx.marryfriend.bean.dynamic.CommentTipBean;
import com.twx.marryfriend.bean.dynamic.LikeTipBean;
import com.twx.marryfriend.net.callback.dynamic.IGetCommentTipsCallback;
import com.twx.marryfriend.net.callback.dynamic.IGetTrendTipsCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.dynamic.IGetCommentTipsPresent;
import com.twx.marryfriend.net.present.dynamic.IGetTrendTipsPresent;

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
public class getCommentTipsPresentImpl implements IGetCommentTipsPresent {

    public static getCommentTipsPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetCommentTipsCallback> mCallback = new ArrayList<>();

    public getCommentTipsPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getCommentTipsPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getCommentTipsPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getCommentTips(Map<String, String> info) {
        handlerLoading();
        mUserData.getCommentTips(info, new Callback<CommentTipBean>() {

            private CommentTipBean mBody;

            @Override
            public void onResponse(Call<CommentTipBean> call, Response<CommentTipBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetCommentTipsCallback callback : mCallback) {
                            callback.onGetCommentTipsSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentTipBean> call, Throwable t) {
                for (IGetCommentTipsCallback callback : mCallback) {
                    callback.onGetCommentTipsError();
                }
            }
        });
    }

    private void handlerLoading() {
        for (IGetCommentTipsCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetCommentTipsCallback iGetCommentTipsCallback) {
        if (!mCallback.contains(iGetCommentTipsCallback)) {
            mCallback.add(iGetCommentTipsCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetCommentTipsCallback iGetCommentTipsCallback) {
        mCallback.remove(iGetCommentTipsCallback);
    }

}
