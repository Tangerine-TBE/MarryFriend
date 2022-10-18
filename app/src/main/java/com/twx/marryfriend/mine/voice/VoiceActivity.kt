package com.twx.marryfriend.mine.voice

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import com.baidubce.auth.DefaultBceCredentials
import com.baidubce.services.bos.BosClient
import com.baidubce.services.bos.BosClientConfiguration
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.UpdateGreetInfoBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.mine.record.AudioRecorder
import com.twx.marryfriend.net.callback.IDoUpdateGreetInfoCallback
import com.twx.marryfriend.net.impl.doUpdateGreetInfoPresentImpl
import kotlinx.android.synthetic.main.activity_voice.*
import java.io.File
import java.util.*

class VoiceActivity : MainBaseViewActivity(), IDoUpdateGreetInfoCallback {


    // 录音按钮当前模式
    private var recordMode = "start"

    // 录音文件路径
    private var recordPath = ""

    // 录音结束之后是否结束此界面
    private var close = false

    // 录音工具
    private lateinit var audioRecorder: AudioRecorder

    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var client: BosClient

    private lateinit var doUpdateGreetPresent: doUpdateGreetInfoPresentImpl

    companion object {
        private const val CLOSE = "isClose"
        fun getInt(context: Context, close: Boolean? = false): Intent {
            val intent = Intent(context, VoiceActivity::class.java)
            intent.putExtra(CLOSE, close)
            return intent
        }

    }

    override fun getLayoutView(): Int = R.layout.activity_voice

    override fun initView() {
        super.initView()

        close = intent.getBooleanExtra("isClose", false)

        doUpdateGreetPresent = doUpdateGreetInfoPresentImpl.getsInstance()
        doUpdateGreetPresent.registerCallback(this)

        tv_voice_button.text = "点击开始录音"

        recordPath = "/storage/emulated/0/Android/data/com.jiaou.love/cache/record.wav"

        audioRecorder = AudioRecorder.getInstance()
        mediaPlayer = MediaPlayer()

        val config: BosClientConfiguration = BosClientConfiguration()
        config.credentials = DefaultBceCredentials("545c965a81ba49889f9d070a1e147a7b",
            "1b430f2517d0460ebdbecfd910c572f8")
        config.endpoint = "http://adrmf.gz.bcebos.com"
        client = BosClient(config)

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_voice_finish.setOnClickListener {
            finish()
        }

        ll_voice_button.setOnClickListener {

            XXPermissions.with(this).permission(Permission.RECORD_AUDIO)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        if (all) {
                            when (recordMode) {
                                "start" -> {
                                    tv_voice_button.text = "点击结束"
                                    recordMode = "stop"

                                    iv_voice_state.setImageResource(R.drawable.ic_record_start)
                                    iv_voice_state.visibility = View.GONE
                                    avv_voice_state.visibility = View.VISIBLE

                                    FileUtils.delete(recordPath)

                                    // 计时器
                                    voice_timer.base = SystemClock.elapsedRealtime() //计时器清零
                                    val hour =
                                        (SystemClock.elapsedRealtime() - voice_timer.base) / 1000 / 3600
                                    voice_timer.format = "0$hour:%s"
                                    voice_timer.start()

                                    // 录音
                                    audioRecorder.createDefaultAudio("record", this@VoiceActivity)
                                    audioRecorder.startRecord(null)
                                }
                                "stop" -> {

                                    Log.i("guo",
                                        "time :${(SystemClock.elapsedRealtime() - voice_timer.base).toString()}")

                                    // 存储录音文件的长度
                                    SPStaticUtils.put(Constant.ME_VOICE_LONG,
                                        (SystemClock.elapsedRealtime() - voice_timer.base).toString())
                                    SPStaticUtils.put(Constant.ME_VOICE_NAME, "Greet")

                                    tv_voice_button.text = "点击播放"
                                    ll_voice_delete.visibility = View.VISIBLE
                                    ll_voice_confirm.visibility = View.VISIBLE
                                    recordMode = "listen"
                                    iv_voice_state.visibility = View.VISIBLE
                                    avv_voice_state.visibility = View.GONE
                                    iv_voice_state.setImageResource(R.drawable.ic_record_play)

                                    voice_timer.stop()

                                    audioRecorder.stopRecord()
                                }
                                "listen" -> {
                                    ToastUtils.showShort("播放录音")
                                    tv_voice_button.text = "播放结束"
                                    iv_voice_state.visibility = View.GONE
                                    avv_voice_state.visibility = View.VISIBLE
                                    recordMode = "listenStop"

                                    mediaPlayer.reset()
                                    mediaPlayer.setDataSource(recordPath);
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();
                                }
                                "listenStop" -> {
                                    ToastUtils.showShort("结束播放录音")
                                    tv_voice_button.text = "点击播放"
                                    recordMode = "listen"
                                    iv_voice_state.visibility = View.VISIBLE
                                    avv_voice_state.visibility = View.GONE

                                    mediaPlayer.stop()
                                }

                            }
                        } else {
                            ToastUtils.showShort("请授予用户相应权限")
                        }
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        super.onDenied(permissions, never)
                        ToastUtils.showShort("请授予用户相应权限")
                    }

                })

        }

        ll_voice_delete.setOnClickListener {
            ll_voice_delete.visibility = View.GONE
            ll_voice_confirm.visibility = View.GONE
            iv_voice_state.setImageResource(R.drawable.ic_record_start)
            iv_voice_state.visibility = View.VISIBLE
            avv_voice_state.visibility = View.GONE

            tv_voice_button.text = "点击开始录音"
            recordMode = "start"

        }

        ll_voice_confirm.setOnClickListener {

            ToastUtils.showShort("录音选择完成，开始上传")

            Thread {

                //上传Object
                val file = File(recordPath)
                // bucketName 为文件夹名 ，使用用户id来进行命名
                // key值为保存文件名，试用固定的几种格式来命名

                val name = TimeUtils.getNowMills()

                val putObjectFromFileResponse = client.putObject("user${
                    SPStaticUtils.getString(Constant.USER_ID, "default")
                }", "${name}.mp3", file)

                val mVoiceUrl = client.generatePresignedUrl("user${
                    SPStaticUtils.getString(Constant.USER_ID, "default")
                }", "${name}.mp3", -1).toString()

                Log.i("guo", mVoiceUrl)

                SPStaticUtils.put(Constant.ME_VOICE, mVoiceUrl)

                val map: MutableMap<String, String> = TreeMap()
                map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
                map[Contents.GREET_UPDATE] = getGreetInfo()
                doUpdateGreetPresent.doUpdateGreetInfo(map)

            }.start()

        }

    }

    // 获取语音
    private fun getGreetInfo(): String {

        val sex = SPStaticUtils.getInt(Constant.ME_SEX, 1)
        val voiceUrl = SPStaticUtils.getString(Constant.ME_VOICE, "")
        val voiceLong = SPStaticUtils.getString(Constant.ME_VOICE_LONG, "")
        val voiceName = SPStaticUtils.getString(Constant.ME_VOICE_NAME, "")
        val greet = SPStaticUtils.getString(Constant.ME_GREET, "")

        Log.i("guo", "voice_long : $voiceLong")

        val greetInfo =
            " {\"user_sex\":                    $sex, " + "\"voice_url\":           \"$voiceUrl\"," + "\"voice_long\":          \"$voiceLong\"," + "\"voice_name\":          \"$voiceName\"," + " \"zhaohuyu_content\":   \"$greet\"}"

        return greetInfo

    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUpdateGreetInfoSuccess(updateGreetInfoBean: UpdateGreetInfoBean?) {
        if (updateGreetInfoBean != null) {
            if (updateGreetInfoBean.code == 200) {

                ToastUtils.showShort("上传成功")

                if (close) {
                    this.finish()
                }
            } else {
                ToastUtils.showShort(updateGreetInfoBean.msg)
            }
        }

    }

    override fun onDoUpdateGreetInfoError() {
        ToastUtils.showShort("上传失败，请稍后再试")
    }

    override fun onDestroy() {
        super.onDestroy()

        doUpdateGreetPresent.unregisterCallback(this)

    }

}