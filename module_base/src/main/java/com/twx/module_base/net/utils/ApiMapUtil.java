package com.twx.module_base.net.utils;

import com.twx.module_base.constant.Contents;

import java.util.HashMap;
import java.util.Map;

public class ApiMapUtil {

    public static Map<String, Object> setMapValues(String service, long currentTimeMillis, int random, String md5, Map<String, String> userInfo) {
        Map<String, Object> map = new HashMap<>();
        map.put(Contents.SERVICE, service);
        map.put(Contents.TIMESTAMP, currentTimeMillis);
        map.put(Contents.NONCE, random);
        map.put(Contents.SIGNATURE, md5);
        map.putAll(userInfo);
        return map;
    }

    public static Map<String, Object> setMapValues(String service, long currentTimeMillis, int random, String md5, Map<String, String> userInfo,int Kind) {
        Map<String, Object> map = new HashMap<>();
        map.put(Contents.SERVICE, service);
        map.put(Contents.TIMESTAMP, currentTimeMillis);
        map.put(Contents.NONCE, random);
        map.put(Contents.SIGNATURE, md5);
        map.put(Contents.KIND, Kind);
        map.putAll(userInfo);
        return map;
    }

}
