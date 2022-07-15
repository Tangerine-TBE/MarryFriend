package com.twx.module_dynamic.net.impl;

import com.twx.module_dynamic.bean.CommentOneDeleteBean;
import com.twx.module_dynamic.bean.CommentTwoDeleteBean;
import com.twx.module_dynamic.net.callback.IDoCommentOneDeleteCallback;
import com.twx.module_dynamic.net.callback.IDoCommentTwoDeleteCallback;
import com.twx.module_dynamic.net.module.UserData;
import com.twx.module_dynamic.net.present.IDoCommentOneDeletePresent;
import com.twx.module_dynamic.net.present.IDoCommentTwoDeletePresent;

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
public class doCommentTwoDeletePresentImpl implements IDoCommentTwoDeletePresent {

    public static doCommentTwoDeletePresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoCommentTwoDeleteCallback> mCallback = new ArrayList<>();

    public doCommentTwoDeletePresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doCommentTwoDeletePresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doCommentTwoDeletePresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doCommentTwoDelete(Map<String, String> info) {
        handlerLoading();
        mUserData.doCommentTwoDelete(info, new Callback<CommentTwoDeleteBean>() {

            private CommentTwoDeleteBean mBody;

            @Override
            public void onResponse(Call<CommentTwoDeleteBean> call, Response<CommentTwoDeleteBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoCommentTwoDeleteCallback callback : mCallback) {
                            callback.onDoCommentTwoDeleteSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentTwoDeleteBean> call, Throwable t) {
                for (IDoCommentTwoDeleteCallback callback : mCallback) {
                    callback.onDoCommentTwoDeleteError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoCommentTwoDeleteCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoCommentTwoDeleteCallback iDoCommentTwoDeleteCallback) {
        if (!mCallback.contains(iDoCommentTwoDeleteCallback)) {
            mCallback.add(iDoCommentTwoDeleteCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoCommentTwoDeleteCallback iDoCommentTwoDeleteCallback) {
        mCallback.remove(iDoCommentTwoDeleteCallback);
    }

}
