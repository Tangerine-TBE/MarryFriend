package com.twx.marryfriend.dynamic.send

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import com.twx.marryfriend.bean.dynamic.UploadTrendBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.utils.emoji.EmojiDetailAdapter
import com.twx.marryfriend.dynamic.send.adapter.OnNineGridViewListener
import com.twx.marryfriend.dynamic.send.adapter.PhotoPublishAdapter
import com.twx.marryfriend.dynamic.send.location.LocationActivity
import com.twx.marryfriend.dynamic.send.utils.ItemTouchHelperCallback
import com.twx.marryfriend.utils.DynamicFileProvider
import com.twx.marryfriend.utils.GlideEngine
import com.twx.marryfriend.utils.emoji.EmojiUtils
import kotlinx.android.synthetic.main.activity_dynamic_send.*
import java.io.File
import java.io.Serializable
import java.util.*

class DynamicSendActivity : MainBaseViewActivity(),
    com.twx.marryfriend.net.callback.dynamic.IDoUploadTrendCallback {

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
    private val videoUrl = ""

    // 视频封面
    private val videoCover = ""

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

    private var mDataList: ArrayList<String> = arrayListOf()

    private lateinit var mAdapter: PhotoPublishAdapter

    private lateinit var myCallBack: ItemTouchHelperCallback

    private lateinit var mItemTouchHelper: ItemTouchHelper

    private lateinit var doUploadTrendPresent: com.twx.marryfriend.net.impl.dynamic.doUploadTrendPresentImpl

    var emojiList: MutableList<String> = arrayListOf()
    private lateinit var emojiAdapter: EmojiDetailAdapter

    private lateinit var client: BosClient

    override fun getLayoutView(): Int = R.layout.activity_dynamic_send

    override fun initView() {
        super.initView()

        doUploadTrendPresent = com.twx.marryfriend.net.impl.dynamic.doUploadTrendPresentImpl.getsInstance()
        doUploadTrendPresent.registerCallback(this)

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

    }

    override fun initLoadData() {
        super.initLoadData()

//        mTempPhotoPath = Environment.getExternalStorageDirectory()
//            .toString() + File.separator + "333 .jpeg"


    }

    override fun initPresent() {
        super.initPresent()

        val config: BosClientConfiguration = BosClientConfiguration()
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

        mAdapter.setOnNineGridViewListener(object : OnNineGridViewListener {
            override fun onAddPic(addCount: Int) {

                XPopup.Builder(this@DynamicSendActivity)
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(AddChooseDialog(this@DynamicSendActivity))
                    .show()

            }

            override fun onDisplayImg(context: Context?, data: String?, imageView: ImageView?) {

                ToastUtils.showShort(data)

                if (imageView != null) {

                    Glide.with(context!!)
                        .load(data)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .into(imageView)

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

                if (FileUtils.getFileExtension(data) != "mp4") {
                    val intent = Intent(this@DynamicSendActivity, com.twx.marryfriend.dynamic.preview.image.ImagePreviewActivity::class.java)
                    intent.putExtra("imageList", mDataList as Serializable)
                    intent.putExtra("imageIndex", position)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@DynamicSendActivity, com.twx.marryfriend.dynamic.preview.video.VideoPreviewActivity::class.java)
                    intent.putExtra("videoUrl", mDataList[0])
                    intent.putExtra("name", FileUtils.getFileNameNoExtension(mDataList[0]))
                    startActivity(intent)
                }

            }

            override fun onLongClickPic(
                viewHolder: RecyclerView.ViewHolder?,
                data: String?,
                position: Int,
            ) {
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

            // 保证只执行一次
            var x = true

            XXPermissions.with(this)
                .permission(Permission.ACCESS_FINE_LOCATION)
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {

                        val mLocationClient = LocationClient(this@DynamicSendActivity)

                        mLocationClient.registerLocationListener(object :
                            BDAbstractLocationListener() {
                            override fun onReceiveLocation(location: BDLocation) {

                                if (x) {
                                    val city = location.city
                                    val location = "${location.longitude},${location.latitude}"
                                    val intent = Intent(this@DynamicSendActivity,
                                        LocationActivity::class.java)
                                    intent.putExtra("location", location)
                                    intent.putExtra("city", city)

                                    Log.i("Guo", "startActivityForResult")

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
                XXPermissions.with(this)
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                            PictureSelector.create(this@DynamicSendActivity)
                                .openGallery(com.luck.picture.lib.config.SelectMimeType.TYPE_ALL)
                                .setImageEngine(GlideEngine.createGlideEngine())
                                .setLanguage(LanguageConfig.SYSTEM_LANGUAGE)
                                .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)
                                .setImageSpanCount(3)
                                .isPreviewVideo(false)
                                .isDisplayCamera(true)
                                .isPreviewImage(true)
                                .isEmptyResultReturn(true)
                                .setSelectorUIStyle(selectorStyle)
                                .setMaxSelectNum(9)
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
                    XXPermissions.with(this)
                        .permission(Permission.MANAGE_EXTERNAL_STORAGE)
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
                                    .setImageSpanCount(3)
                                    .isPreviewVideo(false)
                                    .isDisplayCamera(true)
                                    .isPreviewImage(true)
                                    .isEmptyResultReturn(true)
                                    .setSelectorUIStyle(selectorStyle)
                                    .setMaxSelectNum(9 - mDataList.size)
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
            ToastUtils.showShort("打开相机")

            XXPermissions.with(this@DynamicSendActivity)
                .permission(Permission.CAMERA)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>?,
                        all: Boolean,
                    ) {

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
                ll_emoji_send_container.visibility = View.GONE
            } else {
                ll_emoji_send_container.visibility = View.VISIBLE
            }
        }

        tv_send_send.setOnClickListener {

            content = et_send_content.text.toString().trim { it <= ' ' }

            if (mDataList.isNotEmpty() || content != "") {

                if (SPStaticUtils.getBoolean(Constant.IS_IDENTITY_VERIFY, false)) {

                    if (System.currentTimeMillis() - lastClickTime >= delayTime) {
                        lastClickTime = System.currentTimeMillis();

                        ToastUtils.showShort("上传")

                        when (mDataList.size) {
                            0 -> {
                                trendsType = 3
                            }
                            1 -> {
                                trendsType =
                                    if (com.blankj.utilcode.util.FileUtils.getFileExtension(
                                            mDataList[0]) == "mp4"
                                    ) {
                                        2
                                    } else {
                                        1
                                    }
                            }
                            else -> {
                                trendsType = 1
                            }
                        }


                        // 还需要上传图片

                        if (mDataList.isNotEmpty()) {
                            // 根据类型上传图片或视频后在上传动态

                            val xlist: MutableList<String> = arrayListOf()

                            Thread {

                                Log.i("guo", mDataList.toString())

                                // 分图片与视频两种不同上传方式
                                if (FileUtils.getFileExtension(mDataList[0]) == "mp4") {
                                    // 视频

                                    val file = File(mDataList[0])
                                    val putObjectFromFileResponse = client.putObject("user${
                                        SPStaticUtils.getString(Constant.USER_ID, "default")
                                    }", "${TimeUtils.getNowString()}.mp4", file)

                                    val mLifeFirstUrl = client.generatePresignedUrl("user${
                                        SPStaticUtils.getString(Constant.USER_ID, "default")
                                    }", "${TimeUtils.getNowString()}.mp4", -1).toString()

                                    xlist.add(mLifeFirstUrl)

                                } else {
                                    // 图片
                                    for (i in 0.until(mDataList.size)) {

                                        val file = File(mDataList[i])
                                        val putObjectFromFileResponse = client.putObject("user${
                                            SPStaticUtils.getString(Constant.USER_ID, "default")
                                        }", "${TimeUtils.getNowString()}.jpg", file)

                                        val mLifeFirstUrl = client.generatePresignedUrl("user${
                                            SPStaticUtils.getString(Constant.USER_ID, "default")
                                        }", "${TimeUtils.getNowString()}.jpg", -1).toString()

                                        xlist.add(mLifeFirstUrl)

                                    }
                                }

                                val x = xlist.toString().replace("[", "")

                                imageUrl = x.replace("]", "")

                                Log.i("guo", imageUrl)

                                uploadTrend()

                            }.start()

                        } else {
                            // 不需要上传图片，直接上传文字即可
                            uploadTrend()
                        }

                    } else {
                        ToastUtils.showShort("点击太频繁了，请稍后再评论")
                    }
                } else {
                    // 前往认证
                    XPopup.Builder(this@DynamicSendActivity)
                        .dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true)
                        .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                        .asCustom(IdentityDialog(this@DynamicSendActivity))
                        .show()
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

    private fun uploadTrend() {

        val map: MutableMap<String, String> = TreeMap()
        map[Contents.TREND_INFO] = getUploadTrendInfo()
        doUploadTrendPresent.doUploadTrend(map)

        ThreadUtils.runOnUiThread {
            ll_send_loading.visibility = View.VISIBLE
        }

    }

    private fun getUploadTrendInfo(): String {

        val id = SPStaticUtils.getString(Constant.USER_ID, "13")

        val trendInfo =
            " {\"user_id\":              $id, " +                // 用户id
                    "\"trends_type\":  \"$trendsType\"," +      // 动态类型
                    "\"text_content\": \"$content\"," +          // 文字内容
                    "\"image_url\":    \"$imageUrl\"," +         // 图片地址
                    "\"video_url\":    \"$videoUrl\"," +         // 视频地址
                    "\"video_cover\":  \"$videoCover\"," +       // 视频封面
                    "\"label\":        \"$label\"," +            // 储备字段，暂时不用
                    "\"jingdu\":         $jingdu," +             // 经度
                    "\"weidu\":          $weidu," +              // 纬度
                    "\"position\":     \"$position\"}"           // 定位


        Log.i("guo", "trendInfo :$trendInfo")

        return trendInfo

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

    override fun onDoUploadTrendSuccess(uploadTrendBean: UploadTrendBean) {
        if (uploadTrendBean.code == 200) {
            // 返回上一页
            // 更新视图

            ll_send_loading.visibility = View.GONE

            val intent = intent
            setResult(RESULT_OK, intent)
            finish()

        }

    }

    override fun onDoUploadTrendError() {
        ll_send_loading.visibility = View.GONE
    }

    inner class AddChooseDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_send_add_choose

        override fun onCreate() {
            super.onCreate()

            val close: ImageView = findViewById(R.id.iv_dialog_add_choose_close)
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
                    XXPermissions.with(context)
                        .permission(Permission.MANAGE_EXTERNAL_STORAGE)
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
                                    .setImageSpanCount(3)
                                    .isPreviewVideo(false)
                                    .isDisplayCamera(true)
                                    .isPreviewImage(true)
                                    .isEmptyResultReturn(true)
                                    .setSelectorUIStyle(selectorStyle)
                                    .setMaxSelectNum(9)
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
                        XXPermissions.with(context)
                            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
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
                                        .setImageSpanCount(3)
                                        .isPreviewVideo(false)
                                        .isDisplayCamera(true)
                                        .isPreviewImage(true)
                                        .isEmptyResultReturn(true)
                                        .setSelectorUIStyle(selectorStyle)
                                        .setMaxSelectNum(9 - mDataList.size)
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

                ToastUtils.showShort("打开相机")

                XXPermissions.with(this@DynamicSendActivity)
                    .permission(Permission.CAMERA)
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(
                            permissions: MutableList<String>?,
                            all: Boolean,
                        ) {

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

                ToastUtils.showShort("打开相机,录制视频")

                XXPermissions.with(this@DynamicSendActivity)
                    .permission(Permission.CAMERA)
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(
                            permissions: MutableList<String>?,
                            all: Boolean,
                        ) {

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
            }


        }

        override fun onDismiss() {
            super.onDismiss()
        }

    }

}