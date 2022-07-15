package com.twx.module_dynamic.net.impl;

import com.twx.module_dynamic.bean.CommentOneBean;
import com.twx.module_dynamic.bean.LikeListBean;
import com.twx.module_dynamic.net.callback.IGetCommentOneCallback;
import com.twx.module_dynamic.net.callback.IGetLikeListCallback;
import com.twx.module_dynamic.net.module.UserData;
import com.twx.module_dynamic.net.present.IGetCommentOnePresent;
import com.twx.module_dynamic.net.present.IGetLikeListPresent;

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
public class getCommentOnePresentImpl implements IGetCommentOnePresent {

    public static getCommentOnePresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetCommentOneCallback> mCallback = new ArrayList<>();

    public getCommentOnePresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getCommentOnePresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getCommentOnePresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getCommentOne(Map<String, String> info) {
        handlerLoading();
        mUserData.getCommentOne(info, new Callback<CommentOneBean>() {

            private CommentOneBean mBody;

            @Override
            public void onResponse(Call<CommentOneBean> call, Response<CommentOneBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetCommentOneCallback callback : mCallback) {
                            callback.onGetCommentOneSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentOneBean> call, Throwable t) {
                for (IGetCommentOneCallback callback : mCallback) {
                    callback.onGetCommentOneCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetCommentOneCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetCommentOneCallback iGetCommentOneCallback) {
        if (!mCallback.contains(iGetCommentOneCallback)) {
            mCallback.add(iGetCommentOneCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetCommentOneCallback iGetCommentOneCallback) {
        mCallback.remove(iGetCommentOneCallback);
    }

}
