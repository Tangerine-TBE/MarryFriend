package com.twx.marryfriend.net.module;

import androidx.annotation.AnyRes;

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
import com.twx.marryfriend.constant.Contents;
import com.twx.marryfriend.net.utils.ApiMapUtil;
import com.twx.marryfriend.net.utils.Md5Util;
import com.twx.marryfriend.net.utils.RetrofitManager;
import com.twx.marryfriend.net.utils.SortMapUtil;

import java.io.File;
import java.io.PipedReader;
import java.util.Map;

import retrofit2.Callback;
import retrofit2.Retrofit;

public class UserData {

    private final Api mApi;

    private final Api mBaiduApi;

    private static UserData sInstance;

    public static UserData getInstance() {
        if (sInstance == null) {
            sInstance = new UserData();
        }
        return sInstance;
    }

    private UserData() {

        Retrofit retrofitUser = RetrofitManager.getInstance().getRetrofitUser();
        Retrofit baiduRetrofitUser = RetrofitManager.getInstance().getBaiduRetrofitUser();

        mApi = retrofitUser.create(Api.class);
        mBaiduApi = baiduRetrofitUser.create(Api.class);

    }

    // 发送手机验证码
    public void getVerifyCode(Map<String, String> map, Callback<VerifyCodeBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.UPDATE_GREET_INFO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.UPDATE_GREET_INFO, currentTimeMillis, random, checkCode, map);
        mApi.doUpdateGreetInfo(map1).enqueue(callback);
    }


    //查看列表(头像,三张,相册)
    public void getPhotoList(Map<String, String> map, Callback<PhotoListBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.UPLOAD_PHOTO + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.UPLOAD_PHOTO, currentTimeMillis, random, checkCode, map);
        mApi.doUploadPhoto(map1).enqueue(callback);
    }

    // 更新资料完善度
    public void doUpdateProportion(Map<String, String> map, Callback<UpdateProportionInfoBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.UPDATE_PROPORTION + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.UPDATE_PROPORTION, currentTimeMillis, random, checkCode, map);
        mApi.doUpdateProportion(map1).enqueue(callback);
    }


    // 获取学校
    public void getCity(Map<String, String> map, Callback<CityBean> callback) {
        // 获取随机数
        int random = 523146;
        //获取时间戳
        long currentTimeMillis = System.currentTimeMillis();
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
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
        String value = SortMapUtil.sortMapByValue(map);
        String checkCode = Md5Util.md5(Contents.TOKEN + currentTimeMillis + random + Contents.DO_TEXT_VERIFY + value);
        Map<String, Object> map1 = ApiMapUtil.setMapValues(Contents.DO_TEXT_VERIFY, currentTimeMillis, random, checkCode, map);
        mBaiduApi.doTextVerify(map1).enqueue(callback);
    }

}

