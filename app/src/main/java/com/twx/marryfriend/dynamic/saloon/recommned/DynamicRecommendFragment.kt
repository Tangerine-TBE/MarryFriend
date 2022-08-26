package com.twx.marryfriend.dynamic.saloon.recommned

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.dynamic.*
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.preview.image.ImagePreviewActivity
import com.twx.marryfriend.dynamic.saloon.adapter.SaloonAdapter
import com.twx.marryfriend.dynamic.saloon.tip.TipsActivity
import com.twx.marryfriend.dynamic.show.others.DynamicOtherShowActivity
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.message.ChatActivity
import com.twx.marryfriend.mine.user.UserActivity
import com.twx.marryfriend.net.callback.dynamic.*
import com.twx.marryfriend.net.impl.dynamic.*
import com.twx.marryfriend.utils.AnimalUtils
import kotlinx.android.synthetic.main.activity_dynamic_mine_like.*
import kotlinx.android.synthetic.main.fragment_dynamic_recommend.*
import java.io.Serializable
import java.util.*

class DynamicRecommendFragment : Fragment(), IGetTrendSaloonCallback, IDoLikeClickCallback,
    IDoLikeCancelCallback, IDoPlusFocusCallback, IDoCancelFocusCallback, IGetTotalCountCallback,
    SaloonAdapter.OnItemClickListener {

    // 上次点击时间
    private var lastClickTime = 0L

    // 两次点击间隔时间（毫秒）
    private val delayTime = 3000

    // 数据加载模式
    private var mode = "first"

    // 当前页的最大动态id
    private var max = 3

    // 当前页的最小动态id
    private var min = 2

    private lateinit var mContext: Context

    // 大图展示时进入时应该展示点击的那张图片
    private var imageIndex = 0


    // 点赞时选择的position
    private var mLikePosition: Int = 0


    // 未读评论数据
    private var commentSum = 0

    // 未读评论数据
    private var likeSum = 0

    // 关注与点赞数据
    private var mDiyList: MutableList<LikeBean> = arrayListOf()

    private var mTrendList: MutableList<TrendSaloonList> = arrayListOf()

    private lateinit var adapter: SaloonAdapter

    private lateinit var getTrendSaloonPresent: getTrendSaloonPresentImpl
    private lateinit var doLikeClickPresent: doLikeClickPresentImpl
    private lateinit var doLikeCancelPresent: doLikeCancelPresentImpl
    private lateinit var doPlusFocusPresent: doPlusFocusPresentImpl
    private lateinit var doCancelFocusPresent: doCancelFocusPresentImpl
    private lateinit var getTotalCountPresent: getTotalCountPresentImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_dynamic_recommend, container, false)
    }

    fun newInstance(context: Context): DynamicRecommendFragment {
        val fragment = DynamicRecommendFragment()
        fragment.mContext = context
        return fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initPresent()
        initEvent()
    }

    private fun initView() {

        getTrendSaloonPresent = getTrendSaloonPresentImpl.getsInstance()
        getTrendSaloonPresent.registerCallback(this)

        doLikeClickPresent = doLikeClickPresentImpl.getsInstance()
        doLikeClickPresent.registerCallback(this)

        doLikeCancelPresent = doLikeCancelPresentImpl.getsInstance()
        doLikeCancelPresent.registerCallback(this)

        doPlusFocusPresent = doPlusFocusPresentImpl.getsInstance()
        doPlusFocusPresent.registerCallback(this)

        doCancelFocusPresent = doCancelFocusPresentImpl.getsInstance()
        doCancelFocusPresent.registerCallback(this)

        getTotalCountPresent = getTotalCountPresentImpl.getsInstance()
        getTotalCountPresent.registerCallback(this)

        AnimalUtils.getAnimal(iv_like_animal)



        adapter = SaloonAdapter(mTrendList, mDiyList)
        adapter.setOnItemClickListener(this)

        rv_dynamic_recommend_container.adapter = adapter
        rv_dynamic_recommend_container.layoutManager = LinearLayoutManager(context)

        srl_dynamic_recommend_refresh.setRefreshHeader(ClassicsHeader(requireContext()));
        srl_dynamic_recommend_refresh.setRefreshFooter(ClassicsFooter(requireContext()));

    }

    private fun initData() {

        getTrendSaloon(mode, max, min)

        getTotalCount()

    }

    private fun initPresent() {

    }

    private fun initEvent() {

        srl_dynamic_recommend_refresh.setOnRefreshListener {
            // 刷新数据
            mode = "first"
            max = 3
            min = 2
            getTrendSaloon(mode, max, min)
            getTotalCount()
            srl_dynamic_recommend_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        srl_dynamic_recommend_refresh.setOnLoadMoreListener {
            // 刷新数据
            mode = "down"
            getTrendSaloon(mode, max, min)
            srl_dynamic_recommend_refresh.finishLoadMore(2000/*,false*/);//传入false表示刷新失败
        }


        rl_dynamic_tips.setOnClickListener {

            rl_dynamic_tips.visibility = View.GONE

            startActivity(context?.let { it1 -> TipsActivity.getIntent(it1, commentSum, likeSum) })

            Log.i("guo", "commentSum :$commentSum , likeSum :$likeSum")

        }

        adapter.setOnVideoClickListener(object : SaloonAdapter.OnVideoClickListener {
            override fun onVideoClick(v: View?, position: Int) {
                val intent = Intent(mContext,
                    com.twx.marryfriend.dynamic.preview.video.VideoPreviewActivity::class.java)
                intent.putExtra("videoUrl", mTrendList[position].video_url)
                intent.putExtra("name", mTrendList[position].nick)
                startActivity(intent)
            }
        })


        adapter.setOnAvatarClickListener(object : SaloonAdapter.OnAvatarClickListener {
            override fun onAvatarClick(v: View?, position: Int) {
                ToastUtils.showShort("头像,进入资料详情界面")

                startActivity(FriendInfoActivity.getIntent(requireContext(),
                    mTrendList[position].user_id.toInt()))

            }
        })

        adapter.setOnFocusClickListener(object : SaloonAdapter.OnFocusClickListener {
            override fun onFocusClick(v: View?, position: Int) {
                ToastUtils.showShort("关注，需要判断模式，未关注的时候，和关注的时候分开判断")
            }
        })

        adapter.setOnLikeClickListener(object : SaloonAdapter.OnLikeClickListener {
            override fun onLikeClick(v: View?, position: Int) {
                ToastUtils.showShort("点赞，给该用户点赞")
            }
        })

        adapter.setOnCommentClickListener(object : SaloonAdapter.OnCommentClickListener {
            override fun onCommentClick(v: View?, position: Int) {
                val intent = Intent(context, DynamicOtherShowActivity::class.java)
                intent.putExtra("trendId", mTrendList[position].id)
                intent.putExtra("usersId", mTrendList[position].user_id.toInt())
                val mode = if (mDiyList[position].focus) {
                    1
                } else {
                    0
                }
                intent.putExtra("mode", mode)
                startActivity(intent)
            }
        })

        adapter.setOnOneClickListener(object : SaloonAdapter.OnOneClickListener {
            override fun onOneClick(v: View?, position: Int) {
                ToastUtils.showShort("one")
                imageIndex = 0

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        imageIndex)
                })
            }
        })

        adapter.setOnTwoClickListener(object : SaloonAdapter.OnTwoClickListener {
            override fun onTwoClick(v: View?, position: Int) {
                ToastUtils.showShort("two")
                imageIndex = 1

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        imageIndex)
                })
            }
        })

        adapter.setOnThreeClickListener(object : SaloonAdapter.OnThreeClickListener {
            override fun onThreeClick(v: View?, position: Int) {
                ToastUtils.showShort("three")
                imageIndex = 2

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        imageIndex)
                })
            }
        })

        adapter.setOnFourClickListener(object : SaloonAdapter.OnFourClickListener {
            override fun onFourClick(v: View?, position: Int) {
                ToastUtils.showShort("four")
                imageIndex = 3

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                // 图片展示问题，需要调整一下imageIndex

                if (images.size == 4) {
                    imageIndex = 2
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        imageIndex)
                })
            }
        })

        adapter.setOnFiveClickListener(object : SaloonAdapter.OnFiveClickListener {
            override fun onFiveClick(v: View?, position: Int) {
                ToastUtils.showShort("five")
                imageIndex = 4


                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }

                }

                // 图片展示问题，需要调整一下imageIndex

                if (images.size == 4) {
                    imageIndex = 3
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        imageIndex)
                })
            }
        })

        adapter.setOnSixClickListener(object : SaloonAdapter.OnSixClickListener {
            override fun onSixClick(v: View?, position: Int) {
                ToastUtils.showShort("six")
                imageIndex = 5

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        imageIndex)
                })
            }
        })

        adapter.setOnSevenClickListener(object : SaloonAdapter.OnSevenClickListener {
            override fun onSevenClick(v: View?, position: Int) {
                ToastUtils.showShort("seven")
                imageIndex = 6

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        imageIndex)
                })
            }
        })

        adapter.setOnEightClickListener(object : SaloonAdapter.OnEightClickListener {
            override fun onEightClick(v: View?, position: Int) {
                ToastUtils.showShort("eight")
                imageIndex = 7

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        imageIndex)
                })
            }
        })

        adapter.setOnNineClickListener(object : SaloonAdapter.OnNineClickListener {
            override fun onNineClick(v: View?, position: Int) {
                ToastUtils.showShort("nine")
                imageIndex = 8

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        imageIndex)
                })
            }
        })

        adapter.setOnLikeClickListener(object : SaloonAdapter.OnLikeClickListener {
            override fun onLikeClick(v: View?, position: Int) {

                // 点赞， 此时需要验证是否上传头像
                if (SPStaticUtils.getString(Constant.ME_AVATAR,
                        "") != "" || SPStaticUtils.getString(Constant.ME_AVATAR_AUDIT, "") != ""
                ) {
                    mLikePosition = position

                    if (!mDiyList[position].like) {
                        // 点赞

                        if (mTrendList[position].user_id != SPStaticUtils.getString(Constant.USER_ID,
                                "13")
                        ) {
                            mDiyList[position].anim = true


                            AnimalUtils.getAnimal(v as ImageView)


                            doLikeClick(mTrendList[position].id,
                                mTrendList[position].user_id,
                                SPStaticUtils.getString(Constant.USER_ID, "13"))
                        } else {
                            ToastUtils.showShort("不能给自己点赞")
                        }

                    } else {
                        // 取消赞
                        doLikeCancelClick(mTrendList[position].id,
                            mTrendList[position].user_id,
                            SPStaticUtils.getString(Constant.USER_ID, "13"))
                    }
                } else {
                    XPopup.Builder(context)
                        .dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true)
                        .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                        .asCustom(AvatarDialog(requireContext()))
                        .show()
                }
            }
        })

        adapter.setOnFocusClickListener(object : SaloonAdapter.OnFocusClickListener {
            override fun onFocusClick(v: View?, position: Int) {
                if (!mDiyList[position].focus) {
                    // 点关注
                    mDiyList[position].focus = true
                    doPlusFocus(SPStaticUtils.getString(Constant.USER_ID, "13"),
                        mTrendList[position].user_id)
                } else {
                    ToastUtils.showShort("消息界面")

                    val identity = mTrendList[position].identity_status == 1

                    startActivity(context?.let {
                        ChatActivity.getIntent(
                            it,
                            mTrendList[position].user_id,
                            mTrendList[position].nick,
                            mTrendList[position].headface,
                            identity
                        )
                    })
                }
                adapter.notifyDataSetChanged()
            }
        })
    }

    // 获取消息提醒列表
    private fun getTotalCount() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getTotalCountPresent.getTotalCount(map)
    }

    // 获取动态列表
    private fun getTrendSaloon(mode: String, max: Int, min: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.UP_DOWN] = mode
        map[Contents.MAX_ID] = max.toString()
        map[Contents.MIN_ID] = min.toString()
        map[Contents.SIZE] = 10.toString()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getTrendSaloonPresent.getTrendSaloon(map)
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

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetTotalCountSuccess(totalCountBean: TotalCountBean?) {
        if (totalCountBean != null) {
            if (totalCountBean.code == 200) {
                rl_dynamic_tips.visibility = View.VISIBLE
                tv_dynamic_tips_count.text =
                    "${totalCountBean.data.discuss.toInt() + totalCountBean.data.like}条新消息"

                commentSum = totalCountBean.data.discuss.toInt()
                likeSum = totalCountBean.data.like.toInt()
            }
        }
    }

    override fun onGetTotalCountError() {

    }

    override fun onDoPlusFocusSuccess(plusFocusBean: PlusFocusBean?) {
        if (plusFocusBean != null) {
            if (plusFocusBean.code == 200) {
                ToastUtils.showShort("关注成功")

            }
        }
    }

    override fun onDoPlusFocusError() {
    }

    override fun onDoCancelFocusSuccess(cancelFocusBean: CancelFocusBean?) {
    }

    override fun onDoCancelFocusError() {
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
    }

    override fun onDoLikeClickSuccess(likeClickBean: LikeClickBean?) {
        // 添加赞
        if (likeClickBean != null) {
            if (likeClickBean.code == 200) {

                mDiyList[mLikePosition].like = true
                mDiyList[mLikePosition].likeCount++
                adapter.notifyDataSetChanged()
            } else {
                ToastUtils.showShort(likeClickBean.msg)
            }
        }

    }

    override fun onDoLikeClickError() {

    }

    override fun onGetTrendSaloonSuccess(trendSaloonBean: TrendSaloonBean) {

        if (trendSaloonBean.data.list.isNotEmpty()) {

            srl_dynamic_recommend_refresh.visibility = View.VISIBLE
            ll_dynamic_recommend_empty.visibility = View.GONE

            val mIdList: MutableList<Int> = arrayListOf()

            if (mode == "first") {
                mTrendList.clear()
                mDiyList.clear()
            }

            for (i in 0.until(trendSaloonBean.data.list.size)) {
                mTrendList.add(trendSaloonBean.data.list[i])
                mIdList.add(trendSaloonBean.data.list[i].id)

                val focus = trendSaloonBean.data.list[i].focus_uid != null

                val like = trendSaloonBean.data.list[i].guest_uid != null

                mDiyList.add(LikeBean(focus, like, trendSaloonBean.data.list[i].like_count))

            }

            max = Collections.max(mIdList)
            min = Collections.min(mIdList)

            adapter.notifyDataSetChanged()

            srl_dynamic_recommend_refresh.finishRefresh(true)
            srl_dynamic_recommend_refresh.finishLoadMore(true)

        } else {
            srl_dynamic_recommend_refresh.finishRefresh(true)
            srl_dynamic_recommend_refresh.finishLoadMore(true)
        }
    }

    override fun onGetTrendSaloonError() {
        srl_dynamic_recommend_refresh.finishRefresh(false)
        srl_dynamic_recommend_refresh.finishLoadMore(false)
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
                ToastUtils.showShort("跳转到资料填写界面")
                startActivity(Intent(context, UserActivity::class.java))
            }

        }

        override fun onDismiss() {
            super.onDismiss()
        }

    }

    override fun onItemClick(v: View?, position: Int) {
        val intent = Intent(context, DynamicOtherShowActivity::class.java)
        intent.putExtra("trendId", mTrendList[position].id)
        intent.putExtra("usersId", mTrendList[position].user_id.toInt())
        val mode = if (mDiyList[position].focus) {
            1
        } else {
            0
        }
        intent.putExtra("mode", mode)
        startActivity(intent)
    }

    override fun onTextClick(v: View?, position: Int) {
        val intent = Intent(context, DynamicOtherShowActivity::class.java)
        intent.putExtra("trendId", mTrendList[position].id)
        intent.putExtra("usersId", mTrendList[position].user_id.toInt())
        val mode = if (mDiyList[position].focus) {
            1
        } else {
            0
        }
        intent.putExtra("mode", mode)
        startActivity(intent)
    }

}