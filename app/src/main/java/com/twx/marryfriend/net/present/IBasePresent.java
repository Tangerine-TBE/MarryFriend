package com.twx.marryfriend.net.present;

public interface IBasePresent<T> {

    void registerCallback(T t);

    void unregisterCallback(T t);

}
