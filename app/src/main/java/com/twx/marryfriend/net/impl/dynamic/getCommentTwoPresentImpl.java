package com.twx.marryfriend.net.impl.dynamic;

import com.twx.marryfriend.bean.dynamic.CommentTwoBean;
import com.twx.marryfriend.net.callback.dynamic.IGetCommentTwoCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.dynamic.IGetCommentTwoPresent;

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
public class getCommentTwoPresentImpl implements IGetCommentTwoPresent {

    public static getCommentTwoPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetCommentTwoCallback> mCallback = new ArrayList<>();

    public getCommentTwoPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getCommentTwoPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getCommentTwoPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getCommentTwo(Map<String, String> info, Integer page, Integer size) {
        handlerLoading();
        mUserData.getCommentTwo(info, page, size, new Callback<CommentTwoBean>() {

            private CommentTwoBean mBody;

            @Override
            public void onResponse(Call<CommentTwoBean> call, Response<CommentTwoBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetCommentTwoCallback callback : mCallback) {
                            callback.onGetCommentTwoSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentTwoBean> call, Throwable t) {
                for (IGetCommentTwoCallback callback : mCallback) {
                    callback.onGetCommentTwoCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetCommentTwoCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetCommentTwoCallback iGetCommentTwoCallback) {
        if (!mCallback.contains(iGetCommentTwoCallback)) {
            mCallback.add(iGetCommentTwoCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetCommentTwoCallback iGetCommentTwoCallback) {
        mCallback.remove(iGetCommentTwoCallback);
    }

}
