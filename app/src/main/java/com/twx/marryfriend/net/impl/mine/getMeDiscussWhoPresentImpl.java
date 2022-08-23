package com.twx.marryfriend.net.impl.mine;

import android.util.Log;

import com.twx.marryfriend.bean.mine.MeDiscussWhoBean;
import com.twx.marryfriend.bean.mine.MeFocusWhoBean;
import com.twx.marryfriend.net.callback.mine.IGetMeDiscussWhoCallback;
import com.twx.marryfriend.net.callback.mine.IGetMeFocusWhoCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.mine.IGetMeDiscussWhoPresent;
import com.twx.marryfriend.net.present.mine.IGetMeFocusWhoPresent;

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
public class getMeDiscussWhoPresentImpl implements IGetMeDiscussWhoPresent {

    public static getMeDiscussWhoPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetMeDiscussWhoCallback> mCallback = new ArrayList<>();

    public getMeDiscussWhoPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getMeDiscussWhoPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getMeDiscussWhoPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getMeDiscussWho(Map<String, String> info, Integer page) {
        handlerLoading();
        mUserData.getMeDiscussWho(info, page, new Callback<MeDiscussWhoBean>() {

            private MeDiscussWhoBean mBody;

            @Override
            public void onResponse(Call<MeDiscussWhoBean> call, Response<MeDiscussWhoBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetMeDiscussWhoCallback callback : mCallback) {
                            callback.onGetMeDiscussWhoSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MeDiscussWhoBean> call, Throwable t) {
                Log.i("guo", "erroe: " + t);
                for (IGetMeDiscussWhoCallback callback : mCallback) {
                    callback.onGetMeDiscussWhoCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetMeDiscussWhoCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetMeDiscussWhoCallback iGetMeDiscussWhoCallback) {
        if (!mCallback.contains(iGetMeDiscussWhoCallback)) {
            mCallback.add(iGetMeDiscussWhoCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetMeDiscussWhoCallback iGetMeDiscussWhoCallback) {
        mCallback.remove(iGetMeDiscussWhoCallback);
    }
}
