package com.twx.marryfriend.net.impl.dynamic;

import com.twx.marryfriend.bean.dynamic.PlaceSearchBean;
import com.twx.marryfriend.net.callback.dynamic.IDoPlaceSearchCallback;
import com.twx.marryfriend.net.module.UserData;
import com.twx.marryfriend.net.present.dynamic.IDoPlaceSearchPresent;

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
public class doPlaceSearchPresentImpl implements IDoPlaceSearchPresent {

    public static doPlaceSearchPresentImpl sInstance;
    public final UserData mUserData;

    private List<IDoPlaceSearchCallback> mCallback = new ArrayList<>();

    public doPlaceSearchPresentImpl() {
        this.mUserData = UserData.getInstance();
    }

    public static doPlaceSearchPresentImpl getsInstance() {
        if (sInstance == null) {
            sInstance = new doPlaceSearchPresentImpl();
        }
        return sInstance;
    }

    @Override
    public void doPlaceSearch(Map<String, String> info) {
        handlerLoading();
        mUserData.doPlaceSearch(info, new Callback<PlaceSearchBean>() {

            private PlaceSearchBean mBody;

            @Override
            public void onResponse(Call<PlaceSearchBean> call, Response<PlaceSearchBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mBody = response.body();
                    if (mBody != null) {
                        for (IDoPlaceSearchCallback callback : mCallback) {
                            callback.onDoPlaceSearchSuccess(mBody);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaceSearchBean> call, Throwable t) {
                for (IDoPlaceSearchCallback callback : mCallback) {
                    callback.onDoPlaceSearchError();
                }
            }
        });

    }

    private void handlerLoading() {
        for (IDoPlaceSearchCallback callback : mCallback) {
            callback.onLoading();
        }
    }

    @Override
    public void registerCallback(IDoPlaceSearchCallback iDoPlaceSearchCallback) {
        if (!mCallback.contains(iDoPlaceSearchCallback)) {
            mCallback.add(iDoPlaceSearchCallback);
        }
    }

    @Override
    public void unregisterCallback(IDoPlaceSearchCallback iDoPlaceSearchCallback) {
        mCallback.remove(iDoPlaceSearchCallback);
    }

}
