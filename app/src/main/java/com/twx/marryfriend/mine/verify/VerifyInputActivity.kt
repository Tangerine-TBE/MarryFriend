package com.twx.marryfriend.mine.verify

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.baidu.idl.face.platform.FaceEnvironment
import com.baidu.idl.face.platform.FaceSDKManager
import com.baidu.idl.face.platform.LivenessTypeEnum
import com.baidu.idl.face.platform.listener.IInitCallback
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.IdentityVerifyBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.login.retrieve.QualityConfig
import com.twx.marryfriend.login.retrieve.QualityConfigManager
import com.twx.marryfriend.mine.verify.face.VerifyFaceLivenessExpActivity
import com.twx.marryfriend.net.callback.IDoIdentityVerifyCallback
import com.twx.marryfriend.net.impl.doIdentityVerifyPresentImpl
import kotlinx.android.synthetic.main.activity_verify_input.*
import java.util.*

class VerifyInputActivity : MainBaseViewActivity(), IDoIdentityVerifyCallback {

    private var state = ""

    private var livenessList: MutableList<LivenessTypeEnum> = ArrayList()

    // 实名认证 姓名
    private var name = ""

    // 实名认证 身份证号
    private var identityCode = ""

    private lateinit var doIdentityVerifyPresent: doIdentityVerifyPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_verify_input

    override fun initView() {
        super.initView()

        initLicense()

        doIdentityVerifyPresent = doIdentityVerifyPresentImpl.getsInstance()
        doIdentityVerifyPresent.registerCallback(this)

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_verify_input_finish.setOnClickListener {
            finish()
        }

        et_verify_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                name = s.toString()
            }

        })

        et_verify_id.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                identityCode = s.toString()

                if (s.length == 18) {
                    KeyboardUtils.hideSoftInput(this@VerifyInputActivity)
                }
            }

        })

        tv_verify_verify.setOnClickListener {

            if (name != "" && identityCode != "") {

                if (RegexUtils.isIDCard18Exact(identityCode)) {
                    val map: MutableMap<String, String> = TreeMap()

                    map[Contents.ACCESS_TOKEN] =
                        SPStaticUtils.getString(Constant.ID_ACCESS_TOKEN, "")
                    map[Contents.CONTENT_TYPE] = "application/json"
                    map[Contents.ID_CARD_NUMBER] = identityCode
                    map[Contents.NAME] = name
                    state = "VerifyInputActivity"
                    doIdentityVerifyPresent.doIdentityVerify(map)


                    ll_verify_input_loading.visibility = View.VISIBLE

                } else {
                    ToastUtils.showShort("请输入正确的身份证号")
                }
            } else {
                ToastUtils.showShort("请填写详细信息")
            }
        }

    }

    private fun initLicense() {
        val success = setFaceConfig()
        if (!success) {
            ToastUtils.showShort("初始化失败 = json配置文件解析出错")
            return
        }
        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().initialize(this, "jiaou-hunlian-face-android",
            "idl-license.face-android", object : IInitCallback {
                override fun initSuccess() {
                    runOnUiThread {
                        Log.e("guo", "初始化成功")
                        ToastUtils.showShort("初始化成功")
//                        mIsInitSuccess = true
                    }
                }

                override fun initFailure(errCode: Int, errMsg: String) {
                    runOnUiThread {
                        Log.i("guo", "初始化失败 = $errCode $errMsg")
                        ToastUtils.showShort("初始化失败 = $errCode, $errMsg")
//                        mIsInitSuccess = false
                    }
                }
            })
    }

    private fun addActionLive() {
        // 根据需求添加活体动作
        livenessList.clear()
        livenessList.add(LivenessTypeEnum.Eye)
        livenessList.add(LivenessTypeEnum.Mouth)
        livenessList.add(LivenessTypeEnum.HeadRight)
    }

    /**
     * 参数配置方法
     */
    private fun setFaceConfig(): Boolean {
        val config = FaceSDKManager.getInstance().faceConfig
        // SDK初始化已经设置完默认参数（推荐参数），也可以根据实际需求进行数值调整
        // 质量等级（0：正常、1：宽松、2：严格、3：自定义）
        // 获取保存的质量等级

        val qualityLevel = 0

        // 根据质量等级获取相应的质量值（注：第二个参数要与质量等级的set方法参数一致）
        val manager: QualityConfigManager = QualityConfigManager.getInstance()
        manager.readQualityFile(this.applicationContext, qualityLevel)
        val qualityConfig: QualityConfig = manager.config ?: return false
        // 设置模糊度阈值
        config.blurnessValue = qualityConfig.blur
        // 设置最小光照阈值（范围0-255）
        config.brightnessValue = qualityConfig.minIllum
        // 设置最大光照阈值（范围0-255）
        config.brightnessMaxValue = qualityConfig.maxIllum
        // 设置左眼遮挡阈值
        config.occlusionLeftEyeValue = qualityConfig.leftEyeOcclusion
        // 设置右眼遮挡阈值
        config.occlusionRightEyeValue = qualityConfig.rightEyeOcclusion
        // 设置鼻子遮挡阈值
        config.occlusionNoseValue = qualityConfig.noseOcclusion
        // 设置嘴巴遮挡阈值
        config.occlusionMouthValue = qualityConfig.mouseOcclusion
        // 设置左脸颊遮挡阈值
        config.occlusionLeftContourValue = qualityConfig.leftContourOcclusion
        // 设置右脸颊遮挡阈值
        config.occlusionRightContourValue = qualityConfig.rightContourOcclusion
        // 设置下巴遮挡阈值
        config.occlusionChinValue = qualityConfig.chinOcclusion
        // 设置人脸姿态角阈值
        config.headPitchValue = qualityConfig.pitch
        config.headYawValue = qualityConfig.yaw
        config.headRollValue = qualityConfig.roll
        // 设置可检测的最小人脸阈值
        config.minFaceSize = FaceEnvironment.VALUE_MIN_FACE_SIZE
        // 设置可检测到人脸的阈值
        config.notFaceValue = FaceEnvironment.VALUE_NOT_FACE_THRESHOLD
        // 设置闭眼阈值
        config.eyeClosedValue = FaceEnvironment.VALUE_CLOSE_EYES
        // 设置图片缓存数量
        config.cacheImageNum = FaceEnvironment.VALUE_CACHE_IMAGE_NUM
        // 设置活体动作，通过设置list，LivenessTypeEunm.Eye, LivenessTypeEunm.Mouth,
        // LivenessTypeEunm.HeadUp, LivenessTypeEunm.HeadDown, LivenessTypeEunm.HeadLeft,
        // LivenessTypeEunm.HeadRight
        config.livenessTypeList = livenessList
        // 设置动作活体是否随机
        config.isLivenessRandom = true
        // 设置开启提示音
        config.isSound = false
        // 原图缩放系数
        config.scale = FaceEnvironment.VALUE_SCALE
        // 抠图宽高的设定，为了保证好的抠图效果，建议高宽比是4：3
        config.cropHeight = FaceEnvironment.VALUE_CROP_HEIGHT
        config.cropWidth = FaceEnvironment.VALUE_CROP_WIDTH
        // 抠图人脸框与背景比例
        config.enlargeRatio = FaceEnvironment.VALUE_CROP_ENLARGERATIO
        // 检测超时设置
        config.timeDetectModule = FaceEnvironment.TIME_DETECT_MODULE
        // 检测框远近比率
        config.faceFarRatio = FaceEnvironment.VALUE_FAR_RATIO
        config.faceClosedRatio = FaceEnvironment.VALUE_CLOSED_RATIO
        FaceSDKManager.getInstance().faceConfig = config
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 1) {
            val intent = intent
            intent.putExtra("name", name)
            intent.putExtra("id", identityCode)
            setResult(RESULT_OK, intent)
            finish()
        }

    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoIdentityVerifySuccess(identityVerifyBean: IdentityVerifyBean) {

        if (state == "VerifyInputActivity") {
            ll_verify_input_loading.visibility = View.GONE

            SPStaticUtils.put(Constant.TRUE_NAME, name)
            SPStaticUtils.put(Constant.TRUE_ID, identityCode)

            if (identityVerifyBean.error_msg == "SUCCESS") {

                XXPermissions.with(this)
                    .permission(Permission.CAMERA)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                            val intent = Intent(this@VerifyInputActivity, VerifyFaceLivenessExpActivity::class.java)
                            startActivityForResult(intent, 1)
                        }

                        override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                            super.onDenied(permissions, never)
                            ToastUtils.showShort("请授予应用拍照权限，否则应用无法进行人脸识别认证")
                        }
                    })

            } else {

                if (identityVerifyBean.error_code == 222351) {
                    ToastUtils.showShort("身份证号与姓名不匹配或身份证号不存在")
                } else {
                    ToastUtils.showShort(identityVerifyBean.error_msg)
                }
            }

        }
    }

    override fun onDoIdentityVerifyError() {
        ll_verify_input_loading.visibility = View.GONE
    }

}