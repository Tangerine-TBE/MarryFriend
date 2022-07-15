package com.twx.module_dynamic.net.impl;

import com.twx.module_dynamic.bean.CommentOneCreateBean;
import com.twx.module_dynamic.bean.LikeClickBean;
import com.twx.module_dynamic.net.callback.IDoCommentOneCreateCallback;
import com.twx.module_dynamic.net.callback.IDoLikeClickCallback;
import com.twx.module_dynamic.net.module.UserData;
import com.twx.module_dynamic.net.present.IDoCommentOneCreatePresent;
import com.twx.module_dynamic.net.present.IDoLikeClickPresent;

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
public class doCommentOneCreatePresentImpl implements IDoCommentOneCreatePresent {

    public static doCommentOneCreatePresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoCommentOneCreateCallback> mCallback = new ArrayList<>();

    public doCommentOneCreatePresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doCommentOneCreatePresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doCommentOneCreatePresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doCommentOneCreate(Map<String, String> info) {
        handlerLoading();
        mUserData.doCommentOneCreate(info, new Callback<CommentOneCreateBean>() {

            private CommentOneCreateBean mBody;

            @Override
            public void onResponse(Call<CommentOneCreateBean> call, Response<CommentOneCreateBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoCommentOneCreateCallback callback : mCallback) {
                            callback.onDoCommentOneCreateSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentOneCreateBean> call, Throwable t) {
                for (IDoCommentOneCreateCallback callback : mCallback) {
                    callback.onDoCommentOneCreateError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoCommentOneCreateCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoCommentOneCreateCallback iDoCommentOneCreateCallback) {
        if (!mCallback.contains(iDoCommentOneCreateCallback)) {
            mCallback.add(iDoCommentOneCreateCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoCommentOneCreateCallback iDoCommentOneCreateCallback) {
        mCallback.remove(iDoCommentOneCreateCallback);
    }

}
