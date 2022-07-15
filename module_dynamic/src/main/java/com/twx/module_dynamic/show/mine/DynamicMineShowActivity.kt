package com.twx.module_dynamic.show.mine

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.*
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.twx.module_base.base.MainBaseViewActivity
import com.twx.module_base.constant.Constant
import com.twx.module_base.constant.Contents
import com.twx.module_base.utils.EmojiUtils
import com.twx.module_base.utils.TimeUtil
import com.twx.module_dynamic.R
import com.twx.module_dynamic.bean.*
import com.twx.module_dynamic.emoji.EmojiDetailAdapter
import com.twx.module_dynamic.net.callback.*
import com.twx.module_dynamic.net.impl.*
import com.twx.module_dynamic.preview.image.ImagePreviewActivity
import com.twx.module_dynamic.preview.video.VideoPreviewActivity
import com.twx.module_dynamic.show.mine.adapter.CommentOneAdapter
import kotlinx.android.synthetic.main.activity_dynamic_mine_show.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class DynamicMineShowActivity : MainBaseViewActivity(), IDoCheckTrendCallback,
    IGetCommentOneCallback, IGetCommentTwoCallback, CommentOneAdapter.OnItemClickListener,
    IDoCommentOneCreateCallback, IDoCommentTwoCreateCallback {

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


    private lateinit var doCheckTrendPresent: doCheckTrendPresentImpl
    private lateinit var getCommentOnePresent: getCommentOnePresentImpl
    private lateinit var doCommentOneCreatePresent: doCommentOneCreatePresentImpl
    private lateinit var getCommentTwoPresent: getCommentTwoPresentImpl
    private lateinit var doCommentTwoCreatePresent: doCommentTwoCreatePresentImpl

    private lateinit var adapter: CommentOneAdapter


    override fun getLayoutView(): Int = R.layout.activity_dynamic_mine_show

    override fun initView() {
        super.initView()
        id = intent.getIntExtra("id", 0);

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


        if (SPStaticUtils.getBoolean(Constant.HIDE_REPORT_TIP, false)) {
            iv_dynamic_mine_show_tip.visibility = View.GONE
        } else {
            iv_dynamic_mine_show_tip.visibility = View.VISIBLE
        }

        adapter = CommentOneAdapter(mCommentOneList)
        adapter.setOnItemClickListener(this)

        rv_dynamic_mine_show_container.layoutManager = LinearLayoutManager(this)
        rv_dynamic_mine_show_container.adapter = adapter

        emojiList = EmojiUtils.getEmojiList()

        emojiAdapter = EmojiDetailAdapter(emojiList)

        rv_emoji_mine_container.adapter = emojiAdapter
        rv_emoji_mine_container.layoutManager = GridLayoutManager(this, 8)

        //设置“发送"的按钮
        eet_emoji_mine_edit.imeOptions = EditorInfo.IME_ACTION_SEND;  //属于重要说明的第一条
        // 设置inputType
        eet_emoji_mine_edit.inputType = TYPE_TEXT_FLAG_MULTI_LINE;    //属于重要说明的第二条
        eet_emoji_mine_edit.isSingleLine = false;                       //属于重要说明的第四条
        eet_emoji_mine_edit.maxLines = 4;                              //属于重要说明的第三条，此处限制在四行以内

    }

    override fun initLoadData() {
        super.initLoadData()

        eduList.add("大专以下")
        eduList.add("大专")
        eduList.add("本科")
        eduList.add("硕士")
        eduList.add("博士")
        eduList.add("博士以上")

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

        fl_dynamic_mine_show_video.setOnClickListener {
            // 播放视频

            val intent = Intent(this, VideoPreviewActivity::class.java)
            intent.putExtra("videoUrl", mVideoUrl)
            intent.putExtra("name", mName)
            startActivity(intent)

        }

        iv_dynamic_mine_show_one.setOnClickListener {
            imageIndex = 0

            val intent = Intent(this, ImagePreviewActivity::class.java)
            intent.putExtra("imageList", mPicList as Serializable)
            intent.putExtra("imageIndex", imageIndex)
            startActivity(intent)
        }

        iv_dynamic_mine_show_two.setOnClickListener {
            imageIndex = 1

            val intent = Intent(this, ImagePreviewActivity::class.java)
            intent.putExtra("imageList", mPicList as Serializable)
            intent.putExtra("imageIndex", imageIndex)
            startActivity(intent)
        }

        iv_dynamic_mine_show_three.setOnClickListener {
            imageIndex = 2

            val intent = Intent(this, ImagePreviewActivity::class.java)
            intent.putExtra("imageList", mPicList as Serializable)
            intent.putExtra("imageIndex", imageIndex)
            startActivity(intent)
        }

        iv_dynamic_mine_show_four.setOnClickListener {
            imageIndex = 3

            // 图片展示问题，需要调整一下imageIndex
            if (mPicList.size == 4) {
                imageIndex = 2
            }

            val intent = Intent(this, ImagePreviewActivity::class.java)
            intent.putExtra("imageList", mPicList as Serializable)
            intent.putExtra("imageIndex", imageIndex)
            startActivity(intent)
        }

        iv_dynamic_mine_show_five.setOnClickListener {
            imageIndex = 4

            // 图片展示问题，需要调整一下imageIndex
            if (mPicList.size == 4) {
                imageIndex = 3
            }

            val intent = Intent(this, ImagePreviewActivity::class.java)
            intent.putExtra("imageList", mPicList as Serializable)
            intent.putExtra("imageIndex", imageIndex)
            startActivity(intent)
        }

        iv_dynamic_mine_show_six.setOnClickListener {
            imageIndex = 5

            val intent = Intent(this, ImagePreviewActivity::class.java)
            intent.putExtra("imageList", mPicList as Serializable)
            intent.putExtra("imageIndex", imageIndex)
            startActivity(intent)
        }

        iv_dynamic_mine_show_seven.setOnClickListener {
            imageIndex = 6

            val intent = Intent(this, ImagePreviewActivity::class.java)
            intent.putExtra("imageList", mPicList as Serializable)
            intent.putExtra("imageIndex", imageIndex)
            startActivity(intent)
        }

        iv_dynamic_mine_show_eight.setOnClickListener {
            imageIndex = 7

            val intent = Intent(this, ImagePreviewActivity::class.java)
            intent.putExtra("imageList", mPicList as Serializable)
            intent.putExtra("imageIndex", imageIndex)
            startActivity(intent)
        }

        iv_dynamic_mine_show_nine.setOnClickListener {
            imageIndex = 8

            val intent = Intent(this, ImagePreviewActivity::class.java)
            intent.putExtra("imageList", mPicList as Serializable)
            intent.putExtra("imageIndex", imageIndex)
            startActivity(intent)
        }

        iv_dynamic_mine_show_tip_hide.setOnClickListener {
            iv_dynamic_mine_show_tip.visibility = View.GONE
            SPStaticUtils.put(Constant.HIDE_REPORT_TIP, true)
        }

        rl_dynamic_mine_show_like.setOnClickListener {
            val intent = Intent(this, DynamicMineLikeActivity::class.java)
            intent.putExtra("trendId", id)
            startActivity(intent)
        }


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

        eet_emoji_mine_edit.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {


                    if (System.currentTimeMillis() - lastClickTime >= delayTime) {
                        if (KeyboardUtils.isSoftInputVisible(this@DynamicMineShowActivity)) {
                            x = false
                            KeyboardUtils.hideSoftInput(this@DynamicMineShowActivity)
                        }
                        lastClickTime = System.currentTimeMillis();
                        var content = eet_emoji_mine_edit.text.toString().trim { it <= ' ' }
                        eet_emoji_mine_edit.setText("")
                        ll_emoji_mine_container.visibility = View.GONE
                        switchSendMode(mode, trendsId, hostUid, threeId,oneLevelId, firstUid, lastUid, content)
                    } else {
                        ToastUtils.showShort("点击太频繁了，请稍后再评论")
                    }
                }
                return false
            }
        })

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

                    trendsId = info.id
                    hostUid = info.user_id.toInt()
                    threeId = SPStaticUtils.getInt(Constant.USER_ID, 13)

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
                    ToastUtils.showShort("重新加载数据")
                    mCommentOneList[mItem].all += 1
                    mCommentOneList[mItem].total += 1

                    adapter.notifyDataSetChanged()

                    trendsId = info.id
                    hostUid = info.user_id.toInt()
                    threeId = SPStaticUtils.getInt(Constant.USER_ID, 13)

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

    override fun onDoCheckTrendSuccess(checkTrendBean: CheckTrendBean) {

        info = checkTrendBean.data.list[0]
        val image = checkTrendBean.data.imgs

        trendsId = info.id
        hostUid = info.user_id.toInt()
        threeId = SPStaticUtils.getInt(Constant.USER_ID, 13)


        Glide.with(applicationContext).load(info.headface).into(riv_dynamic_mine_show_heard_avatar)
        riv_dynamic_mine_show_heard_name.text = info.nick

        Glide.with(applicationContext).load(info.headface).into(riv_dynamic_mine_show_avatar)
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

        tv_dynamic_mine_show_location.text = info.position

        if (info.discuss_count == null) {
            tv_dynamic_mine_show_comment.text = 0.toString()
        } else {
            tv_dynamic_mine_show_comment.text = info.discuss_count.toString()
        }

        if (info.like_count == null) {
            tv_dynamic_mine_show_like.text = 0.toString()
        } else {
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
                ll_dynamic_mine_show_one.visibility = View.VISIBLE
                ll_dynamic_mine_show_two.visibility = View.VISIBLE
                ll_dynamic_mine_show_three.visibility = View.VISIBLE

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
                        iv_dynamic_mine_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[0])
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
                        iv_dynamic_mine_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[0])
                            .into(iv_dynamic_mine_show_one)
                        iv_dynamic_mine_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[1])
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
                        iv_dynamic_mine_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[0])
                            .into(iv_dynamic_mine_show_one)
                        iv_dynamic_mine_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[1])
                            .into(iv_dynamic_mine_show_two)
                        iv_dynamic_mine_show_three.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[2])
                            .into(iv_dynamic_mine_show_three)
                        iv_dynamic_mine_show_four.visibility = View.GONE
                        iv_dynamic_mine_show_five.visibility = View.GONE
                        iv_dynamic_mine_show_six.visibility = View.GONE
                        iv_dynamic_mine_show_seven.visibility = View.GONE
                        iv_dynamic_mine_show_eight.visibility = View.GONE
                        iv_dynamic_mine_show_nine.visibility = View.GONE
                    }
                    4 -> {
                        iv_dynamic_mine_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[0])
                            .into(iv_dynamic_mine_show_one)
                        iv_dynamic_mine_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[1])
                            .into(iv_dynamic_mine_show_two)
                        iv_dynamic_mine_show_three.visibility = View.INVISIBLE
                        iv_dynamic_mine_show_four.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[2])
                            .into(iv_dynamic_mine_show_four)
                        iv_dynamic_mine_show_five.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[3])
                            .into(iv_dynamic_mine_show_five)
                        iv_dynamic_mine_show_six.visibility = View.INVISIBLE
                        iv_dynamic_mine_show_seven.visibility = View.GONE
                        iv_dynamic_mine_show_eight.visibility = View.GONE
                        iv_dynamic_mine_show_nine.visibility = View.GONE
                    }
                    5 -> {
                        iv_dynamic_mine_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[0])
                            .into(iv_dynamic_mine_show_one)
                        iv_dynamic_mine_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[1])
                            .into(iv_dynamic_mine_show_two)
                        iv_dynamic_mine_show_three.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[2])
                            .into(iv_dynamic_mine_show_three)
                        iv_dynamic_mine_show_four.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[3])
                            .into(iv_dynamic_mine_show_four)
                        iv_dynamic_mine_show_five.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[4])
                            .into(iv_dynamic_mine_show_five)
                        iv_dynamic_mine_show_six.visibility = View.INVISIBLE
                        iv_dynamic_mine_show_seven.visibility = View.GONE
                        iv_dynamic_mine_show_eight.visibility = View.GONE
                        iv_dynamic_mine_show_nine.visibility = View.GONE
                    }
                    6 -> {
                        iv_dynamic_mine_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[0])
                            .into(iv_dynamic_mine_show_one)
                        iv_dynamic_mine_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[1])
                            .into(iv_dynamic_mine_show_two)
                        iv_dynamic_mine_show_three.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[2])
                            .into(iv_dynamic_mine_show_three)
                        iv_dynamic_mine_show_four.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[3])
                            .into(iv_dynamic_mine_show_four)
                        iv_dynamic_mine_show_five.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[4])
                            .into(iv_dynamic_mine_show_five)
                        iv_dynamic_mine_show_six.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[5])
                            .into(iv_dynamic_mine_show_six)
                        iv_dynamic_mine_show_seven.visibility = View.GONE
                        iv_dynamic_mine_show_eight.visibility = View.GONE
                        iv_dynamic_mine_show_nine.visibility = View.GONE
                    }
                    7 -> {
                        iv_dynamic_mine_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[0])
                            .into(iv_dynamic_mine_show_one)
                        iv_dynamic_mine_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[1])
                            .into(iv_dynamic_mine_show_two)
                        iv_dynamic_mine_show_three.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[2])
                            .into(iv_dynamic_mine_show_three)
                        iv_dynamic_mine_show_four.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[3])
                            .into(iv_dynamic_mine_show_four)
                        iv_dynamic_mine_show_five.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[4])
                            .into(iv_dynamic_mine_show_five)
                        iv_dynamic_mine_show_six.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[5])
                            .into(iv_dynamic_mine_show_six)
                        iv_dynamic_mine_show_seven.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[6])
                            .into(iv_dynamic_mine_show_seven)
                        iv_dynamic_mine_show_eight.visibility = View.INVISIBLE
                        iv_dynamic_mine_show_nine.visibility = View.INVISIBLE
                    }
                    8 -> {
                        iv_dynamic_mine_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[0])
                            .into(iv_dynamic_mine_show_one)
                        iv_dynamic_mine_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[1])
                            .into(iv_dynamic_mine_show_two)
                        iv_dynamic_mine_show_three.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[2])
                            .into(iv_dynamic_mine_show_three)
                        iv_dynamic_mine_show_four.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[3])
                            .into(iv_dynamic_mine_show_four)
                        iv_dynamic_mine_show_five.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[4])
                            .into(iv_dynamic_mine_show_five)
                        iv_dynamic_mine_show_six.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[5])
                            .into(iv_dynamic_mine_show_six)
                        iv_dynamic_mine_show_seven.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[6])
                            .into(iv_dynamic_mine_show_seven)
                        iv_dynamic_mine_show_eight.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[7])
                            .into(iv_dynamic_mine_show_eight)
                        iv_dynamic_mine_show_nine.visibility = View.INVISIBLE
                    }
                    9 -> {
                        iv_dynamic_mine_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[0])
                            .into(iv_dynamic_mine_show_one)
                        iv_dynamic_mine_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[1])
                            .into(iv_dynamic_mine_show_two)
                        iv_dynamic_mine_show_three.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[2])
                            .into(iv_dynamic_mine_show_three)
                        iv_dynamic_mine_show_four.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[3])
                            .into(iv_dynamic_mine_show_four)
                        iv_dynamic_mine_show_five.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[4])
                            .into(iv_dynamic_mine_show_five)
                        iv_dynamic_mine_show_six.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[5])
                            .into(iv_dynamic_mine_show_six)
                        iv_dynamic_mine_show_seven.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[6])
                            .into(iv_dynamic_mine_show_seven)
                        iv_dynamic_mine_show_eight.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[7])
                            .into(iv_dynamic_mine_show_eight)
                        iv_dynamic_mine_show_nine.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(mPicList[8])
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

                Glide.with(applicationContext).load(info.video_url).into(iv_dynamic_mine_show_video)

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

    }

    override fun onDoCheckTrendError() {

    }

    class DynamicEditDialog(context: Context) : FullScreenPopupView(context),
        IDoDeleteTrendCallback {

        private lateinit var doDeleteTrendPresent: doDeleteTrendPresentImpl

        // 是否删除动态，弹窗消失时结束
        private var isFinish = false

        override fun getImplLayoutId(): Int = R.layout.dialog_dynamic_mine_edit

        override fun onCreate() {
            super.onCreate()

            doDeleteTrendPresent = doDeleteTrendPresentImpl.getsInstance()
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
        lastUid = SPStaticUtils.getInt(Constant.USER_ID, 12)

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

        Log.i("guo",  "one_level_uid : ${mCommentOneList[positionOne].list.one_level_uid}")

        mode = 1
        trendsId = mCommentOneList[positionOne].list.trends_id
        hostUid = mCommentOneList[positionOne].list.host_uid
        threeId = mCommentOneList[positionOne].list.id
        oneLevelId = mCommentOneList[positionOne].list.one_level_uid
        firstUid = mCommentOneList[positionOne].list.two_last_uid
        lastUid = SPStaticUtils.getInt(Constant.USER_ID, 12)

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
        firstUid = mCommentOneList[positionOne].twoList[two].id
        lastUid = SPStaticUtils.getInt(Constant.USER_ID, 12)

    }

    override fun onChildReplyAvatarClick(positionOne: Int, two: Int) {
        // 子评论回复的用户名点击事件
        ToastUtils.showShort(" 子评论回复用户名点击 ${positionOne}/${two}")
    }

}