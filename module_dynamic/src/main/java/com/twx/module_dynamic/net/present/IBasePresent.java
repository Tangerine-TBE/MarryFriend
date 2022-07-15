package com.twx.module_dynamic.net.present;

public interface IBasePresent<T> {

    void registerCallback(T t);

    void unregisterCallback(T t);

}
