package com.twx.module_dynamic.net.module;

import com.twx.module_base.constant.Contents;
import com.twx.module_dynamic.bean.CancelFocusBean;
import com.twx.module_dynamic.bean.CheckTrendBean;
import com.twx.module_dynamic.bean.CommentOneBean;
import com.twx.module_dynamic.bean.CommentOneCreateBean;
import com.twx.module_dynamic.bean.CommentOneDeleteBean;
import com.twx.module_dynamic.bean.CommentTwoBean;
import com.twx.module_dynamic.bean.CommentTwoCreateBean;
import com.twx.module_dynamic.bean.CommentTwoDeleteBean;
import com.twx.module_dynamic.bean.DeleteTrendBean;
import com.twx.module_dynamic.bean.LikeCancelBean;
import com.twx.module_dynamic.bean.LikeClickBean;
import com.twx.module_dynamic.bean.LikeListBean;
import com.twx.module_dynamic.bean.MyFocusBean;
import com.twx.module_dynamic.bean.MyTrendsListBean;
import com.twx.module_dynamic.bean.OtherFocusBean;
import com.twx.module_dynamic.bean.PlaceSearchBean;
import com.twx.module_dynamic.bean.PlusFocusBean;
import com.twx.module_dynamic.bean.SearchBean;
import com.twx.module_dynamic.bean.TotalCountBean;
import com.twx.module_dynamic.bean.TrendFocusBean;
import com.twx.module_dynamic.bean.TrendSaloonBean;
import com.twx.module_dynamic.bean.TrendTipBean;
import com.twx.module_dynamic.bean.UploadTrendBean;
import com.twx.module_dynamic.net.utils.ApiMapUtil;
import com.twx.module_dynamic.net.utils.Md5Util;
import com.twx.module_dynamic.net.utils.RetrofitManager;
import com.twx.module_dynamic.net.utils.SortMapUtil;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public class UserData {

    private final Api mApi;
    private final Api mBaiduApi;
    private final Api mMapApi;

    private static UserData sInstance;

    public static UserData getInstance() {
        if (sInstance == null) {
            sInstance = new UserData();
        }
        return sInstance;
    }

    private UserData() {

        Retrofit retrofitUser = RetrofitManager.getInstance().getRetrofitUser();
        mApi = retrofitUser.create(Api.class);

        Retrofit gaoDeMapRetrofitUser = RetrofitManager.getInstance().getGaodeMapRetrofitUser();
        mMapApi = gaoDeMapRetrofitUser.create(Api.class);

        Retrofit baiduRetrofit = RetrofitManager.getInstance().getBaiduRetrofitUser();
        mBaiduApi = baiduRetrofit.create(Api.class);

    }


    // 获取我的动态列表
    public void getMyTrendsList(Map<String, String> map, Integer page, Integer size, Callback<MyTrendsListBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_MY_TREND_LIST + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_MY_TREND_LIST, currentTimeMillis, random, checkCode, map, page, size);
        mApi.getMyTrendsList(map1).enqueue(callback);
    }

    // 上传动态列表
    public void doUploadTrend(Map<String, String> map, Callback<UploadTrendBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_UPLOAD_TREND + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_UPLOAD_TREND, currentTimeMillis, random, checkCode, map);
        mApi.doUploadTrend(map1).enqueue(callback);
    }


    // 查看自己的某一条动态
    public void doCheckTrend(Map<String, String> map, Callback<CheckTrendBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_CHECK_TREND + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_CHECK_TREND, currentTimeMillis, random, checkCode, map);
        mApi.doCheckTrend(map1).enqueue(callback);
    }

    // 删除我的动态
    public void doDeleteTrend(Map<String, String> map, Callback<DeleteTrendBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_DELETE_TREND + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_DELETE_TREND, currentTimeMillis, random, checkCode, map);
        mApi.doDeleteTrend(map1).enqueue(callback);
    }


    // 动态的点赞列表
    public void getLikeList(Map<String, String> map, Integer page, Integer size, Callback<LikeListBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_LIKE_LIST + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_LIKE_LIST, currentTimeMillis, random, checkCode, map, page, size);
        mApi.getLikeList(map1).enqueue(callback);
    }

    // 给动态点击送赞
    public void doLikeClick(Map<String, String> map, Callback<LikeClickBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_LICK_CLICK + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_LICK_CLICK, currentTimeMillis, random, checkCode, map);
        mApi.doLikeClick(map1).enqueue(callback);
    }

    // 给动态取消点赞
    public void doLikeCancel(Map<String, String> map, Callback<LikeCancelBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_LICK_CANCEL + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_LICK_CANCEL, currentTimeMillis, random, checkCode, map);
        mApi.doLikeCancel(map1).enqueue(callback);
    }

    // 一级评论 动态的父评论列表
    public void getCommentOne(Map<String, String> map, Callback<CommentOneBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_COMMENT_ONE + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_COMMENT_ONE, currentTimeMillis, random, checkCode, map);
        mApi.getCommentOne(map1).enqueue(callback);
    }

    // 一级评论 给动态提交父评论
    public void doCommentOneCreate(Map<String, String> map, Callback<CommentOneCreateBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_COMMENT_ONE_CREATE + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_COMMENT_ONE_CREATE, currentTimeMillis, random, checkCode, map);
        mApi.doCommentOneCreate(map1).enqueue(callback);
    }


    // 一级评论 删除动态的父评价
    public void doCommentOneDelete(Map<String, String> map, Callback<CommentOneDeleteBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_COMMENT_ONE_DELETE + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_COMMENT_ONE_DELETE, currentTimeMillis, random, checkCode, map);
        mApi.doCommentOneDelete(map1).enqueue(callback);
    }


    // 二级评论 动态的子评论列表
    public void getCommentTwo(Map<String, String> map,Integer page, Integer size, Callback<CommentTwoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_COMMENT_TWO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_COMMENT_TWO, currentTimeMillis, random, checkCode, map, page, size);
        mApi.getCommentTwo(map1).enqueue(callback);
    }

    // 二级评论 动态的子评论增加
    public void doCommentTwoCreate(Map<String, String> map, Callback<CommentTwoCreateBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_COMMENT_TWO_CREATE + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_COMMENT_TWO_CREATE, currentTimeMillis, random, checkCode, map);
        mApi.doCommentTwoCreate(map1).enqueue(callback);
    }


    // 二级评论 动态的子评论删除
    public void doCommentTwoDelete(Map<String, String> map, Callback<CommentTwoDeleteBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_COMMENT_TWO_DELETE + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_COMMENT_TWO_DELETE, currentTimeMillis, random, checkCode, map);
        mApi.doCommentTwoDelete(map1).enqueue(callback);
    }


    // 我关注其它人，列表
    public void getMyFocus(Map<String, String> map, Callback<MyFocusBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.MINE_FOCUS_OTHER_LIST + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.MINE_FOCUS_OTHER_LIST, currentTimeMillis, random, checkCode, map);
        mApi.getMyFocus(map1).enqueue(callback);
    }


    // 其它人关注我，列表
    public void getOtherFocus(Map<String, String> map, Callback<OtherFocusBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.OTHER_FOCUS_MINE_LIST + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.OTHER_FOCUS_MINE_LIST, currentTimeMillis, random, checkCode, map);
        mApi.getOtherFocus(map1).enqueue(callback);
    }


    // 点击添加关注别人
    public void doPlusFocusOther(Map<String, String> map, Callback<PlusFocusBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_PLUS_FOCUS_OTHER + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_PLUS_FOCUS_OTHER, currentTimeMillis, random, checkCode, map);
        mApi.doPlusFocusOther(map1).enqueue(callback);
    }


    // 点击取消关注别人
    public void doCancelFocusOther(Map<String, String> map, Callback<CancelFocusBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_CANCEL_FOCUS_OTHER + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_CANCEL_FOCUS_OTHER, currentTimeMillis, random, checkCode, map);
        mApi.doCancelFocusOther(map1).enqueue(callback);
    }


    // 动态沙龙列表
    public void getTrendSaloon(Map<String, String> map, Callback<TrendSaloonBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_TREND_SALOON + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_TREND_SALOON, currentTimeMillis, random, checkCode, map);
        mApi.getTrendSaloon(map1).enqueue(callback);
    }


    // 关注的人动态列表
    public void getTrendFocus(Map<String, String> map, Callback<TrendFocusBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_TREND_FOCUS + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_TREND_FOCUS, currentTimeMillis, random, checkCode, map);
        mApi.getTrendFocus(map1).enqueue(callback);
    }


    // 获取评论和点赞的未读次数
    public void getTotalCount(Map<String, String> map, Callback<TotalCountBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_TOTAL_COUNT + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_TOTAL_COUNT, currentTimeMillis, random, checkCode, map);
        mApi.getTotalCount(map1).enqueue(callback);
    }

    // 点赞未读列表
    public void getTrendTips(Map<String, String> map,Integer page, Callback<TrendTipBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_TREND_TIP + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_TREND_TIP, currentTimeMillis, random, checkCode, map,page,10);
        mApi.getTrendTips(map1).enqueue(callback);
    }


    // 高德地图-地点检索
    public void doPlaceSearch(Map<String, String> map, Callback<PlaceSearchBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.PLACE_SEARCH + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.PLACE_SEARCH, currentTimeMillis, random, checkCode, map);
        mMapApi.doPlaceSearch(map1).enqueue(callback);
    }

    // 百度行政区划区域检索
    public void doSearch(Map<String, String> map, Callback<SearchBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.BAIDU_SEARCH + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.BAIDU_SEARCH, currentTimeMillis, random, checkCode, map);
        mBaiduApi.doSearch(map1).enqueue(callback);
    }

}

