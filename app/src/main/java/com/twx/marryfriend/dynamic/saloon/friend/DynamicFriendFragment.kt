package com.twx.marryfriend.dynamic.saloon.friend

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.dynamic.*
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.net.callback.dynamic.IDoLikeCancelCallback
import com.twx.marryfriend.net.callback.dynamic.IDoLikeClickCallback
import com.twx.marryfriend.net.callback.dynamic.IGetTrendFocusCallback
import com.twx.marryfriend.net.impl.dynamic.doLikeCancelPresentImpl
import com.twx.marryfriend.net.impl.dynamic.doLikeClickPresentImpl
import com.twx.marryfriend.net.impl.dynamic.getTrendFocusPresentImpl
import com.twx.marryfriend.dynamic.preview.image.ImagePreviewActivity
import com.twx.marryfriend.dynamic.preview.video.VideoPreviewActivity
import com.twx.marryfriend.dynamic.saloon.adapter.SaloonFocusAdapter
import com.twx.marryfriend.dynamic.show.others.DynamicOtherShowActivity
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.message.ChatActivity
import com.twx.marryfriend.mine.user.UserActivity
import com.twx.marryfriend.utils.AnimalUtils
import kotlinx.android.synthetic.main.fragment_dynamic_friend.*
import java.io.Serializable
import java.util.*

class DynamicFriendFragment : Fragment(), IGetTrendFocusCallback, IDoLikeClickCallback,
    IDoLikeCancelCallback {

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

    // 关注与点赞数据
    private var mDiyList: MutableList<LikeBean> = arrayListOf()


    private var mTrendList: MutableList<TrendFocusList> = arrayListOf()

    private lateinit var adapter: SaloonFocusAdapter

    private lateinit var getTrendFocusPresent: getTrendFocusPresentImpl
    private lateinit var doLikeClickPresent: doLikeClickPresentImpl
    private lateinit var doLikeCancelPresent: doLikeCancelPresentImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_dynamic_friend, container, false)
    }

    fun newInstance(context: Context): DynamicFriendFragment {
        val fragment = DynamicFriendFragment()
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

        getTrendFocusPresent = getTrendFocusPresentImpl.getsInstance()
        getTrendFocusPresent.registerCallback(this)

        doLikeClickPresent = doLikeClickPresentImpl.getsInstance()
        doLikeClickPresent.registerCallback(this)

        doLikeCancelPresent = doLikeCancelPresentImpl.getsInstance()
        doLikeCancelPresent.registerCallback(this)



        adapter = SaloonFocusAdapter(mTrendList, mDiyList)

        rv_dynamic_focus_container.adapter = adapter
        rv_dynamic_focus_container.layoutManager = LinearLayoutManager(context)

        srl_dynamic_focus_refresh.setRefreshHeader(ClassicsHeader(requireContext()));
        srl_dynamic_focus_refresh.setRefreshFooter(ClassicsFooter(requireContext()));

    }

    private fun initData() {
        getTrendFocus(mode, max, min)
    }

    private fun initPresent() {

    }

    private fun initEvent() {

        srl_dynamic_focus_refresh.setOnRefreshListener {
            // 刷新数据
            mode = "first"
            max = 3
            min = 2
            getTrendFocus(mode, max, min)
            srl_dynamic_focus_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        srl_dynamic_focus_refresh.setOnLoadMoreListener {
            // 刷新数据
            mode = "down"
            getTrendFocus(mode, max, min)
            srl_dynamic_focus_refresh.finishLoadMore(2000/*,false*/);//传入false表示刷新失败
        }

        adapter.setOnVideoClickListener(object : SaloonFocusAdapter.OnVideoClickListener {
            override fun onVideoClick(v: View?, position: Int) {

                startActivity(VideoPreviewActivity.getIntent(mContext,
                    mTrendList[position].video_url,
                    mTrendList[position].nick))
            }
        })

        adapter.setOnItemClickListener(object : SaloonFocusAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                val intent = Intent(context, DynamicOtherShowActivity::class.java)
                intent.putExtra("trendId", mTrendList[position].id)
                intent.putExtra("usersId", mTrendList[position].user_id.toInt())
                intent.putExtra("mode", 1)
                startActivity(intent)
            }
        })

        adapter.setOnAvatarClickListener(object : SaloonFocusAdapter.OnAvatarClickListener {
            override fun onAvatarClick(v: View?, position: Int) {
                ToastUtils.showShort("头像,进入资料详情界面")

                startActivity(FriendInfoActivity.getIntent(requireContext(),
                    mTrendList[position].user_id.toInt()))

            }
        })

        adapter.setOnFocusClickListener(object : SaloonFocusAdapter.OnFocusClickListener {
            override fun onFocusClick(v: View?, position: Int) {
                ToastUtils.showShort("消息，进入消息界面")

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
        })

        adapter.setOnLikeClickListener(object : SaloonFocusAdapter.OnLikeClickListener {
            override fun onLikeClick(v: View?, position: Int) {
                ToastUtils.showShort("点赞，给该用户点赞")
            }
        })

        adapter.setOnCommentClickListener(object : SaloonFocusAdapter.OnCommentClickListener {
            override fun onCommentClick(v: View?, position: Int) {
                ToastUtils.showShort("评论，跳转到动态详情界面，显示评论")
            }
        })

        adapter.setOnOneClickListener(object : SaloonFocusAdapter.OnOneClickListener {
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

        adapter.setOnTwoClickListener(object : SaloonFocusAdapter.OnTwoClickListener {
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

        adapter.setOnThreeClickListener(object : SaloonFocusAdapter.OnThreeClickListener {
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

        adapter.setOnFourClickListener(object : SaloonFocusAdapter.OnFourClickListener {
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

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        imageIndex)
                })
            }
        })

        adapter.setOnFiveClickListener(object : SaloonFocusAdapter.OnFiveClickListener {
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

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        imageIndex)
                })
            }
        })


        adapter.setOnSixClickListener(object : SaloonFocusAdapter.OnSixClickListener {
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

        adapter.setOnSevenClickListener(object : SaloonFocusAdapter.OnSevenClickListener {
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

        adapter.setOnEightClickListener(object : SaloonFocusAdapter.OnEightClickListener {
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

        adapter.setOnNineClickListener(object : SaloonFocusAdapter.OnNineClickListener {
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

        adapter.setOnLikeClickListener(object : SaloonFocusAdapter.OnLikeClickListener {
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
                        doLikeCancelClick(mTrendList[position].id, mTrendList[position].user_id,
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

    }

    // 获取动态列表
    private fun getTrendFocus(mode: String, max: Int, min: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.UP_DOWN] = mode
        map[Contents.MAX_ID] = max.toString()
        map[Contents.MIN_ID] = min.toString()
        map[Contents.SIZE] = 10.toString()
        getTrendFocusPresent.getTrendFocus(map)
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


    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoLikeClickSuccess(likeClickBean: LikeClickBean?) {
        // 点赞
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

    override fun onGetTrendFocusSuccess(trendFocusBean: TrendFocusBean) {

        if (trendFocusBean.data.list.isNotEmpty()) {

            srl_dynamic_focus_refresh.visibility = View.VISIBLE
            ll_dynamic_focus_empty.visibility = View.GONE

            val mIdList: MutableList<Int> = arrayListOf()

            if (mode == "first") {
                mTrendList.clear()
                mDiyList.clear()
            }

            for (i in 0.until(trendFocusBean.data.list.size)) {
                mTrendList.add(trendFocusBean.data.list[i])
                mIdList.add(trendFocusBean.data.list[i].id)

                val focus = true
                val like = trendFocusBean.data.list[i].guest_uid != null

                mDiyList.add(LikeBean(focus, like, trendFocusBean.data.list[i].like_count))

            }

            max = Collections.max(mIdList)
            min = Collections.min(mIdList)

            adapter.notifyDataSetChanged()

            srl_dynamic_focus_refresh.finishRefresh(true)
            srl_dynamic_focus_refresh.finishLoadMore(true)

        } else {
            srl_dynamic_focus_refresh.finishRefresh(true)
            srl_dynamic_focus_refresh.finishLoadMore(true)
        }

    }

    override fun onGetTrendFocusError() {
        srl_dynamic_focus_refresh.finishRefresh(false)
        srl_dynamic_focus_refresh.finishLoadMore(false)
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

}