package com.twx.marryfriend.guide


import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity


class testActivity : MainBaseViewActivity() {

    override fun getLayoutView(): Int = R.layout.activity_test

//    override fun initView() {
//        super.initView()
//
//        val config = FaceSDKManager.getInstance().faceConfig
//        // SDK初始化已经设置完默认参数（推荐参数），也可以根据实际需求进行数值调整
//        // 质量等级（0：正常、1：宽松、2：严格、3：自定义）
//        // 获取保存的质量等级
//        // SDK初始化已经设置完默认参数（推荐参数），也可以根据实际需求进行数值调整
//        // 质量等级（0：正常、1：宽松、2：严格、3：自定义）
//
//        // 质量等级设置为正常
//        var qualityLevel = 0
//
//        // 根据质量等级获取相应的质量值（注：第二个参数要与质量等级的set方法参数一致）
//        val manager = QualityConfigManager.getInstance()
//        manager.readQualityFile(this.applicationContext, qualityLevel)
//        val qualityConfig = manager.config
//
//        // 设置模糊度阈值
//        config.blurnessValue = qualityConfig.blur
//
//        // 设置最小光照阈值（范围0-255）
//        config.brightnessValue = qualityConfig.minIllum
//
//        // 设置最大光照阈值（范围0-255）
//        config.brightnessMaxValue = qualityConfig.maxIllum
//
//        // 设置左眼遮挡阈值
//        config.occlusionLeftEyeValue = qualityConfig.leftEyeOcclusion
//
//        // 设置右眼遮挡阈值
//        config.occlusionRightEyeValue = qualityConfig.rightEyeOcclusion
//
//        // 设置鼻子遮挡阈值
//        config.occlusionNoseValue = qualityConfig.noseOcclusion
//
//        // 设置嘴巴遮挡阈值
//        config.occlusionMouthValue = qualityConfig.mouseOcclusion
//
//        // 设置左脸颊遮挡阈值
//        config.occlusionLeftContourValue = qualityConfig.leftContourOcclusion
//
//        // 设置右脸颊遮挡阈值
//        config.occlusionRightContourValue = qualityConfig.rightContourOcclusion
//
//        // 设置下巴遮挡阈值
//        config.occlusionChinValue = qualityConfig.chinOcclusion
//
//        // 设置人脸姿态角阈值
//        config.headPitchValue = qualityConfig.pitch
//        config.headYawValue = qualityConfig.yaw
//        config.headRollValue = qualityConfig.roll
//
//        // 设置可检测的最小人脸阈值
//        config.minFaceSize = FaceEnvironment.VALUE_MIN_FACE_SIZE
//
//        // 设置可检测到人脸的阈值
//        config.notFaceValue = FaceEnvironment.VALUE_NOT_FACE_THRESHOLD
//
//        // 设置闭眼阈值
//        config.eyeClosedValue = FaceEnvironment.VALUE_CLOSE_EYES
//
//        // 设置图片缓存数量
//        config.cacheImageNum = FaceEnvironment.VALUE_CACHE_IMAGE_NUM
//        // 设置活体动作，通过设置list，LivenessTypeEunm.Eye, LivenessTypeEunm.Mouth,
//        // LivenessTypeEunm.HeadUp, LivenessTypeEunm.HeadDown, LivenessTypeEunm.HeadLeft,
//        // LivenessTypeEunm.HeadRight
//        // 设置活体动作，通过设置list，LivenessTypeEunm.Eye, LivenessTypeEunm.Mouth,
//        // LivenessTypeEunm.HeadUp, LivenessTypeEunm.HeadDown, LivenessTypeEunm.HeadLeft,
//        // LivenessTypeEunm.HeadRight
//        config.livenessTypeList = ExampleApplication.livenessList
//
//        // 设置动作活体是否随机
//        config.isLivenessRandom = true
//
//        // 设置开启提示音
//        config.isSound = false
//
//        // 原图缩放系数
//        config.scale = FaceEnvironment.VALUE_SCALE
//
//        // 抠图宽高的设定，为了保证好的抠图效果，建议高宽比是4：3
//        config.cropHeight = FaceEnvironment.VALUE_CROP_HEIGHT
//        config.cropWidth = FaceEnvironment.VALUE_CROP_WIDTH
//
//        // 抠图人脸框与背景比例
//        config.enlargeRatio = FaceEnvironment.VALUE_CROP_ENLARGERATIO
//
//        // 加密类型，0：Base64加密，上传时image_sec传false；1：百度加密文件加密，上传时image_sec传true
//        config.secType = FaceEnvironment.VALUE_SEC_TYPE
//
//        // 检测超时设置
//        config.timeDetectModule = FaceEnvironment.TIME_DETECT_MODULE
//
//        // 检测框远近比率
//        config.faceFarRatio = FaceEnvironment.VALUE_FAR_RATIO
//        config.faceClosedRatio = FaceEnvironment.VALUE_CLOSED_RATIO
//        FaceSDKManager.getInstance().faceConfig = config
//
//
//        startActivity(Intent(this, FaceLivenessExpActivity))
//    }


//    private fun startFaceRecognize(name: String, id: String) {
//        // 人脸阈值设置
//        setFaceQualityConfig()
//        // LogicServiceManager入参
//        val params: MutableMap<String, Any> = HashMap()
//        // 必须携带access_token
//        params["access_token"] = "此处需要替换为服务端获取的access_token"
//        // 开放平台控制台配置的方案Id
//        params["plan_id"] = consoleConfig.getPlanId()
//        // 证件类型，0：大陆 1：港澳 2：外国 3：定居国外中国护照
//        params["verify_type"] = 0
//        // recogType为FaceRecognize 需要带姓名、证件号
//        params["name"] = name
//        params["id_card_number"] = id
//        /**
//         * recogType为FaceRecognize可以携带以下参数
//         * quality_control 对应console.config中的onlineImageQuality
//         * liveness_control 对应console.onlineLivenessQuality
//         */
//        params["quality_control"] = consoleConfig.getOnlineImageQuality()
//        params["liveness_control"] = consoleConfig.getOnlineLivenessQuality()
//        LogicServiceManager.getInstance().startFaceRecognize(this, params, object : LogicServiceCallbck() {
//                fun onCallback(resultCode: Int, resultMap: Map<String?, Any?>?) {
//                    this@testActivity.runOnUiThread(Runnable { handleResult(resultCode, resultMap) })
//                }
//            })
//    }

}