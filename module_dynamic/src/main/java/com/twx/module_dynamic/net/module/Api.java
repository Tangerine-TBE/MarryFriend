package com.twx.module_dynamic.net.module;

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
import com.twx.module_dynamic.bean.TrendFocusBean;
import com.twx.module_dynamic.bean.TrendSaloonBean;
import com.twx.module_dynamic.bean.UploadTrendBean;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface Api {

    // 获取我的动态列表
    @POST("/marryfriend/UserCenter/trendsList")
    Call<MyTrendsListBean> getMyTrendsList(@QueryMap Map<String, Object> params);


    // 上传动态列表
    @POST("/marryfriend/UserCenter/uploadTrend")
    Call<UploadTrendBean> doUploadTrend(@QueryMap Map<String, Object> params);

    // 查看自己的某一条动态
    @POST("/marryfriend/UserCenter/checkOne")
    Call<CheckTrendBean> doCheckTrend(@QueryMap Map<String, Object> params);

    // 删除我的动态
    @POST("/marryfriend/UserCenter/deleteTrend")
    Call<DeleteTrendBean> doDeleteTrend(@QueryMap Map<String, Object> params);


    // 动态的点赞列表
    @POST("/marryfriend/UserCenter/likeList")
    Call<LikeListBean> getLikeList(@QueryMap Map<String, Object> params);

    // 给动态点击送赞
    @POST("/marryfriend/UserCenter/likeClick")
    Call<LikeClickBean> doLikeClick(@QueryMap Map<String, Object> params);

    // 给动态取消点赞
    @POST("/marryfriend/UserCenter/likeCancel")
    Call<LikeCancelBean> doLikeCancel(@QueryMap Map<String, Object> params);


    // 一级评论 动态的父评论列表
    @POST("/marryfriend/UserCenter/discussListOne")
    Call<CommentOneBean> getCommentOne(@QueryMap Map<String, Object> params);

    // 一级评论 给动态提交父评论
    @POST("/marryfriend/UserCenter/discussOneCreate")
    Call<CommentOneCreateBean> doCommentOneCreate(@QueryMap Map<String, Object> params);

    // 一级评论 删除动态的父评价
    @POST("/marryfriend/UserCenter/discussOneDelete")
    Call<CommentOneDeleteBean> doCommentOneDelete(@QueryMap Map<String, Object> params);


    // 二级评论 动态的子评论列表
    @POST("/marryfriend/UserCenter/discussListTwo")
    Call<CommentTwoBean> getCommentTwo(@QueryMap Map<String, Object> params);

    // 二级评论 动态的子评论增加
    @POST("/marryfriend/UserCenter/discussTwoCreate")
    Call<CommentTwoCreateBean> doCommentTwoCreate(@QueryMap Map<String, Object> params);

    // 二级评论 动态的子评论删除
    @POST("/marryfriend/UserCenter/discussTwoDelete")
    Call<CommentTwoDeleteBean> doCommentTwoDelete(@QueryMap Map<String, Object> params);


    // 我关注其它人，列表
    @POST("/marryfriend/UserCenter/mineFocusOtherList")
    Call<MyFocusBean> getMyFocus(@QueryMap Map<String, Object> params);


    // 其它人关注我，列表
    @POST("/marryfriend/UserCenter/otherFocusMineList")
    Call<OtherFocusBean> getOtherFocus(@QueryMap Map<String, Object> params);


    // 点击添加关注别人
    @POST("/marryfriend/UserCenter/plusFocusOther")
    Call<PlusFocusBean> doPlusFocusOther(@QueryMap Map<String, Object> params);


    // 点击取消关注别人
    @POST("/marryfriend/UserCenter/cancelFocusOther")
    Call<CancelFocusBean> doCancelFocusOther(@QueryMap Map<String, Object> params);


    // 动态沙龙列表
    @POST("/marryfriend/TrendsNotice/trendsSaloon")
    Call<TrendSaloonBean> getTrendSaloon(@QueryMap Map<String, Object> params);


    // 关注的人动态列表
    @POST("/marryfriend/TrendsNotice/focousTrends")
    Call<TrendFocusBean> getTrendFocus(@QueryMap Map<String, Object> params);


    // 高德地图-地点检索
    @POST("/v3/place/around")
    Call<PlaceSearchBean> doPlaceSearch(@QueryMap Map<String, Object> params);


}

