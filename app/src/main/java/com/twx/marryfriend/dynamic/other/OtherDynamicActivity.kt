package com.twx.marryfriend.dynamic.other

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.luck.picture.lib.decoration.WrapContentLinearLayoutManager
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.dynamic.*
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.other.adapter.OtherDynamicAdapter
import com.twx.marryfriend.dynamic.preview.image.ImagePreviewActivity
import com.twx.marryfriend.dynamic.preview.video.VideoPreviewActivity
import com.twx.marryfriend.dynamic.show.others.DynamicOtherShowActivity
import com.twx.marryfriend.mine.user.UserActivity
import com.twx.marryfriend.net.callback.dynamic.*
import com.twx.marryfriend.net.impl.dynamic.*
import kotlinx.android.synthetic.main.activity_other_dynamic.*
import java.util.*

class OtherDynamicActivity : MainBaseViewActivity(),
    IGetOtherTrendsListCallback, IDoLikeClickCallback,
    IDoLikeCancelCallback, IDoPlusFocusCallback, IDoCancelFocusCallback,
    OtherDynamicAdapter.OnItemClickListener {

    companion object {

        private const val TA_NAME = "ta_name"
        private const val TA_SEX = "ta_sex"
        private const val TA_AVATAR = "ta_avatar"
        private const val TA_ID = "ta_id"
        private const val HAVE_FOCUS = "have_focus"

        fun getIntent(
            context: Context,
            name: String,
            sex: Int,
            avatar: String,
            user: String,
            haveFocus: Boolean,
        ): Intent {
            val intent = Intent(context, OtherDynamicActivity::class.java)
            intent.putExtra(TA_NAME, name)
            intent.putExtra(TA_SEX, sex)
            intent.putExtra(TA_AVATAR, avatar)
            intent.putExtra(TA_ID, user)
            intent.putExtra(HAVE_FOCUS, haveFocus)
            return intent
        }

    }

    private var userId = ""

    // 喜欢的回调是否执行
    private var haveDoLikeSuccess = false

    // 我是否关注此用户
    private var haveFocus = false

    // 大图展示时进入时应该展示点击的那张图片
    private var imageIndex = 0

    // 点赞时选择的position
    private var mLikePosition: Int = 0

    private var trendType = 0

    private var currentPaper = 1

    private lateinit var linearLayoutManager: LinearLayoutManager

    // 关注与点赞数据
    private var mDiyList: MutableList<LikeBean> = arrayListOf()

    private var trendList: MutableList<OtherTrendsList> = arrayListOf()

    private lateinit var adapter: OtherDynamicAdapter

    private lateinit var getOtherTrendsListPresent: getOtherTrendsListPresentImpl
    private lateinit var doLikeClickPresent: doLikeClickPresentImpl
    private lateinit var doLikeCancelPresent: doLikeCancelPresentImpl

    private lateinit var doPlusFocusPresent: doPlusFocusPresentImpl
    private lateinit var doCancelFocusPresent: doCancelFocusPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_other_dynamic

    override fun initView() {
        super.initView()

        val name = intent.getStringExtra("ta_name")
        val sex = intent.getIntExtra("ta_sex", 1)
        val avatar = intent.getStringExtra("ta_avatar")
        userId = intent.getStringExtra("ta_id").toString()
        haveFocus = intent.getBooleanExtra("have_focus", false)

        if (sex == 1) {
            tv_dynamic_other_title.text = "他的动态"
            Glide.with(this)
                .load(avatar)
                .error(R.drawable.ic_mine_male_default)
                .placeholder(R.drawable.ic_mine_male_default)
                .into(iv_dynamic_other_avatar)
        } else {
            tv_dynamic_other_title.text = "她的动态"
            Glide.with(this)
                .load(avatar)
                .error(R.drawable.ic_mine_female_default)
                .placeholder(R.drawable.ic_mine_female_default)
                .into(iv_dynamic_other_avatar)
        }

        tv_dynamic_other_name.text = name

        getOtherTrendsListPresent = getOtherTrendsListPresentImpl.getsInstance()
        getOtherTrendsListPresent.registerCallback(this)

        doLikeClickPresent = doLikeClickPresentImpl.getsInstance()
        doLikeClickPresent.registerCallback(this)

        doLikeCancelPresent = doLikeCancelPresentImpl.getsInstance()
        doLikeCancelPresent.registerCallback(this)

        doPlusFocusPresent = doPlusFocusPresentImpl.getsInstance()
        doPlusFocusPresent.registerCallback(this)

        doCancelFocusPresent = doCancelFocusPresentImpl.getsInstance()
        doCancelFocusPresent.registerCallback(this)

        adapter = OtherDynamicAdapter(trendList, mDiyList)
        adapter.setOnItemClickListener(this)

        linearLayoutManager =
            WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_dynamic_other_container.layoutManager = linearLayoutManager
        rv_dynamic_other_container.adapter = adapter

        srl_dynamic_other_refresh.setRefreshHeader(ClassicsHeader(this))
        srl_dynamic_other_refresh.setRefreshFooter(ClassicsFooter(this))

        initEmojiCompat()

    }

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

    override fun initLoadData() {
        super.initLoadData()
        getFirstTrendsList()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_dynamic_other_finish.setOnClickListener {
            finish()
        }

        ll_dynamic_other_all.setOnClickListener {
            currentPaper = 1
            trendType = 0
            getFirstTrendsList()
        }

        ll_dynamic_other_pic.setOnClickListener {
            currentPaper = 1
            trendType = 1
            getFirstTrendsList()
        }

        ll_dynamic_other_video.setOnClickListener {
            currentPaper = 1
            trendType = 2
            getFirstTrendsList()
        }

        ll_dynamic_other_text.setOnClickListener {
            currentPaper = 1
            trendType = 3
            getFirstTrendsList()
        }

        srl_dynamic_other_refresh.setOnRefreshListener {
            // 刷新数据
            currentPaper = 1
            trendType = 0
            getFirstTrendsList()
            srl_dynamic_other_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        srl_dynamic_other_refresh.setOnLoadMoreListener {

            Log.i("guo", "currentPaper : $currentPaper")

            getMoreTrendsList(currentPaper)
            srl_dynamic_other_refresh.finishLoadMore(2000/*,false*/);//传入false表示加载失败
        }

        adapter.setOnCommentClickListener(object : OtherDynamicAdapter.OnCommentClickListener {
            override fun onCommentClick(v: View?, position: Int) {

                startActivity(DynamicOtherShowActivity.getIntent(this@OtherDynamicActivity,
                    trendList[position].id,
                    trendList[position].user_id.toInt()))

            }
        })

        adapter.setOnOneClickListener(object : OtherDynamicAdapter.OnOneClickListener {
            override fun onOneClick(v: View?, position: Int) {
                imageIndex = 0

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }
                startActivity(ImagePreviewActivity.getIntent(this@OtherDynamicActivity,
                    images,
                    imageIndex))
            }
        })

        adapter.setOnTwoClickListener(object : OtherDynamicAdapter.OnTwoClickListener {
            override fun onTwoClick(v: View?, position: Int) {
                imageIndex = 1

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }
                startActivity(ImagePreviewActivity.getIntent(this@OtherDynamicActivity,
                    images,
                    imageIndex))
            }
        })

        adapter.setOnThreeClickListener(object : OtherDynamicAdapter.OnThreeClickListener {
            override fun onThreeClick(v: View?, position: Int) {
                imageIndex = 2

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }
                startActivity(ImagePreviewActivity.getIntent(this@OtherDynamicActivity,
                    images,
                    imageIndex))
            }
        })

        adapter.setOnFourClickListener(object : OtherDynamicAdapter.OnFourClickListener {
            override fun onFourClick(v: View?, position: Int) {
                imageIndex = 3

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                // 图片展示问题，需要调整一下imageIndex

                if (images.size == 4) {
                    imageIndex = 2
                }

                startActivity(ImagePreviewActivity.getIntent(this@OtherDynamicActivity,
                    images,
                    imageIndex))
            }
        })

        adapter.setOnFiveClickListener(object : OtherDynamicAdapter.OnFiveClickListener {
            override fun onFiveClick(v: View?, position: Int) {
                imageIndex = 4

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                // 图片展示问题，需要调整一下imageIndex

                if (images.size == 4) {
                    imageIndex = 3
                }

                startActivity(ImagePreviewActivity.getIntent(this@OtherDynamicActivity,
                    images,
                    imageIndex))
            }
        })

        adapter.setOnSixClickListener(object : OtherDynamicAdapter.OnSixClickListener {
            override fun onSixClick(v: View?, position: Int) {
                imageIndex = 5

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }
                startActivity(ImagePreviewActivity.getIntent(this@OtherDynamicActivity,
                    images,
                    imageIndex))
            }
        })

        adapter.setOnSevenClickListener(object : OtherDynamicAdapter.OnSevenClickListener {
            override fun onSevenClick(v: View?, position: Int) {
                imageIndex = 6

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }
                startActivity(ImagePreviewActivity.getIntent(this@OtherDynamicActivity,
                    images,
                    imageIndex))
            }
        })

        adapter.setOnEightClickListener(object : OtherDynamicAdapter.OnEightClickListener {
            override fun onEightClick(v: View?, position: Int) {
                imageIndex = 7

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }
                startActivity(ImagePreviewActivity.getIntent(this@OtherDynamicActivity,
                    images,
                    imageIndex))
            }
        })

        adapter.setOnNineClickListener(object : OtherDynamicAdapter.OnNineClickListener {
            override fun onNineClick(v: View?, position: Int) {
                imageIndex = 8

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }
                startActivity(ImagePreviewActivity.getIntent(this@OtherDynamicActivity,
                    images,
                    imageIndex))
            }
        })

        adapter.setOnVideoClickListener(object : OtherDynamicAdapter.OnVideoClickListener {
            override fun onVideoClick(v: View?, position: Int) {
                startActivity(VideoPreviewActivity.getIntent(this@OtherDynamicActivity,
                    trendList[position].video_url,
                    ""))
            }
        })

    }

    // 初次加载动态列表
    private fun getFirstTrendsList() {



            val map: MutableMap<String, String> = TreeMap()
            map[Contents.MYSELF_UID] = SPStaticUtils.getString(Constant.USER_ID, "13")
            map[Contents.FRIEND_UID] = userId
            map[Contents.TRENDS_TYPE] = trendType.toString()
            map[Contents.PAGE] = "1"
            map[Contents.SIZE] = "10"
            getOtherTrendsListPresent.getOtherTrendsList(map)




    }

    // 加载更多我的动态列表
    private fun getMoreTrendsList(currentPaper: Int) {



            val map: MutableMap<String, String> = TreeMap()
            map[Contents.MYSELF_UID] = SPStaticUtils.getString(Constant.USER_ID, "13")
            map[Contents.FRIEND_UID] = userId
            map[Contents.TRENDS_TYPE] = trendType.toString()
            map[Contents.PAGE] = currentPaper.toString()
            map[Contents.SIZE] = "10"
            getOtherTrendsListPresent.getOtherTrendsList(map)




    }

    // 动态点赞
    private fun doLikeClick(trendId: Int, hostUid: String, guestUid: String) {



            val map: MutableMap<String, String> = TreeMap()
            map[Contents.TRENDS_ID] = trendId.toString()
            map[Contents.HOST_UID] = hostUid.toString()
            map[Contents.GUEST_UID] = guestUid.toString()
            doLikeClickPresent.doLikeClick(map)





    }

    // 取消点赞
    private fun doLikeCancelClick(trendId: Int, hostUid: String, guestUid: String) {



            val map: MutableMap<String, String> = TreeMap()
            map[Contents.TRENDS_ID] = trendId.toString()
            map[Contents.HOST_UID] = hostUid.toString()
            map[Contents.GUEST_UID] = guestUid.toString()
            doLikeCancelPresent.doLikeCancel(map)




    }

    // 关注
    private fun doPlusFocus(hostUid: String, guestUid: String) {



            val map: MutableMap<String, String> = TreeMap()
            map[Contents.HOST_UID] = hostUid.toString()
            map[Contents.GUEST_UID] = guestUid.toString()
            doPlusFocusPresent.doPlusFocusOther(map)



    }

    // 取消关注
    private fun doCancelFocus(hostUid: String, guestUid: String) {



            val map: MutableMap<String, String> = TreeMap()
            map[Contents.HOST_UID] = hostUid.toString()
            map[Contents.GUEST_UID] = guestUid.toString()
            doCancelFocusPresent.doCancelFocusOther(map)




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> {
                    currentPaper = 1
                    trendType = 0
                    getFirstTrendsList()
                }
            }
        }
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoPlusFocusSuccess(plusFocusBean: PlusFocusBean?) {
        if (plusFocusBean != null) {
            if (plusFocusBean.code == 200) {
                haveFocus = true
                ToastUtils.showShort("关注成功")
            }else{
                ToastUtils.showShort(plusFocusBean.msg)
            }
        }
    }

    override fun onDoPlusFocusError() {
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    override fun onDoCancelFocusSuccess(cancelFocusBean: CancelFocusBean?) {
        if (cancelFocusBean != null) {
            if (cancelFocusBean.code == 200) {
                haveFocus = false
                ToastUtils.showShort("取消关注成功")
            }else{
                ToastUtils.showShort(cancelFocusBean.msg)
            }
        }
    }

    override fun onDoCancelFocusError() {
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    override fun onGetOtherTrendsListSuccess(otherTrendsListBean: OtherTrendsListBean?) {
        if (otherTrendsListBean != null) {
            if (otherTrendsListBean.data.list.isNotEmpty()) {

                if (currentPaper == 1) {
                    trendList.clear()
                    mDiyList.clear()
                }

                currentPaper++

                srl_dynamic_other_refresh.finishRefresh();//传入false表示刷新失败

                when (trendType) {
                    0 -> {
                        //  获取全部数据，此时应该更新数据
                        tv_dynamic_other_all.text = otherTrendsListBean.data.total.all.toString()
                        tv_dynamic_other_pic.text = otherTrendsListBean.data.total.image.toString()
                        tv_dynamic_other_video.text =
                            otherTrendsListBean.data.total.video.toString()
                        tv_dynamic_other_text.text = otherTrendsListBean.data.total.wenzi.toString()

                        for (i in 0.until(otherTrendsListBean.data.list.size)) {
                            trendList.add(otherTrendsListBean.data.list[i])

                            val focus = true
                            val like = otherTrendsListBean.data.list[i].is_like != null

                            mDiyList.add(LikeBean(
                                otherTrendsListBean.data.list[i].id,
                                focus,
                                like,
                                otherTrendsListBean.data.list[i].like_count))
                        }

                    }
                    1 -> {
                        for (i in 0.until(otherTrendsListBean.data.list.size)) {
                            if (otherTrendsListBean.data.list[i].trends_type == 1) {
                                trendList.add(otherTrendsListBean.data.list[i])

                                val focus = true
                                val like = otherTrendsListBean.data.list[i].is_like != null

                                mDiyList.add(LikeBean(
                                    otherTrendsListBean.data.list[i].id,
                                    focus,
                                    like,
                                    otherTrendsListBean.data.list[i].like_count))
                            }
                        }
                    }
                    2 -> {
                        for (i in 0.until(otherTrendsListBean.data.list.size)) {
                            if (otherTrendsListBean.data.list[i].trends_type == 2) {
                                trendList.add(otherTrendsListBean.data.list[i])

                                val focus = true
                                val like = otherTrendsListBean.data.list[i].is_like != null

                                mDiyList.add(LikeBean(
                                    otherTrendsListBean.data.list[i].id,
                                    focus,
                                    like,
                                    otherTrendsListBean.data.list[i].like_count))
                            }
                        }
                    }
                    3 -> {
                        for (i in 0.until(otherTrendsListBean.data.list.size)) {
                            if (otherTrendsListBean.data.list[i].trends_type == 3) {
                                trendList.add(otherTrendsListBean.data.list[i])

                                val focus = true
                                val like = otherTrendsListBean.data.list[i].is_like != null

                                mDiyList.add(LikeBean(
                                    otherTrendsListBean.data.list[i].id,
                                    focus,
                                    like,
                                    otherTrendsListBean.data.list[i].like_count))
                            }
                        }
                    }
                }

                ll_dynamic_other_empty.visibility = View.GONE
                rv_dynamic_other_container.visibility = View.VISIBLE

                adapter.notifyDataSetChanged()

            }
        }

        srl_dynamic_other_refresh.finishRefresh(true)
        srl_dynamic_other_refresh.finishLoadMore(true)
    }

    override fun onGetOtherTrendsListCodeError() {
        srl_dynamic_other_refresh.finishRefresh(false)
        srl_dynamic_other_refresh.finishLoadMore(false)

        ToastUtils.showShort("网络请求失败，请稍后再试")

    }

    override fun onDoLikeClickSuccess(likeClickBean: LikeClickBean?) {
        if (!haveDoLikeSuccess) {
            haveDoLikeSuccess = true
            // 点赞
            if (likeClickBean != null) {
                if (likeClickBean.code == 200) {

                    mDiyList[mLikePosition].like = true
                    mDiyList[mLikePosition].anim = true
                    mDiyList[mLikePosition].likeCount++

                    adapter.notifyDataSetChanged()

                    val task: TimerTask = object : TimerTask() {
                        override fun run() {
                            mDiyList[mLikePosition].anim = false
                        }
                    }
                    val timer = Timer()
                    timer.schedule(task, 100)

                } else {
                    ToastUtils.showShort(likeClickBean.msg)
                }
            }
        }

    }

    override fun onDoLikeClickError() {
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    override fun onDoLikeCancelSuccess(likeCancelBean: LikeCancelBean?) {
        // 取消赞
        if (likeCancelBean != null) {
            if (likeCancelBean.code == 200) {
                mDiyList[mLikePosition].like = false
                mDiyList[mLikePosition].likeCount--
                adapter.notifyDataSetChanged()
            } else {
                ToastUtils.showShort(likeCancelBean.msg)
            }
        }
    }

    override fun onLikeCancelError() {
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    inner class DynamicEditDialog(context: Context, val position: Int) :
        FullScreenPopupView(context) {

        // 是否删除动态，弹窗消失时结束
        private var isFinish = false

        override fun getImplLayoutId(): Int = R.layout.dialog_dynamic_report

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<TextView>(R.id.tv_dialog_dynamic_other_edit_cancel)
            val report = findViewById<TextView>(R.id.tv_dialog_dynamic_other_edit_report)
            val focus = findViewById<TextView>(R.id.tv_dialog_dynamic_other_edit_focus)

            if (haveFocus) {
                focus.text = "取消关注"
                focus.setTextColor(Color.parseColor("#FF4444"))
            } else {
                focus.text = "关注"
                focus.setTextColor(Color.parseColor("#101010"))
            }

            close.setOnClickListener {
                dismiss()
            }

            report.setOnClickListener {
                ToastUtils.showShort("举报动态，待添加")

                isFinish = true
                dismiss()

            }

            focus.setOnClickListener {
                if (haveFocus) {
                    doCancelFocus(SPStaticUtils.getString(Constant.USER_ID, "13"), userId)
                } else {
                    doPlusFocus(SPStaticUtils.getString(Constant.USER_ID, "13"), userId)
                }
                dismiss()
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

            if (SPStaticUtils.getInt(Constant.ME_SEX,1) == 1){
                findViewById<ImageView>(R.id.iv_dialog_like_avatar_example).setImageResource(R.drawable.ic_dialog_avatar_male)
            }else{
                findViewById<ImageView>(R.id.iv_dialog_like_avatar_example).setImageResource(R.drawable.ic_dialog_avatar_female)
            }

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

    override fun onItemClick(v: View?, position: Int) {

        startActivity(DynamicOtherShowActivity.getIntent(this@OtherDynamicActivity,
            trendList[position].id,
            trendList[position].user_id.toInt()))

    }

    override fun onTextClick(v: View?, position: Int) {

        startActivity(DynamicOtherShowActivity.getIntent(this@OtherDynamicActivity,
            trendList[position].id,
            trendList[position].user_id.toInt()))

    }

    override fun onItemMoreClick(v: View?, position: Int) {
        XPopup.Builder(this@OtherDynamicActivity)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(DynamicEditDialog(this@OtherDynamicActivity, position))
            .show()
    }

    override fun onLikeClick(v: View?, position: Int) {
        // 点赞， 此时需要验证是否上传头像
        if (SPStaticUtils.getString(Constant.ME_AVATAR, "") != "" || SPStaticUtils.getString(
                Constant.ME_AVATAR_AUDIT,
                "") != ""
        ) {
            mLikePosition = position
            if (!mDiyList[mLikePosition].like) {
                // 点赞

                haveDoLikeSuccess = false
                doLikeClick(
                    trendList[mLikePosition].id,
                    trendList[mLikePosition].user_id,
                    SPStaticUtils.getString(Constant.USER_ID, "13"))

            } else {
                // 取消赞
                doLikeCancelClick(
                    trendList[mLikePosition].id,
                    trendList[mLikePosition].user_id,
                    SPStaticUtils.getString(Constant.USER_ID, "13"))
            }
        } else {
            XPopup.Builder(this@OtherDynamicActivity)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(AvatarDialog(this@OtherDynamicActivity))
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        getOtherTrendsListPresent.unregisterCallback(this)
        doLikeClickPresent.unregisterCallback(this)
        doLikeCancelPresent.unregisterCallback(this)

        doPlusFocusPresent.unregisterCallback(this)
        doCancelFocusPresent.unregisterCallback(this)

    }

}