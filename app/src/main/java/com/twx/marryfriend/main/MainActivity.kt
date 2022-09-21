package com.twx.marryfriend.main

import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.annotation.Nullable
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.ImHelper
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.vip.UpdateTokenBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.DynamicFragment
import com.twx.marryfriend.likeme.LoveFragment
import com.twx.marryfriend.message.ImConversationFragment
import com.twx.marryfriend.mine.MineFragment
import com.twx.marryfriend.net.callback.vip.IDoUpdateTokenCallback
import com.twx.marryfriend.net.impl.vip.doUpdateTokenPresentImpl
import com.twx.marryfriend.push.help.PushHelper
import com.twx.marryfriend.recommend.RecommendFragment
import com.umeng.message.PushAgent
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : MainBaseViewActivity(), IDoUpdateTokenCallback {

    private var recommend: RecommendFragment? = null
    private var love: LoveFragment? = null
    private var dynamic: DynamicFragment? = null
    private var conversationListFragment: ImConversationFragment? = null
    private var mine: MineFragment? = null

    private lateinit var doUpdateTokenPresent: doUpdateTokenPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_main

    override fun initView() {
        super.initView()

        doUpdateTokenPresent = doUpdateTokenPresentImpl.getsInstance()
        doUpdateTokenPresent.registerCallback(this)

        initEmojiCompat()
        ImHelper.init()
        initRecommendFragment()
        Thread {
            PushHelper.init(applicationContext)
//            Bugly.init(applicationContext, "2128c50665", true);
        }.start()



        PushAgent.getInstance(this).onAppStart()


    }

    override fun initLoadData() {
        super.initLoadData()

        val task: TimerTask = object : TimerTask() {
            override fun run() {

                Log.i("guo",
                    "main_device_token : ${SPStaticUtils.getString(Constant.DEVICE_TOKEN, "")}")

                val token = SPStaticUtils.getString(Constant.DEVICE_TOKEN, "")

                if (token != "") {
                    updateToken(token)
                } else {
                    ToastUtils.showShort("获取友盟推送token失败")
                }

            }
        }
        val timer = Timer()
        //10秒后执行
        timer.schedule(task, 3 * 1000)

    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        rb_main_recommend.setOnClickListener {
            initRecommendFragment()
        }

        rb_main_love.setOnClickListener {
            initLoveFragment()
        }

        rb_main_dynamic.setOnClickListener {
            initDynamicFragment()
        }

        rb_main_message.setOnClickListener {
            initMessageFragment()
        }

        rb_main_mine.setOnClickListener {
            initMineFragment()
        }

    }


    private fun updateToken(token: String) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.UMENG_TOKEN] = token
        doUpdateTokenPresent.doUpdateToken(map)
    }


    private fun initRecommendFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (recommend == null) {
            recommend = RecommendFragment()
            transaction.add(R.id.fl_main_container, recommend!!)
        }
        hideFragment(transaction)
        transaction.show(recommend!!)
        transaction.commit()
    }

    private fun initLoveFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (love == null) {
            love = LoveFragment()
            transaction.add(R.id.fl_main_container, love!!)
        }
        hideFragment(transaction)
        transaction.show(love!!)
        transaction.commit()
    }

    private fun initDynamicFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (dynamic == null) {
            dynamic = DynamicFragment().newInstance(this)
            transaction.add(R.id.fl_main_container, dynamic!!)
        }
        hideFragment(transaction)
        transaction.show(dynamic!!)
        transaction.commit()
    }

    private fun initMessageFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (conversationListFragment == null) {
            conversationListFragment = ImConversationFragment()
            transaction.add(R.id.fl_main_container, conversationListFragment!!)
        } else {
            conversationListFragment?.onResume()
        }
        hideFragment(transaction)
        transaction.show(conversationListFragment!!)
        transaction.commit()
    }

    private fun initMineFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (mine == null) {
            mine = MineFragment()
            transaction.add(R.id.fl_main_container, mine!!)
        }
        hideFragment(transaction)
        transaction.show(mine!!)
        transaction.commit()
    }

    private fun hideFragment(transaction: FragmentTransaction) {
        if (recommend != null) {
            transaction.hide(recommend!!)
        }
        if (love != null) {
            transaction.hide(love!!)
        }
        if (dynamic != null) {
            transaction.hide(dynamic!!)
        }
        if (conversationListFragment != null) {
            transaction.hide(conversationListFragment!!)
        }
        if (mine != null) {
            transaction.hide(mine!!)
        }
    }

    // 加载Emoji
    private fun initEmojiCompat() {
        val config: EmojiCompat.Config
        // Use a downloadable font for EmojiCompat
        val fontRequest = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            "Noto Color Emoji Compat",
            R.array.com_google_android_gms_fonts_certs)
        config = FontRequestEmojiCompatConfig(applicationContext, fontRequest)

        config.setReplaceAll(true)
            .registerInitCallback(object : EmojiCompat.InitCallback() {
                override fun onInitialized() {
                }

                override fun onFailed(@Nullable throwable: Throwable?) {
                    Log.e("guo", "EmojiCompat initialization failed", throwable)
                }
            })
        EmojiCompat.init(config)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == FragmentActivity.RESULT_OK) {
            when (requestCode) {
                UCrop.REQUEST_CROP -> {
                    if (data != null) {
                        mine?.handlePhotoCropResult(data)
                    };
                }
                UCrop.RESULT_ERROR -> {
                    if (data != null) {
                        mine?.handlePhotoCropError(data)
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        recommend?.dispatchTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppUtils.exitApp()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUpdateTokenSuccess(updateTokenBean: UpdateTokenBean?) {
        if (updateTokenBean != null) {
            if (updateTokenBean.code == 200) {
                ToastUtils.showShort("token上传")
            }
        }
    }

    override fun onDoUpdateTokenError() {

    }


}