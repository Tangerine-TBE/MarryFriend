package com.twx.marryfriend.dynamic.show.mine

import android.content.Context
import android.content.Intent
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
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.dynamic.*
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.preview.image.ImagePreviewActivity
import com.twx.marryfriend.dynamic.preview.video.VideoPreviewActivity
import com.twx.marryfriend.utils.emoji.EmojiDetailAdapter
import com.twx.marryfriend.dynamic.show.mine.adapter.CommentOneAdapter
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.utils.TimeUtil
import com.twx.marryfriend.utils.emoji.EmojiUtils
import kotlinx.android.synthetic.main.activity_dynamic_mine_show.*
import java.io.Serializable
import java.util.*

class DynamicMineShowActivity : MainBaseViewActivity(),
    com.twx.marryfriend.net.callback.dynamic.IDoCheckTrendCallback,
    com.twx.marryfriend.net.callback.dynamic.IGetCommentOneCallback,
    com.twx.marryfriend.net.callback.dynamic.IGetCommentTwoCallback,
    CommentOneAdapter.OnItemClickListener, CommentOneAdapter.OnItemLongClickListener,
    com.twx.marryfriend.net.callback.dynamic.IDoCommentOneCreateCallback,
    com.twx.marryfriend.net.callback.dynamic.IDoCommentTwoCreateCallback {

    private var x = true

    // 大图展示时进入时应该展示点击的那张图片
    private var imageIndex = 0

    private var id = 0

    private var mPicList: MutableList<String> = arrayListOf()

    private var mVideoUrl: String = ""
    private var mName: String = ""

    // 上次点击时间
    private var lastClickTime = 0L

    // 两次点击间隔时间（毫秒）
    private val delayTime = 3000

    // 评论发送模式
    private var mode = 0

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

    private var mCommentTwoList: MutableList<CommentTwoList> = arrayListOf()

    private var mPageList: MutableList<Int> = arrayListOf()

    private var mItem = 0

    var emojiList: MutableList<String> = arrayListOf()

    private lateinit var emojiAdapter: EmojiDetailAdapter

    // 是否需要更新父评论
    private var needUpdateParent = false


    private lateinit var doCheckTrendPresent: com.twx.marryfriend.net.impl.dynamic.doCheckTrendPresentImpl
    private lateinit var getCommentOnePresent: com.twx.marryfriend.net.impl.dynamic.getCommentOnePresentImpl
    private lateinit var doCommentOneCreatePresent: com.twx.marryfriend.net.impl.dynamic.doCommentOneCreatePresentImpl
    private lateinit var getCommentTwoPresent: com.twx.marryfriend.net.impl.dynamic.getCommentTwoPresentImpl
    private lateinit var doCommentTwoCreatePresent: com.twx.marryfriend.net.impl.dynamic.doCommentTwoCreatePresentImpl


    private lateinit var adapter: CommentOneAdapter


    override fun getLayoutView(): Int = R.layout.activity_dynamic_mine_show

    override fun initView() {
        super.initView()
        id = intent.getIntExtra("id", 0);

        doCheckTrendPresent =
            com.twx.marryfriend.net.impl.dynamic.doCheckTrendPresentImpl.getsInstance()
        doCheckTrendPresent.registerCallback(this)

        getCommentOnePresent =
            com.twx.marryfriend.net.impl.dynamic.getCommentOnePresentImpl.getsInstance()
        getCommentOnePresent.registerCallback(this)

        doCommentOneCreatePresent =
            com.twx.marryfriend.net.impl.dynamic.doCommentOneCreatePresentImpl.getsInstance()
        doCommentOneCreatePresent.registerCallback(this)

        getCommentTwoPresent =
            com.twx.marryfriend.net.impl.dynamic.getCommentTwoPresentImpl.getsInstance()
        getCommentTwoPresent.registerCallback(this)

        doCommentTwoCreatePresent =
            com.twx.marryfriend.net.impl.dynamic.doCommentTwoCreatePresentImpl.getsInstance()
        doCommentTwoCreatePresent.registerCallback(this)



        if (SPStaticUtils.getBoolean(Constant.HIDE_REPORT_TIP, false)) {
            iv_dynamic_mine_show_tip.visibility = View.GONE
        } else {
            iv_dynamic_mine_show_tip.visibility = View.VISIBLE
        }

        adapter = CommentOneAdapter(mCommentOneList)
        adapter.setOnItemClickListener(this)
        adapter.setOnItemLongClickListener(this)

        rv_dynamic_mine_show_container.layoutManager = LinearLayoutManager(this)
        rv_dynamic_mine_show_container.adapter = adapter

        emojiList = EmojiUtils.getEmojiList()

        emojiAdapter = EmojiDetailAdapter(emojiList)

        rv_emoji_mine_container.adapter = emojiAdapter
        rv_emoji_mine_container.layoutManager = GridLayoutManager(this, 8)

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
        getCommentOne()

        KeyboardUtils.fixAndroidBug5497(this)
        KeyboardUtils.clickBlankArea2HideSoftInput()

    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_dynamic_mine_show_finish.setOnClickListener {
            finish()
        }

        iv_dynamic_mine_show_heard_edit.setOnClickListener {
            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(DynamicEditDialog(this))
                .show()
        }

        riv_dynamic_mine_show_avatar.setOnClickListener {
            startActivity(FriendInfoActivity.getIntent(this,
                SPStaticUtils.getString(Constant.USER_ID, "13").toInt()))
        }

        fl_dynamic_mine_show_video.setOnClickListener {
            // 播放视频

            val intent = Intent(this, VideoPreviewActivity::class.java)
            intent.putExtra("videoUrl", mVideoUrl)
            intent.putExtra("name", mName)
            startActivity(intent)

        }

        iv_dynamic_mine_show_one.setOnClickListener {
            imageIndex = 0

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_mine_show_two.setOnClickListener {
            imageIndex = 1

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_mine_show_three.setOnClickListener {
            imageIndex = 2

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_mine_show_four.setOnClickListener {
            imageIndex = 3

            // 图片展示问题，需要调整一下imageIndex
            if (mPicList.size == 4) {
                imageIndex = 2
            }

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_mine_show_five.setOnClickListener {
            imageIndex = 4

            // 图片展示问题，需要调整一下imageIndex
            if (mPicList.size == 4) {
                imageIndex = 3
            }

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_mine_show_six.setOnClickListener {
            imageIndex = 5

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_mine_show_seven.setOnClickListener {
            imageIndex = 6

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_mine_show_eight.setOnClickListener {
            imageIndex = 7

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_mine_show_nine.setOnClickListener {
            imageIndex = 8

            startActivity(ImagePreviewActivity.getIntent(this, mPicList, imageIndex))
        }

        iv_dynamic_mine_show_tip_hide.setOnClickListener {
            iv_dynamic_mine_show_tip.visibility = View.GONE
            SPStaticUtils.put(Constant.HIDE_REPORT_TIP, true)
        }

        rl_dynamic_mine_show_like.setOnClickListener {
            val intent = Intent(this, DynamicMineLikeActivity::class.java)
            intent.putExtra("trendId", id)
            intent.putExtra("userId", SPStaticUtils.getString(Constant.USER_ID, "13"))
            startActivity(intent)
        }

        // 避免触发下层界面点击事件
        ll_dynamic_mine_show_bottom.setOnClickListener {

        }

        iv_dynamic_mine_show_like.setOnClickListener {
            ToastUtils.showShort("您不能给自己点赞")
        }

        iv_dynamic_mine_show_emoji.setOnClickListener {

            if (ll_emoji_mine_container.visibility == View.VISIBLE) {
                ll_emoji_mine_container.visibility = View.GONE
            } else {
                // 加个延时试一下


                val task: TimerTask = object : TimerTask() {
                    override fun run() {
                        ThreadUtils.runOnUiThread {
                            ll_emoji_mine_container.visibility = View.VISIBLE
                        }

                    }
                }
                val timer = Timer()
                timer.schedule(task, 50)


                if (KeyboardUtils.isSoftInputVisible(this@DynamicMineShowActivity)) {
                    x = false
                    KeyboardUtils.hideSoftInput(this@DynamicMineShowActivity)
                }
//                KeyboardUtils.hideSoftInput(this)
            }

        }

        iv_dynamic_mine_show_send.setOnClickListener {

            if (eet_emoji_mine_edit.text.toString().trim { it <= ' ' } != "") {

                if (System.currentTimeMillis() - lastClickTime >= delayTime) {
                    lastClickTime = System.currentTimeMillis();

                    var content = eet_emoji_mine_edit.text.toString().trim { it <= ' ' }
                    eet_emoji_mine_edit.setText("")
                    KeyboardUtils.hideSoftInput(this)
                    ll_emoji_mine_container.visibility = View.GONE

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
                ToastUtils.showShort("请输入您的评论")
            }
        }

        eet_emoji_mine_edit.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            eet_emoji_mine_edit.getWindowVisibleDisplayFrame(r)
            val screenHeight: Int = eet_emoji_mine_edit.rootView.height
            val heightDifference: Int = screenHeight - r.bottom
            if (heightDifference > 200) {
                // 软键盘显示
                if (x) {
                    ll_emoji_mine_container.visibility = View.GONE
                }
            } else {
                // 软键盘隐藏
            }
            x = !x
        }

        emojiAdapter.setOnItemClickListener(object : EmojiDetailAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                var x = eet_emoji_mine_edit.text.toString().trim { it <= ' ' }
                x += EmojiUtils.getCompatEmojiString(emojiList[position])
                eet_emoji_mine_edit.setText(x)
                eet_emoji_mine_edit.setSelection(x.length)
            }
        })

        iv_emoji_mine_delete.setOnClickListener {
            val keyCode = KeyEvent.KEYCODE_DEL
            val keyEventDown = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
            val keyEventUp = KeyEvent(KeyEvent.ACTION_UP, keyCode);
            eet_emoji_mine_edit.onKeyDown(keyCode, keyEventDown)
            eet_emoji_mine_edit.onKeyUp(keyCode, keyEventUp);
        }


        iv_emoji_mine_send.setOnClickListener {

            if (eet_emoji_mine_edit.text.toString().trim { it <= ' ' } != "") {

                if (System.currentTimeMillis() - lastClickTime >= delayTime) {
                    lastClickTime = System.currentTimeMillis();

                    var content = eet_emoji_mine_edit.text.toString().trim { it <= ' ' }
                    eet_emoji_mine_edit.setText("")
                    KeyboardUtils.hideSoftInput(this)
                    ll_emoji_mine_container.visibility = View.GONE

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
                ToastUtils.showShort("请输入您的评论")
            }

        }

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

                needUpdateParent = true

                doCommentOne(trends_id, host_uid, three_id, content)

            }
            1 -> {
                // 子动态

                needUpdateParent = true

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
        map[Contents.ID] = id.toString()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        doCheckTrendPresent.doCheckTrend(map)
    }

    // 获取一级父评论
    private fun getCommentOne() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.TRENDS_ID] = 6.toString()
        map[Contents.HOST_UID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getCommentOnePresent.getCommentOne(map)
    }

    // 给动态提交父评论
    private fun doCommentOne(trendsId: Int, hostId: Int, GuestId: Int, content: String) {

//        val map: MutableMap<String, String> = TreeMap()
//        map[Contents.TRENDS_ID] = trendsId.toString()
//        map[Contents.HOST_UID] = hostId.toString()
//        map[Contents.GUEST_UID] = GuestId.toString()
//        map[Contents.CONTENT_ONE] = content
//        doCommentOneCreatePresent.doCommentOneCreate(map)

        ToastUtils.showShort("不能回复自己")

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


    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoCommentTwoCreateSuccess(commentTwoCreateBean: CommentTwoCreateBean?) {

        if (commentTwoCreateBean != null) {

            when (commentTwoCreateBean.code) {
                200 -> {
                    ToastUtils.showShort("重新加载数据")
//                    mCommentOneList[mItem].all += 1
//                    mCommentOneList[mItem].total += 1
//
//                    adapter.notifyDataSetChanged()

                    getCommentOne()

                    mode = 0
                    trendsId = info.id
                    hostUid = info.user_id.toInt()
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

                    // 这里不能重新加载数据，需要本地添加数据进去

                    ToastUtils.showShort("重新加载数据")
                    mCommentOneList[mItem].all += 1
                    mCommentOneList[mItem].total += 1

                    adapter.notifyDataSetChanged()

                    mode = 0
                    trendsId = info.id
                    hostUid = info.user_id.toInt()
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

    override fun onGetCommentOneSuccess(commentOneBean: CommentOneBean) {

        if (needUpdateParent) {
            mCommentOneList.clear()
            needUpdateParent = false
        }

        for (i in 0.until(commentOneBean.data.list.size)) {

            if (commentOneBean.data.list[i].count_two !== null) {
                mCommentOneList.add(CommentBean(commentOneBean.data.list[i],
                    arrayListOf<CommentTwoList>() as MutableList<CommentTwoList>,
                    commentOneBean.data.list[i].count_two!!,
                    commentOneBean.data.list[i].count_two!!))
            } else {
                mCommentOneList.add(CommentBean(commentOneBean.data.list[i],
                    arrayListOf<CommentTwoList>() as MutableList<CommentTwoList>,
                    0,
                    0))
            }
            mPageList.add(1)
        }

        Log.i("guo", "list : ${mCommentOneList[2]}")

        adapter.notifyDataSetChanged()
    }

    override fun onGetCommentOneCodeError() {

    }

    override fun onGetCommentTwoSuccess(commentTwoBean: CommentTwoBean) {

        if (mPageList[mItem] == 1) {
            mCommentOneList[mItem].twoList.clear()
        }


        for (i in 0.until(commentTwoBean.data.list.size)) {
            mCommentOneList[mItem].twoList.add(commentTwoBean.data.list[i])
        }

        Log.i("guo", "mItem : $mItem")

        mCommentOneList[mItem].total = mCommentOneList[mItem].total - commentTwoBean.data.list.size

        if (mPageList[mItem] == 1) {
            mCommentOneList[mItem].twoList.removeAt(0)
        }

        mPageList[mItem]++

        Log.i("guo", "onGetCommentTwoSuccess : ${GsonUtils.toJson(mCommentOneList)}")

        adapter.notifyDataSetChanged()

    }

    override fun onGetCommentTwoCodeError() {

    }

    override fun onDoCheckTrendSuccess(checkTrendBean: CheckTrendBean?) {

        if (checkTrendBean != null) {
            if (checkTrendBean.code == 200) {
                info = checkTrendBean.data.list[0]
                val image = checkTrendBean.data.imgs

                trendsId = info.id
                hostUid = info.user_id.toInt()
                threeId = SPStaticUtils.getString(Constant.USER_ID, "13").toInt()


                Glide.with(applicationContext).load(info.headface)
                    .into(riv_dynamic_mine_show_heard_avatar)
                riv_dynamic_mine_show_heard_name.text = info.nick

                Glide.with(applicationContext).load(info.headface)
                    .into(riv_dynamic_mine_show_avatar)
                tv_dynamic_mine_show_name.text = info.nick

                val year = (TimeUtils.getValueByCalendarField(TimeUtils.getNowDate(),
                    Calendar.YEAR) - info.age).toString().substring(2, 4)
                val city = info.work_city_str
                val edu = eduList[info.education]

                val job = if (info.industry_str == "") {
                    "${info.industry_str}"
                } else {
                    " ${info.industry_str}/${info.occupation_str}"
                }

                tv_dynamic_mine_show_info.text = "${year}年  $city  $edu  $job"

                tv_dynamic_mine_show_time.text = TimeUtil.getCommonTime(info.create_time)

                if (info.position != "") {
                    ll_dynamic_mine_show_location.visibility = View.VISIBLE
                    tv_dynamic_mine_show_location.text = info.position
                } else {
                    ll_dynamic_mine_show_location.visibility = View.GONE
                }

                if (info.discuss_count == null) {
                    tv_dynamic_mine_show_comment.text = 0.toString()
                } else {
                    tv_dynamic_mine_show_comment.text = info.discuss_count.toString()
                }

                if (info.like_count == null) {
                    rl_dynamic_mine_show_like.visibility = View.GONE
                    tv_dynamic_mine_show_like.text = 0.toString()
                } else {
                    rl_dynamic_mine_show_like.visibility = View.VISIBLE
                    tv_dynamic_mine_show_like.text = info.like_count.toString()
                }

                if (info.text_content != "") {
                    tv_dynamic_mine_show_text.text = info.text_content
                } else {
                    tv_dynamic_mine_show_text.visibility = View.GONE
                }

                when (info.trends_type) {
                    1 -> {

                        ll_dynamic_mine_show_video.visibility = View.GONE

                        // 图片
                        if (info.text_content != "") {
                            tv_dynamic_mine_show_text.text = info.text_content
                        } else {
                            tv_dynamic_mine_show_text.visibility = View.GONE
                        }

                        mPicList = info.image_url.split(",") as MutableList<String>
                        for (i in 0.until(mPicList.size)) {
                            mPicList[i] = mPicList[i].replace(" ", "")
                        }

                        when (mPicList.size) {
                            1 -> {
                                ll_dynamic_mine_show_one.visibility = View.VISIBLE
                                ll_dynamic_mine_show_two.visibility = View.GONE
                                ll_dynamic_mine_show_three.visibility = View.GONE

                                iv_dynamic_mine_show_one.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[0])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_one)
                                iv_dynamic_mine_show_two.visibility = View.INVISIBLE
                                iv_dynamic_mine_show_three.visibility = View.INVISIBLE
                                iv_dynamic_mine_show_four.visibility = View.GONE
                                iv_dynamic_mine_show_five.visibility = View.GONE
                                iv_dynamic_mine_show_six.visibility = View.GONE
                                iv_dynamic_mine_show_seven.visibility = View.GONE
                                iv_dynamic_mine_show_eight.visibility = View.GONE
                                iv_dynamic_mine_show_nine.visibility = View.GONE
                            }
                            2 -> {
                                ll_dynamic_mine_show_one.visibility = View.VISIBLE
                                ll_dynamic_mine_show_two.visibility = View.GONE
                                ll_dynamic_mine_show_three.visibility = View.GONE

                                iv_dynamic_mine_show_one.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[0])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_one)
                                iv_dynamic_mine_show_two.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[1])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_two)
                                iv_dynamic_mine_show_three.visibility = View.INVISIBLE
                                iv_dynamic_mine_show_four.visibility = View.GONE
                                iv_dynamic_mine_show_five.visibility = View.GONE
                                iv_dynamic_mine_show_six.visibility = View.GONE
                                iv_dynamic_mine_show_seven.visibility = View.GONE
                                iv_dynamic_mine_show_eight.visibility = View.GONE
                                iv_dynamic_mine_show_nine.visibility = View.GONE
                            }
                            3 -> {
                                ll_dynamic_mine_show_one.visibility = View.VISIBLE
                                ll_dynamic_mine_show_two.visibility = View.GONE
                                ll_dynamic_mine_show_three.visibility = View.GONE

                                iv_dynamic_mine_show_one.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[0])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_one)
                                iv_dynamic_mine_show_two.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[1])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_two)
                                iv_dynamic_mine_show_three.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[2])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_three)
                                iv_dynamic_mine_show_four.visibility = View.GONE
                                iv_dynamic_mine_show_five.visibility = View.GONE
                                iv_dynamic_mine_show_six.visibility = View.GONE
                                iv_dynamic_mine_show_seven.visibility = View.GONE
                                iv_dynamic_mine_show_eight.visibility = View.GONE
                                iv_dynamic_mine_show_nine.visibility = View.GONE
                            }
                            4 -> {
                                ll_dynamic_mine_show_one.visibility = View.VISIBLE
                                ll_dynamic_mine_show_two.visibility = View.VISIBLE
                                ll_dynamic_mine_show_three.visibility = View.GONE

                                iv_dynamic_mine_show_one.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[0])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_one)
                                iv_dynamic_mine_show_two.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[1])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_two)
                                iv_dynamic_mine_show_three.visibility = View.INVISIBLE
                                iv_dynamic_mine_show_four.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[2])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_four)
                                iv_dynamic_mine_show_five.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[3])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_five)
                                iv_dynamic_mine_show_six.visibility = View.INVISIBLE
                                iv_dynamic_mine_show_seven.visibility = View.GONE
                                iv_dynamic_mine_show_eight.visibility = View.GONE
                                iv_dynamic_mine_show_nine.visibility = View.GONE
                            }
                            5 -> {
                                ll_dynamic_mine_show_one.visibility = View.VISIBLE
                                ll_dynamic_mine_show_two.visibility = View.VISIBLE
                                ll_dynamic_mine_show_three.visibility = View.GONE

                                iv_dynamic_mine_show_one.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[0])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_one)
                                iv_dynamic_mine_show_two.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[1])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_two)
                                iv_dynamic_mine_show_three.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[2])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_three)
                                iv_dynamic_mine_show_four.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[3])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_four)
                                iv_dynamic_mine_show_five.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[4])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_five)
                                iv_dynamic_mine_show_six.visibility = View.INVISIBLE
                                iv_dynamic_mine_show_seven.visibility = View.GONE
                                iv_dynamic_mine_show_eight.visibility = View.GONE
                                iv_dynamic_mine_show_nine.visibility = View.GONE
                            }
                            6 -> {
                                ll_dynamic_mine_show_one.visibility = View.VISIBLE
                                ll_dynamic_mine_show_two.visibility = View.VISIBLE
                                ll_dynamic_mine_show_three.visibility = View.GONE

                                iv_dynamic_mine_show_one.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[0])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_one)
                                iv_dynamic_mine_show_two.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[1])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_two)
                                iv_dynamic_mine_show_three.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[2])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_three)
                                iv_dynamic_mine_show_four.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[3])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_four)
                                iv_dynamic_mine_show_five.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[4])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_five)
                                iv_dynamic_mine_show_six.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[5])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_six)
                                iv_dynamic_mine_show_seven.visibility = View.GONE
                                iv_dynamic_mine_show_eight.visibility = View.GONE
                                iv_dynamic_mine_show_nine.visibility = View.GONE
                            }
                            7 -> {
                                ll_dynamic_mine_show_one.visibility = View.VISIBLE
                                ll_dynamic_mine_show_two.visibility = View.VISIBLE
                                ll_dynamic_mine_show_three.visibility = View.VISIBLE

                                iv_dynamic_mine_show_one.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[0])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_one)
                                iv_dynamic_mine_show_two.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[1])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_two)
                                iv_dynamic_mine_show_three.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[2])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_three)
                                iv_dynamic_mine_show_four.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[3])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_four)
                                iv_dynamic_mine_show_five.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[4])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_five)
                                iv_dynamic_mine_show_six.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[5])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_six)
                                iv_dynamic_mine_show_seven.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[6])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_seven)
                                iv_dynamic_mine_show_eight.visibility = View.INVISIBLE
                                iv_dynamic_mine_show_nine.visibility = View.INVISIBLE
                            }
                            8 -> {
                                ll_dynamic_mine_show_one.visibility = View.VISIBLE
                                ll_dynamic_mine_show_two.visibility = View.VISIBLE
                                ll_dynamic_mine_show_three.visibility = View.VISIBLE

                                iv_dynamic_mine_show_one.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[0])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_one)
                                iv_dynamic_mine_show_two.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[1])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_two)
                                iv_dynamic_mine_show_three.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[2])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_three)
                                iv_dynamic_mine_show_four.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[3])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_four)
                                iv_dynamic_mine_show_five.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[4])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_five)
                                iv_dynamic_mine_show_six.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[5])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_six)
                                iv_dynamic_mine_show_seven.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[6])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_seven)
                                iv_dynamic_mine_show_eight.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[7])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_eight)
                                iv_dynamic_mine_show_nine.visibility = View.INVISIBLE
                            }
                            9 -> {
                                ll_dynamic_mine_show_one.visibility = View.VISIBLE
                                ll_dynamic_mine_show_two.visibility = View.VISIBLE
                                ll_dynamic_mine_show_three.visibility = View.VISIBLE

                                iv_dynamic_mine_show_one.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[0])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_one)
                                iv_dynamic_mine_show_two.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[1])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_two)
                                iv_dynamic_mine_show_three.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[2])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_three)
                                iv_dynamic_mine_show_four.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[3])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_four)
                                iv_dynamic_mine_show_five.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[4])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_five)
                                iv_dynamic_mine_show_six.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[5])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_six)
                                iv_dynamic_mine_show_seven.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[6])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_seven)
                                iv_dynamic_mine_show_eight.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[7])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_eight)
                                iv_dynamic_mine_show_nine.visibility = View.VISIBLE
                                Glide.with(applicationContext).load(mPicList[8])
                                    .error(R.drawable.ic_pic_default)
                                    .placeholder(R.drawable.ic_pic_default)
                                    .into(iv_dynamic_mine_show_nine)
                            }
                        }

                    }
                    2 -> {
                        // 视频

                        mVideoUrl = info.video_url
                        mName = info.nick

                        ll_dynamic_mine_show_video.visibility = View.VISIBLE
                        ll_dynamic_mine_show_one.visibility = View.GONE
                        ll_dynamic_mine_show_two.visibility = View.GONE
                        ll_dynamic_mine_show_three.visibility = View.GONE

                        Glide.with(applicationContext).load(info.video_url)
                            .into(iv_dynamic_mine_show_video)

                        if (info.text_content != "") {
                            tv_dynamic_mine_show_text.text = info.text_content
                        } else {
                            tv_dynamic_mine_show_text.visibility = View.GONE
                        }

                    }
                    3 -> {
                        // 文字

                        ll_dynamic_mine_show_video.visibility = View.GONE
                        ll_dynamic_mine_show_one.visibility = View.GONE
                        ll_dynamic_mine_show_two.visibility = View.GONE
                        ll_dynamic_mine_show_three.visibility = View.GONE

                        tv_dynamic_mine_show_text.text = info.text_content
                    }
                }

                when (checkTrendBean.data.imgs.size) {
                    0 -> {
                        ll_dynamic_mine_show_like.visibility = View.GONE
                    }
                    1 -> {
                        iv_dynamic_mine_show_like1.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(image[0].image_url)
                            .into(iv_dynamic_mine_show_like1)

                        iv_dynamic_mine_show_like2.visibility = View.GONE

                        iv_dynamic_mine_show_like3.visibility = View.GONE
                    }
                    2 -> {
                        iv_dynamic_mine_show_like1.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(image[0].image_url)
                            .into(iv_dynamic_mine_show_like1)

                        iv_dynamic_mine_show_like2.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(image[1].image_url)
                            .into(iv_dynamic_mine_show_like2)

                        iv_dynamic_mine_show_like3.visibility = View.GONE
                    }
                    3 -> {
                        iv_dynamic_mine_show_like1.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(image[0].image_url)
                            .into(iv_dynamic_mine_show_like1)

                        iv_dynamic_mine_show_like2.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(image[1].image_url)
                            .into(iv_dynamic_mine_show_like2)

                        iv_dynamic_mine_show_like3.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(image[2].image_url)
                            .into(iv_dynamic_mine_show_like3)
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

        ToastUtils.showShort("点击父评论头像 ： $positionOne ")


    }

    override fun onItemContentClick(v: View?, positionOne: Int) {
        // 父评论点击评论

        ToastUtils.showShort("点击父评论的评论内容 ： $positionOne ")

        // edittext 获取焦点 模式切换成父评论

        eet_emoji_mine_edit.isFocusable = true
        eet_emoji_mine_edit.isFocusableInTouchMode = true
        eet_emoji_mine_edit.requestFocus()
        KeyboardUtils.showSoftInput()

        mode = 1
        trendsId = mCommentOneList[positionOne].list.trends_id
        hostUid = mCommentOneList[positionOne].list.host_uid
        threeId = mCommentOneList[positionOne].list.id
        oneLevelId = mCommentOneList[positionOne].list.one_level_uid
        firstUid = 0
        lastUid = SPStaticUtils.getString(Constant.USER_ID, "12").toInt()

    }

    override fun onItemMoreClick(v: View?, positionOne: Int) {
        // 请求数据

        mItem = positionOne

        getCommentTwo(6, mCommentOneList[positionOne].list.id, mPageList[positionOne])

    }

    override fun onItemChildAvatarClick(v: View?, positionOne: Int) {
        ToastUtils.showShort(" 子评论头像点击 ${positionOne}/000")
    }

    override fun onItemChildContentClick(v: View?, positionOne: Int) {
        ToastUtils.showShort(" 子评论回复内容点击 ${positionOne}/000")

        // edittext 获取焦点 模式切换成父评论

        eet_emoji_mine_edit.isFocusable = true
        eet_emoji_mine_edit.isFocusableInTouchMode = true
        eet_emoji_mine_edit.requestFocus()
        KeyboardUtils.showSoftInput()

        Log.i("guo", "one_level_uid : ${mCommentOneList[positionOne].list.one_level_uid}")

        mode = 1
        trendsId = mCommentOneList[positionOne].list.trends_id
        hostUid = mCommentOneList[positionOne].list.host_uid
        threeId = mCommentOneList[positionOne].list.id
        oneLevelId = mCommentOneList[positionOne].list.one_level_uid
        firstUid = mCommentOneList[positionOne].list.two_last_uid
        lastUid = SPStaticUtils.getString(Constant.USER_ID, "12").toInt()

    }

    override fun onChildClick(positionOne: Int, two: Int) {
        // 子评论点击事件
        ToastUtils.showShort("${positionOne}/${two}")


    }

    override fun onChildAvatarClick(positionOne: Int, two: Int) {
        // 子评论头像点击事件
        ToastUtils.showShort(" 子评论头像点击 ${positionOne}/${two}")
    }

    override fun onChildReplyClick(positionOne: Int, two: Int) {
        // 子评论回复内容点击事件
        ToastUtils.showShort(" 子评论回复内容点击 ${positionOne}/${two}")

        // edittext 获取焦点 模式切换成父评论

        eet_emoji_mine_edit.isFocusable = true
        eet_emoji_mine_edit.isFocusableInTouchMode = true
        eet_emoji_mine_edit.requestFocus()
        KeyboardUtils.showSoftInput()

        mode = 1
        trendsId = mCommentOneList[positionOne].list.trends_id
        hostUid = mCommentOneList[positionOne].list.host_uid
        threeId = mCommentOneList[positionOne].list.id
        oneLevelId = mCommentOneList[positionOne].twoList[two].pid
        firstUid = mCommentOneList[positionOne].twoList[two].two_last_uid
        lastUid = SPStaticUtils.getString(Constant.USER_ID, "12").toInt()

    }

    override fun onChildReplyAvatarClick(positionOne: Int, two: Int) {
        // 子评论回复的用户名点击事件
        ToastUtils.showShort(" 子评论回复用户名点击 ${positionOne}/${two}")
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
            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(DeleteDialog(this, id, trendId, hostId, positionOne, 0, 0, 0))
                .show()

        } else {
            ToastUtils.showShort("不是本人发的，无法删除")
            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(ReportDialog(this))
                .show()
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
            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(DeleteDialog(this, id, trendId, hostId, positionOne, 0, 1, 0))
                .show()

        } else {
            ToastUtils.showShort("不是本人发的，无法删除")
            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(ReportDialog(this))
                .show()
        }
    }

    override fun onChildContentLongClick(positionOne: Int, two: Int) {
        ToastUtils.showShort(" $positionOne  ,  $two")

        if (mCommentOneList[positionOne].twoList[two].two_last_uid.toString() ==
            SPStaticUtils.getString(Constant.USER_ID, "13")
        ) {

            ToastUtils.showShort("本人发的")
            val id = mCommentOneList[positionOne].twoList[two].id
            val trendId = mCommentOneList[positionOne].twoList[two].trends_id
            val hostId = mCommentOneList[positionOne].twoList[two].pid

            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(DeleteDialog(this, id, trendId, hostId, positionOne, two, 1, 1))
                .show()
        } else {
            ToastUtils.showShort("不是本人发的，无法删除")
            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(ReportDialog(this))
                .show()
        }

    }

    class DynamicEditDialog(context: Context) : FullScreenPopupView(context),
        com.twx.marryfriend.net.callback.dynamic.IDoDeleteTrendCallback {

        private lateinit var doDeleteTrendPresent: com.twx.marryfriend.net.impl.dynamic.doDeleteTrendPresentImpl

        // 是否删除动态，弹窗消失时结束
        private var isFinish = false

        override fun getImplLayoutId(): Int = R.layout.dialog_dynamic_mine_edit

        override fun onCreate() {
            super.onCreate()

            doDeleteTrendPresent =
                com.twx.marryfriend.net.impl.dynamic.doDeleteTrendPresentImpl.getsInstance()
            doDeleteTrendPresent.registerCallback(this)

            val close = findViewById<ImageView>(R.id.iv_dialog_dynamic_mine_edit_close)
            val delete = findViewById<TextView>(R.id.tv_dialog_dynamic_mine_edit_delete)
            val cancel = findViewById<TextView>(R.id.tv_dialog_dynamic_mine_edit_cancel)

            close.setOnClickListener {
                dismiss()
            }

            delete.setOnClickListener {
                ToastUtils.showShort("删除动态,百度云图片还未添加删除功能，待添加")

//                val map: MutableMap<String, String> = TreeMap()
//                map[Contents.ID] = SPStaticUtils.getString(Constant.USER_ID)
//                map[Contents.USER_ID] = id.toString()
//                doDeleteTrendPresent.doDeleteTrend(map)

            }

            cancel.setOnClickListener {
                dismiss()
            }

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isFinish) {
                ToastUtils.showShort("此处需要finish 这个 activity ，然后触发我的动态界面的数据更新")
            }
        }

        override fun onLoading() {

        }

        override fun onError() {

        }

        override fun onDoDeleteTrendSuccess(deleteTrendBean: DeleteTrendBean) {

            if (deleteTrendBean.code == 200) {
                ToastUtils.showShort("动态删除完成")
                isFinish = true
                dismiss()
            } else {
                ToastUtils.showShort(deleteTrendBean.msg)
            }


        }

        override fun onDoDeleteTrendError() {

        }

    }

    inner class DeleteDialog(
        context: Context,
        id: Int,
        trendsId: Int,
        hostId: Int,
        one: Int,
        two: Int,
        mode: Int,
        childMode: Int,
    ) :
        FullScreenPopupView(context),
        com.twx.marryfriend.net.callback.dynamic.IDoCommentOneDeleteCallback,
        com.twx.marryfriend.net.callback.dynamic.IDoCommentTwoDeleteCallback {

        private val mid = id
        private val trendsId = trendsId
        private val hostId = hostId
        private val one = one
        private val two = two
        private val mode = mode
        private val childMode = childMode


        private lateinit var doCommentOneDeletePresent: com.twx.marryfriend.net.impl.dynamic.doCommentOneDeletePresentImpl
        private lateinit var doCommentTwoDeletePresent: com.twx.marryfriend.net.impl.dynamic.doCommentTwoDeletePresentImpl

        override fun getImplLayoutId(): Int = R.layout.dialog_tips

        override fun onCreate() {
            super.onCreate()

            doCommentOneDeletePresent =
                com.twx.marryfriend.net.impl.dynamic.doCommentOneDeletePresentImpl.getsInstance()
            doCommentOneDeletePresent.registerCallback(this)

            doCommentTwoDeletePresent =
                com.twx.marryfriend.net.impl.dynamic.doCommentTwoDeletePresentImpl.getsInstance()
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
                        map[Contents.GUEST_UID] = SPStaticUtils.getString(Constant.USER_ID, "13")
                        doCommentOneDeletePresent.doCommentOneDelete(map)
                    }
                    1 -> {
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
                    mCommentOneList.removeAt(one)
                    adapter.notifyDataSetChanged()
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
                            mCommentOneList[one].twoList.removeAt(two)
                            mCommentOneList[one].all - 1
                            adapter.notifyDataSetChanged()
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

}