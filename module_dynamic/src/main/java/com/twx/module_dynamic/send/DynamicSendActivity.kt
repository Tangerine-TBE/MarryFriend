package com.twx.module_dynamic.send

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
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
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.style.PictureWindowAnimationStyle
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.twx.module_base.base.MainBaseViewActivity
import com.twx.module_base.constant.Constant
import com.twx.module_base.constant.Contents
import com.twx.module_base.utils.pictureSelector.GlideEngine
import com.twx.module_dynamic.R
import com.twx.module_dynamic.bean.UploadTrendBean
import com.twx.module_dynamic.net.callback.IDoUploadTrendCallback
import com.twx.module_dynamic.net.impl.doUploadTrendPresentImpl
import com.twx.module_dynamic.preview.image.ImagePreviewActivity
import com.twx.module_dynamic.send.adapter.OnNineGridViewListener
import com.twx.module_dynamic.send.adapter.PhotoPublishAdapter
import com.twx.module_dynamic.send.location.LocationActivity
import com.twx.module_dynamic.send.utils.DragItemHelperCallBack
import com.twx.module_dynamic.send.utils.FileUtils
import com.twx.module_dynamic.send.utils.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.activity_dynamic_send.*
import java.io.File
import java.io.Serializable
import java.util.*

class DynamicSendActivity : MainBaseViewActivity(), IDoUploadTrendCallback {

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

    private var mDataList: ArrayList<String> = arrayListOf()

    private lateinit var mAdapter: PhotoPublishAdapter

    private lateinit var myCallBack: ItemTouchHelperCallback

    private lateinit var mItemTouchHelper: ItemTouchHelper

    private lateinit var doUploadTrendPresent: doUploadTrendPresentImpl


    private lateinit var client: BosClient

    override fun getLayoutView(): Int = R.layout.activity_dynamic_send

    override fun initView() {
        super.initView()

        doUploadTrendPresent = doUploadTrendPresentImpl.getsInstance()
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
    }

    override fun initLoadData() {
        super.initLoadData()
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
                }
            }

            override fun onDeletePic(data: String?, position: Int) {

            }

            override fun onClickPic(data: String?, position: Int) {
                val intent = Intent(this@DynamicSendActivity, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", mDataList as Serializable)
                intent.putExtra("imageIndex", position)
                startActivity(intent)
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

                //item删除后需要重新计算底部区域的显示位置，否则会造成底部区域显示混乱
                fixBottom()
            }

        })

        rl_send_location.setOnClickListener {

            XXPermissions.with(this)
                .permission(Permission.ACCESS_FINE_LOCATION)
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {

                        val mLocationClient = LocationClient(this@DynamicSendActivity)

                        mLocationClient.registerLocationListener(object :
                            BDAbstractLocationListener() {
                            override fun onReceiveLocation(location: BDLocation) {
                                val location = "${location.longitude},${location.latitude}"
                                val intent =
                                    Intent(this@DynamicSendActivity, LocationActivity::class.java)
                                intent.putExtra("location", location)
                                startActivityForResult(intent, 0)
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

        tv_send_send.setOnClickListener {

            when (mDataList.size) {
                0 -> {
                    trendsType = 3
                }
                1 -> {
                    trendsType =
                        if (com.blankj.utilcode.util.FileUtils.getFileExtension(mDataList[0]) == "mp4") {
                            2
                        } else {
                            1
                        }
                }
                else -> {
                    trendsType = 1
                }
            }

            content = et_send_content.text.toString().trim { it <= ' ' }

            // 还需要上传图片

            val xlist: MutableList<String> = arrayListOf()

            Thread {

                Log.i("guo", mDataList.toString())

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

                val x = xlist.toString().replace("[","")

                imageUrl = x.replace("]","")

                Log.i("guo",imageUrl)

                uploadTrend()

            }.start()

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

    }

    private fun getUploadTrendInfo(): String {

        val id = SPStaticUtils.getInt(Constant.USER_ID, 13)

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

        return trendInfo

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                position = data.getStringExtra("name").toString()
                weidu = data.getStringExtra("address").toString().split(",")[0]
                jingdu = data.getStringExtra("address").toString().split(",")[1]

                tv_send_location.text = position

            }


        }
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUploadTrendSuccess(uploadTrendBean: UploadTrendBean?) {

    }

    override fun onDoUploadTrendError() {

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
                animationStyle.setActivityEnterAnimation(com.twx.module_base.R.anim.ps_anim_up_in)
                animationStyle.setActivityExitAnimation(com.twx.module_base.R.anim.ps_anim_down_out)
                selectorStyle.windowAnimationStyle = animationStyle

                PictureSelector.create(context)
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

                            Log.i("guo", "mDataList : ${mDataList}")

                            fixBottom()
                            dismiss()
                        }

                        override fun onCancel() {
                        }
                    })
            }



            photo.setOnClickListener {

            }

            video.setOnClickListener {

            }

        }

        override fun onDismiss() {
            super.onDismiss()
        }

    }


}