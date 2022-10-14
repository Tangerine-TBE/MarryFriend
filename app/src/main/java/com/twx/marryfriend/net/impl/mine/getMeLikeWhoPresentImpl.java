package com.twx.marryfriend.net.impl.mine;

import android.util.Log;

import com.twx.marryfriend.bean.mine.MeFocusWhoBean;
import com.twx.marryfriend.bean.mine.MeLikeWhoBean;
import com.twx.marryfriend.net.callback.mine.IGetMeFocusWhoCallback;
import com.twx.marryfriend.net.callback.mine.IGetMeLikeWhoCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.mine.IGetMeFocusWhoPresent;
import com.twx.marryfriend.net.present.mine.IGetMeLikeWhoPresent;

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
public class getMeLikeWhoPresentImpl implements IGetMeLikeWhoPresent {

    public static getMeLikeWhoPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetMeLikeWhoCallback> mCallback = new ArrayList<>();

    public getMeLikeWhoPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getMeLikeWhoPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getMeLikeWhoPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getMeLikeWho(Map<String, String> info) {
        handlerLoading();
        mUserData.getMeLikeWho(info, new Callback<MeLikeWhoBean>() {

            private MeLikeWhoBean mBody;

            @Override
            public void onResponse(Call<MeLikeWhoBean> call, Response<MeLikeWhoBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetMeLikeWhoCallback callback : mCallback) {
                            callback.onGetMeLikeWhoSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MeLikeWhoBean> call, Throwable t) {
                Log.i("guo", "erroe: " + t);
                for (IGetMeLikeWhoCallback callback : mCallback) {
                    callback.onGetMeLikeWhoCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetMeLikeWhoCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetMeLikeWhoCallback iGetMeLikeWhoCallback) {
        if (!mCallback.contains(iGetMeLikeWhoCallback)) {
            mCallback.add(iGetMeLikeWhoCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetMeLikeWhoCallback iGetMeLikeWhoCallback) {
        mCallback.remove(iGetMeLikeWhoCallback);
    }

}
