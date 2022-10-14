package com.twx.marryfriend.net.module;

import android.util.Log;

import com.twx.marryfriend.bean.AccessTokenBean;
import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.BanBean;
import com.twx.marryfriend.bean.BaseInfoUpdateBean;
import com.twx.marryfriend.bean.CityBean;
import com.twx.marryfriend.bean.DeletePhotoBean;
import com.twx.marryfriend.bean.DemandAddressBean;
import com.twx.marryfriend.bean.FaceDetectBean;
import com.twx.marryfriend.bean.FaceVerifyBean;
import com.twx.marryfriend.bean.FiveInfoBean;
import com.twx.marryfriend.bean.GreetInfoBean;
import com.twx.marryfriend.bean.IdentityVerifyBean;
import com.twx.marryfriend.bean.IndustryBean;
import com.twx.marryfriend.bean.JobBean;
import com.twx.marryfriend.bean.dynamic.DeleteTipsBean;
import com.twx.marryfriend.bean.mine.FourTotalBean;
import com.twx.marryfriend.bean.mine.MeDiscussWhoBean;
import com.twx.marryfriend.bean.mine.MeFocusWhoBean;
import com.twx.marryfriend.bean.mine.MeLikeWhoBean;
import com.twx.marryfriend.bean.mine.MeSeeWhoBean;
import com.twx.marryfriend.bean.PhoneLoginBean;
import com.twx.marryfriend.bean.PhotoListBean;
import com.twx.marryfriend.bean.PlusDemandAddressBean;
import com.twx.marryfriend.bean.SchoolBean;
import com.twx.marryfriend.bean.TextVerifyBean;
import com.twx.marryfriend.bean.UpdateDemandInfoBean;
import com.twx.marryfriend.bean.UpdateGreetInfoBean;
import com.twx.marryfriend.bean.UpdateMoreInfoBean;
import com.twx.marryfriend.bean.UpdateProportionInfoBean;
import com.twx.marryfriend.bean.UpdateVerifyInfoBean;
import com.twx.marryfriend.bean.UploadAvatarBean;
import com.twx.marryfriend.bean.UploadPhotoBean;
import com.twx.marryfriend.bean.VerifyCodeBean;
import com.twx.marryfriend.bean.ViewHeadfaceBean;
import com.twx.marryfriend.bean.mine.WhoDiscussMeBean;
import com.twx.marryfriend.bean.mine.WhoFocusMeBean;
import com.twx.marryfriend.bean.mine.WhoLikeMeBean;
import com.twx.marryfriend.bean.mine.WhoSeeMeBean;
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
import com.twx.marryfriend.bean.dynamic.OtherTrendsListBean;
import com.twx.marryfriend.bean.dynamic.PlaceSearchBean;
import com.twx.marryfriend.bean.dynamic.PlusFocusBean;
import com.twx.marryfriend.bean.dynamic.SearchBean;
import com.twx.marryfriend.bean.dynamic.TotalCountBean;
import com.twx.marryfriend.bean.dynamic.TrendFocusBean;
import com.twx.marryfriend.bean.dynamic.TrendSaloonBean;
import com.twx.marryfriend.bean.dynamic.UploadTrendBean;
import com.twx.marryfriend.bean.set.DeleteBlackListBean;
import com.twx.marryfriend.bean.vip.AliPayBean;
import com.twx.marryfriend.bean.vip.BlackListBean;
import com.twx.marryfriend.bean.vip.CoinPriceBean;
import com.twx.marryfriend.bean.vip.CoinRecordBean;
import com.twx.marryfriend.bean.vip.GetPushSetBean;
import com.twx.marryfriend.bean.vip.PreviewOtherBean;
import com.twx.marryfriend.bean.vip.RefreshSelfBean;
import com.twx.marryfriend.bean.vip.ReportOtherBean;
import com.twx.marryfriend.bean.vip.UpdatePushSetBean;
import com.twx.marryfriend.bean.vip.UpdateTokenBean;
import com.twx.marryfriend.bean.vip.UploadFeedbackBean;
import com.twx.marryfriend.bean.vip.VipPriceBean;
import com.twx.marryfriend.constant.Contents;
import com.twx.marryfriend.net.utils.ApiMapUtil;
import com.twx.marryfriend.net.utils.Md5Util;
import com.twx.marryfriend.net.utils.RetrofitManager;
import com.twx.marryfriend.net.utils.SortMapUtil;

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

    private final Api mBaiduMapApi;


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

        Retrofit baiduRetrofitUser = RetrofitManager.getInstance().getBaiduRetrofitUser();
        mBaiduApi = baiduRetrofitUser.create(Api.class);

        Retrofit gaoDeMapRetrofitUser = RetrofitManager.getInstance().getGaodeMapRetrofitUser();
        mMapApi = gaoDeMapRetrofitUser.create(Api.class);

        Retrofit baiduRetrofit = RetrofitManager.getInstance().getBaiduMapRetrofitUser();
        mBaiduMapApi = baiduRetrofit.create(Api.class);

    }

    // 发送手机验证码
    public void getVerifyCode(Map<String, String> map, Callback<VerifyCodeBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_VERIFY + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_VERIFY, currentTimeMillis, random, checkCode, map);
        mApi.getVerifyCode(map1).enqueue(callback);
    }

    // 验证码登录注册
    public void doPhoneLogin(Map<String, String> map, Callback<PhoneLoginBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.PHONE_LOGIN + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.PHONE_LOGIN, currentTimeMillis, random, checkCode, map);
        mApi.doPhoneLogin(map1).enqueue(callback);
    }

    // 阿里云一键登录
    public void doAutoLogin(Map<String, String> map, Callback<AutoLoginBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.AUTO_LOGIN + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.AUTO_LOGIN, currentTimeMillis, random, checkCode, map);
        mApi.doAutoLogin(map1).enqueue(callback);
    }


    // 修改基础信息
    public void doUpdateBaseInfo(Map<String, String> map, Callback<BaseInfoUpdateBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.UPDATE_BASE_INFO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.UPDATE_BASE_INFO, currentTimeMillis, random, checkCode, map);
        mApi.doUpdateBaseInfo(map1).enqueue(callback);
    }

    // 修改更多信息
    public void doUpdateMoreInfo(Map<String, String> map, Callback<UpdateMoreInfoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.UPDATE_MORE_INFO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.UPDATE_MORE_INFO, currentTimeMillis, random, checkCode, map);
        mApi.doUpdateMoreInfo(map1).enqueue(callback);
    }

    // 修改择偶需求
    public void doUpdateDemandInfo(Map<String, String> map, Callback<UpdateDemandInfoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.UPDATE_DEMAND_INFO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.UPDATE_DEMAND_INFO, currentTimeMillis, random, checkCode, map);
        mApi.doUpdateDemandInfo(map1).enqueue(callback);
    }

    // 修改认证信息
    public void doUpdateVerifyInfo(Map<String, String> map, Callback<UpdateVerifyInfoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.UPDATE_VERIFY_INFO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.UPDATE_VERIFY_INFO, currentTimeMillis, random, checkCode, map);
        mApi.doUpdateVerifyInfo(map1).enqueue(callback);
    }

    // 修改招呼语需求
    public void doUpdateGreetInfo(Map<String, String> map, Callback<UpdateGreetInfoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.UPDATE_GREET_INFO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.UPDATE_GREET_INFO, currentTimeMillis, random, checkCode, map);
        mApi.doUpdateGreetInfo(map1).enqueue(callback);
    }

    // 增加择偶省市要求列表
    public void plusDemandAddress(Map<String, String> map, Callback<PlusDemandAddressBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.PLUS_DEMAND_ADDRESS + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.PLUS_DEMAND_ADDRESS, currentTimeMillis, random, checkCode, map);
        mApi.plusDemandAddress(map1).enqueue(callback);
    }

    // 增加择偶省市要求列表
    public void getDemandAddress(Map<String, String> map, Callback<DemandAddressBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_DEMAND_ADDRESS + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_DEMAND_ADDRESS, currentTimeMillis, random, checkCode, map);
        mApi.getDemandAddress(map1).enqueue(callback);
    }


    // 获取 招呼语信息
    public void getGreetInfo(Map<String, String> map, Callback<GreetInfoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_GREET_INFO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_GREET_INFO, currentTimeMillis, random, checkCode, map);
        mApi.getGreetInfo(map1).enqueue(callback);
    }

    // 五个一起获取
    public void getFiveInfo(Map<String, String> map, Callback<FiveInfoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_FIVE_INFO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_FIVE_INFO, currentTimeMillis, random, checkCode, map);
        mApi.getFiveInfo(map1).enqueue(callback);
    }


    //查看列表(头像,三张,相册)
    public void getPhotoList(Map<String, String> map, Callback<PhotoListBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.PHOTO_LIST + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.PHOTO_LIST, currentTimeMillis, random, checkCode, map);
        mApi.getPhotoList(map1).enqueue(callback);
    }

    //图片上传(头像,三张,相册)
    public void doUploadPhoto(Map<String, String> map, Callback<UploadPhotoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.UPLOAD_PHOTO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.UPLOAD_PHOTO, currentTimeMillis, random, checkCode, map);
        mApi.doUploadPhoto(map1).enqueue(callback);
    }


    // 上传头像
    public void doUploadAvatar(Map<String, String> map, Callback<UploadAvatarBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.UPLOAD_AVATAR + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.UPLOAD_AVATAR, currentTimeMillis, random, checkCode, map);
        mApi.doUploadAvatar(map1).enqueue(callback);
    }


    // 查看头像
    public void doViewHeadface(Map<String, String> map, Callback<ViewHeadfaceBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.VIEW_HEADFACE + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.VIEW_HEADFACE, currentTimeMillis, random, checkCode, map);
        mApi.doViewHeadface(map1).enqueue(callback);
    }


    // 删除图片(生活照)
    public void doDeletePhoto(Map<String, String> map, Callback<DeletePhotoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DELETE_PHOTO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DELETE_PHOTO, currentTimeMillis, random, checkCode, map);
        mApi.doDeletePhoto(map1).enqueue(callback);
    }


    // 更新资料完善度
    public void doUpdateProportion(Map<String, String> map, Callback<UpdateProportionInfoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.UPDATE_PROPORTION + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.UPDATE_PROPORTION, currentTimeMillis, random, checkCode, map);
        mApi.doUpdateProportion(map1).enqueue(callback);
    }

    // 最近来访 -- 我看过谁列表
    public void getMeSeeWho(Map<String, String> map, Callback<MeSeeWhoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.ME_SEE_WHO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.ME_SEE_WHO, currentTimeMillis, random, checkCode, map);
        mApi.getMeSeeWho(map1).enqueue(callback);
    }

    // 最近来访 -- 谁看过我列表
    public void getWhoSeeMe(Map<String, String> map, Callback<WhoSeeMeBean> callback) {
        // 获取随机数
        int random = 523146;
        // 获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.WHO_SEE_ME + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.WHO_SEE_ME, currentTimeMillis, random, checkCode, map);
        mApi.getWhoSeeMe(map1).enqueue(callback);
    }


    // 我关注其它人，列表
    public void getMeFocusWho(Map<String, String> map, Callback<MeFocusWhoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.ME_FOCUS_WHO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.ME_FOCUS_WHO, currentTimeMillis, random, checkCode, map);
        mApi.getMeFocusWho(map1).enqueue(callback);
    }

    // 其它人关注我，列表
    public void getWhoFocusMe(Map<String, String> map, Callback<WhoFocusMeBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.WHO_FOCUS_ME + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.WHO_FOCUS_ME, currentTimeMillis, random, checkCode, map);
        mApi.getWhoFocusMe(map1).enqueue(callback);
    }


    // 我点过谁赞列表
    public void getMeLikeWho(Map<String, String> map, Callback<MeLikeWhoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.ME_LIKE_WHO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.ME_LIKE_WHO, currentTimeMillis, random, checkCode, map);
        mApi.getMeLikeWho(map1).enqueue(callback);
    }

    // 谁点过我赞列表
    public void getWhoLikeMe(Map<String, String> map, Callback<WhoLikeMeBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.WHO_LIKE_ME + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.WHO_LIKE_ME, currentTimeMillis, random, checkCode, map);
        mApi.getWhoLikeMe(map1).enqueue(callback);
    }


    // 我评论过谁的列表
    public void getMeDiscussWho(Map<String, String> map, Callback<MeDiscussWhoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.ME_DISCUSS_WHO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.ME_DISCUSS_WHO, currentTimeMillis, random, checkCode, map);
        mApi.getMeDiscussWho(map1).enqueue(callback);
    }

    // 谁评论过我的列表
    public void getWhoDiscussMe(Map<String, String> map, Callback<WhoDiscussMeBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.WHO_DISCUSS_ME + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.WHO_DISCUSS_ME, currentTimeMillis, random, checkCode, map);
        mApi.getWhoDiscussMe(map1).enqueue(callback);
    }


    // 获取四个统计数字
    public void getFourTotal(Map<String, String> map, Callback<FourTotalBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_FOUR_TOTAL + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_FOUR_TOTAL, currentTimeMillis, random, checkCode, map);
        mApi.getFourTotal(map1).enqueue(callback);
    }


    // 获取学校
    public void getCity(Map<String, String> map, Callback<CityBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_CITY + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_CITY, currentTimeMillis, random, checkCode, map);
        mApi.getCity(map1).enqueue(callback);
    }


    // 获取学校
    public void getSchool(Map<String, String> map, Callback<SchoolBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_SCHOOL + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_SCHOOL, currentTimeMillis, random, checkCode, map);
        mApi.getSchool(map1).enqueue(callback);
    }


    // 获取敏感字
    public void getBan(Map<String, String> map, Callback<BanBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_BAN + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_BAN, currentTimeMillis, random, checkCode, map);
        mApi.getBan(map1).enqueue(callback);
    }

    // 获取行业
    public void getIndustry(Map<String, String> map, Callback<IndustryBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_INDUSTRY + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_INDUSTRY, currentTimeMillis, random, checkCode, map);
        mApi.getIndustry(map1).enqueue(callback);
    }

    // 获取行业
    public void getJob(Map<String, String> map, Callback<JobBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_JOB + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_JOB, currentTimeMillis, random, checkCode, map);
        mApi.getJob(map1).enqueue(callback);
    }


    // 获取 百度api所需的 Access Token
    public void getAccessToken(Map<String, String> map, Callback<AccessTokenBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_ACCESS_TOKEN + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_ACCESS_TOKEN, currentTimeMillis, random, checkCode, map);
        mBaiduApi.getAccessToken(map1).enqueue(callback);
    }


    // 百度身份证验证
    public void doIdentityVerify(Map<String, String> map, Callback<IdentityVerifyBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_IDENTITY_VERIFY + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_IDENTITY_VERIFY, currentTimeMillis, random, checkCode, map);
        mBaiduApi.doIdentityVerify(map1).enqueue(callback);
    }


    // 百度人脸实名认证
    public void doFaceVerify(Map<String, String> map, Callback<FaceVerifyBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_FACE_VERIFY + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_FACE_VERIFY, currentTimeMillis, random, checkCode, map);
        mBaiduApi.doFaceVerify(map1).enqueue(callback);
    }


    // 百度人脸识别(鉴黄)
    public void doFaceDetect(Map<String, String> map, Callback<FaceDetectBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_FACE_DETECT + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_FACE_DETECT, currentTimeMillis, random, checkCode, map);
        mBaiduApi.doFaceDetect(map1).enqueue(callback);
    }

    // 百度文字审核识别
    public void doTextVerify(Map<String, String> map, Callback<TextVerifyBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_TEXT_VERIFY + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_TEXT_VERIFY, currentTimeMillis, random, checkCode, map);
        mBaiduApi.doTextVerify(map1).enqueue(callback);
    }


    // 获取我的动态列表
    public void getMyTrendsList(Map<String, String> map, Callback<MyTrendsListBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_MY_TREND_LIST + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_MY_TREND_LIST, currentTimeMillis, random, checkCode, map);
        mApi.getMyTrendsList(map1).enqueue(callback);
    }


    // 获取其它人动态列表
    public void getOtherTrendsList(Map<String, String> map, Callback<OtherTrendsListBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_OTHER_TREND_LIST + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_OTHER_TREND_LIST, currentTimeMillis, random, checkCode, map);
        mApi.getOtherTrendsList(map1).enqueue(callback);
    }


    // 上传动态列表
    public void doUploadTrend(Map<String, String> map, Callback<UploadTrendBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
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
        String value = SortMapUtil.sortMapByKey(map);
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
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_DELETE_TREND + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_DELETE_TREND, currentTimeMillis, random, checkCode, map);
        mApi.doDeleteTrend(map1).enqueue(callback);
    }


    // 动态的点赞列表
    public void getLikeList(Map<String, String> map, Callback<LikeListBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_LIKE_LIST + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_LIKE_LIST, currentTimeMillis, random, checkCode, map);
        mApi.getLikeList(map1).enqueue(callback);
    }

    // 给动态点击送赞
    public void doLikeClick(Map<String, String> map, Callback<LikeClickBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
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
        String value = SortMapUtil.sortMapByKey(map);
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
        String value = SortMapUtil.sortMapByKey(map);
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
        String value = SortMapUtil.sortMapByKey(map);
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
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_COMMENT_ONE_DELETE + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_COMMENT_ONE_DELETE, currentTimeMillis, random, checkCode, map);
        mApi.doCommentOneDelete(map1).enqueue(callback);
    }


    // 二级评论 动态的子评论列表
    public void getCommentTwo(Map<String, String> map, Callback<CommentTwoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_COMMENT_TWO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_COMMENT_TWO, currentTimeMillis, random, checkCode, map);
        mApi.getCommentTwo(map1).enqueue(callback);
    }

    // 二级评论 动态的子评论增加
    public void doCommentTwoCreate(Map<String, String> map, Callback<CommentTwoCreateBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
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
        String value = SortMapUtil.sortMapByKey(map);
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
        String value = SortMapUtil.sortMapByKey(map);
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
        String value = SortMapUtil.sortMapByKey(map);
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
        String value = SortMapUtil.sortMapByKey(map);
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
        String value = SortMapUtil.sortMapByKey(map);
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
        String value = SortMapUtil.sortMapByKey(map);
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
        String value = SortMapUtil.sortMapByKey(map);
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
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_TOTAL_COUNT + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_TOTAL_COUNT, currentTimeMillis, random, checkCode, map);
        mApi.getTotalCount(map1).enqueue(callback);
    }

    // 点赞未读列表
    public void getTrendTips(Map<String, String> map, Callback<LikeTipBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_TREND_TIP + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_TREND_TIP, currentTimeMillis, random, checkCode, map);
        mApi.getTrendTips(map1).enqueue(callback);
    }

    // 评论未读列表
    public void getCommentTips(Map<String, String> map, Callback<CommentTipBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_COMMENT_TIP + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_COMMENT_TIP, currentTimeMillis, random, checkCode, map);
        mApi.getCommentTips(map1).enqueue(callback);
    }

    // 评论未读列表
    public void doDeleteTips(Map<String, String> map, Callback<DeleteTipsBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_DELETE_TIPS + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_DELETE_TIPS, currentTimeMillis, random, checkCode, map);
        mApi.doDeleteTips(map1).enqueue(callback);
    }


    // 会员价格列表
    public void getVipPrice(Map<String, String> map, Callback<VipPriceBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_VIP_PRICE + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_VIP_PRICE, currentTimeMillis, random, checkCode, map);
        mApi.getVipPrice(map1).enqueue(callback);
    }

    // 金币价格列表
    public void getCoinPrice(Map<String, String> map, Callback<CoinPriceBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String key = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_COIN_PRICE + key);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_COIN_PRICE, currentTimeMillis, random, checkCode, map);
        mApi.getCoinPrice(map1).enqueue(callback);
    }

    // 苹果安卓发起支付宝
    public void doAliPay(Map<String, String> map, Callback<AliPayBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_ALI_PAY + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_ALI_PAY, currentTimeMillis, random, checkCode, map);
        mApi.doAliPay(map1).enqueue(callback);
    }

    // 预览对方
    public void doPreviewOther(Map<String, String> map, Callback<PreviewOtherBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_PREVIEW_OTHER + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_PREVIEW_OTHER, currentTimeMillis, random, checkCode, map);
        mApi.doPreviewOther(map1).enqueue(callback);
    }

    // 金币收支记录列表
    public void getCoinRecord(Map<String, String> map, Callback<CoinRecordBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_COIN_RECORD + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_COIN_RECORD, currentTimeMillis, random, checkCode, map);
        mApi.getCoinRecord(map1).enqueue(callback);
    }


    // 刷新金币与会员
    public void doRefreshSelf(Map<String, String> map, Callback<RefreshSelfBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_REFRESH_SELF + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_REFRESH_SELF, currentTimeMillis, random, checkCode, map);
        mApi.doRefreshSelf(map1).enqueue(callback);
    }


    // 上传用户反馈
    public void doUploadFeedback(Map<String, String> map, Callback<UploadFeedbackBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_UPLOAD_FEEDBACK + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_UPLOAD_FEEDBACK, currentTimeMillis, random, checkCode, map);
        mApi.doUploadFeedback(map1).enqueue(callback);
    }


    // 投诉举报其它人
    public void doReportOther(Map<String, String> map, Callback<ReportOtherBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_REPORT_OTHER + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_REPORT_OTHER, currentTimeMillis, random, checkCode, map);
        mApi.doReportOther(map1).enqueue(callback);
    }


    // 修改友盟token
    public void doUpdateToken(Map<String, String> map, Callback<UpdateTokenBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_UPDATE_TOKEN + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_UPDATE_TOKEN, currentTimeMillis, random, checkCode, map);
        mApi.doUpdateToken(map1).enqueue(callback);
    }


    // 修改友盟推送设置
    public void doUpdatePushSet(Map<String, String> map, Callback<UpdatePushSetBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_UPDATE_PUSH_SET + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_UPDATE_PUSH_SET, currentTimeMillis, random, checkCode, map);
        mApi.doUpdatePushSet(map1).enqueue(callback);
    }


    // 获取友盟推送状态
    public void getPushSet(Map<String, String> map, Callback<GetPushSetBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_PUSH_SET + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_PUSH_SET, currentTimeMillis, random, checkCode, map);
        mApi.getPushSet(map1).enqueue(callback);
    }

    // 屏蔽列表
    public void getBlackList(Map<String, String> map, Callback<BlackListBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.GET_BLACK_LIST + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.GET_BLACK_LIST, currentTimeMillis, random, checkCode, map);
        mApi.getBlackList(map1).enqueue(callback);
    }


    // 删除屏蔽会话
    public void doDeleteBlackList(Map<String, String> map, Callback<DeleteBlackListBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DELETE_BLACK_LIST + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DELETE_BLACK_LIST, currentTimeMillis, random, checkCode, map);
        mApi.doDeleteBlackList(map1).enqueue(callback);
    }


    // 高德地图-地点检索
    public void doPlaceSearch(Map<String, String> map, Callback<PlaceSearchBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByKey(map);
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
        String value = SortMapUtil.sortMapByKey(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.BAIDU_SEARCH + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.BAIDU_SEARCH, currentTimeMillis, random, checkCode, map);
        mBaiduMapApi.doSearch(map1).enqueue(callback);
    }


}

