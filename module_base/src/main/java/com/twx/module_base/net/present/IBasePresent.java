package com.twx.module_base.net.present;

public interface IBasePresent<T> {

    void registerCallback(T t);

    void unregisterCallback(T t);

}
