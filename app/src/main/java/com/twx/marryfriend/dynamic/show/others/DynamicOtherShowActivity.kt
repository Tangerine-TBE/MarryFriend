package com.twx.marryfriend.dynamic.show.others

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.*
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.dynamic.*
import com.twx.marryfriend.bean.vip.VipGifEnum
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.preview.image.ImagePreviewActivity
import com.twx.marryfriend.utils.emoji.EmojiDetailAdapter
import com.twx.marryfriend.dynamic.show.mine.DynamicMineLikeActivity
import com.twx.marryfriend.dynamic.show.mine.adapter.CommentOneAdapter
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.message.ChatActivity
import com.twx.marryfriend.message.ImChatActivity
import com.twx.marryfriend.net.callback.dynamic.*
import com.twx.marryfriend.net.impl.dynamic.*
import com.twx.marryfriend.set.report.ReportReasonActivity
import com.twx.marryfriend.tools.avatar.AvatarToolActivity
import com.twx.marryfriend.utils.SpUtil
import com.twx.marryfriend.utils.TimeUtil
import com.twx.marryfriend.utils.UnicodeUtils
import com.twx.marryfriend.utils.emoji.EmojiUtils
import com.twx.marryfriend.vip.VipActivity
import kotlinx.android.synthetic.main.activity_dynamic_mine_show.*
import kotlinx.android.synthetic.main.activity_dynamic_other_show.*
import java.util.*

class DynamicOtherShowActivity : MainBaseViewActivity(),
    IDoCheckTrendCallback, IGetCommentOneCallback, IGetCommentTwoCallback,
    CommentOneAdapter.OnItemClickListener, IDoCommentOneCreateCallback, IDoCommentTwoCreateCallback,
    IDoLikeClickCallback, IDoLikeCancelCallback, IDoPlusFocusCallback,
    CommentOneAdapter.OnItemLongClickListener, IDoCancelFocusCallback, IDoDeleteTrendCallback {

    private var currentPaper = 1

    private var x = true

    private var trendId = 0

    private var userId = 0

    // 此条动态是否是本人
    private var isMine = false

    // 是否关注
    private var haveFocus = false

    private var mName: String = ""

    // 大图展示时进入时应该展示点击的那张图片
    private var imageIndex = 0

    private var mPicList: MutableList<String> = arrayListOf()
    private var mVideoUrl: String = ""

    // 上次点击时间
    private var lastClickTime = 0L

    // 两次点击间隔时间（毫秒）
    private val delayTime = 3000

    private var isLike = false

    // 评论发送模式
    private var mode = 0

    // 评论内容
    private var content = ""

    // 是否完成本地修改
    private var isLocalAdd = false

    // 子评论回复时前者的头像、昵称、性别
    private var childAvatar = ""
    private var childNick = ""
    private var childSex = 0

    // 动态id
    private var trendsId = 0

    // 动态主人uid
    private var hostUid = 0

    // 第三方可变Id
    private var threeId = 0

    // 父级动态主人uid
    private var oneLevelId = 0

    // 前一个用户uid，没有就用0
    private var firstUid = 0

    // 现在这个用户uid
    private var lastUid = 0


    private lateinit var info: CheckTrendList
    private var eduList: MutableList<String> = arrayListOf()
    private var mCommentOneList: MutableList<CommentBean> = arrayListOf()

    private var mPageList: MutableList<Int> = arrayListOf()

    // 点击的父评论position
    private var mItem = 0

    // 点击的子评论position
    private var mChildItem = 0

    // 点击的本地子评论position
    private var mLocalChildItem = 0


    // 删除dialog 对应的position
    private var twoPosition = 0

    private var isBackFresh = false

    // 此条动态点赞数量
    private var likeSum = 0

    // 点赞头像列表
    private var likeAvatar: MutableList<String> = arrayListOf()

    var emojiList: MutableList<String> = arrayListOf()
    private lateinit var emojiAdapter: EmojiDetailAdapter

    private lateinit var adapter: CommentOneAdapter

    private lateinit var doCheckTrendPresent: doCheckTrendPresentImpl
    private lateinit var getCommentOnePresent: getCommentOnePresentImpl
    private lateinit var doCommentOneCreatePresent: doCommentOneCreatePresentImpl
    private lateinit var getCommentTwoPresent: getCommentTwoPresentImpl
    private lateinit var doCommentTwoCreatePresent: doCommentTwoCreatePresentImpl
    private lateinit var doLikeClickPresent: doLikeClickPresentImpl
    private lateinit var doLikeCancelPresent: doLikeCancelPresentImpl
    private lateinit var doPlusFocusPresent: doPlusFocusPresentImpl
    private lateinit var doCancelFocusPresent: doCancelFocusPresentImpl
    private lateinit var doDeleteTrendPresent: doDeleteTrendPresentImpl

    companion object {

        private val TREND_ID = "trendId"
        private val USER_ID = "usersId"

        fun getIntent(context: Context, trendId: Int, userId: Int): Intent {
            val intent = Intent(context, DynamicOtherShowActivity::class.java)
            intent.putExtra(TREND_ID, trendId)
            intent.putExtra(USER_ID, userId)
            return intent
        }

    }

    override fun getLayoutView(): Int = R.layout.activity_dynamic_other_show

    override fun initView() {
        super.initView()

        trendId = intent.getIntExtra("trendId", 0)
        userId = intent.getIntExtra("usersId", 0)

        // 判断是否是我的动态，是我的动态就修改一下相关的逻辑
        isMine = userId.toString() == SPStaticUtils.getString(Constant.USER_ID, "13")

        doCheckTrendPresent = doCheckTrendPresentImpl.getsInstance()
        doCheckTrendPresent.registerCallback(this)

        getCommentOnePresent = getCommentOnePresentImpl.getsInstance()
        getCommentOnePresent.registerCallback(this)

        doCommentOneCreatePresent = doCommentOneCreatePresentImpl.getsInstance()
        doCommentOneCreatePresent.registerCallback(this)

        getCommentTwoPresent = getCommentTwoPresentImpl.getsInstance()
        getCommentTwoPresent.registerCallback(this)

        doCommentTwoCreatePresent = doCommentTwoCreatePresentImpl.getsInstance()
        doCommentTwoCreatePresent.registerCallback(this)

        doLikeClickPresent = doLikeClickPresentImpl.getsInstance()
        doLikeClickPresent.registerCallback(this)

        doLikeCancelPresent = doLikeCancelPresentImpl.getsInstance()
        doLikeCancelPresent.registerCallback(this)

        doPlusFocusPresent = doPlusFocusPresentImpl.getsInstance()
        doPlusFocusPresent.registerCallback(this)

        doCancelFocusPresent = doCancelFocusPresentImpl.getsInstance()
        doCancelFocusPresent.registerCallback(this)

        doDeleteTrendPresent = doDeleteTrendPresentImpl.getsInstance()
        doDeleteTrendPresent.registerCallback(this)


        if (SPStaticUtils.getBoolean(Constant.HIDE_REPORT_TIP, false)) {
            iv_dynamic_other_show_tip.visibility = View.GONE
        } else {
            iv_dynamic_other_show_tip.visibility = View.VISIBLE
        }

        adapter = CommentOneAdapter(mCommentOneList)
        adapter.setOnItemClickListener(this)
        adapter.setOnItemLongClickListener(this)

        rv_dynamic_other_show_container.layoutManager = LinearLayoutManager(this)
        rv_dynamic_other_show_container.adapter = adapter

        emojiList = EmojiUtils.getEmojiList()
        emojiAdapter = EmojiDetailAdapter(emojiList)

        rv_emoji_other_container.adapter = emojiAdapter
        rv_emoji_other_container.layoutManager = GridLayoutManager(this, 8)

        sfl_dynamic_other_show_refresh.setRefreshFooter(ClassicsFooter(this))

        isBackFresh = false

    }

    override fun initLoadData() {
        super.initLoadData()

        eduList.add("大专以下")
        eduList.add("大专")
        eduList.add("本科")
        eduList.add("硕士")
        eduList.add("博士")
        eduList.add("博士以上")
        eduList.add("")

        getTrendsList()
        getCommentOne(currentPaper)

        KeyboardUtils.fixAndroidBug5497(this)
        KeyboardUtils.clickBlankArea2HideSoftInput()

    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_dynamic_other_show_finish.setOnClickListener {

            if (isBackFresh) {
                val intent = intent
                setResult(RESULT_OK, intent)
                finish()
            } else {
                finish()
            }

        }

        ll_dynamic_other_show_mode.setOnClickListener {
            if (haveFocus) {


                if (SPStaticUtils.getInt(Constant.USER_VIP_LEVEL, 0) == 0) {
                    startActivity(
                        VipActivity.getVipIntent(this,
                            info.user_id.toInt(),
                            VipGifEnum.Message)
                    )
                } else {
                    // 关注了，需要跳转到聊天界面
                    startActivity(ImChatActivity.getIntent(
                        this,
                        info.user_id
                    ))
                }


            } else {
                // 未关注了，需要关注
                doPlusFocus(SPStaticUtils.getString(Constant.USER_ID, "13"), userId)

            }
        }

        iv_dynamic_other_show_heard_edit.setOnClickListener {

            if (isMine) {
                XPopup.Builder(this)
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(DynamicDeleteDialog(this))
                    .show()
            } else {
                XPopup.Builder(this)
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(DynamicReportDialog(this))
                    .show()
            }

        }

        riv_dynamic_other_show_avatar.setOnClickListener {
            startActivity(FriendInfoActivity.getIntent(this, hostUid))
        }

        fl_dynamic_other_show_video.setOnClickListener {
            // 播放视频

            val intent = Intent(this,
                com.twx.marryfriend.dynamic.preview.video.VideoPreviewActivity::class.java)
            intent.putExtra("videoUrl", mVideoUrl)
            intent.putExtra("name", mName)
            startActivity(intent)

        }

        iv_dynamic_other_show_one.setOnClickListener {
            imageIndex = 0


            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))

        }

        iv_dynamic_other_show_two.setOnClickListener {
            imageIndex = 1

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_other_show_three.setOnClickListener {
            imageIndex = 2

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_other_show_four.setOnClickListener {
            imageIndex = 3

            // 图片展示问题，需要调整一下imageIndex
            if (mPicList.size == 4) {
                imageIndex = 2
            }

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_other_show_five.setOnClickListener {
            imageIndex = 4

            // 图片展示问题，需要调整一下imageIndex
            if (mPicList.size == 4) {
                imageIndex = 3
            }

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_other_show_six.setOnClickListener {
            imageIndex = 5

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_other_show_seven.setOnClickListener {
            imageIndex = 6

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_other_show_eight.setOnClickListener {
            imageIndex = 7

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_other_show_nine.setOnClickListener {
            imageIndex = 8

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_other_show_tip_hide.setOnClickListener {
            iv_dynamic_other_show_tip.visibility = View.GONE
            SPStaticUtils.put(Constant.HIDE_REPORT_TIP, true)
        }

        rl_dynamic_other_show_like.setOnClickListener {
            val intent = Intent(this, DynamicMineLikeActivity::class.java)
            intent.putExtra("trendId", trendId)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }


        sfl_dynamic_other_show_refresh.setOnLoadMoreListener {
            Log.i("guo", "CommentOneRefresh")
            getCommentOne(currentPaper)
            sfl_dynamic_other_show_refresh.finishLoadMore(2000);//传入false表示刷新失败
        }

        // 避免触发下层界面点击事件
        ll_dynamic_other_show_bottom.setOnClickListener {

        }

        iv_dynamic_other_show_like.setOnClickListener {


            if (SPStaticUtils.getString(Constant.ME_AVATAR, "") != "" || SPStaticUtils.getString(
                    Constant.ME_AVATAR_AUDIT,
                    "") != ""
            ) {

                if (userId != SPStaticUtils.getString(Constant.USER_ID, "13").toInt()) {


                    if (!isLike) {
                        // 点赞
                        isLike = true

                        doLikeClick(trendId,
                            userId,
                            SPStaticUtils.getString(Constant.USER_ID, "13"))

                    } else {
                        // 取消赞
                        isLike = false

                        doLikeCancelClick(trendId,
                            userId,
                            SPStaticUtils.getString(Constant.USER_ID, "13"))
                    }

                    adapter.notifyDataSetChanged()

                } else {
                    ToastUtils.showShort("不能点赞自己的动态")
                }

            } else {
                XPopup.Builder(this)
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(AvatarDialog(this))
                    .show()
            }

        }

        iv_dynamic_other_show_emoji.setOnClickListener {

            if (ll_emoji_other_container.visibility == View.VISIBLE) {
                ll_emoji_other_container.visibility = View.GONE
            } else {
                val task: TimerTask = object : TimerTask() {
                    override fun run() {
                        ThreadUtils.runOnUiThread {
                            ll_emoji_other_container.visibility = View.VISIBLE
                        }
                    }
                }
                val timer = Timer()
                timer.schedule(task, 50)


                if (KeyboardUtils.isSoftInputVisible(this)) {
                    x = false
                    KeyboardUtils.hideSoftInput(this)
                }
            }
        }


        eet_emoji_other_edit.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            eet_emoji_other_edit.getWindowVisibleDisplayFrame(r)
            val screenHeight: Int = eet_emoji_other_edit.rootView.height
            val heightDifference: Int = screenHeight - r.bottom
            if (heightDifference > 200) {
                // 软键盘显示
                if (x) {
                    ll_emoji_other_container.visibility = View.GONE
                }
            } else {
                // 软键盘隐藏
            }
            x = !x
        }

        iv_dynamic_other_show_send.setOnClickListener {

            // 判断是否有头像

            if (eet_emoji_other_edit.text.toString().trim { it <= ' ' } != "") {

                if (SPStaticUtils.getString(Constant.ME_AVATAR,
                        "") != "" || SPStaticUtils.getString(Constant.ME_AVATAR_AUDIT, "") != ""
                ) {

                    if (System.currentTimeMillis() - lastClickTime >= delayTime) {
                        lastClickTime = System.currentTimeMillis();
                        var content = eet_emoji_other_edit.text.toString().trim { it <= ' ' }
                        eet_emoji_other_edit.setText("")
                        KeyboardUtils.hideSoftInput(this)
                        ll_emoji_other_container.visibility = View.GONE

                        this.content = UnicodeUtils.newLineText(content)

                        switchSendMode(mode,
                            trendsId,
                            hostUid,
                            threeId,
                            oneLevelId,
                            firstUid,
                            lastUid,
                            content)

                    } else {
                        ToastUtils.showShort("点击太频繁了，请稍后再评论")
                    }
                } else {
                    XPopup.Builder(this)
                        .dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true)
                        .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                        .asCustom(AvatarDialog(this))
                        .show()
                }
            } else {
                ToastUtils.showShort("请输入您的评论")
            }

        }

        emojiAdapter.setOnItemClickListener(object : EmojiDetailAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                var x = eet_emoji_other_edit.text.toString().trim { it <= ' ' }
                x += EmojiUtils.getCompatEmojiString(emojiList[position])
                eet_emoji_other_edit.setText(x)
                eet_emoji_other_edit.setSelection(x.length)
            }
        })

        iv_emoji_other_delete.setOnClickListener {
            val keyCode = KeyEvent.KEYCODE_DEL
            val keyEventDown = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
            val keyEventUp = KeyEvent(KeyEvent.ACTION_UP, keyCode);
            eet_emoji_other_edit.onKeyDown(keyCode, keyEventDown)
            eet_emoji_other_edit.onKeyUp(keyCode, keyEventUp);
        }

        iv_emoji_other_send.setOnClickListener {

            if (eet_emoji_other_edit.text.toString().trim { it <= ' ' } != "") {

                if (SPStaticUtils.getString(Constant.ME_AVATAR,
                        "") != "" || SPStaticUtils.getString(Constant.ME_AVATAR_AUDIT, "") != ""
                ) {

                    if (System.currentTimeMillis() - lastClickTime >= delayTime) {
                        lastClickTime = System.currentTimeMillis();

                        var content = eet_emoji_other_edit.text.toString().trim { it <= ' ' }
                        eet_emoji_other_edit.setText("")
                        KeyboardUtils.hideSoftInput(this)
                        ll_emoji_other_container.visibility = View.GONE

                        this.content = UnicodeUtils.newLineText(content)

                        switchSendMode(mode,
                            trendsId,
                            hostUid,
                            threeId,
                            oneLevelId,
                            firstUid,
                            lastUid,
                            content)

                    } else {
                        ToastUtils.showShort("点击太频繁了，请稍后再评论")
                    }
                } else {
                    XPopup.Builder(this)
                        .dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true)
                        .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                        .asCustom(AvatarDialog(this))
                        .show()
                }
            } else {
                ToastUtils.showShort("请输入您的评论")
            }
        }


        KeyboardUtils.registerSoftInputChangedListener(this,
            object : KeyboardUtils.OnSoftInputChangedListener {
                override fun onSoftInputChanged(height: Int) {
                    if (height == 0) {

                        ToastUtils.showShort("恢复到添加父评论模式")

                        eet_emoji_other_edit.hint = "走心,说点好听的"

                        mode = 0
                        trendsId = trendId
                        hostUid = userId
                        threeId = SPStaticUtils.getString(Constant.USER_ID, "13").toInt()

                    }
                }

            })


    }

    /**
     * mode : 发送接口模式
     *  trends_id ： 动态id
     *  host_uid ： 动态主人uid
     *  three_id : 当发送父评论时为 guest_uid（浏览客人uid） ，否则为 parent_id（父级评论id）
     *  first_uid ： 前一个用户uid，没有就用0
     *  last_uid ： 现在这个用户uid
     *  content ： 评论内容
     * */
    private fun switchSendMode(
        mode: Int,
        trends_id: Int,
        host_uid: Int,
        three_id: Int,
        oneLevelId: Int,
        first_uid: Int,
        last_uid: Int,
        content: String,
    ) {
        // 切换发送模式
        when (mode) {
            0 -> {
                // 父动态

                doCommentOne(trends_id, host_uid, three_id, content)
            }
            1 -> {
                // 子动态
                doCommentTwo(trends_id,
                    host_uid,
                    three_id,
                    oneLevelId,
                    first_uid,
                    last_uid,
                    content)
            }
        }
    }

    // 获取具体动态
    private fun getTrendsList() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.ID] = trendId.toString()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        doCheckTrendPresent.doCheckTrend(map)
    }

    // 获取一级父评论
    private fun getCommentOne(page: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.TRENDS_ID] = trendId.toString()
        map[Contents.HOST_UID] = userId.toString()
        getCommentOnePresent.getCommentOne(map, page, 10)
    }

    // 给动态提交父评论
    private fun doCommentOne(trendsId: Int, hostId: Int, GuestId: Int, content: String) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.TRENDS_ID] = trendsId.toString()
        map[Contents.HOST_UID] = hostId.toString()
        map[Contents.GUEST_UID] = GuestId.toString()
        map[Contents.CONTENT_ONE] = content
        doCommentOneCreatePresent.doCommentOneCreate(map)
    }

    // 获取二级子评论
    private fun getCommentTwo(trendId: Int, parentId: Int, page: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.TRENDS_ID] = trendId.toString()
        map[Contents.PARENT_ID] = parentId.toString()
        getCommentTwoPresent.getCommentTwo(map, page, 10)
    }

    // 给动态提交子评论
    private fun doCommentTwo(
        trendsId: Int,
        hostId: Int,
        parentId: Int,
        oneLevelId: Int,
        first_uid: Int,
        last_uid: Int,
        content: String,
    ) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.TRENDS_ID] = trendsId.toString()
        map[Contents.HOST_UID] = hostId.toString()
        map[Contents.PARENT_ID] = parentId.toString()
        map[Contents.ONE_LEVEL_UID] = oneLevelId.toString()
        map[Contents.FIRST_UID] = first_uid.toString()
        map[Contents.LAST_UID] = last_uid.toString()
        map[Contents.CONTENT_TWO] = content
        doCommentTwoCreatePresent.doCommentTwoCreate(map)
    }

    // 动态点赞
    private fun doLikeClick(trendId: Int, hostUid: Int, guestUid: String) {

        val map: MutableMap<String, String> = TreeMap()
        map[Contents.TRENDS_ID] = trendId.toString()
        map[Contents.HOST_UID] = hostUid.toString()
        map[Contents.GUEST_UID] = guestUid.toString()
        doLikeClickPresent.doLikeClick(map)
    }

    // 取消点赞
    private fun doLikeCancelClick(trendId: Int, hostUid: Int, guestUid: String) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.TRENDS_ID] = trendId.toString()
        map[Contents.HOST_UID] = hostUid.toString()
        map[Contents.GUEST_UID] = guestUid.toString()
        doLikeCancelPresent.doLikeCancel(map)
    }

    // 关注
    private fun doPlusFocus(hostUid: String, guestUid: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.HOST_UID] = hostUid.toString()
        map[Contents.GUEST_UID] = guestUid.toString()
        doPlusFocusPresent.doPlusFocusOther(map)
    }

    // 取消关注
    private fun doCancelFocus(hostUid: String, guestUid: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.HOST_UID] = hostUid.toString()
        map[Contents.GUEST_UID] = guestUid.toString()
        doCancelFocusPresent.doCancelFocusOther(map)
    }

    // 删除我的动态
    private fun deleteTrends(trendId: Int, userId: String) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.ID] = trendId.toString()
        map[Contents.USER_ID] = userId
        doDeleteTrendPresent.doDeleteTrend(map)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isBackFresh) {
                val intent = intent
                setResult(RESULT_OK, intent)
                finish()
            } else {
                finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoDeleteTrendSuccess(deleteTrendBean: DeleteTrendBean?) {
        ToastUtils.showShort("删除动态成功，返回至上一界面，删除数据")

        val intent = intent
        setResult(RESULT_OK, intent)
        this.finish()
    }

    override fun onDoDeleteTrendError() {

    }

    override fun onDoCancelFocusSuccess(cancelFocusBean: CancelFocusBean?) {

        if (cancelFocusBean != null) {
            if (cancelFocusBean.code == 200) {
                haveFocus = false

                iv_dynamic_other_show_mode.setImageResource(R.drawable.ic_base_focus)

                ToastUtils.showShort("取消关注成功")
            } else {
                ToastUtils.showShort(cancelFocusBean.msg)
            }
        }

    }

    override fun onDoCancelFocusError() {

    }

    override fun onDoPlusFocusSuccess(plusFocusBean: PlusFocusBean?) {

        if (plusFocusBean != null) {
            if (plusFocusBean.code == 200) {
                haveFocus = true

                iv_dynamic_other_show_mode.setImageResource(R.drawable.ic_base_chat)

                ToastUtils.showShort("关注成功")
            } else {
                ToastUtils.showShort(plusFocusBean.msg)
            }
        }

    }

    override fun onDoPlusFocusError() {

    }

    override fun onDoLikeClickSuccess(likeClickBean: LikeClickBean?) {
        if (likeClickBean != null) {
            if (likeClickBean.code == 200) {

                likeSum++


                ll_dynamic_other_show_like.visibility = View.VISIBLE
                rl_dynamic_other_show_like.visibility = View.VISIBLE

                tv_dynamic_other_show_like.text = likeSum.toString()

                if (likeSum == 1) {
                    iv_dynamic_other_show_like1.visibility = View.VISIBLE
                    Glide.with(applicationContext)
                        .load(SPStaticUtils.getString(Constant.ME_AVATAR))
                        .error(R.drawable.ic_mine_male_default)
                        .placeholder(R.drawable.ic_mine_male_default)
                        .into(iv_dynamic_other_show_like1)

                    iv_dynamic_other_show_like2.visibility = View.GONE

                    iv_dynamic_other_show_like3.visibility = View.GONE
                }

//                if (likeAvatar.size < 3) {
//                    likeAvatar.add(SPStaticUtils.getString(Constant.ME_AVATAR))
//                }

//                when (likeAvatar.size) {
//                    0 -> {
//                        ll_dynamic_other_show_like.visibility = View.GONE
//                    }
//                    1 -> {
//
//                        iv_dynamic_other_show_like1.visibility = View.VISIBLE
//                        Glide.with(applicationContext)
//                            .load(likeAvatar[0])
//                            .error(R.drawable.ic_mine_male_default)
//                            .placeholder(R.drawable.ic_mine_male_default)
//                            .into(iv_dynamic_other_show_like1)
//
//                        iv_dynamic_other_show_like2.visibility = View.GONE
//
//                        iv_dynamic_other_show_like3.visibility = View.GONE
//                    }
//                    2 -> {
//
//                        iv_dynamic_other_show_like1.visibility = View.VISIBLE
//                        Glide.with(applicationContext)
//                            .load(likeAvatar[0])
//                            .error(R.drawable.ic_mine_male_default)
//                            .placeholder(R.drawable.ic_mine_male_default)
//                            .into(iv_dynamic_other_show_like1)
//
//                        iv_dynamic_other_show_like2.visibility = View.VISIBLE
//                        Glide.with(applicationContext)
//                            .load(likeAvatar[1])
//                            .error(R.drawable.ic_mine_male_default)
//                            .placeholder(R.drawable.ic_mine_male_default)
//                            .into(iv_dynamic_other_show_like2)
//
//                        iv_dynamic_other_show_like3.visibility = View.GONE
//                    }
//                    3 -> {
//
//                        iv_dynamic_other_show_like1.visibility = View.VISIBLE
//                        Glide.with(applicationContext)
//                            .load(likeAvatar[0])
//                            .error(R.drawable.ic_mine_male_default)
//                            .placeholder(R.drawable.ic_mine_male_default)
//                            .into(iv_dynamic_other_show_like1)
//
//                        iv_dynamic_other_show_like2.visibility = View.VISIBLE
//                        Glide.with(applicationContext)
//                            .load(likeAvatar[1])
//                            .error(R.drawable.ic_mine_male_default)
//                            .placeholder(R.drawable.ic_mine_male_default)
//                            .into(iv_dynamic_other_show_like2)
//
//                        iv_dynamic_other_show_like3.visibility = View.VISIBLE
//                        Glide.with(applicationContext)
//                            .load(likeAvatar[2])
//                            .error(R.drawable.ic_mine_male_default)
//                            .placeholder(R.drawable.ic_mine_male_default)
//                            .into(iv_dynamic_other_show_like3)
//                    }
//                }

                iv_dynamic_other_show_like.setImageResource(R.drawable.ic_dynamic_like)

            } else {
                ToastUtils.showShort(likeClickBean.msg)
            }
        }

    }

    override fun onDoLikeClickError() {
        ToastUtils.showShort("点赞失败，请稍后再试")
    }

    override fun onDoLikeCancelSuccess(likeCancelBean: LikeCancelBean?) {
        if (likeCancelBean != null) {
            if (likeCancelBean.code == 200) {

                likeSum--

                if (likeSum == 0) {
                    ll_dynamic_other_show_like.visibility = View.GONE
                    rl_dynamic_other_show_like.visibility = View.GONE

                    tv_dynamic_other_show_like.text = likeSum.toString()
                } else {
                    ll_dynamic_other_show_like.visibility = View.VISIBLE
                    rl_dynamic_other_show_like.visibility = View.VISIBLE

                    tv_dynamic_other_show_like.text = likeSum.toString()
                }

//                if (likeAvatar.contains(SPStaticUtils.getString(Constant.ME_AVATAR))){
//                    likeAvatar.remove(SPStaticUtils.getString(Constant.ME_AVATAR))
//                }
//
//                when (likeAvatar.size) {
//                    0 -> {
//                        ll_dynamic_other_show_like.visibility = View.GONE
//                    }
//                    1 -> {
//
//                        iv_dynamic_other_show_like1.visibility = View.VISIBLE
//                        Glide.with(applicationContext)
//                            .load(likeAvatar[0])
//                            .error(R.drawable.ic_mine_male_default)
//                            .placeholder(R.drawable.ic_mine_male_default)
//                            .into(iv_dynamic_other_show_like1)
//
//                        iv_dynamic_other_show_like2.visibility = View.GONE
//
//                        iv_dynamic_other_show_like3.visibility = View.GONE
//                    }
//                    2 -> {
//
//                        iv_dynamic_other_show_like1.visibility = View.VISIBLE
//                        Glide.with(applicationContext)
//                            .load(likeAvatar[0])
//                            .error(R.drawable.ic_mine_male_default)
//                            .placeholder(R.drawable.ic_mine_male_default)
//                            .into(iv_dynamic_other_show_like1)
//
//                        iv_dynamic_other_show_like2.visibility = View.VISIBLE
//                        Glide.with(applicationContext)
//                            .load(likeAvatar[1])
//                            .error(R.drawable.ic_mine_male_default)
//                            .placeholder(R.drawable.ic_mine_male_default)
//                            .into(iv_dynamic_other_show_like2)
//
//                        iv_dynamic_other_show_like3.visibility = View.GONE
//                    }
//                    3 -> {
//
//                        iv_dynamic_other_show_like1.visibility = View.VISIBLE
//                        Glide.with(applicationContext)
//                            .load(likeAvatar[0])
//                            .error(R.drawable.ic_mine_male_default)
//                            .placeholder(R.drawable.ic_mine_male_default)
//                            .into(iv_dynamic_other_show_like1)
//
//                        iv_dynamic_other_show_like2.visibility = View.VISIBLE
//                        Glide.with(applicationContext)
//                            .load(likeAvatar[1])
//                            .error(R.drawable.ic_mine_male_default)
//                            .placeholder(R.drawable.ic_mine_male_default)
//                            .into(iv_dynamic_other_show_like2)
//
//                        iv_dynamic_other_show_like3.visibility = View.VISIBLE
//                        Glide.with(applicationContext)
//                            .load(likeAvatar[2])
//                            .error(R.drawable.ic_mine_male_default)
//                            .placeholder(R.drawable.ic_mine_male_default)
//                            .into(iv_dynamic_other_show_like3)
//                    }
//                }


                iv_dynamic_other_show_like.setImageResource(R.drawable.ic_dynamic_base_like)
            } else {
                ToastUtils.showShort(likeCancelBean.msg)
            }
        }

    }

    override fun onLikeCancelError() {
        ToastUtils.showShort("取消点赞失败，请稍后再试")
    }

    override fun onGetCommentTwoSuccess(commentTwoBean: CommentTwoBean) {

        if (mPageList[mItem] == 1) {
            mCommentOneList[mItem].twoList.clear()
        }

        for (i in 0.until(commentTwoBean.data.list.size)) {
            mCommentOneList[mItem].twoList.add(commentTwoBean.data.list[i])
        }

        Log.i("two", "mCommentOneList[mItem].twoList : ${mCommentOneList[mItem].twoList}")

        mCommentOneList[mItem].total = mCommentOneList[mItem].total - commentTwoBean.data.list.size

        if (mPageList[mItem] == 1) {
            mCommentOneList[mItem].twoList.removeAt(0)
        }

        mPageList[mItem]++

        adapter.notifyDataSetChanged()
    }

    override fun onGetCommentTwoCodeError() {

    }

    override fun onGetCommentOneSuccess(commentOneBean: CommentOneBean) {

        for (i in 0.until(commentOneBean.data.list.size)) {

            if (currentPaper == 1) {
                mCommentOneList.clear()
            }
            currentPaper++

            if (commentOneBean.data.list[i].count_two !== null) {
                mCommentOneList.add(CommentBean(commentOneBean.data.list[i],
                    arrayListOf<CommentTwoList>() as MutableList<CommentTwoList>,
                    arrayListOf<CommentTwoList>() as MutableList<CommentTwoList>,
                    commentOneBean.data.list[i].count_two!!,
                    commentOneBean.data.list[i].count_two!!))
            } else {
                mCommentOneList.add(CommentBean(commentOneBean.data.list[i],
                    arrayListOf<CommentTwoList>() as MutableList<CommentTwoList>,
                    arrayListOf<CommentTwoList>() as MutableList<CommentTwoList>,
                    0,
                    0))
            }
            mPageList.add(1)
        }

        adapter.notifyDataSetChanged()

        sfl_dynamic_other_show_refresh.finishLoadMore(true)

    }

    override fun onGetCommentOneCodeError() {
        sfl_dynamic_other_show_refresh.finishLoadMore(true)
    }

    override fun onDoCommentTwoCreateSuccess(commentTwoCreateBean: CommentTwoCreateBean?) {
        if (commentTwoCreateBean != null) {
            when (commentTwoCreateBean.code) {
                200 -> {
                    ToastUtils.showShort("重新加载数据")

                    when (mCommentOneList[mItem].all) {
                        0 -> {
                            // 此时一个数据都没有，不要添加到two里面，直接添加到one里面

                            mCommentOneList[mItem].list.content_two = content
                            mCommentOneList[mItem].list.count_two = 1
                            mCommentOneList[mItem].list.id_two = commentTwoCreateBean.data.one_id
                            mCommentOneList[mItem].list.image_two =
                                SPStaticUtils.getString(Constant.ME_AVATAR, "")
                            mCommentOneList[mItem].list.nick_two =
                                SPStaticUtils.getString(Constant.ME_NAME, "")
                            mCommentOneList[mItem].list.pid_two = mCommentOneList[mItem].list.id
                            mCommentOneList[mItem].list.sex_two =
                                SPStaticUtils.getInt(Constant.ME_SEX, 1)
                            mCommentOneList[mItem].list.tid_two = trendsId
                            mCommentOneList[mItem].list.time_two =
                                commentTwoCreateBean.data.server_time
                            mCommentOneList[mItem].list.two_last_uid =
                                SPStaticUtils.getString(Constant.USER_ID, "13").toInt()

                            mCommentOneList[mItem].all = 1
                            mCommentOneList[mItem].total = 1


                        }
                        else -> {
                            // 需要添加到two里面

                            Log.i("guo", "tttttxxxxxxxxx")

                            val two = CommentTwoList(
                                content,
                                commentTwoCreateBean.data.server_time,
                                childAvatar,
                                childNick,
                                childSex,
                                0,
                                hostUid,
                                commentTwoCreateBean.data.one_id,
                                SPStaticUtils.getString(Constant.ME_AVATAR, ""),
                                SPStaticUtils.getString(Constant.ME_NAME, ""),
                                SPStaticUtils.getInt(Constant.ME_SEX, 1),
                                mCommentOneList[mItem].list.one_level_uid,
                                0,
                                mCommentOneList[mItem].list.id,
                                trendsId,
                                0,
                                SPStaticUtils.getString(Constant.USER_ID, "13").toInt(),
                                0
                            )

                            mCommentOneList[mItem].twoLocalList.add(two)

                        }
                    }


                    adapter.notifyDataSetChanged()

                    isLocalAdd = true

                    childAvatar = ""
                    childNick = ""
                    childSex = 1

                    mode = 0
                    trendsId = trendId
                    hostUid = userId
                    threeId = SPStaticUtils.getString(Constant.USER_ID, "13").toInt()
                }
                207 -> {
                    ToastUtils.showShort("回复内容不能为空")
                }
                444 -> {
                    ToastUtils.showShort("不能回复自己")
                }
            }
        }
    }

    override fun onDoCommentTwoCreateError() {

    }

    override fun onDoCommentOneCreateSuccess(commentOneCreateBean: CommentOneCreateBean?) {
        if (commentOneCreateBean != null) {
            when (commentOneCreateBean.code) {
                200 -> {
                    ToastUtils.showShort("本地添加数据")

                    val list = CommentOneList(
                        content,
                        "",
                        0,
                        hostUid,
                        commentOneCreateBean.data.one_id,
                        0,
                        "",
                        SPStaticUtils.getString(Constant.ME_AVATAR, ""),
                        SPStaticUtils.getString(Constant.ME_NAME, ""),
                        "",
                        SPStaticUtils.getString(Constant.USER_ID, "13").toInt(),
                        0,
                        SPStaticUtils.getInt(Constant.ME_SEX, 1),
                        0,
                        0,
                        commentOneCreateBean.data.server_time,
                        "",
                        trendsId,
                        0
                    )

                    val x: MutableList<CommentTwoList> = arrayListOf()
                    val y: MutableList<CommentTwoList> = arrayListOf()
                    val bean = CommentBean(list, x, y, 0, 0)
                    mCommentOneList.add(0, bean)
                    adapter.notifyDataSetChanged()

                    mode = 0
                    trendsId = trendId
                    hostUid = userId
                    threeId = SPStaticUtils.getString(Constant.USER_ID, "13").toInt()
                }
                207 -> {
                    ToastUtils.showShort("回复内容不能为空")
                }
                444 -> {
                    ToastUtils.showShort("不能回复自己")
                }
            }
        }
    }

    override fun onDoCommentOneCreateError() {

    }

    override fun onDoCheckTrendSuccess(checkTrendBean: CheckTrendBean?) {

        if (checkTrendBean != null) {
            if (checkTrendBean.code == 200) {
                if (checkTrendBean.data.list.isNotEmpty()) {

                    info = checkTrendBean.data.list[0]
                    val image = checkTrendBean.data.imgs

                    SpUtil.storeVipInfo(info.close_time_low, info.close_time_high)

                    trendsId = info.id
                    hostUid = info.user_id.toInt()
                    threeId = SPStaticUtils.getString(Constant.USER_ID, "13").toInt()


                    if (isMine) {
                        riv_dynamic_other_show_heard_name.text = "我的动态"
                    } else {
                        when (info.user_sex) {
                            1 -> riv_dynamic_other_show_heard_name.text = "他的动态"
                            2 -> riv_dynamic_other_show_heard_name.text = "她的动态"
                            else -> riv_dynamic_other_show_heard_name.text = "他的动态"
                        }
                    }

                    if (info.user_sex == 1) {
                        Glide.with(applicationContext)
                            .load(info.headface)
                            .error(R.drawable.ic_mine_male_default)
                            .placeholder(R.drawable.ic_mine_male_default)
                            .into(riv_dynamic_other_show_avatar)
                    } else {
                        Glide.with(applicationContext)
                            .load(info.headface)
                            .error(R.drawable.ic_mine_female_default)
                            .placeholder(R.drawable.ic_mine_female_default)
                            .into(riv_dynamic_other_show_avatar)
                    }

                    tv_dynamic_other_show_name.text = info.nick


                    when (info.real_face) {
                        1 -> {
                            iv_detail_dynamic_other_avatar.visibility = View.VISIBLE
                        }
                        else -> {
                            iv_detail_dynamic_other_avatar.visibility = View.GONE
                        }
                    }

                    when (info.identity_status) {
                        1 -> {
                            iv_detail_dynamic_other_identity.visibility = View.VISIBLE
                        }
                        else -> {
                            iv_detail_dynamic_other_identity.visibility = View.GONE
                        }
                    }

                    when (SpUtil.getVipLevel(info.close_time_low, info.close_time_high)) {
                        0 -> {
                            iv_detail_dynamic_other_vip.visibility = View.GONE
                        }
                        1 -> {
                            iv_detail_dynamic_other_vip.visibility = View.VISIBLE
                            iv_detail_dynamic_other_vip.setImageResource(R.drawable.ic_vip)
                        }
                        2 -> {
                            iv_detail_dynamic_other_vip.visibility = View.VISIBLE
                            iv_detail_dynamic_other_vip.setImageResource(R.drawable.ic_svip)
                        }
                    }


                    val year = (TimeUtils.getValueByCalendarField(TimeUtils.getNowDate(),
                        Calendar.YEAR) - info.age).toString().substring(2, 4)
                    val city = info.work_city_str
                    val edu = eduList[info.education]

                    val job = if (info.industry_str == "") {
                        "${info.industry_str}"
                    } else {
                        " ${info.industry_str}/${info.occupation_str}"
                    }

                    tv_dynamic_other_show_info.text = "${year}年  $city  $edu  $job"

                    if (info.focous_uid != null) {
                        haveFocus = true

                        iv_dynamic_other_show_mode.setImageResource(R.drawable.ic_base_chat)

                    } else {
                        haveFocus = false

                        iv_dynamic_other_show_mode.setImageResource(R.drawable.ic_base_focus)

                    }

                    tv_dynamic_other_show_time.text = TimeUtil.getCommonTime(info.create_time)

                    if (info.guest_uid != null) {
                        isLike = true
                        iv_dynamic_other_show_like.setImageResource(R.drawable.ic_dynamic_like_bottom_check)
                    } else {
                        isLike = false
                        iv_dynamic_other_show_like.setImageResource(R.drawable.ic_dynamic_like_bottom)
                    }

                    if (info.position != "") {
                        ll_dynamic_other_show_location.visibility = View.VISIBLE
                        tv_dynamic_other_show_location.text = info.position
                    } else {
                        ll_dynamic_other_show_location.visibility = View.GONE
                    }

                    if (info.discuss_count == null) {
                        tv_dynamic_other_show_comment.text = 0.toString()
                    } else {
                        tv_dynamic_other_show_comment.text = info.discuss_count.toString()
                    }

                    if (info.like_count == null || info.like_count == 0) {

                        likeSum = 0

                        rl_dynamic_other_show_like.visibility = View.GONE

                        tv_dynamic_other_show_like.text = likeSum.toString()
                    } else {

                        likeSum = info.like_count!!

                        rl_dynamic_other_show_like.visibility = View.VISIBLE

                        tv_dynamic_other_show_like.text = likeSum.toString()
                    }

                    if (info.text_content != "") {
                        tv_dynamic_other_show_text.text = info.text_content
                    } else {
                        tv_dynamic_other_show_text.visibility = View.GONE
                    }

                    when (info.trends_type) {
                        1 -> {

                            ll_dynamic_other_show_video.visibility = View.GONE


                            // 图片
                            if (info.text_content != "") {
                                tv_dynamic_other_show_text.text = info.text_content
                            } else {
                                tv_dynamic_other_show_text.visibility = View.GONE
                            }

                            mPicList = info.image_url.split(",") as MutableList<String>
                            for (i in 0.until(mPicList.size)) {
                                if (mPicList[i].contains(" ")) {
                                    mPicList[i] = mPicList[i].replace(" ", "")
                                }
                            }

                            when (mPicList.size) {
                                1 -> {
                                    ll_dynamic_other_show_one.visibility = View.VISIBLE
                                    ll_dynamic_other_show_two.visibility = View.GONE
                                    ll_dynamic_other_show_three.visibility = View.GONE

                                    iv_dynamic_other_show_one.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[0])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_one)
                                    iv_dynamic_other_show_two.visibility = View.INVISIBLE
                                    iv_dynamic_other_show_three.visibility = View.INVISIBLE
                                    iv_dynamic_other_show_four.visibility = View.GONE
                                    iv_dynamic_other_show_five.visibility = View.GONE
                                    iv_dynamic_other_show_six.visibility = View.GONE
                                    iv_dynamic_other_show_seven.visibility = View.GONE
                                    iv_dynamic_other_show_eight.visibility = View.GONE
                                    iv_dynamic_other_show_nine.visibility = View.GONE
                                }
                                2 -> {
                                    ll_dynamic_other_show_one.visibility = View.VISIBLE
                                    ll_dynamic_other_show_two.visibility = View.GONE
                                    ll_dynamic_other_show_three.visibility = View.GONE

                                    iv_dynamic_other_show_one.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[0])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_one)
                                    iv_dynamic_other_show_two.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[1])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_two)
                                    iv_dynamic_other_show_three.visibility = View.INVISIBLE
                                    iv_dynamic_other_show_four.visibility = View.GONE
                                    iv_dynamic_other_show_five.visibility = View.GONE
                                    iv_dynamic_other_show_six.visibility = View.GONE
                                    iv_dynamic_other_show_seven.visibility = View.GONE
                                    iv_dynamic_other_show_eight.visibility = View.GONE
                                    iv_dynamic_other_show_nine.visibility = View.GONE
                                }
                                3 -> {
                                    ll_dynamic_other_show_one.visibility = View.VISIBLE
                                    ll_dynamic_other_show_two.visibility = View.GONE
                                    ll_dynamic_other_show_three.visibility = View.GONE

                                    iv_dynamic_other_show_one.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[0])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_one)
                                    iv_dynamic_other_show_two.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[1])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_two)
                                    iv_dynamic_other_show_three.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[2])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_three)
                                    iv_dynamic_other_show_four.visibility = View.GONE
                                    iv_dynamic_other_show_five.visibility = View.GONE
                                    iv_dynamic_other_show_six.visibility = View.GONE
                                    iv_dynamic_other_show_seven.visibility = View.GONE
                                    iv_dynamic_other_show_eight.visibility = View.GONE
                                    iv_dynamic_other_show_nine.visibility = View.GONE
                                }
                                4 -> {
                                    ll_dynamic_other_show_one.visibility = View.VISIBLE
                                    ll_dynamic_other_show_two.visibility = View.VISIBLE
                                    ll_dynamic_other_show_three.visibility = View.GONE

                                    iv_dynamic_other_show_one.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[0])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_one)
                                    iv_dynamic_other_show_two.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[1])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_two)
                                    iv_dynamic_other_show_three.visibility = View.INVISIBLE
                                    iv_dynamic_other_show_four.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[2])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_four)
                                    iv_dynamic_other_show_five.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[3])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_five)
                                    iv_dynamic_other_show_six.visibility = View.INVISIBLE
                                    iv_dynamic_other_show_seven.visibility = View.GONE
                                    iv_dynamic_other_show_eight.visibility = View.GONE
                                    iv_dynamic_other_show_nine.visibility = View.GONE
                                }
                                5 -> {
                                    ll_dynamic_other_show_one.visibility = View.VISIBLE
                                    ll_dynamic_other_show_two.visibility = View.VISIBLE
                                    ll_dynamic_other_show_three.visibility = View.GONE

                                    iv_dynamic_other_show_one.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[0])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_one)
                                    iv_dynamic_other_show_two.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[1])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_two)
                                    iv_dynamic_other_show_three.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[2])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_three)
                                    iv_dynamic_other_show_four.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[3])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_four)
                                    iv_dynamic_other_show_five.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[4])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_five)
                                    iv_dynamic_other_show_six.visibility = View.INVISIBLE
                                    iv_dynamic_other_show_seven.visibility = View.GONE
                                    iv_dynamic_other_show_eight.visibility = View.GONE
                                    iv_dynamic_other_show_nine.visibility = View.GONE
                                }
                                6 -> {
                                    ll_dynamic_other_show_one.visibility = View.VISIBLE
                                    ll_dynamic_other_show_two.visibility = View.VISIBLE
                                    ll_dynamic_other_show_three.visibility = View.GONE

                                    iv_dynamic_other_show_one.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[0])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_one)
                                    iv_dynamic_other_show_two.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[1])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_two)
                                    iv_dynamic_other_show_three.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[2])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_three)
                                    iv_dynamic_other_show_four.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[3])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_four)
                                    iv_dynamic_other_show_five.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[4])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_five)
                                    iv_dynamic_other_show_six.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[5])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_six)
                                    iv_dynamic_other_show_seven.visibility = View.GONE
                                    iv_dynamic_mine_show_eight.visibility = View.GONE
                                    iv_dynamic_other_show_nine.visibility = View.GONE
                                }
                                7 -> {
                                    ll_dynamic_other_show_one.visibility = View.VISIBLE
                                    ll_dynamic_other_show_two.visibility = View.VISIBLE
                                    ll_dynamic_other_show_three.visibility = View.VISIBLE

                                    iv_dynamic_other_show_one.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[0])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_one)
                                    iv_dynamic_other_show_two.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[1])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_two)
                                    iv_dynamic_other_show_three.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[2])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_three)
                                    iv_dynamic_other_show_four.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[3])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_four)
                                    iv_dynamic_other_show_five.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[4])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_five)
                                    iv_dynamic_other_show_six.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[5])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_six)
                                    iv_dynamic_other_show_seven.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[6])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_seven)
                                    iv_dynamic_other_show_eight.visibility = View.INVISIBLE
                                    iv_dynamic_other_show_nine.visibility = View.INVISIBLE
                                }
                                8 -> {
                                    ll_dynamic_other_show_one.visibility = View.VISIBLE
                                    ll_dynamic_other_show_two.visibility = View.VISIBLE
                                    ll_dynamic_other_show_three.visibility = View.VISIBLE

                                    iv_dynamic_other_show_one.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[0])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_one)
                                    iv_dynamic_other_show_two.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[1])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_two)
                                    iv_dynamic_other_show_three.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[2])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_three)
                                    iv_dynamic_other_show_four.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[3])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_four)
                                    iv_dynamic_other_show_five.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[4])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_five)
                                    iv_dynamic_other_show_six.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[5])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_six)
                                    iv_dynamic_other_show_seven.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[6])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_seven)
                                    iv_dynamic_other_show_eight.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[7])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_eight)
                                    iv_dynamic_other_show_nine.visibility = View.INVISIBLE
                                }
                                9 -> {
                                    ll_dynamic_other_show_one.visibility = View.VISIBLE
                                    ll_dynamic_other_show_two.visibility = View.VISIBLE
                                    ll_dynamic_other_show_three.visibility = View.VISIBLE

                                    iv_dynamic_other_show_one.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[0])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_one)
                                    iv_dynamic_other_show_two.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[1])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_two)
                                    iv_dynamic_other_show_three.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[2])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_three)
                                    iv_dynamic_other_show_four.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[3])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_four)
                                    iv_dynamic_other_show_five.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[4])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_five)
                                    iv_dynamic_other_show_six.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[5])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_six)
                                    iv_dynamic_other_show_seven.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[6])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_seven)
                                    iv_dynamic_other_show_eight.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[7])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_eight)
                                    iv_dynamic_other_show_nine.visibility = View.VISIBLE
                                    Glide.with(applicationContext).load(mPicList[8])
                                        .error(R.drawable.ic_pic_default)
                                        .placeholder(R.drawable.ic_pic_default)
                                        .into(iv_dynamic_other_show_nine)
                                }
                            }

                        }
                        2 -> {
                            // 视频

                            mVideoUrl = info.video_url
                            mName = info.nick

                            ll_dynamic_other_show_video.visibility = View.VISIBLE
                            ll_dynamic_other_show_one.visibility = View.GONE
                            ll_dynamic_other_show_two.visibility = View.GONE
                            ll_dynamic_other_show_three.visibility = View.GONE

                            Glide.with(applicationContext).load(info.video_url)
                                .into(iv_dynamic_other_show_video)

                            if (info.text_content != "") {
                                tv_dynamic_other_show_text.text = info.text_content
                            } else {
                                tv_dynamic_other_show_text.visibility = View.GONE
                            }

                        }
                        3 -> {
                            // 文字

                            ll_dynamic_other_show_video.visibility = View.GONE
                            ll_dynamic_other_show_one.visibility = View.GONE
                            ll_dynamic_other_show_two.visibility = View.GONE
                            ll_dynamic_other_show_three.visibility = View.GONE

                            tv_dynamic_other_show_text.text = info.text_content
                        }
                    }

                    when (checkTrendBean.data.imgs.size) {
                        0 -> {
                            ll_dynamic_other_show_like.visibility = View.GONE
                        }
                        1 -> {

                            likeAvatar.add(checkTrendBean.data.imgs[0].image_url)

                            iv_dynamic_other_show_like1.visibility = View.VISIBLE
                            Glide.with(applicationContext)
                                .load(image[0].image_url)
                                .error(R.drawable.ic_mine_male_default)
                                .placeholder(R.drawable.ic_mine_male_default)
                                .into(iv_dynamic_other_show_like1)

                            iv_dynamic_other_show_like2.visibility = View.GONE

                            iv_dynamic_other_show_like3.visibility = View.GONE
                        }
                        2 -> {

                            likeAvatar.add(checkTrendBean.data.imgs[0].image_url)
                            likeAvatar.add(checkTrendBean.data.imgs[1].image_url)

                            iv_dynamic_other_show_like1.visibility = View.VISIBLE
                            Glide.with(applicationContext)
                                .load(image[0].image_url)
                                .error(R.drawable.ic_mine_male_default)
                                .placeholder(R.drawable.ic_mine_male_default)
                                .into(iv_dynamic_other_show_like1)

                            iv_dynamic_other_show_like2.visibility = View.VISIBLE
                            Glide.with(applicationContext).load(image[1].image_url)
                                .into(iv_dynamic_other_show_like2)

                            iv_dynamic_other_show_like3.visibility = View.GONE
                        }
                        3 -> {

                            likeAvatar.add(checkTrendBean.data.imgs[0].image_url)
                            likeAvatar.add(checkTrendBean.data.imgs[1].image_url)
                            likeAvatar.add(checkTrendBean.data.imgs[2].image_url)

                            iv_dynamic_other_show_like1.visibility = View.VISIBLE
                            Glide.with(applicationContext)
                                .load(image[0].image_url)
                                .error(R.drawable.ic_mine_male_default)
                                .placeholder(R.drawable.ic_mine_male_default)
                                .into(iv_dynamic_other_show_like1)

                            iv_dynamic_other_show_like2.visibility = View.VISIBLE
                            Glide.with(applicationContext).load(image[1].image_url)
                                .into(iv_dynamic_other_show_like2)

                            iv_dynamic_other_show_like3.visibility = View.VISIBLE
                            Glide.with(applicationContext).load(image[2].image_url)
                                .into(iv_dynamic_other_show_like3)
                        }
                    }

                }

            } else {
                ToastUtils.showShort("网络请求错误")
            }
        }

    }

    override fun onDoCheckTrendError() {
    }

    override fun onItemClick(v: View?, positionOne: Int) {

    }

    override fun onItemAvatarClick(v: View?, positionOne: Int) {
        // 父评论点击头像
        startActivity(FriendInfoActivity.getIntent(
            this,
            mCommentOneList[positionOne].list.one_level_uid
        ))
    }

    override fun onItemContentClick(v: View?, positionOne: Int) {
        // 父评论点击评论

        mItem = positionOne

        // edittext 获取焦点 模式切换成父评论
        eet_emoji_other_edit.hint = "回复${mCommentOneList[positionOne].list.nick_one}"

        eet_emoji_other_edit.isFocusable = true
        eet_emoji_other_edit.isFocusableInTouchMode = true
        eet_emoji_other_edit.requestFocus()
        KeyboardUtils.showSoftInput()


        mode = 1
        trendsId = mCommentOneList[positionOne].list.trends_id
        hostUid = mCommentOneList[positionOne].list.host_uid
        threeId = mCommentOneList[positionOne].list.id
        oneLevelId = mCommentOneList[positionOne].list.one_level_uid
        firstUid = 0
        lastUid = SPStaticUtils.getString(Constant.USER_ID, "12").toInt()

        ToastUtils.showShort(KeyboardUtils.isSoftInputVisible(this).toString())

    }

    override fun onItemMoreClick(v: View?, positionOne: Int) {
        // 请求数据

        // 存在本地数据未清掉，又添加数据的情况

        mItem = positionOne

        Log.i("guo", " mItem : $mItem")

        getCommentTwo(trendId, mCommentOneList[positionOne].list.id, mPageList[positionOne])


    }

    override fun onItemChildAvatarClick(v: View?, positionOne: Int) {
        startActivity(FriendInfoActivity.getIntent(
            this,
            mCommentOneList[positionOne].list.two_last_uid
        ))
    }

    override fun onItemChildContentClick(v: View?, positionOne: Int) {

        mItem = positionOne

        childAvatar = mCommentOneList[positionOne].list.image_two
        childNick = mCommentOneList[positionOne].list.nick_two
        childSex = mCommentOneList[positionOne].list.sex_two

        // edittext 获取焦点 模式切换成父评论

        eet_emoji_other_edit.hint = "回复${mCommentOneList[positionOne].list.nick_two}"

        eet_emoji_other_edit.isFocusable = true
        eet_emoji_other_edit.isFocusableInTouchMode = true
        eet_emoji_other_edit.requestFocus()
        KeyboardUtils.showSoftInput()



        Log.i("guo", "one_level_uid : ${mCommentOneList[positionOne].list.one_level_uid}")

        mode = 1
        trendsId = mCommentOneList[positionOne].list.trends_id
        hostUid = mCommentOneList[positionOne].list.host_uid
        threeId = mCommentOneList[positionOne].list.id
        oneLevelId = mCommentOneList[positionOne].list.one_level_uid
        firstUid = mCommentOneList[positionOne].list.two_last_uid
        lastUid = SPStaticUtils.getString(Constant.USER_ID, "12").toInt()

        ToastUtils.showShort(KeyboardUtils.isSoftInputVisible(this).toString())

    }

    override fun onChildClick(positionOne: Int, two: Int) {
        // 子评论点击事件
        ToastUtils.showShort("${positionOne}/${two}")
    }

    override fun onChildAvatarClick(positionOne: Int, two: Int) {
        // 子评论头像点击事件
        ToastUtils.showShort(" 子评论头像点击 ${positionOne}/${two}")
        startActivity(FriendInfoActivity.getIntent(
            this,
            mCommentOneList[positionOne].twoList[two].two_last_uid
        ))
    }

    override fun onChildReplyClick(positionOne: Int, two: Int) {
        // 子评论回复内容点击事件

        mItem = positionOne
        mChildItem = two

        childAvatar = mCommentOneList[positionOne].twoList[two].last_img_url
        childNick = mCommentOneList[positionOne].twoList[two].last_nick
        childSex = mCommentOneList[positionOne].twoList[two].last_sex

        // edittext 获取焦点 模式切换成父评论

        eet_emoji_other_edit.hint = "回复${mCommentOneList[positionOne].twoList[two].last_nick}"

        eet_emoji_other_edit.isFocusable = true
        eet_emoji_other_edit.isFocusableInTouchMode = true
        eet_emoji_other_edit.requestFocus()
        KeyboardUtils.showSoftInput()



        mode = 1
        trendsId = mCommentOneList[positionOne].list.trends_id
        hostUid = mCommentOneList[positionOne].list.host_uid
        threeId = mCommentOneList[positionOne].list.id
        oneLevelId = mCommentOneList[positionOne].twoList[two].pid
        firstUid = mCommentOneList[positionOne].twoList[two].two_last_uid
        lastUid = SPStaticUtils.getString(Constant.USER_ID, "12").toInt()

        ToastUtils.showShort(KeyboardUtils.isSoftInputVisible(this).toString())

    }

    override fun onChildReplyAvatarClick(positionOne: Int, two: Int) {
        // 子评论回复的用户名点击事件
        ToastUtils.showShort(" 子评论回复用户名点击 ${positionOne}/${two}")
        startActivity(FriendInfoActivity.getIntent(
            this,
            mCommentOneList[positionOne].twoList[two].two_first_uid
        ))
    }

    override fun onLocalChildClick(positionOne: Int, two: Int) {
        // 子评论点击事件
        ToastUtils.showShort("${positionOne}/${two}")
    }

    override fun onLocalChildAvatarClick(positionOne: Int, two: Int) {
        // 子评论头像点击事件
        ToastUtils.showShort(" 子评论头像点击 ${positionOne}/${two}")
        startActivity(FriendInfoActivity.getIntent(
            this,
            mCommentOneList[positionOne].twoLocalList[two].two_last_uid
        ))
    }

    override fun onLocalChildReplyClick(positionOne: Int, two: Int) {
        // 子评论回复内容点击事件

        mItem = positionOne

        mLocalChildItem = two

        childAvatar = mCommentOneList[positionOne].twoLocalList[two].last_img_url
        childNick = mCommentOneList[positionOne].twoLocalList[two].last_nick
        childSex = mCommentOneList[positionOne].twoLocalList[two].last_sex

        // edittext 获取焦点 模式切换成父评论

        eet_emoji_other_edit.hint = "回复${mCommentOneList[positionOne].twoLocalList[two].last_nick}"

        eet_emoji_other_edit.isFocusable = true
        eet_emoji_other_edit.isFocusableInTouchMode = true
        eet_emoji_other_edit.requestFocus()
        KeyboardUtils.showSoftInput()



        mode = 1
        trendsId = mCommentOneList[positionOne].list.trends_id
        hostUid = mCommentOneList[positionOne].list.host_uid
        threeId = mCommentOneList[positionOne].list.id
        oneLevelId = mCommentOneList[positionOne].twoLocalList[two].pid
        firstUid = mCommentOneList[positionOne].twoLocalList[two].two_last_uid
        lastUid = SPStaticUtils.getString(Constant.USER_ID, "13").toInt()

        ToastUtils.showShort(KeyboardUtils.isSoftInputVisible(this).toString())

    }

    override fun onLocalChildReplyAvatarClick(positionOne: Int, two: Int) {
        // 子评论回复的用户名点击事件
        ToastUtils.showShort(" 子评论回复用户名点击 ${positionOne}/${two}")
        startActivity(FriendInfoActivity.getIntent(
            this,
            mCommentOneList[positionOne].twoLocalList[two].two_first_uid
        ))
    }

    override fun onItemLongClick(v: View?, positionOne: Int) {
        ToastUtils.showShort(positionOne)
        // adapter

        if (mCommentOneList[positionOne].list.one_level_uid.toString() ==
            SPStaticUtils.getString(Constant.USER_ID, "13")
        ) {

            val id = mCommentOneList[positionOne].list.id
            val trendId = mCommentOneList[positionOne].list.trends_id
            val hostId = mCommentOneList[positionOne].list.host_uid

            ToastUtils.showShort("本人发的")

            twoPosition = 0

            if (id != 0) {
                XPopup.Builder(this)
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(DeleteDialog(this, id, trendId, hostId, positionOne, 0, 0))
                    .show()
            } else {
                ToastUtils.showShort("此条数据刚添加，暂时无法删除")
            }

        } else {
            ToastUtils.showShort("已复制此评论")
            ClipboardUtils.copyText(mCommentOneList[positionOne].list.content_one)
        }

    }

    override fun onItemChildContentLongClick(v: View?, positionOne: Int) {
        ToastUtils.showShort("子评论 : $positionOne")
        // 父评论附带的那条子评论
        // 分情况
        // 当除了这条没其他数据时，直接删除这个
        // 当还有其他数据时，首先需要取出adapter 中数据的第一条数据，然后替换上面固定的数据 ，然后adapter的数据移除第一条

        if (mCommentOneList[positionOne].list.two_last_uid.toString() ==
            SPStaticUtils.getString(Constant.USER_ID, "13")
        ) {

            val id = mCommentOneList[positionOne].list.id_two
            val trendId = mCommentOneList[positionOne].list.trends_id
            val hostId = mCommentOneList[positionOne].list.id

            ToastUtils.showShort("本人发的")

            twoPosition = 0

            if (id != 0) {
                XPopup.Builder(this)
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(DeleteDialog(this, id, trendId, hostId, positionOne, 1, 0))
                    .show()
            } else {
                ToastUtils.showShort("此条数据刚添加，暂时无法删除")
            }

        } else {
            ToastUtils.showShort("已复制此评论")
            ClipboardUtils.copyText(mCommentOneList[positionOne].list.content_two)
        }

    }

    override fun onChildContentLongClick(positionOne: Int, two: Int) {

        Log.i("guo", "guo : ${two}")

        if (mCommentOneList[positionOne].twoList[two].two_last_uid.toString() ==
            SPStaticUtils.getString(Constant.USER_ID, "13")
        ) {

            ToastUtils.showShort("本人发的")
            val id = mCommentOneList[positionOne].twoList[two].id
            val trendId = mCommentOneList[positionOne].list.trends_id
            val hostId = mCommentOneList[positionOne].list.id

            twoPosition = two

            if (id != 0) {
                XPopup.Builder(this)
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(DeleteDialog(this, id, trendId, hostId, positionOne, 1, 1))
                    .show()
            } else {
                ToastUtils.showShort("此条数据刚添加，暂时无法删除")
            }


        } else {
            ToastUtils.showShort("已复制此评论")
            ClipboardUtils.copyText(mCommentOneList[positionOne].twoList[two].content)

        }
    }

    override fun onLocalChildContentLongClick(positionOne: Int, two: Int) {
        Log.i("guo", "guo : ${two}")

        if (mCommentOneList[positionOne].twoLocalList[two].two_last_uid.toString() ==
            SPStaticUtils.getString(Constant.USER_ID, "13")
        ) {

            ToastUtils.showShort("本人发的")
            val id = mCommentOneList[positionOne].twoLocalList[two].id
            val trendId = mCommentOneList[positionOne].list.trends_id
            val hostId = mCommentOneList[positionOne].list.id

            twoPosition = two

            if (id != 0) {
                XPopup.Builder(this)
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(DeleteDialog(this, id, trendId, hostId, positionOne, 1, 1))
                    .show()
            } else {
                ToastUtils.showShort("此条数据刚添加，暂时无法删除")
            }


        } else {
            ToastUtils.showShort("已复制此评论")
            ClipboardUtils.copyText(mCommentOneList[positionOne].twoLocalList[two].content)
        }
    }

    inner class DynamicReportDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_dynamic_report

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<TextView>(R.id.tv_dialog_dynamic_other_edit_cancel)
            val focus = findViewById<TextView>(R.id.tv_dialog_dynamic_other_edit_focus)
            val report = findViewById<TextView>(R.id.tv_dialog_dynamic_other_edit_report)

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

            focus.setOnClickListener {
                isBackFresh = true
                if (haveFocus) {
                    ToastUtils.showShort("取消关注")
                    doCancelFocus(SPStaticUtils.getString(Constant.USER_ID, "13"), userId)
                } else {
                    ToastUtils.showShort("关注")
                    doPlusFocus(SPStaticUtils.getString(Constant.USER_ID, "13"), userId)
                }
                dismiss()
            }

            report.setOnClickListener {
                ToastUtils.showShort("举报该动态")
                startActivity(ReportReasonActivity.getIntent(
                    this@DynamicOtherShowActivity,
                    SPStaticUtils.getString(Constant.USER_ID, "13"),
                    userId.toString()
                ))
                dismiss()
            }

        }

        override fun onDismiss() {
            super.onDismiss()
        }

    }

    inner class DynamicDeleteDialog(context: Context) :
        FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_dynamic_delete

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_dialog_dynamic_mine_edit_close)
            val delete = findViewById<TextView>(R.id.tv_dialog_dynamic_mine_edit_delete)
            val cancel = findViewById<TextView>(R.id.tv_dialog_dynamic_mine_edit_cancel)

            close.setOnClickListener {
                dismiss()
            }

            delete.setOnClickListener {
                deleteTrends(info.id, info.user_id)
                dismiss()
            }

            cancel.setOnClickListener {
                dismiss()
            }

        }

        override fun onDismiss() {
            super.onDismiss()
        }

    }

    inner class DeleteDialog(
        context: Context,
        private val mid: Int,
        private val trendsId: Int,
        private val hostId: Int,
        private val one: Int,
        private val mode: Int,
        private val childMode: Int,
    ) :
        FullScreenPopupView(context),
        IDoCommentOneDeleteCallback,
        IDoCommentTwoDeleteCallback {


        private lateinit var doCommentOneDeletePresent: doCommentOneDeletePresentImpl
        private lateinit var doCommentTwoDeletePresent: doCommentTwoDeletePresentImpl

        private var load = false

        override fun getImplLayoutId(): Int = R.layout.dialog_tips

        override fun onCreate() {
            super.onCreate()

            doCommentOneDeletePresent = doCommentOneDeletePresentImpl.getsInstance()
            doCommentOneDeletePresent.registerCallback(this)

            doCommentTwoDeletePresent = doCommentTwoDeletePresentImpl.getsInstance()
            doCommentTwoDeletePresent.registerCallback(this)

            findViewById<TextView>(R.id.tv_dialog_tip_info).text = "您确定要删除该动态吗"

            findViewById<TextView>(R.id.tv_dialog_tip_cancel).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_dialog_tip_confirm).setOnClickListener {
                // 删除动态
                when (mode) {
                    0 -> {
                        val map: MutableMap<String, String> = TreeMap()
                        map[Contents.ID] = mid.toString()
                        map[Contents.TRENDS_ID] = trendsId.toString()
                        map[Contents.HOST_UID] = hostId.toString()
                        map[Contents.GUEST_UID] =
                            SPStaticUtils.getString(Constant.USER_ID, "13")
                        doCommentOneDeletePresent.doCommentOneDelete(map)
                    }
                    1 -> {
                        load = true
                        val map: MutableMap<String, String> = TreeMap()
                        map[Contents.ID] = mid.toString()
                        map[Contents.TRENDS_ID] = trendsId.toString()
                        map[Contents.PARENT_ID] = hostId.toString()
                        doCommentTwoDeletePresent.doCommentTwoDelete(map)
                    }
                }
            }
        }

        override fun onDismiss() {
            super.onDismiss()
        }

        override fun onLoading() {

        }

        override fun onError() {

        }

        override fun onDoCommentOneDeleteSuccess(commentOneDeleteBean: CommentOneDeleteBean?) {
            dismiss()
            if (commentOneDeleteBean != null) {
                if (commentOneDeleteBean.code == 200) {
                    ToastUtils.showShort("删除父动态，更新视图")
                    if (mCommentOneList.size > one) {
                        mCommentOneList.removeAt(one)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        override fun onDoCommentOneDeleteError() {
            dismiss()
        }

        override fun onDoCommentTwoDeleteSuccess(commentTwoDeleteBean: CommentTwoDeleteBean?) {
            dismiss()

            if (commentTwoDeleteBean != null) {
                if (commentTwoDeleteBean.code == 200) {
                    when (childMode) {
                        0 -> {
                            when (mCommentOneList[one].all) {
                                1 -> {
                                    mCommentOneList[one].all = mCommentOneList[one].all - 1
                                    adapter.notifyDataSetChanged()
                                }
                                else -> {

                                    // 判断当 ${mCommentOneList[positionOne].twoList} 为空时，也就是没展开数据时，对用户做出的操作不做反应

                                    if (mCommentOneList[one].twoList.isNotEmpty()) {

                                        // 二级评论列表id
                                        mCommentOneList[one].list.id_two =
                                            mCommentOneList[one].twoList[0].id
                                        // 二级评论首条
                                        mCommentOneList[one].list.content_two =
                                            mCommentOneList[one].twoList[0].content
                                        // 二级评论首条uid
                                        mCommentOneList[one].list.two_last_uid =
                                            mCommentOneList[one].twoList[0].two_last_uid
                                        // 二级评论首条u昵称
                                        mCommentOneList[one].list.nick_two =
                                            mCommentOneList[one].twoList[0].last_nick
                                        // 二级评论首条性别
                                        mCommentOneList[one].list.sex_two =
                                            mCommentOneList[one].twoList[0].last_sex
                                        // 二级评论首条头像
                                        mCommentOneList[one].list.image_two =
                                            mCommentOneList[one].twoList[0].last_img_url
                                        // 二级评论总计多少条
                                        mCommentOneList[one].list.count_two =
                                            mCommentOneList[one].list.count_two!! - 1
                                        mCommentOneList[one].all = mCommentOneList[one].all - 1

                                        mCommentOneList[one].twoList.removeAt(0)

                                        adapter.notifyDataSetChanged()
                                    } else {
                                        ToastUtils.showShort("未展开，此时不提供删除功能")
                                    }
                                }
                            }
                        }
                        1 -> {
                            ToastUtils.showShort("删除子动态，更新视图")
                            if (load) {
                                load = false
                                Log.i("guo", "one : $one, two :$twoPosition")
                                if (mCommentOneList[one].twoList.size > twoPosition) {
                                    mCommentOneList[one].twoList.removeAt(twoPosition)
                                    mCommentOneList[one].all - 1
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            }

        }

        override fun onDoCommentTwoDeleteError() {
            dismiss()
        }

    }

    class ReportDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_tips

        override fun onCreate() {
            super.onCreate()

            findViewById<TextView>(R.id.tv_dialog_tip_info).text = "您确定要举报该动态吗"

            findViewById<TextView>(R.id.tv_dialog_tip_cancel).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_dialog_tip_confirm).setOnClickListener {
                dismiss()
                ToastUtils.showShort("举报")
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
                ToastUtils.showShort("跳转到头像界面")
                startActivity(Intent(context, AvatarToolActivity::class.java))
            }

        }

        override fun onDismiss() {
            super.onDismiss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        doCheckTrendPresent.unregisterCallback(this)
        getCommentOnePresent.unregisterCallback(this)
        doCommentOneCreatePresent.unregisterCallback(this)
        getCommentTwoPresent.unregisterCallback(this)
        doCommentTwoCreatePresent.unregisterCallback(this)
        doLikeClickPresent.unregisterCallback(this)
        doLikeCancelPresent.unregisterCallback(this)
        doPlusFocusPresent.unregisterCallback(this)
        doCancelFocusPresent.unregisterCallback(this)
        doDeleteTrendPresent.unregisterCallback(this)

    }

}