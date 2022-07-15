package com.twx.marryfriend.login.retrieve.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import com.baidu.idl.face.platform.FaceSDKManager
import com.baidu.idl.face.platform.ui.utils.IntentUtils
import com.baidu.idl.face.platform.utils.Base64Utils
import com.baidu.idl.face.platform.utils.DensityUtils
import com.blankj.utilcode.util.*
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.*
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.main.MainActivity
import com.twx.marryfriend.net.callback.*
import com.twx.marryfriend.net.impl.*
import kotlinx.android.synthetic.main.activity_collect_success.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.URLDecoder
import java.util.*

open class CollectionSuccessActivity : MainBaseViewActivity(), IDoFaceVerifyCallback,
    IDoUpdateBaseInfoCallback, IDoUpdateMoreInfoCallback, IDoUpdateDemandInfoCallback,
    IDoUploadPhotoCallback {

    protected var mDestroyType = ""

    private var str = ""

    // 各文件是否上传完成
    private var photoCompleteLoad = false
    private var demandCompleteLoad = false
    private var moreInfoCompleteLoad = false
    private var baseInfoCompleteLoad = false

    private lateinit var doFaceVerifyPresent: doFaceVerifyPresentImpl

    private lateinit var doUpdateMoreInfoPresent: doUpdateMoreInfoPresentImpl
    private lateinit var doUpdateBaseInfoPresent: doUpdateBaseInfoPresentImpl
    private lateinit var updateDemandInfoPresent: doUpdateDemandInfoPresentImpl
    private lateinit var uploadPhotoPresent: doUploadPhotoPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_collect_success

    override fun initView() {
        super.initView()


        Log.i("guo"," ageMin : ${SPStaticUtils.getInt(Constant.TA_AGE_MIN, 0)}")
        Log.i("guo"," ageMax : ${SPStaticUtils.getInt(Constant.TA_AGE_MAX, 0)}")

        doFaceVerifyPresent = doFaceVerifyPresentImpl.getsInstance()
        doFaceVerifyPresent.registerCallback(this)

        doUpdateMoreInfoPresent = doUpdateMoreInfoPresentImpl.getsInstance()
        doUpdateMoreInfoPresent.registerCallback(this)

        doUpdateBaseInfoPresent = doUpdateBaseInfoPresentImpl.getsInstance()
        doUpdateBaseInfoPresent.registerCallback(this)

        updateDemandInfoPresent = doUpdateDemandInfoPresentImpl.getsInstance()
        updateDemandInfoPresent.registerCallback(this)

        uploadPhotoPresent = doUploadPhotoPresentImpl.getsInstance()
        uploadPhotoPresent.registerCallback(this)

    }

    override fun initLoadData() {
        super.initLoadData()

        update()


        // 头像验证所需
//        mDestroyType = intent.getStringExtra("destroyType")!!
//        val bmpStr = IntentUtils.getInstance().bitmap
//        if (TextUtils.isEmpty(bmpStr)) {
//            return
//        }
//        var bmp = base64ToBitmap(bmpStr)
//        saveImage(bmp)
//        bmp = FaceSDKManager.getInstance().scaleImage(bmp,
//            DensityUtils.dip2px(applicationContext, 97f),
//            DensityUtils.dip2px(applicationContext, 97f))
//        circle_head.setImageBitmap(bmp)
//        str = bmpStr

//        SecRequest.sendMessage(this,bmpStr,0)


    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        btn_recollect.setOnClickListener {
            val map: MutableMap<String, String> = TreeMap()
            map["access_token"] = "24.4b751c0ec563309da71a3aa85d43236f.2592000.1656486335.282335-26278103"
            map["Content-Type"] = "application/json"
            map["image_type"] = "BASE64"
            map["image"] = str.toString()

//            map["image"] = "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"
            map["id_card_number"] = "421081199911123210"
            map["quality_control"] = "NONE"
            map["name"] = URLDecoder.decode("张三", "UTF-8")
            map["liveness_control"] = "NONE"
            map["spoofing_control"] = "NONE"
            doFaceVerifyPresent.doFaceVerify(map)

            Log.i("guo", "str : $str")

        }

    }


    // 开始上传信息
    private fun update() {

        val moreInfoMap: MutableMap<String, String> = TreeMap()
        moreInfoMap[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
        moreInfoMap[Contents.MORE_UPDATE] = getMoreInfo()
        doUpdateMoreInfoPresent.doUpdateMoreInfo(moreInfoMap)

        val baseInfoMap: MutableMap<String, String> = TreeMap()
        baseInfoMap[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
        baseInfoMap[Contents.BASE_UPDATE] = getBaseInfo()
        doUpdateBaseInfoPresent.doUpdateBaseInfo(baseInfoMap)

        val demandInfoMap: MutableMap<String, String> = TreeMap()
        demandInfoMap[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
        demandInfoMap[Contents.DEMAND_UPDATE] = getDemandInfo()
        updateDemandInfoPresent.doUpdateDemandInfo(demandInfoMap)

        Log.i("guo", "avatar update : ${SPStaticUtils.getString(Constant.ME_AVATAR)}")

        val uploadPhotoMap: MutableMap<String, String> = TreeMap()
        uploadPhotoMap[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
        uploadPhotoMap[Contents.IMAGE_URL] = SPStaticUtils.getString(Constant.ME_AVATAR)
        uploadPhotoMap[Contents.FILE_TYPE] = "png"
        uploadPhotoMap[Contents.FILE_NAME] = "head.png"
        uploadPhotoMap[Contents.CONTENT] = "0"
        uploadPhotoMap[Contents.KIND] = 1.toString()
        uploadPhotoPresent.doUploadPhoto(uploadPhotoMap)

    }


    // 需要上传的基础信息
    private fun getBaseInfo(): String {

        val sex = SPStaticUtils.getInt(Constant.ME_SEX, 0)
        val nick = SPStaticUtils.getString(Constant.ME_NAME, "")
        val age = SPStaticUtils.getInt(Constant.ME_AGE, 0)
        val birthday = SPStaticUtils.getString(Constant.ME_BIRTH, "")
        val height = SPStaticUtils.getInt(Constant.ME_HEIGHT, 0)
        val school = SPStaticUtils.getString(Constant.ME_SCHOOL, "")
        val edu = SPStaticUtils.getInt(Constant.ME_EDU, 0)
        val industryCode = SPStaticUtils.getInt(Constant.ME_INDUSTRY_CODE, 0)
        val industryName = SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME, "")
        val occupationCode = SPStaticUtils.getInt(Constant.ME_OCCUPATION_CODE, 0)
        val occupationName = SPStaticUtils.getString(Constant.ME_OCCUPATION_NAME, "")
        val province = SPStaticUtils.getString(Constant.ME_WORK_PROVINCE_CODE, "")
        val cityCode = SPStaticUtils.getString(Constant.ME_WORK_CITY_CODE, "")
        val cityName = SPStaticUtils.getString(Constant.ME_WORK_CITY_NAME, "")
        val home = SPStaticUtils.getString(Constant.ME_HOME, "")
        val income = SPStaticUtils.getInt(Constant.ME_INCOME, 0)
        val marryState = SPStaticUtils.getInt(Constant.ME_MARRY_STATE, 0)
        val introduce = SPStaticUtils.getString(Constant.ME_INTRODUCE, "")
        val hobby = SPStaticUtils.getString(Constant.ME_HOBBY, "")
        val ta = SPStaticUtils.getString(Constant.ME_TA, "")

        val baseInfo =
            " {\"user_sex\":                $sex, " +               // 性别
                    "\"nick\":              \"$nick\"," +           // 昵称
                    "\"age\":               $age," +                // 年龄
                    "\"birthday\":          \"$birthday\"," +       // 出生年月日
                    "\"height\":            $height," +             // 身高厘米
                    "\"school_name\":       \"$school\"," +         // 学校名字
                    "\"education\":         $edu," +                // 学历
                    "\"industry_num\":      $industryCode," +       // 行业编码
                    "\"industry_str\":      \"$industryName\"," +       // 行业名字
                    "\"occupation_num\":    $occupationCode," +     // 岗位编码
                    "\"occupation_str\":    \"$occupationName\"," +     // 岗位名字
                    "\"work_province_num\": \"$province\"," +           // 工作省份编码
                    "\"work_city_num\":     \"$cityCode\"," +           // 工作城市编码
                    "\"work_city_str\":     \"$cityName\"," +           // 工作城市名字
                    "\"hometown\":          \"$home\"," +               // 故乡
                    "\"salary_range\":      $income," +             // 月薪范围
                    "\"marry_had\":         $marryState," +         // 当前婚育状况
                    "\"introduce_self\":    \"$introduce\"," +          // 文字自我介绍
                    "\"daily_hobbies\":     \"$hobby\"," +              // 日常兴趣爱好
                    " \"ta_in_my_mind\":    \"$ta\"}"                   // 我心目中的Ta

        return baseInfo

    }

    // 需要上传的更多信息
    private fun getMoreInfo(): String {

        val sex = SPStaticUtils.getInt(Constant.ME_SEX, 0)
        val weight = SPStaticUtils.getInt(Constant.ME_WEIGHT, 0)
        val body = SPStaticUtils.getInt(Constant.ME_BODY, 0)
        val blood = SPStaticUtils.getString(Constant.ME_BLOOD, "")
        val constellation = SPStaticUtils.getString(Constant.ME_CONSTELLATION, "")
        val nationality = SPStaticUtils.getString(Constant.ME_NATIONALITY, "")
        val loveTarget = SPStaticUtils.getInt(Constant.ME_LOVE_TARGET, 0)
        val loveTargetShow = SPStaticUtils.getInt(Constant.ME_LOVE_TARGET_SHOW, 0)
        val car = SPStaticUtils.getInt(Constant.ME_CAR, 0)
        val house = SPStaticUtils.getInt(Constant.ME_HOUSE, 0)
        val smoke = SPStaticUtils.getInt(Constant.ME_SMOKE, 0)
        val drink = SPStaticUtils.getInt(Constant.ME_DRINK, 0)
        val haveChild = SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0)
        val wantChild = SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0)
        val marryTime = SPStaticUtils.getInt(Constant.ME_MARRY_TIME, 0)

        val moreInfo =
            " {\"user_sex\": $sex, " +                        // 性别
                    "\"weight\":       $weight," +            // 体重公斤
                    "\"figure_nan\":       $body," +          // 身材男
                    "\"figure_nv\":       $body," +           // 身材女
                    "\"blood_type\":   \"$blood\"," +         // 血型，不作筛选条
                    "\"constellation\":   \"$constellation\"," +     // 星座，不作筛选条件
                    "\"nationality\":   \"$nationality\"," +       // 民族，不作筛选条件
                    "\"love_target\":       $loveTarget," +       // 恋爱目标
                    "\"target_show\":       $loveTargetShow," +       // 是否展示目标
                    "\"buy_car\":       $car," +           // 是否买车
                    "\"buy_house\":       $house," +         // 是否买房
                    "\"is_smoking\":       $smoke," +        // 是否抽烟
                    "\"is_drinking\":       $drink," +       // 是否喝酒
                    "\"child_had\":       $haveChild," +         // 是否有娃
                    "\"want_child\":       $wantChild," +        // 想要生娃
                    "\"marry_time\":    $marryTime}"  // 想结婚时间

        return moreInfo

    }

    // 需要上传的择偶条件信息
    private fun getDemandInfo(): String {

        val sex = SPStaticUtils.getInt(Constant.ME_SEX, 0)
        val ageMin = SPStaticUtils.getInt(Constant.TA_AGE_MIN, 0)
        val ageMax = SPStaticUtils.getInt(Constant.TA_AGE_MAX, 0)
        val heightMin = SPStaticUtils.getInt(Constant.TA_HEIGHT_MIN, 0)
        val heightMax = SPStaticUtils.getInt(Constant.TA_HEIGHT_MAX, 0)
        val income = SPStaticUtils.getInt(Constant.TA_INCOME_MIN, 0)
        val incomeMax = SPStaticUtils.getInt(Constant.TA_INCOME_MAX, 0)
        val edu = SPStaticUtils.getString(Constant.TA_EDU, "")
        val marryState = SPStaticUtils.getInt(Constant.TA_MARRY_STATE, 0)
        val body = SPStaticUtils.getInt(Constant.TA_BODY, 0)
        val childHave = SPStaticUtils.getInt(Constant.TA_HAVE_CHILD, 0)
        val childWant = SPStaticUtils.getInt(Constant.TA_WANT_CHILD, 0)
        val smoke = SPStaticUtils.getInt(Constant.TA_SMOKE, 0)
        val drink = SPStaticUtils.getInt(Constant.TA_DRINK, 0)
        val havePhoto = SPStaticUtils.getInt(Constant.TA_HAVE_PHOTO, 0)
        val marryTime = SPStaticUtils.getInt(Constant.TA_MARRY, 0)
        val car = SPStaticUtils.getInt(Constant.TA_CAR, 0)
        val house = SPStaticUtils.getInt(Constant.TA_HOUSE, 0)
        val city = SPStaticUtils.getString(Constant.TA_WORK_CITY_NAME, "")
        val cityCode = SPStaticUtils.getInt(Constant.TA_WORK_CITY_CODE, 0)

        val demandInfo =
            " {\"user_sex\": $sex, " +
                    "\"age_min\":       $ageMin," +
                    "\"age_max\":       $ageMax," +
                    "\"min_high\":      $heightMin," +
                    "\"max_high\":      $heightMax," +
                    "\"figure_nan\":    $body," +
                    "\"figure_nv\":     $body," +
                    "\"salary_range\":  $income," +
                    "\"education\":     $edu," +
                    "\"marry_status\":  $marryState," +
                    "\"child_had\":     $childHave," +
                    "\"want_child\":    $childWant," +
                    "\"is_smoking\":    $smoke," +
                    "\"drink_wine\":    $drink," +
                    "\"is_headface\":   $havePhoto," +
                    "\"marry_time\":    $marryTime," +
                    "\"buy_car\":       $car," +
                    "\"buy_house\":     $house," +
                    "\"work_place_str\":\"$city\"," +
                    "\"work_place_code\": $cityCode}"

        return demandInfo

    }

    private fun judgeLoading() {
        if (photoCompleteLoad && demandCompleteLoad && moreInfoCompleteLoad && baseInfoCompleteLoad) {
            ToastUtils.showShort("资料全部上传完成，跳转至首页")

            SPStaticUtils.put(Constant.IS_IDENTITY_VERIFY, true)

            val identityCode = SPStaticUtils.getString(Constant.TRUE_ID, "")

            SPStaticUtils.put(Constant.ME_BIRTH_YEAR, identityCode.substring(6, 10).toInt() - TimeUtils.date2String(TimeUtils.getNowDate(), "yyyy").toInt() + 100)
            SPStaticUtils.put(Constant.ME_BIRTH_MONTH, identityCode.substring(10, 12).toInt() - 1)
            SPStaticUtils.put(Constant.ME_BIRTH_DAY, identityCode.substring(12, 14).toInt() - 1)

            SPStaticUtils.put(Constant.DETAIL_INFO_FINISH, true)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()

        }
    }

    private fun saveImage(bitmap: Bitmap) {
        val path = PathUtils.getInternalAppCachePath() + File.separator + "1.jpeg"
        Log.i("guo", path)
        ImageUtils.save(bitmap, path, Bitmap.CompressFormat.PNG)
    }

    private fun base64ToBitmap(base64Data: String): Bitmap {
        val bytes = Base64Utils.decode(base64Data, Base64Utils.NO_WRAP)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun bitmapToBase64(bitmap: Bitmap?): String {

        var result: String = ""
        var baos: ByteArrayOutputStream? = null
        try {
            if (bitmap != null) {
                baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                baos.flush()
                baos.close()
                val bitmapBytes = baos.toByteArray()
                result =
                    android.util.Base64.encodeToString(bitmapBytes, android.util.Base64.NO_WRAP)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (baos != null) {
                    baos.flush()
                    baos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        Log.i("guo", "su；$result")
        return result

    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUploadPhotoSuccess(uploadPhotoBean: UploadPhotoBean?) {
        photoCompleteLoad = true

        judgeLoading()
    }

    override fun onDoUploadPhotoError() {

    }

    override fun onDoUpdateDemandInfoSuccess(updateDemandInfoBean: UpdateDemandInfoBean?) {
        demandCompleteLoad = true

        judgeLoading()
    }

    override fun onDoUpdateDemandInfoError() {

    }

    override fun onDoUpdateMoreInfoSuccess(updateMoreInfoBean: UpdateMoreInfoBean?) {
        moreInfoCompleteLoad = true

        judgeLoading()
    }

    override fun onDoUpdateMoreInfoError() {

    }

    override fun onDoUpdateBaseInfoSuccess(baseInfoUpdateBean: BaseInfoUpdateBean?) {
        baseInfoCompleteLoad = true

        judgeLoading()
    }

    override fun onDoUpdateBaseInfoError() {

    }

    override fun onDoFaceVerifySuccess(faceVerifyBean: FaceVerifyBean?) {

    }

    override fun onDoFaceVerifyError() {

    }

}