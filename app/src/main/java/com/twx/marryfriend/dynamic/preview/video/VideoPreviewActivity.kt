package com.twx.marryfriend.dynamic.preview.video

import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import kotlinx.android.synthetic.main.activity_video_preview.*

class VideoPreviewActivity : MainBaseViewActivity() {

    private var url = ""
    private var name = ""

    private lateinit var orientationUtils: OrientationUtils

    override fun getLayoutView(): Int = R.layout.activity_video_preview

    override fun initView() {
        super.initView()

        url = intent.getStringExtra("videoUrl").toString()
        name = intent.getStringExtra("name").toString()

        vp_video_preview_container.setUp(url, true, name)

        //增加title
        vp_video_preview_container.titleTextView.visibility = View.VISIBLE
        //设置返回键
        vp_video_preview_container.backButton.visibility = android.view.View.VISIBLE

        //是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏
        vp_video_preview_container.isAutoFullWithSize = true
        //音频焦点冲突时是否释放
        vp_video_preview_container.isReleaseWhenLossAudio = false
        //全屏动画
        vp_video_preview_container.isShowFullAnimation = true

        //是否可以滑动调整
        vp_video_preview_container.setIsTouchWiget(true)

        //设置旋转
        orientationUtils = OrientationUtils(this, vp_video_preview_container)

        //设置返回按键功能
        vp_video_preview_container.backButton.setOnClickListener {
            vp_video_preview_container.setVideoAllCallBack(null)
            finish()
        }

        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        vp_video_preview_container.fullscreenButton.setOnClickListener {
            orientationUtils.resolveByClick();
            vp_video_preview_container.startWindowFullscreen(this, false, true)
        }

        vp_video_preview_container.startPlayLogic()

    }

    override fun initLoadData() {
        super.initLoadData()
    }


    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()
    }

    override fun onPause() {
        super.onPause()
        vp_video_preview_container.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        vp_video_preview_container.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
        if (orientationUtils != null) orientationUtils!!.releaseListener()
    }

}