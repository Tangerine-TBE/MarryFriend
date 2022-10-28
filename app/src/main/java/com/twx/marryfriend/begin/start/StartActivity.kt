package com.twx.marryfriend.begin.start

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
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
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.begin.BeginActivity
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.push.PushManager
import com.twx.marryfriend.utils.GlideEngine
import com.xyzz.myutils.show.iLog
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.util.ArrayList

class StartActivity : MainBaseViewActivity() {

    override fun getLayoutView(): Int = R.layout.activity_start

    override fun initView() {
        super.initView()
        intent.extras?.also {
            iLog(it.toString(),"推送，启动页传数据")
            val t=it.getString("t")
            val f=it.getString("f")
            if (t==null||f==null){
                PushManager.onNotificationMessageClicked(this)
            }else{
                PushManager.onNotificationMessageClicked(this,t,f)
            }
        }

        if (!SPStaticUtils.getBoolean(Constant.FIRST_START, true)) {

            lifecycleScope.launch {
                delay(500)
                startActivity(Intent(this@StartActivity, BeginActivity::class.java))
                this@StartActivity.finish()
            }

        } else {

            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(StartDialog(this)).show()

        }


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

    // 上传头像
    inner class StartDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.layout_start

        override fun onCreate() {
            super.onCreate()

            findViewById<TextView>(R.id.tv_start_refuse).setOnClickListener {
                dismiss()
                AppUtils.exitApp()
            }

            findViewById<TextView>(R.id.tv_start_agree).setOnClickListener {

                dismiss()

                SPStaticUtils.put(Constant.FIRST_START, false)

                startActivity(Intent(this@StartActivity, BeginActivity::class.java))
                this@StartActivity.finish()

            }

        }

    }

}