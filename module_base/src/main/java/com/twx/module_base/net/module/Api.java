package com.twx.module_base.net.module;

import com.twx.module_base.net.bean.AccessTokenBean;
import com.twx.module_base.net.bean.AutoLoginBean;
import com.twx.module_base.net.bean.BaseInfoUpdateBean;
import com.twx.module_base.net.bean.FaceDetectBean;
import com.twx.module_base.net.bean.FaceVerifyBean;
import com.twx.module_base.net.bean.IdentityVerifyBean;
import com.twx.module_base.net.bean.PhoneLoginBean;
import com.twx.module_base.net.bean.PhotoListBean;
import com.twx.module_base.net.bean.UpdateDemandInfoBean;
import com.twx.module_base.net.bean.UpdateGreetInfoBean;
import com.twx.module_base.net.bean.UpdateMoreInfoBean;
import com.twx.module_base.net.bean.UpdateProportionInfoBean;
import com.twx.module_base.net.bean.UpdateVerifyInfoBean;
import com.twx.module_base.net.bean.UploadPhotoBean;
import com.twx.module_base.net.bean.VerifyCodeBean;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
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

}

