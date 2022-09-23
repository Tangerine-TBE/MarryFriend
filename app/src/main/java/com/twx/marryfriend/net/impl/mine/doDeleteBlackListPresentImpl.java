package com.twx.marryfriend.net.impl.mine;

import com.twx.marryfriend.bean.set.DeleteBlackListBean;
import com.twx.marryfriend.bean.vip.BlackListBean;
import com.twx.marryfriend.net.callback.mine.IDoDeleteBlackListCallback;
import com.twx.marryfriend.net.callback.mine.IGetBlackListCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.mine.IDoDeleteBlackListPresent;
import com.twx.marryfriend.net.present.mine.IGetBlackListPresent;

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
public class doDeleteBlackListPresentImpl implements IDoDeleteBlackListPresent {

    public static doDeleteBlackListPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoDeleteBlackListCallback> mCallback = new ArrayList<>();

    public doDeleteBlackListPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doDeleteBlackListPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doDeleteBlackListPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doDeleteBlackList(Map<String, String> info) {
        handlerLoading();
        mUserData.doDeleteBlackList(info, new Callback<DeleteBlackListBean>() {

            private DeleteBlackListBean mBody;

            @Override
            public void onResponse(Call<DeleteBlackListBean> call, Response<DeleteBlackListBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoDeleteBlackListCallback callback : mCallback) {
                            callback.onDoDeleteBlackListSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DeleteBlackListBean> call, Throwable t) {
                for (IDoDeleteBlackListCallback callback : mCallback) {
                    callback.onDoDeleteBlackListCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoDeleteBlackListCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoDeleteBlackListCallback iDoDeleteBlackListCallback) {
        if (!mCallback.contains(iDoDeleteBlackListCallback)) {
            mCallback.add(iDoDeleteBlackListCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoDeleteBlackListCallback iDoDeleteBlackListCallback) {
        mCallback.remove(iDoDeleteBlackListCallback);
    }


}
