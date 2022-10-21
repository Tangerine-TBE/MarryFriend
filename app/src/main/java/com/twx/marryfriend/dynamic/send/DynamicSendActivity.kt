package com.twx.marryfriend.dynamic.send

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidubce.BceClientException
import com.baidubce.BceServiceException
import com.baidubce.auth.DefaultBceCredentials
import com.baidubce.services.bos.BosClient
import com.baidubce.services.bos.BosClientConfiguration
import com.blankj.utilcode.util.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.luck.picture.lib.animators.AnimationType
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.style.PictureWindowAnimationStyle
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.TextVerifyBean
import com.twx.marryfriend.bean.dynamic.UploadTrendBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.dynamic.preview.image.ImagePreviewActivity
import com.twx.marryfriend.dynamic.preview.video.VideoPreviewActivity
import com.twx.marryfriend.dynamic.send.adapter.OnNineGridViewListener
import com.twx.marryfriend.dynamic.send.adapter.PhotoPublishAdapter
import com.twx.marryfriend.dynamic.send.dialog.VideoWaterMarkDialog
import com.twx.marryfriend.dynamic.send.dialog.VideoZipDialog
import com.twx.marryfriend.dynamic.send.location.LocationActivity
import com.twx.marryfriend.dynamic.send.utils.ItemTouchHelperCallback
import com.twx.marryfriend.mine.user.UserActivity
import com.twx.marryfriend.mine.verify.VerifyActivity
import com.twx.marryfriend.net.callback.IDoTextVerifyCallback
import com.twx.marryfriend.net.callback.dynamic.IDoUploadTrendCallback
import com.twx.marryfriend.net.impl.doTextVerifyPresentImpl
import com.twx.marryfriend.net.impl.dynamic.doUploadTrendPresentImpl
import com.twx.marryfriend.set.web.SetWebActivity
import com.twx.marryfriend.utils.*
import com.twx.marryfriend.utils.VideoUtil.getVideoCover
import com.twx.marryfriend.utils.emoji.EmojiDetailAdapter
import com.twx.marryfriend.utils.emoji.EmojiUtils
import io.microshow.rxffmpeg.RxFFmpegCommandList
import io.microshow.rxffmpeg.RxFFmpegInvoke
import io.microshow.rxffmpeg.RxFFmpegSubscriber
import kotlinx.android.synthetic.main.activity_dynamic_send.*
import java.io.File
import java.net.UnknownHostException
import java.util.*

class DynamicSendActivity : MainBaseViewActivity(), IDoUploadTrendCallback, IDoTextVerifyCallback {

    // 上次点击时间
    private var lastClickTime = 0L

    // 两次点击间隔时间（毫秒）
    private val delayTime = 3000

    // 拍照文件路径
    private var mTempPhotoPath = ""

    // 动态类型
    private var trendsType = 0

    // 文字内容
    private var content = ""

    // 图片地址
    private var imageUrl = ""

    // 视频地址
    private var videoUrl = ""

    // 视频封面
    private var coverUrl = ""

    // 储备字段，暂时不用
    private val label = ""

    // 经度
    private var jingdu = ""

    // 纬度
    private var weidu = ""

    // 定位
    private var position = ""

    // 添加按钮是否可见
    private var addVisible = true

    // 是否完成文字验证
    private var doTextVerify = false

    // 是否完成动态上传
    private var doUploadTrend = false

    private var mDataList: ArrayList<String> = arrayListOf()

    private lateinit var mAdapter: PhotoPublishAdapter

    private lateinit var myCallBack: ItemTouchHelperCallback

    private lateinit var mItemTouchHelper: ItemTouchHelper

    private lateinit var doTextVerifyPresent: doTextVerifyPresentImpl
    private lateinit var doUploadTrendPresent: doUploadTrendPresentImpl

    var emojiList: MutableList<String> = arrayListOf()
    private lateinit var emojiAdapter: EmojiDetailAdapter

    private lateinit var client: BosClient

    private lateinit var mVideoZipDialog: VideoZipDialog

    private lateinit var mVideoWaterMarkDialog: VideoWaterMarkDialog

    override fun getLayoutView(): Int = R.layout.activity_dynamic_send

    override fun initView() {
        super.initView()

        doTextVerifyPresent = doTextVerifyPresentImpl.getsInstance()
        doTextVerifyPresent.registerCallback(this)

        doUploadTrendPresent = doUploadTrendPresentImpl.getsInstance()
        doUploadTrendPresent.registerCallback(this)

        mVideoZipDialog = VideoZipDialog()
        mVideoWaterMarkDialog = VideoWaterMarkDialog()

        mAdapter = PhotoPublishAdapter(this)
        mAdapter.setMaxPic(9)
        myCallBack = ItemTouchHelperCallback(false, mDataList, nsv, mAdapter)
        //实现化ItemTouchHelper(拖拽和滑动删除的过程中会回调ItemTouchHelper.Callback的相关方法)
        mItemTouchHelper = ItemTouchHelper(myCallBack)
        mItemTouchHelper.attachToRecyclerView(rv_send_container) //关联对应的 RecyclerView
        rv_send_container.adapter = mAdapter
        mAdapter.data = mDataList

        fixBottom()

        emojiList = EmojiUtils.getEmojiList()

        emojiAdapter = EmojiDetailAdapter(emojiList)

        rv_emoji_send_container.adapter = emojiAdapter
        rv_emoji_send_container.layoutManager = GridLayoutManager(this, 8)


        val str = "已阅读同意佳偶婚恋交友的《个人动态服务协议》"
        val stringBuilder = SpannableStringBuilder(str)
        val span1 = TextViewSpan()
        stringBuilder.setSpan(span1, 12, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tv_send_agree.text = stringBuilder
        tv_send_agree.movementMethod = LinkMovementMethod.getInstance()

    }

    override fun initLoadData() {
        super.initLoadData()

//        mTempPhotoPath = Environment.getExternalStorageDirectory()
//            .toString() + File.separator + "333 .jpeg"


    }

    override fun initPresent() {
        super.initPresent()

        val config: BosClientConfiguration = BosClientConfiguration()

        // 设置HTTP最大连接数为10
        config.maxConnections = 10

        // 设置TCP连接超时为5000毫秒
        config.connectionTimeoutInMillis = 15000

        // 设置Socket传输数据超时的时间为2000毫秒
        config.socketTimeoutInMillis = 15000

        config.credentials = DefaultBceCredentials("545c965a81ba49889f9d070a1e147a7b",
            "1b430f2517d0460ebdbecfd910c572f8")
        config.endpoint = "http://adrmf.gz.bcebos.com"
        client = BosClient(config)


    }


    override fun initEvent() {
        super.initEvent()

        iv_dynamic_send_finish.setOnClickListener {
            finish()
        }


        ll_send_container.setOnClickListener {
            if (ll_emoji_send_container.visibility == View.VISIBLE) {
                iv_send_emoji.setImageResource(R.drawable.ic_dynamic_emoji)
                ll_emoji_send_container.visibility = View.GONE
            }
        }

        et_send_content.setOnClickListener {
            if (ll_emoji_send_container.visibility == View.VISIBLE) {
                iv_send_emoji.setImageResource(R.drawable.ic_dynamic_emoji)
                ll_emoji_send_container.visibility = View.GONE
            }
        }

        et_send_content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    tv_send_num.text = "${s.length}/3000"
                }
            }

        })

        rv_send_container.setOnTouchListener { v, event ->
            if (v != null) {
                if (v.id != 0) {
                    if (ll_emoji_send_container.visibility == View.VISIBLE) {
                        iv_send_emoji.setImageResource(R.drawable.ic_dynamic_emoji)
                        ll_emoji_send_container.visibility = View.GONE
                    }
                }
            }
            false
        }

        mAdapter.setOnNineGridViewListener(object : OnNineGridViewListener {
            override fun onAddPic(addCount: Int) {

                if (ll_emoji_send_container.visibility == View.VISIBLE) {
                    iv_send_emoji.setImageResource(R.drawable.ic_dynamic_emoji)
                    ll_emoji_send_container.visibility = View.GONE
                }

                XPopup.Builder(this@DynamicSendActivity).dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false).isDestroyOnDismiss(true)
                    .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(AddChooseDialog(this@DynamicSendActivity)).show()

            }

            override fun onDisplayImg(context: Context?, data: String?, imageView: ImageView?) {

                if (imageView != null) {

                    Glide.with(context!!).load(data).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop().into(imageView)

                    if (FileUtils.getFileExtension(data) == "mp4") {
                        if (addVisible) {
                            val task: TimerTask = object : TimerTask() {
                                override fun run() {
                                    ThreadUtils.runOnUiThread {
                                        mAdapter.setNeedAddBtn(false)
                                        addVisible = false
                                    }
                                }
                            }
                            val timer = Timer()
                            timer.schedule(task, 10)
                        }
                    }
                }
            }

            override fun onDeletePic(data: String?, position: Int) {

            }

            override fun onClickPic(data: String?, position: Int) {

                if (ll_emoji_send_container.visibility == View.VISIBLE) {
                    iv_send_emoji.setImageResource(R.drawable.ic_dynamic_emoji)
                    ll_emoji_send_container.visibility = View.GONE
                }

                if (FileUtils.getFileExtension(data) != "mp4") {
                    startActivity(ImagePreviewActivity.getIntent(this@DynamicSendActivity,
                        mDataList,
                        position))
                } else {
                    startActivity(VideoPreviewActivity.getIntent(this@DynamicSendActivity,
                        mDataList[0],
                        FileUtils.getFileNameNoExtension(mDataList[0])))
                }
            }

            override fun onLongClickPic(
                viewHolder: RecyclerView.ViewHolder?,
                data: String?,
                position: Int,
            ) {

                if (ll_emoji_send_container.visibility == View.VISIBLE) {
                    iv_send_emoji.setImageResource(R.drawable.ic_dynamic_emoji)
                    ll_emoji_send_container.visibility = View.GONE
                }

                if (viewHolder!!.layoutPosition != mDataList.size) {
                    mItemTouchHelper.startDrag(viewHolder) //viewHolder开始拖动
                }
            }
        })

        myCallBack.setDragListener(object : ItemTouchHelperCallback.DragListener {
            override fun deleteState(delete: Boolean) {
            }

            override fun dragState(start: Boolean) {

                //根据是否是开始滑动状态，来设置是否显示删除区域
                if (start) {
                    ll_send_delete.visibility = View.VISIBLE
                } else {
                    ll_send_delete.visibility = View.GONE
                }
            }

            override fun deleteOk() {

            }

            override fun clearView() {

                val task: TimerTask = object : TimerTask() {
                    override fun run() {
                        ThreadUtils.runOnUiThread {
                            mAdapter.setNeedAddBtn(true)
                            addVisible = true
                        }
                    }
                }
                val timer = Timer()
                timer.schedule(task, 10)

                //item删除后需要重新计算底部区域的显示位置，否则会造成底部区域显示混乱
                fixBottom()
            }

        })

        // 定位
        rl_send_location.setOnClickListener {

            if (ll_emoji_send_container.visibility == View.VISIBLE) {
                iv_send_emoji.setImageResource(R.drawable.ic_dynamic_emoji)
                ll_emoji_send_container.visibility = View.GONE
            }

            // 保证只执行一次
            var x = true

            XXPermissions.with(this).permission(Permission.ACCESS_FINE_LOCATION)
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {

                        if (SPStaticUtils.getString("dynamic_send_city",
                                "") != "" && SPStaticUtils.getString("dynamic_send_location",
                                "") != ""
                        ) {

                            val locations = SPStaticUtils.getString("dynamic_send_location", "")
                            val city = SPStaticUtils.getString("dynamic_send_city", "")

                            val intent =
                                Intent(this@DynamicSendActivity, LocationActivity::class.java)
                            intent.putExtra("location", locations)
                            intent.putExtra("city", city)

                            startActivityForResult(intent, 0)

                        } else {

                            val mLocationClient = LocationClient(this@DynamicSendActivity)

                            mLocationClient.registerLocationListener(object :
                                BDAbstractLocationListener() {
                                override fun onReceiveLocation(location: BDLocation) {
                                    if (x) {
                                        val city = location.city
                                        val locations = "${location.longitude},${location.latitude}"

                                        SPStaticUtils.put("dynamic_send_city", locations)
                                        SPStaticUtils.put("dynamic_send_location", locations)

                                        val intent = Intent(this@DynamicSendActivity,
                                            LocationActivity::class.java)
                                        intent.putExtra("location", locations)
                                        intent.putExtra("city", city)

                                        startActivityForResult(intent, 0)
                                        x = false
                                    }
                                }
                            })
                            val option = LocationClientOption()
                            option.setIsNeedAddress(true);
                            option.setNeedNewVersionRgc(true);
                            mLocationClient.locOption = option;
                            mLocationClient.start()

                        }


                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        super.onDenied(permissions, never)
                        ToastUtils.showShort("请授予应用相应权限")
                    }
                })

        }

        // 相册
        iv_send_pic.setOnClickListener {
            val selectorStyle = PictureSelectorStyle()
            val animationStyle = PictureWindowAnimationStyle()
            animationStyle.setActivityEnterAnimation(R.anim.ps_anim_up_in)
            animationStyle.setActivityExitAnimation(R.anim.ps_anim_down_out)
            selectorStyle.windowAnimationStyle = animationStyle


            // 当数据列表为空时，视频图片都可选择
            // 当数据列表中有数据时，当有视频时，直接提示不可选取，当为图片时，则为只选择图片模式
            if (mDataList.isEmpty()) {
                // 第一个文件不是视频，即选取都为图片
                XXPermissions.with(this).permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                            PictureSelector.create(this@DynamicSendActivity)
                                .openGallery(com.luck.picture.lib.config.SelectMimeType.TYPE_ALL)
                                .setImageEngine(GlideEngine.createGlideEngine())
                                .setLanguage(LanguageConfig.SYSTEM_LANGUAGE)
                                .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)
                                .setImageSpanCount(3).isPreviewVideo(false).isDisplayCamera(true)
                                .isPreviewImage(true).isEmptyResultReturn(true)
                                .setSelectorUIStyle(selectorStyle).setMaxSelectNum(9)
                                .setMaxVideoSelectNum(1)
                                .forResult(object : OnResultCallbackListener<LocalMedia> {
                                    override fun onResult(result: ArrayList<LocalMedia>) {
                                        for (i in 0.until(result.size)) {
                                            mDataList.add(result[i].realPath)
                                        }
                                        mAdapter.data = mDataList
                                        mAdapter.notifyDataSetChanged()
                                        fixBottom()
                                    }

                                    override fun onCancel() {
                                    }
                                })
                        }

                        override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                            super.onDenied(permissions, never)
                            ToastUtils.showShort("请授予应用所需权限")
                        }
                    })
            } else {
                if (FileUtils.getFileExtension(mDataList[0]) == "mp4") {
                    ToastUtils.showShort("只可同时上传一个视频文件")
                } else {
                    XXPermissions.with(this).permission(Permission.MANAGE_EXTERNAL_STORAGE)
                        .request(object : OnPermissionCallback {
                            override fun onGranted(
                                permissions: MutableList<String>?,
                                all: Boolean,
                            ) {
                                PictureSelector.create(this@DynamicSendActivity)
                                    .openGallery(com.luck.picture.lib.config.SelectMimeType.TYPE_IMAGE)
                                    .setImageEngine(GlideEngine.createGlideEngine())
                                    .setLanguage(LanguageConfig.SYSTEM_LANGUAGE)
                                    .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)
                                    .setImageSpanCount(3).isPreviewVideo(false)
                                    .isDisplayCamera(true).isPreviewImage(true)
                                    .isEmptyResultReturn(true).setSelectorUIStyle(selectorStyle)
                                    .setMaxSelectNum(9 - mDataList.size).setMaxVideoSelectNum(1)
                                    .forResult(object : OnResultCallbackListener<LocalMedia> {
                                        override fun onResult(result: ArrayList<LocalMedia>) {
                                            for (i in 0.until(result.size)) {
                                                mDataList.add(result[i].realPath)
                                            }
                                            mAdapter.data = mDataList
                                            mAdapter.notifyDataSetChanged()
                                            fixBottom()
                                        }

                                        override fun onCancel() {
                                        }
                                    })
                            }

                            override fun onDenied(
                                permissions: MutableList<String>?,
                                never: Boolean,
                            ) {
                                super.onDenied(permissions, never)
                                ToastUtils.showShort("请授予应用所需权限")
                            }
                        })
                }
            }

        }

        // 相机
        iv_send_camera.setOnClickListener {

            XXPermissions.with(this@DynamicSendActivity).permission(Permission.CAMERA)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>?,
                        all: Boolean,
                    ) {

                        if (all) {
                            mTempPhotoPath = Environment.getExternalStorageDirectory()
                                .toString() + File.separator + "${TimeUtils.getNowMills()}.jpeg"

                            val tempPhotoFile: File = File(mTempPhotoPath)
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            // 如果在Android7.0以上,使用FileProvider获取Uri
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                val authority =
                                    this@DynamicSendActivity.packageName.toString() + ".fileProvider"
                                val contentUri: Uri =
                                    DynamicFileProvider.getUriForFile(this@DynamicSendActivity,
                                        authority,
                                        tempPhotoFile)
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                            } else {
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(tempPhotoFile))
                            }
                            startActivityForResult(intent, 2)
                        } else {
                            ToastUtils.showShort("请授予应用相关权限")
                        }

                    }

                    override fun onDenied(
                        permissions: MutableList<String>?,
                        never: Boolean,
                    ) {
                        super.onDenied(permissions, never)
                        ToastUtils.showShort("请授予应用相关权限")
                    }

                })

        }

        // emoji
        iv_send_emoji.setOnClickListener {
            if (ll_emoji_send_container.visibility == View.VISIBLE) {

                iv_send_emoji.setImageResource(R.drawable.ic_dynamic_emoji)
                ll_emoji_send_container.visibility = View.GONE

            } else {

                iv_send_emoji.setImageResource(R.drawable.ic_dynamic_keyboard)
                ll_emoji_send_container.visibility = View.VISIBLE
            }
        }

        tv_send_send.setOnClickListener {

            content = UnicodeUtils.newLineText(et_send_content.text.toString().trim { it <= ' ' })


            if (mDataList.isNotEmpty() || content != "") {

                // 此时需要验证是否实名认证
                if (SPStaticUtils.getBoolean(Constant.IS_IDENTITY_VERIFY, false)) {

                    // 此时需要验证是否上传头像
                    if (SPStaticUtils.getString(Constant.ME_AVATAR,
                            "") != "" || SPStaticUtils.getString(Constant.ME_AVATAR_AUDIT, "") != ""
                    ) {

                        if (System.currentTimeMillis() - lastClickTime >= delayTime) {
                            lastClickTime = System.currentTimeMillis();


                            // 还需要上传图片

                            Log.i("guo", "start : ${TimeUtils.getNowDate()}")

                            if (mDataList.isNotEmpty()) {
                                // 有图片文字数据

                                trendsType = when (mDataList.size) {
                                    1 -> {
                                        if (FileUtils.getFileExtension(mDataList[0]) == "mp4") {
                                            2
                                        } else {
                                            1
                                        }
                                    }
                                    else -> {
                                        1
                                    }
                                }


                                when (trendsType) {
                                    1 -> {
                                        // 图片动态 ，上传图片

                                        ll_send_loading.visibility = View.VISIBLE

                                        val xlist: MutableList<String> = arrayListOf()
                                        Thread {

                                            for (i in 0.until(mDataList.size)) {

                                                val name = TimeUtils.getNowMills()

                                                val file = File(mDataList[i])

                                                val bitmap = BitmapUtil.generateBitmap("佳偶婚恋交友",
                                                    16f,
                                                    Color.WHITE)?.let {
                                                    BitmapUtil.createWaterMarkBitmap(ImageUtils.getBitmap(
                                                        file), it)
                                                }

                                                val mPhotoPath =
                                                    this.externalCacheDir.toString() + File.separator + "${
                                                        FileUtils.getFileNameNoExtension(mDataList[i])
                                                    }.png"

                                                if (bitmap != null) {
                                                    BitmapUtil.saveBitmap(bitmap, mPhotoPath)
                                                }

                                                val putObjectFromFileResponse =
                                                    client.putObject("user${
                                                        SPStaticUtils.getString(Constant.USER_ID,
                                                            "default")
                                                    }", "${name}.jpg", File(mPhotoPath))


                                                xlist.add(client.generatePresignedUrl("user${
                                                    SPStaticUtils.getString(Constant.USER_ID,
                                                        "default")
                                                }", "${name}.jpg", -1).toString())

                                                Log.i("guo", " $i : ${xlist}")

                                            }

                                            val x = xlist.toString().replace("[", "")

                                            imageUrl = x.replace("]", "")

                                            if (content != "") {

                                                Log.i("guo", "文字 ----图片 ")
                                                doTextVerify = false
                                                doTextVerify(content)
                                            } else {

                                                Log.i("guo", "end : ${TimeUtils.getNowDate()}")
                                                Log.i("guo",
                                                    "start --- UPLOAD : ${TimeUtils.getNowDate()}")

                                                Log.i("guo", "上传 ----图片 ")
                                                uploadTrend()
                                            }

                                        }.start()

                                    }

                                    2 -> {

                                        val mWatermarkPath =
                                            this.externalCacheDir.toString() + File.separator + "watermark.png"

                                        FileUtils.delete(mWatermarkPath)

                                        val bitmap = ImageUtils.getBitmap(R.mipmap.watermark)

                                        if (bitmap != null) {
                                            BitmapUtil.saveBitmap(bitmap, mWatermarkPath)
                                        }

                                        val coverPath = getVideoCover(mDataList[0], this)

                                        Thread {

                                            try {

                                                val coverName = TimeUtils.getNowMills()

                                                val putObjectFromFileResponse =
                                                    client.putObject("user${
                                                        SPStaticUtils.getString(Constant.USER_ID,
                                                            "default")
                                                    }", "${coverName}.jpg", File(coverPath))


                                                coverUrl = client.generatePresignedUrl("user${
                                                    SPStaticUtils.getString(Constant.USER_ID,
                                                        "default")
                                                }", "${coverName}.jpg", -1).toString()

                                                Log.i("guo", "coverUrl : $coverUrl")

                                            } catch (e: BceClientException) {
                                                e.printStackTrace()
                                                ToastUtils.showShort("网络请求错误，请检查网络后稍后重试")
                                            } catch (e: BceServiceException) {
                                                Log.i("guo", "Error ErrorCode: " + e.errorCode);
                                                Log.i("guo", "Error RequestId: " + e.requestId);
                                                Log.i("guo", "Error StatusCode: " + e.statusCode);
                                                Log.i("guo", "Error ErrorType: " + e.errorType);
                                            } catch (e: UnknownHostException) {
                                                e.printStackTrace()
                                                ToastUtils.showShort("网络请求错误，请检查网络后稍后重试")
                                            }

                                        }.start()


                                        val targetWaterMarkPath =
                                            externalCacheDir.toString() + File.separator + "water" + FileUtils.getFileNameNoExtension(
                                                mDataList[0]) + ".mp4"

                                        addVideoWaterMark(mDataList[0],
                                            mWatermarkPath,
                                            targetWaterMarkPath)
                                    }
                                }

                            } else {
                                // 不需要上传图片，直接上传文字即可

                                ll_send_loading.visibility = View.VISIBLE

                                trendsType = 3

                                if (content != "") {
                                    Log.i("guo", "文字 ---- 空")
                                    doTextVerify = false
                                    doTextVerify(content)
                                } else {
                                    ToastUtils.showShort("请输入动态内容")
                                }

                            }

                        } else {
                            ToastUtils.showShort("点击太频繁了，请稍后再评论")
                        }

                    } else {
                        XPopup.Builder(this).dismissOnTouchOutside(false)
                            .dismissOnBackPressed(false).isDestroyOnDismiss(true)
                            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                            .asCustom(AvatarDialog(this)).show()
                    }

                } else {
                    // 前往认证
                    XPopup.Builder(this@DynamicSendActivity).dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false).isDestroyOnDismiss(true)
                        .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                        .asCustom(IdentityDialog(this@DynamicSendActivity)).show()
                }
            } else {
                ToastUtils.showShort("请输入您想发布的动态内容")
            }

        }

        emojiAdapter.setOnItemClickListener(object : EmojiDetailAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                var x = et_send_content.text.toString().trim { it <= ' ' }
                x += EmojiUtils.getCompatEmojiString(emojiList[position])
                et_send_content.setText(x)
                et_send_content.setSelection(x.length)
            }
        })

        iv_emoji_send_delete.setOnClickListener {
            val keyCode = KeyEvent.KEYCODE_DEL
            val keyEventDown = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
            val keyEventUp = KeyEvent(KeyEvent.ACTION_UP, keyCode);
            et_send_content.onKeyDown(keyCode, keyEventDown)
            et_send_content.onKeyUp(keyCode, keyEventUp);
        }

    }

    /**
     * 处理recyclerView下面的布局
     */
    private fun fixBottom() {
        var row = mAdapter.itemCount / 3
        row = if (0 == mAdapter.itemCount % 3) row else row + 1 //少于3为1行
        val screenWidth: Int = ScreenUtils.getScreenWidth()
        val itemHeight: Int = (screenWidth - ConvertUtils.dp2px(40F)) / 3
        var editHeight: Int = et_send_content.height
        editHeight = if (editHeight == 0) ConvertUtils.dp2px(130F) else et_send_content.height
        val layoutMargin: Int = ConvertUtils.dp2px(20F) //距离上部应用的间隔
        val marginTop = itemHeight * row + editHeight + layoutMargin //+ itemSpace * (row - 1)

    }


    private fun doTextVerify(text: String) {

        val map: MutableMap<String, String> = TreeMap()
        map[Contents.ACCESS_TOKEN] = SPStaticUtils.getString(Constant.ACCESS_TOKEN, "")
        map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
        map[Contents.TEXT] = text
        doTextVerifyPresent.doTextVerify(map)
    }

    private fun uploadTrend() {
        doUploadTrend = false
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.TREND_INFO] = getUploadTrendInfo()

        Log.i("guo", "trendInfo : ${getUploadTrendInfo()}")

        doUploadTrendPresent.doUploadTrend(map)

    }

    private fun getUploadTrendInfo(): String {

        val id = SPStaticUtils.getString(Constant.USER_ID, "13")

        val local = if (position == "不显示位置") {
            ""
        } else {
            position
        }

        val trendInfo = " {\"user_id\":              $id, " +                // 用户id
                "\"trends_type\":  \"$trendsType\"," +      // 动态类型
                "\"text_content\": \"$content\"," +          // 文字内容
                "\"image_url\":    \"$imageUrl\"," +         // 图片地址
                "\"video_url\":    \"$videoUrl\"," +         // 视频地址
                "\"video_cover\":  \"$coverUrl\"," +       // 视频封面
                "\"label\":        \"$label\"," +            // 储备字段，暂时不用
                "\"jingdu\":       \"$jingdu\"," +             // 经度
                "\"weidu\":        \"$weidu\"," +              // 纬度
                "\"position\":     \"$local\"}"           // 定位


        Log.i("guo", "trendInfo :$trendInfo")

        return trendInfo

    }


    // 调整分辨率压缩视频
    private fun doZipVideo(
        path: String,
        vga: String,
        targetPath: String,
    ) {
        RxFFmpegInvoke.getInstance().runCommandRxJava(zipVideo(path, vga, targetPath))
            .subscribe(object : RxFFmpegSubscriber() {
                override fun onError(message: String?) {
                    mVideoZipDialog.setContent(0)
                    mVideoZipDialog.dismissAllowingStateLoss()
                }

                override fun onFinish() {}

                override fun onProgress(progress: Int, progressTime: Long) {
                    mVideoZipDialog.setContent(progress)
                }

                override fun onCancel() {
                    mVideoZipDialog.setContent(0)
                }

                override fun onStart() {
                    super.onStart()
                    mVideoZipDialog.show(supportFragmentManager, "Dialog")
                }

                override fun onComplete() {
                    super.onComplete()

                    Log.i("guo", "complete")

                    Log.i("guo", "targetPath :$targetPath")

                    mVideoZipDialog.setContent(0)
                    mVideoZipDialog.dismissAllowingStateLoss()

                    ThreadUtils.runOnUiThread {

                        ll_send_loading.visibility = View.VISIBLE

                        // 视频动态 ，上传视频
                        val xlist: MutableList<String> = arrayListOf()

                        Thread {
                            val name = TimeUtils.getNowMills()

                            val file = File(targetPath)
                            val putObjectFromFileResponse = client.putObject("user${
                                SPStaticUtils.getString(Constant.USER_ID, "default")
                            }", "${name}.mp4", file)

                            val mLifeFirstUrl = client.generatePresignedUrl("user${
                                SPStaticUtils.getString(Constant.USER_ID, "default")
                            }", "${name}.mp4", -1).toString()

                            xlist.add(mLifeFirstUrl)


                            val x = xlist.toString().replace("[", "")

                            videoUrl = x.replace("]", "")


                            if (content != "") {


                                Log.i("guo", "文字 ----压缩 ")
                                doTextVerify = false
                                doTextVerify(content)
                            } else {

                                Log.i("guo", "上传 ----压缩 ")

                                uploadTrend()
                            }

                        }.start()

                    }


                }
            })
    }


    // 视频添加水印
    private fun addVideoWaterMark(
        path: String,
        picPath: String,
        targetPath: String,
    ) {
        RxFFmpegInvoke.getInstance().runCommandRxJava(addWaterMark(path, picPath, targetPath))
            .subscribe(object : RxFFmpegSubscriber() {
                override fun onError(message: String?) {
                    mVideoWaterMarkDialog.setContent(0)
                    mVideoWaterMarkDialog.dismissAllowingStateLoss()
                }

                override fun onFinish() {}

                override fun onProgress(progress: Int, progressTime: Long) {
                    mVideoWaterMarkDialog.setContent(progress)
                }

                override fun onCancel() {
                    mVideoWaterMarkDialog.setContent(0)
                }

                override fun onStart() {
                    super.onStart()
                    mVideoWaterMarkDialog.show(supportFragmentManager, "Dialog")
                }

                override fun onComplete() {
                    super.onComplete()
                    mVideoWaterMarkDialog.setContent(0)
                    mVideoWaterMarkDialog.dismissAllowingStateLoss()

                    Log.i("guo", "targetPath : $targetPath")


                    ThreadUtils.runOnUiThread {

                        Log.i("guo", "time : ${VideoUtil.getLocalVideoDuration(targetPath)}")

                        if (VideoUtil.getLocalVideoDuration(targetPath) > 120) {
                            ToastUtils.showShort("视频时长大于120秒")
                        }

                        var size = 0F

                        if (FileUtils.getSize(targetPath).contains("MB")) {
                            size = FileUtils.getSize(targetPath).replace("MB", "").toFloat()
                        } else if (FileUtils.getSize(targetPath).contains("KB")) {
                            size = FileUtils.getSize(targetPath).replace("KB", "").toFloat()
                            if (size < 1000) {
                                size = 1F
                            } else {
                                size /= 1000
                            }
                        }

                        Log.i("guo", "size : ${FileUtils.getSize(targetPath)} change to $size")

                        if (size <= 10F) {

                            // 正常符合流程的视频，直接上传

                            // 视频动态 ，上传视频

                            ll_send_loading.visibility = View.VISIBLE

                            val xlist: MutableList<String> = arrayListOf()

                            Thread {
                                val name = TimeUtils.getNowMills()

                                val file = File(targetPath)
                                val putObjectFromFileResponse = client.putObject("user${
                                    SPStaticUtils.getString(Constant.USER_ID, "default")
                                }", "${name}.mp4", file)

                                val mLifeFirstUrl = client.generatePresignedUrl("user${
                                    SPStaticUtils.getString(Constant.USER_ID, "default")
                                }", "${name}.mp4", -1).toString()

                                xlist.add(mLifeFirstUrl)


                                val x = xlist.toString().replace("[", "")

                                videoUrl = x.replace("]", "")


                                if (content != "") {

                                    Log.i("guo", "文字 ----视频 ")
                                    doTextVerify = false
                                    doTextVerify(content)
                                } else {
                                    Log.i("guo", "上传 ----视频 ")
                                    uploadTrend()
                                }

                            }.start()


                        } else {

                            val width = VideoUtil.getLocalVideoWidth(targetPath)
                            val height = VideoUtil.getLocalVideoHeight(targetPath)


                            val widthD = (width * 0.08).toInt()
                            val heightD = (height * 0.08).toInt()

                            val vga = "${widthD * 10}x${heightD * 10}"


                            val targetZipPath =
                                externalCacheDir.toString() + File.separator + "Zip" + FileUtils.getFileNameNoExtension(
                                    targetPath) + ".mp4"

                            doZipVideo(targetPath, vga, targetZipPath)

                        }

                    }

                }
            })
    }

    //  调整分辨率压缩视频
    //  ffmpeg -i Desktop/1.mov -s vga Desktop/1.mp4
    private fun zipVideo(
        VideoPath: String,
        vga: String,
        targetFile: String,
    ): Array<String?> {
        val cmdList = RxFFmpegCommandList()
        cmdList.append("-i")
        cmdList.append(VideoPath)
        cmdList.append("-s")
        cmdList.append(vga)
        cmdList.append(targetFile)
        return cmdList.build()
    }

    // 视频添加图片水印
    // ffmpeg -y -i videoPath.mp4 -i musicPath.png -filter_complex [0:v]scale=iw:ih[outv0];[1:0]scale=0.0:0.0[outv1];[outv0][outv1]overlay=0:0 -preset superfast targetFile.mp4
    //      scale：水印大小，水印长度＊水印的高度；
    //      overlay：水印的位置，距离屏幕左侧的距离＊距离屏幕上侧的距离；mainW主视频宽度， mainH主视频高度，overlayW水印宽度，overlayH水印高度
    //      左上角overlay参数为 overlay=0:0
    //      右上角为 overlay= main_w-overlay_w:0
    //      右下角为 overlay= main_w-overlay_w:main_h-overlay_h
    //      左下角为 overlay=0: main_h-overlay_h
    private fun addWaterMark(
        videoPath: String,
        picPath: String,
        targetFile: String,
    ): Array<String?> {
        val cmdList = RxFFmpegCommandList()
        cmdList.append("-y")
        cmdList.append("-i")
        cmdList.append(videoPath)
        cmdList.append("-i")
        cmdList.append(picPath)
        cmdList.append("-filter_complex")
        cmdList.append("[0:v]scale=iw:ih[outv0];[1:0]scale=0.0:0.0[outv1];[outv0][outv1]overlay= main_w-overlay_w:main_h-overlay_h")
        cmdList.append("-preset")
        cmdList.append("superfast")
        cmdList.append(targetFile)
        return cmdList.build()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> {
                    if (data != null) {
                        position = data.getStringExtra("name").toString()
                        weidu = data.getStringExtra("address").toString().split(",")[0]
                        jingdu = data.getStringExtra("address").toString().split(",")[1]

                        tv_send_location.text = position

                        if (position != "不显示位置") {
                            tv_send_location.setTextColor(Color.parseColor("#43A0FC"))
                            iv_send_location.setImageResource(R.drawable.ic_location_check)
                        } else {
                            tv_send_location.setTextColor(Color.parseColor("#717171"))
                            iv_send_location.setImageResource(R.drawable.ic_base_location)
                        }

                    }
                }
                1 -> {
                    // 录制视频回调
                    val task: TimerTask = object : TimerTask() {
                        override fun run() {
                            ThreadUtils.runOnUiThread {
                                mDataList.add(mTempPhotoPath)
                                mAdapter.data = mDataList
                                mAdapter.notifyDataSetChanged()

                                fixBottom()
                            }
                        }
                    }
                    val timer = Timer()
                    timer.schedule(task, 100)
                }
                2 -> {
                    // 拍照回调
                    val task: TimerTask = object : TimerTask() {
                        override fun run() {
                            ThreadUtils.runOnUiThread {
                                mDataList.add(mTempPhotoPath)
                                mAdapter.data = mDataList
                                mAdapter.notifyDataSetChanged()

                                fixBottom()
                            }
                        }
                    }
                    val timer = Timer()
                    timer.schedule(task, 100)
                }
            }
        }
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoTextVerifySuccess(textVerifyBean: TextVerifyBean?) {
        if (!doTextVerify) {
            doTextVerify = true

            if (textVerifyBean != null) {
                if (textVerifyBean.conclusion == "合规") {
                    // 上传动态

                    Log.i("guo", "上传 ----onDoTextVerifySuccess ")

                    uploadTrend()
                } else {
                    if (textVerifyBean.error_msg != null) {
                        ToastUtils.showShort(textVerifyBean.error_msg)
                    } else {
                        ToastUtils.showShort(textVerifyBean.data[0].msg)
                    }
                    ll_send_loading.visibility = View.GONE
                }
            }
        }

    }

    override fun onDoTextVerifyError() {
        ll_send_loading.visibility = View.GONE
    }

    override fun onDoUploadTrendSuccess(uploadTrendBean: UploadTrendBean?) {
        if (!doUploadTrend) {
            doUploadTrend = true

            Log.i("guo", "end --- UPLOAD : ${TimeUtils.getNowDate()}")

            Log.i("guo", "onDoUploadTrendSuccess")

            ll_send_loading.visibility = View.GONE

            if (uploadTrendBean != null) {
                if (uploadTrendBean.code == 200) {
                    // 返回上一页
                    // 更新视图

                    val intent = intent
                    setResult(RESULT_OK, intent)
                    finish()

                } else {
                    ToastUtils.showShort(uploadTrendBean.msg)
                }
            }
        }

    }

    override fun onDoUploadTrendError() {
        ll_send_loading.visibility = View.GONE
    }

    inner class AddChooseDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_send_add_choose

        override fun onCreate() {
            super.onCreate()

            val close: TextView = findViewById(R.id.tv_dialog_add_choose_cancel)
            val album: TextView = findViewById(R.id.tv_dialog_add_choose_album)
            val photo: TextView = findViewById(R.id.tv_dialog_add_choose_photo)
            val video: TextView = findViewById(R.id.tv_dialog_add_choose_video)

            close.setOnClickListener {
                dismiss()
            }

            album.setOnClickListener {

                val selectorStyle = PictureSelectorStyle()
                val animationStyle = PictureWindowAnimationStyle()
                animationStyle.setActivityEnterAnimation(R.anim.ps_anim_up_in)
                animationStyle.setActivityExitAnimation(R.anim.ps_anim_down_out)
                selectorStyle.windowAnimationStyle = animationStyle


                // 当数据列表为空时，视频图片都可选择
                // 当数据列表中有数据时，当有视频时，直接提示不可选取，当为图片时，则为只选择图片模式
                if (mDataList.isEmpty()) {
                    // 第一个文件不是视频，即选取都为图片
                    XXPermissions.with(context).permission(Permission.MANAGE_EXTERNAL_STORAGE)
                        .request(object : OnPermissionCallback {
                            override fun onGranted(
                                permissions: MutableList<String>?,
                                all: Boolean,
                            ) {
                                PictureSelector.create(this@DynamicSendActivity)
                                    .openGallery(com.luck.picture.lib.config.SelectMimeType.TYPE_ALL)
                                    .setImageEngine(GlideEngine.createGlideEngine())
                                    .setLanguage(LanguageConfig.SYSTEM_LANGUAGE)
                                    .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)
                                    .setImageSpanCount(3).isPreviewVideo(false)
                                    .isDisplayCamera(true).isPreviewImage(true)
                                    .isEmptyResultReturn(true).setSelectorUIStyle(selectorStyle)
                                    .setMaxSelectNum(9).setMaxVideoSelectNum(1)
                                    .forResult(object : OnResultCallbackListener<LocalMedia> {
                                        override fun onResult(result: ArrayList<LocalMedia>) {
                                            for (i in 0.until(result.size)) {
                                                mDataList.add(result[i].realPath)
                                            }


                                            if (FileUtils.getFileExtension(mDataList[0]) == "mp4") {
                                                // 视频查看文件大小，再考虑是否展示
                                                when {
                                                    VideoUtil.getLocalVideoDuration(mDataList[0]) < 5 -> {
                                                        mDataList.clear()
                                                        ToastUtils.showShort("视频长度不能小于5s")
                                                    }
                                                    VideoUtil.getLocalVideoDuration(mDataList[0]) > 120 -> {
                                                        mDataList.clear()
                                                        ToastUtils.showShort("视频时长不能超过2分钟")
                                                    }
                                                    else -> {
                                                        mAdapter.data = mDataList
                                                        mAdapter.notifyDataSetChanged()
                                                        fixBottom()
                                                    }
                                                }

                                            } else {
                                                // 图片直接展示
                                                mAdapter.data = mDataList
                                                mAdapter.notifyDataSetChanged()
                                                fixBottom()

                                            }


                                        }

                                        override fun onCancel() {
                                        }
                                    })
                            }

                            override fun onDenied(
                                permissions: MutableList<String>?,
                                never: Boolean,
                            ) {
                                super.onDenied(permissions, never)
                                ToastUtils.showShort("请授予应用所需权限")
                            }
                        })
                } else {
                    if (FileUtils.getFileExtension(mDataList[0]) == "mp4") {
                        ToastUtils.showShort("只可同时上传一个视频文件")
                    } else {
                        XXPermissions.with(context).permission(Permission.MANAGE_EXTERNAL_STORAGE)
                            .request(object : OnPermissionCallback {
                                override fun onGranted(
                                    permissions: MutableList<String>?,
                                    all: Boolean,
                                ) {
                                    PictureSelector.create(this@DynamicSendActivity)
                                        .openGallery(com.luck.picture.lib.config.SelectMimeType.TYPE_IMAGE)
                                        .setImageEngine(GlideEngine.createGlideEngine())
                                        .setLanguage(LanguageConfig.SYSTEM_LANGUAGE)
                                        .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)
                                        .setImageSpanCount(3).isPreviewVideo(false)
                                        .isDisplayCamera(true).isPreviewImage(true)
                                        .isEmptyResultReturn(true).setSelectorUIStyle(selectorStyle)
                                        .setMaxSelectNum(9 - mDataList.size).setMaxVideoSelectNum(1)
                                        .forResult(object : OnResultCallbackListener<LocalMedia> {
                                            override fun onResult(result: ArrayList<LocalMedia>) {
                                                for (i in 0.until(result.size)) {
                                                    mDataList.add(result[i].realPath)
                                                }
                                                mAdapter.data = mDataList
                                                mAdapter.notifyDataSetChanged()
                                                fixBottom()
                                            }

                                            override fun onCancel() {
                                            }
                                        })
                                }

                                override fun onDenied(
                                    permissions: MutableList<String>?,
                                    never: Boolean,
                                ) {
                                    super.onDenied(permissions, never)
                                    ToastUtils.showShort("请授予应用所需权限")
                                }
                            })
                    }
                }

                dismiss()

            }

            photo.setOnClickListener {

                XXPermissions.with(this@DynamicSendActivity).permission(Permission.CAMERA)
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(
                            permissions: MutableList<String>?,
                            all: Boolean,
                        ) {

                            if (all) {
                                mTempPhotoPath = Environment.getExternalStorageDirectory()
                                    .toString() + File.separator + "${TimeUtils.getNowMills()}.jpeg"

                                val tempPhotoFile: File = File(mTempPhotoPath)
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                // 如果在Android7.0以上,使用FileProvider获取Uri
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    val authority =
                                        this@DynamicSendActivity.packageName.toString() + ".fileProvider"
                                    val contentUri: Uri =
                                        DynamicFileProvider.getUriForFile(this@DynamicSendActivity,
                                            authority,
                                            tempPhotoFile)
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                                } else {
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(tempPhotoFile))
                                }
                                startActivityForResult(intent, 2)
                            } else {
                                ToastUtils.showShort("请授予应用相关权限")
                            }
                        }

                        override fun onDenied(
                            permissions: MutableList<String>?,
                            never: Boolean,
                        ) {
                            super.onDenied(permissions, never)
                            ToastUtils.showShort("请授予应用相关权限")
                        }

                    })

                dismiss()

            }

            video.setOnClickListener {

                XXPermissions.with(this@DynamicSendActivity).permission(Permission.CAMERA)
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(
                            permissions: MutableList<String>?,
                            all: Boolean,
                        ) {

                            if (all) {
                                mTempPhotoPath = Environment.getExternalStorageDirectory()
                                    .toString() + File.separator + "${TimeUtils.getNowMills()}.mp4"

                                val tempPhotoFile: File = File(mTempPhotoPath)
                                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                                // 如果在Android7.0以上,使用FileProvider获取Uri
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    val authority =
                                        this@DynamicSendActivity.packageName.toString() + ".fileProvider"
                                    val contentUri: Uri =
                                        DynamicFileProvider.getUriForFile(this@DynamicSendActivity,
                                            authority,
                                            tempPhotoFile)
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                                } else {
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(tempPhotoFile))
                                }
                                startActivityForResult(intent, 1)
                            }

                        }

                        override fun onDenied(
                            permissions: MutableList<String>?,
                            never: Boolean,
                        ) {
                            super.onDenied(permissions, never)
                            ToastUtils.showShort("请授予应用相关权限")
                        }

                    })

                dismiss()

            }

        }

        override fun onDismiss() {
            super.onDismiss()
        }

    }

    inner class IdentityDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_send_identity

        override fun onCreate() {
            super.onCreate()

            findViewById<ImageView>(R.id.iv_dialog_send_identity_close).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_dialog_send_identity_cancel).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_dialog_send_identity_jump).setOnClickListener {
                dismiss()
                ToastUtils.showShort("前往实名认证界面")
                val intent = Intent(this@DynamicSendActivity, VerifyActivity::class.java)
                startActivity(intent)
            }

        }

        override fun onDismiss() {
            super.onDismiss()
        }

    }

    inner class AvatarDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_like_avatar

        override fun onCreate() {
            super.onCreate()

            findViewById<ImageView>(R.id.iv_dialog_like_avatar_close).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_dialog_like_avatar_jump).setOnClickListener {
                dismiss()
                ToastUtils.showShort("跳转到资料填写界面")
                startActivity(Intent(context, UserActivity::class.java))
            }

        }

        override fun onDismiss() {
            super.onDismiss()
        }

    }

    inner class TextViewSpan : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = resources.getColor(R.color.service_color)
        }

        override fun onClick(widget: View) {
            startActivity(SetWebActivity.getIntent(this@DynamicSendActivity,
                "个人动态服务协议",
                DataProvider.WebUrlData[8].url))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        doTextVerifyPresent.unregisterCallback(this)
        doUploadTrendPresent.unregisterCallback(this)

        SPStaticUtils.put("dynamic_send_city", "")
        SPStaticUtils.put("dynamic_send_location", "")

    }

}