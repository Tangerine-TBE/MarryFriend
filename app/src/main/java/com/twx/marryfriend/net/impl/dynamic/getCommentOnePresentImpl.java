package com.twx.marryfriend.net.impl.dynamic;


import com.twx.marryfriend.bean.dynamic.CommentOneBean;
import com.twx.marryfriend.net.callback.dynamic.IGetCommentOneCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.dynamic.IGetCommentOnePresent;

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
    public void getCommentOne(Map<String, String> info, Integer page, Integer size) {
        handlerLoading();
        mUserData.getCommentOne(info, page, size, new Callback<CommentOneBean>() {

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
