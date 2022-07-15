package com.twx.module_dynamic.utils;

/**
 * @author: Administrator
 * @date: 2022/7/5
 */
public class GetDistanceUtils {
    private static final String TAG = "GetDistanceUtils";
    private static final double EARTH_RADIUS = 6378.137;// 地球赤道半径
    private static final double PI = 3.14159265;//祖率

    /**
     * π是弧度制，180度是角度制，d*π／180,表示每角度等于多少弧度。
     *
     * @param d
     * @return
     */
    private static double rad(double d) {
        return d * PI / 180.0;
    }

    /**
     * @param longitude1 某个加油站的经度
     * @param latitude1  某个加油站的纬度
     * @param longitude2 用户当前位置的经度
     * @param latitude2  用户当前位置的纬度
     * @return 单位：千米/km 保留一位小数
     */
    public static double getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        //有小数的情况;注意这里的10000d中的“d”
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;//单位：米
        //s = Math.round(s/0.1d) /10000d   ;//单位：千米 保留两位小数
        s = Math.round(s / 100d) / 10d;//单位：千米 保留一位小数
        return s;
    }


    /**
     * @param lng1 某个加油站的经度
     * @param lat1 某个加油站的纬度
     * @param lng2 用户当前位置的经度
     * @param lat2 用户当前位置的纬度
     * @return 单位：米/m 保留一位小数
     */
    public static double getDistanceMeter(double lng1, double lat1,
                                          double lng2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        return s;

    }

    /**
     * @param lat    给定的坐标纬度
     * @param lon    给定的坐标经度
     * @param radius 需要搜索的半径 比如 10Km
     * @return 返回给定范围内 返回的最大小经度和最大小纬度
     */
    public static double[] getAround(double lat, double lon, int radius) {

        double latitude = lat;
        double longitude = lon;

        double degree = (24901 * 1609) / 360.0;
        double radiusMile = radius;

        double dpmLat = 1 / degree;
        double radiusLat = dpmLat * radiusMile;
        double minLat = latitude - radiusLat;
        double maxLat = latitude + radiusLat;

        double mpdLng = degree * Math.cos(latitude * (PI / 180));
        double dpmLng = 1 / mpdLng;
        double radiusLng = dpmLng * radiusMile;
        double minLng = longitude - radiusLng;
        double maxLng = longitude + radiusLng;
        return new double[]{minLat, minLng, maxLat, maxLng};
    }

}

