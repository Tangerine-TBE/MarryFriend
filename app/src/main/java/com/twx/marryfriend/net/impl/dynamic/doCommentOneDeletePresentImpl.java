package com.twx.marryfriend.net.impl.dynamic;

import com.twx.marryfriend.bean.dynamic.CommentOneDeleteBean;
import com.twx.marryfriend.net.callback.dynamic.IDoCommentOneDeleteCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.dynamic.IDoCommentOneDeletePresent;

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
public class doCommentOneDeletePresentImpl implements IDoCommentOneDeletePresent {

    public static doCommentOneDeletePresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoCommentOneDeleteCallback> mCallback = new ArrayList<>();

    public doCommentOneDeletePresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doCommentOneDeletePresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doCommentOneDeletePresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doCommentOneDelete(Map<String, String> info) {
        handlerLoading();
        mUserData.doCommentOneDelete(info, new Callback<CommentOneDeleteBean>() {

            private CommentOneDeleteBean mBody;

            @Override
            public void onResponse(Call<CommentOneDeleteBean> call, Response<CommentOneDeleteBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoCommentOneDeleteCallback callback : mCallback) {
                            callback.onDoCommentOneDeleteSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentOneDeleteBean> call, Throwable t) {
                for (IDoCommentOneDeleteCallback callback : mCallback) {
                    callback.onDoCommentOneDeleteError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoCommentOneDeleteCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoCommentOneDeleteCallback iDoCommentOneDeleteCallback) {
        if (!mCallback.contains(iDoCommentOneDeleteCallback)) {
            mCallback.add(iDoCommentOneDeleteCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoCommentOneDeleteCallback iDoCommentOneDeleteCallback) {
        mCallback.remove(iDoCommentOneDeleteCallback);
    }

}
