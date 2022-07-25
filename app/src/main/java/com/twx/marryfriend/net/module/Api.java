package com.twx.marryfriend.net.module;

import com.twx.marryfriend.bean.AccessTokenBean;
import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.BanBean;
import com.twx.marryfriend.bean.BaseInfoUpdateBean;
import com.twx.marryfriend.bean.CityBean;
import com.twx.marryfriend.bean.FaceDetectBean;
import com.twx.marryfriend.bean.FaceVerifyBean;
import com.twx.marryfriend.bean.IdentityVerifyBean;
import com.twx.marryfriend.bean.IndustryBean;
import com.twx.marryfriend.bean.JobBean;
import com.twx.marryfriend.bean.PhoneLoginBean;
import com.twx.marryfriend.bean.PhotoListBean;
import com.twx.marryfriend.bean.SchoolBean;
import com.twx.marryfriend.bean.TextVerifyBean;
import com.twx.marryfriend.bean.UpdateDemandInfoBean;
import com.twx.marryfriend.bean.UpdateGreetInfoBean;
import com.twx.marryfriend.bean.UpdateMoreInfoBean;
import com.twx.marryfriend.bean.UpdateProportionInfoBean;
import com.twx.marryfriend.bean.UpdateVerifyInfoBean;
import com.twx.marryfriend.bean.UploadPhotoBean;
import com.twx.marryfriend.bean.VerifyCodeBean;
import com.twx.marryfriend.bean.dynamic.CancelFocusBean;
import com.twx.marryfriend.bean.dynamic.CheckTrendBean;
import com.twx.marryfriend.bean.dynamic.CommentOneBean;
import com.twx.marryfriend.bean.dynamic.CommentOneCreateBean;
import com.twx.marryfriend.bean.dynamic.CommentOneDeleteBean;
import com.twx.marryfriend.bean.dynamic.CommentTipBean;
import com.twx.marryfriend.bean.dynamic.CommentTwoBean;
import com.twx.marryfriend.bean.dynamic.CommentTwoCreateBean;
import com.twx.marryfriend.bean.dynamic.CommentTwoDeleteBean;
import com.twx.marryfriend.bean.dynamic.DeleteTrendBean;
import com.twx.marryfriend.bean.dynamic.LikeCancelBean;
import com.twx.marryfriend.bean.dynamic.LikeClickBean;
import com.twx.marryfriend.bean.dynamic.LikeListBean;
import com.twx.marryfriend.bean.dynamic.LikeTipBean;
import com.twx.marryfriend.bean.dynamic.MyFocusBean;
import com.twx.marryfriend.bean.dynamic.MyTrendsListBean;
import com.twx.marryfriend.bean.dynamic.OtherFocusBean;
import com.twx.marryfriend.bean.dynamic.PlaceSearchBean;
import com.twx.marryfriend.bean.dynamic.PlusFocusBean;
import com.twx.marryfriend.bean.dynamic.SearchBean;
import com.twx.marryfriend.bean.dynamic.TotalCountBean;
import com.twx.marryfriend.bean.dynamic.TrendFocusBean;
import com.twx.marryfriend.bean.dynamic.TrendSaloonBean;
import com.twx.marryfriend.bean.dynamic.TrendTipBean;
import com.twx.marryfriend.bean.dynamic.UploadTrendBean;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface Api {

    // 发送手机验证码
    @POST("/marryfriend/LoginRegister/sendCode")
    Call<VerifyCodeBean> getVerifyCode(@QueryMap Map<String, Object> params);

    // 验证码登录注册
    @POST("/marryfriend/LoginRegister/mobileLogister")
    Call<PhoneLoginBean> doPhoneLogin(@QueryMap Map<String, Object> params);

    // 阿里云一键登录
    @POST("/marryfriend/LoginRegister/onekeyLogin")
    Call<AutoLoginBean> doAutoLogin(@QueryMap Map<String, Object> params);

    // 修改基础信息
    @POST("/marryfriend/LoginRegister/updateBaseInfo")
    Call<BaseInfoUpdateBean> doUpdateBaseInfo(@QueryMap Map<String, Object> params);

    // 修改更多信息
    @POST("/marryfriend/LoginRegister/updateMoreInfo")
    Call<UpdateMoreInfoBean> doUpdateMoreInfo(@QueryMap Map<String, Object> params);

    // 修改择偶需求
    @POST("/marryfriend/LoginRegister/updateDemandInfo")
    Call<UpdateDemandInfoBean> doUpdateDemandInfo(@QueryMap Map<String, Object> params);

    // 修改认证信息
    @POST("/marryfriend/LoginRegister/updateVerifyInfo")
    Call<UpdateVerifyInfoBean> doUpdateVerifyInfo(@QueryMap Map<String, Object> params);

    // 修改招呼语信息
    @POST("/marryfriend/LoginRegister/updateZhaohuyuInfo")
    Call<UpdateGreetInfoBean> doUpdateGreetInfo(@QueryMap Map<String, Object> params);

    // 查看列表(头像,三张,相册)
    @POST("/marryfriend/LoginRegister/photoList")
    Call<PhotoListBean> getPhotoList(@QueryMap Map<String, Object> params);

    // 图片上传(头像,三张,相册)
    @POST("/marryfriend/LoginRegister/uploadPhoto")
    Call<UploadPhotoBean> doUploadPhoto(@QueryMap Map<String, Object> params);

    // 更新资料完善度
    @POST("/marryfriend/LoginRegister/proportionUpdate")
    Call<UpdateProportionInfoBean> doUpdateProportion(@QueryMap Map<String, Object> params);


    // 获取省市县
    @POST("/marryfriend/GetParameter/provinceCity")
    Call<CityBean> getCity(@QueryMap Map<String, Object> params);


    // 获取学校
    @POST("/marryfriend/GetParameter/manySchool")
    Call<SchoolBean> getSchool(@QueryMap Map<String, Object> params);

    // 获取敏感字
    @POST("/marryfriend/GetParameter/minganWenzi")
    Call<BanBean> getBan(@QueryMap Map<String, Object> params);

    // 获取行业
    @POST("/marryfriend/GetParameter/hangYe")
    Call<IndustryBean> getIndustry(@QueryMap Map<String, Object> params);

    // 获取行业
    @POST("/marryfriend/GetParameter/gangWei")
    Call<JobBean> getJob(@QueryMap Map<String, Object> params);


    // 获取 百度api所需的 Access Token
    @POST("/oauth/2.0/token")
    Call<AccessTokenBean> getAccessToken(@QueryMap Map<String, Object> params);


    // 百度身份证验证
    @POST("/rest/2.0/face/v3/person/idmatch")
    Call<IdentityVerifyBean> doIdentityVerify(@QueryMap Map<String, Object> params);

    // 百度人脸识别认证
    @POST("/rest/2.0/face/v3/person/verify")
    Call<FaceVerifyBean> doFaceVerify(@QueryMap Map<String, Object> params);

    // 百度鉴黄
    @FormUrlEncoded
    @POST("/rest/2.0/solution/v1/img_censor/v2/user_defined")
    Call<FaceDetectBean> doFaceDetect(@FieldMap Map<String, Object> params);

    // 百度文字审核识别
    @FormUrlEncoded
    @POST("/rest/2.0/solution/v1/text_censor/v2/user_defined")
    Call<TextVerifyBean> doTextVerify(@FieldMap Map<String, Object> params);



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


    // 获取评论和点赞的未读次数
    @POST("/marryfriend/TrendsNotice/totalCount")
    Call<TotalCountBean> getTotalCount(@QueryMap Map<String, Object> params);

    // 点赞未读列表
    @POST("/marryfriend/TrendsNotice/dianzanUnreadList")
    Call<LikeTipBean> getTrendTips(@QueryMap Map<String, Object> params);

    // 评论未读列表
    @POST("/marryfriend/TrendsNotice/discussUnreadList")
    Call<CommentTipBean> getCommentTips(@QueryMap Map<String, Object> params);

    // 高德地图-地点检索
    @POST("/v3/place/around")
    Call<PlaceSearchBean> doPlaceSearch(@QueryMap Map<String, Object> params);

    // 百度行政区划区域检索
    @GET("/place/v2/search")
    Call<SearchBean> doSearch(@QueryMap Map<String, Object> params);


}

