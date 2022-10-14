package com.twx.marryfriend.net.impl.mine;

import android.util.Log;

import com.twx.marryfriend.bean.mine.MeFocusWhoBean;
import com.twx.marryfriend.bean.mine.MeSeeWhoBean;
import com.twx.marryfriend.bean.mine.WhoFocusMeBean;
import com.twx.marryfriend.net.callback.mine.IGetMeFocusWhoCallback;
import com.twx.marryfriend.net.callback.mine.IGetMeSeeWhoCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.mine.IGetMeFocusWhoPresent;
import com.twx.marryfriend.net.present.mine.IGetMeSeeWhoPresent;

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
public class getMeFocusWhoPresentImpl implements IGetMeFocusWhoPresent {

    public static getMeFocusWhoPresentImpl sInstance;
    public final UserData mUserData;

    private List<IGetMeFocusWhoCallback> mCallback = new ArrayList<>();

    public getMeFocusWhoPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static getMeFocusWhoPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new getMeFocusWhoPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void getMeFocusWho(Map<String, String> info) {
        handlerLoading();
        mUserData.getMeFocusWho(info, new Callback<MeFocusWhoBean>() {

            private MeFocusWhoBean mBody;

            @Override
            public void onResponse(Call<MeFocusWhoBean> call, Response<MeFocusWhoBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IGetMeFocusWhoCallback callback : mCallback) {
                            callback.onGetMeFocusWhoSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MeFocusWhoBean> call, Throwable t) {
                Log.i("guo", "erroe: " + t);
                for (IGetMeFocusWhoCallback callback : mCallback) {
                    callback.onGetMeFocusWhoCodeError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IGetMeFocusWhoCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IGetMeFocusWhoCallback iGetMeFocusWhoCallback) {
        if (!mCallback.contains(iGetMeFocusWhoCallback)) {
            mCallback.add(iGetMeFocusWhoCallback);
        }
    }

    @Override
    public void unregisterCallback(IGetMeFocusWhoCallback iGetMeFocusWhoCallback) {
        mCallback.remove(iGetMeFocusWhoCallback);
    }


}
