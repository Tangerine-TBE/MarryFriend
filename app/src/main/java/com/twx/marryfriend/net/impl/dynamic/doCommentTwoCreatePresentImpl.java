package com.twx.marryfriend.net.impl.dynamic;

import com.twx.marryfriend.bean.dynamic.CommentTwoCreateBean;
import com.twx.marryfriend.net.callback.dynamic.IDoCommentTwoCreateCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.dynamic.IDoCommentTwoCreatePresent;

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
public class doCommentTwoCreatePresentImpl implements IDoCommentTwoCreatePresent {

    public static doCommentTwoCreatePresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoCommentTwoCreateCallback> mCallback = new ArrayList<>();

    public doCommentTwoCreatePresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doCommentTwoCreatePresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doCommentTwoCreatePresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doCommentTwoCreate(Map<String, String> info) {
        handlerLoading();
        mUserData.doCommentTwoCreate(info, new Callback<CommentTwoCreateBean>() {

            private CommentTwoCreateBean mBody;

            @Override
            public void onResponse(Call<CommentTwoCreateBean> call, Response<CommentTwoCreateBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoCommentTwoCreateCallback callback : mCallback) {
                            callback.onDoCommentTwoCreateSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentTwoCreateBean> call, Throwable t) {
                for (IDoCommentTwoCreateCallback callback : mCallback) {
                    callback.onDoCommentTwoCreateError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoCommentTwoCreateCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoCommentTwoCreateCallback iDoCommentTwoCreateCallback) {
        if (!mCallback.contains(iDoCommentTwoCreateCallback)) {
            mCallback.add(iDoCommentTwoCreateCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoCommentTwoCreateCallback iDoCommentTwoCreateCallback) {
        mCallback.remove(iDoCommentTwoCreateCallback);
    }

}
