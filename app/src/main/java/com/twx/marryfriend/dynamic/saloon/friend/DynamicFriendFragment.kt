package com.twx.marryfriend.dynamic.saloon.friend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.twx.marryfriend.ImHelper
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.dynamic.*
import com.twx.marryfriend.bean.vip.VipGifEnum
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.preview.image.ImagePreviewActivity
import com.twx.marryfriend.dynamic.preview.video.VideoPreviewActivity
import com.twx.marryfriend.dynamic.saloon.adapter.SaloonFocusAdapter
import com.twx.marryfriend.dynamic.show.others.DynamicOtherShowActivity
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.message.ChatActivity
import com.twx.marryfriend.mine.user.UserActivity
import com.twx.marryfriend.net.callback.dynamic.*
import com.twx.marryfriend.net.impl.dynamic.*
import com.twx.marryfriend.utils.AnimalUtils
import com.twx.marryfriend.vip.VipActivity
import kotlinx.android.synthetic.main.fragment_dynamic_friend.*
import java.util.*

class DynamicFriendFragment : Fragment(), IGetTrendFocusCallback, IDoFocusLikeClickCallback,
    IDoFocusLikeCancelCallback, SaloonFocusAdapter.OnItemClickListener {

    // 数据加载模式
    private var mode = "first"

    // 当前页的最大动态id
    private var max = 3

    // 当前页的最小动态id
    private var min = 2

    private lateinit var mContext: Context

    // 大图展示时进入时应该展示点击的那张图片
    private var mFocusImageIndex = 0

    // 点赞时选择的position
    private var mFocusLikePosition: Int = 0

    // 关注与点赞数据
    private var mFocusDiyList: MutableList<LikeBean> = arrayListOf()

    private var mFocusTrendList: MutableList<TrendFocusList> = arrayListOf()

    private lateinit var mFocusAdapter: SaloonFocusAdapter

    private lateinit var getTrendFocusPresent: getTrendFocusPresentImpl
    private lateinit var doFocusLikeClickPresent: doFocusLikeClickPresentImpl
    private lateinit var doFocusLikeCancelPresent: doFocusLikeCancelPresentImpl

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

        doFocusLikeClickPresent = doFocusLikeClickPresentImpl.getsInstance()
        doFocusLikeClickPresent.registerCallback(this)

        doFocusLikeCancelPresent = doFocusLikeCancelPresentImpl.getsInstance()
        doFocusLikeCancelPresent.registerCallback(this)


        mFocusAdapter = SaloonFocusAdapter(mFocusTrendList, mFocusDiyList)
        mFocusAdapter.setOnItemClickListener(this)

        rv_dynamic_focus_container.adapter = mFocusAdapter
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

        mFocusAdapter.setOnVideoClickListener(object : SaloonFocusAdapter.OnVideoClickListener {
            override fun onVideoClick(v: View?, position: Int) {

                for (i in 0.until(mFocusDiyList.size)) {
                    mFocusDiyList[i].anim = false
                }
                mFocusAdapter.notifyDataSetChanged()

                startActivity(VideoPreviewActivity.getIntent(mContext,
                    mFocusTrendList[position].video_url,
                    mFocusTrendList[position].nick))
            }
        })

        mFocusAdapter.setOnAvatarClickListener(object : SaloonFocusAdapter.OnAvatarClickListener {
            override fun onAvatarClick(v: View?, position: Int) {
                ToastUtils.showShort("头像,进入资料详情界面")

                for (i in 0.until(mFocusDiyList.size)) {
                    mFocusDiyList[i].anim = false
                }
                mFocusAdapter.notifyDataSetChanged()

                startActivity(FriendInfoActivity.getIntent(
                    requireContext(),
                    mFocusTrendList[position].user_id.toInt()
                ))

            }
        })

        mFocusAdapter.setOnFocusClickListener(object : SaloonFocusAdapter.OnFocusClickListener {
            override fun onFocusClick(v: View?, position: Int) {

                for (i in 0.until(mFocusDiyList.size)) {
                    mFocusDiyList[i].anim = false
                }
                mFocusAdapter.notifyDataSetChanged()

                val identity = mFocusTrendList[position].identity_status == 1


                if (SPStaticUtils.getInt(Constant.USER_VIP_LEVEL, 0) == 0) {
                    startActivity(context?.let {
                        VipActivity.getVipIntent(it,
                            mFocusTrendList[position].user_id.toInt(),
                            VipGifEnum.Message)
                    })
                } else {

                    startActivity(context?.let {
                        ChatActivity.getIntent(
                            it,
                            mFocusTrendList[position].user_id,
                            identity
                        )
                    })
                }
            }
        })

        mFocusAdapter.setOnLikeClickListener(object : SaloonFocusAdapter.OnLikeClickListener {
            override fun onLikeClick(v: View?, position: Int) {
                ToastUtils.showShort("点赞，给该用户点赞")
            }
        })

        mFocusAdapter.setOnCommentClickListener(object : SaloonFocusAdapter.OnCommentClickListener {
            override fun onCommentClick(v: View?, position: Int) {

                for (i in 0.until(mFocusDiyList.size)) {
                    mFocusDiyList[i].anim = false
                }
                mFocusAdapter.notifyDataSetChanged()

                val intent = Intent(context, DynamicOtherShowActivity::class.java)
                intent.putExtra("trendId", mFocusTrendList[position].id)
                intent.putExtra("usersId", mFocusTrendList[position].user_id.toInt())
                intent.putExtra("mode", 1)
                startActivityForResult(intent, 0)
            }
        })

        mFocusAdapter.setOnOneClickListener(object : SaloonFocusAdapter.OnOneClickListener {
            override fun onOneClick(v: View?, position: Int) {

                for (i in 0.until(mFocusDiyList.size)) {
                    mFocusDiyList[i].anim = false
                }
                mFocusAdapter.notifyDataSetChanged()

                mFocusImageIndex = 0

                val images: MutableList<String> =
                    mFocusTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        mFocusImageIndex)
                })
            }
        })

        mFocusAdapter.setOnTwoClickListener(object : SaloonFocusAdapter.OnTwoClickListener {
            override fun onTwoClick(v: View?, position: Int) {

                for (i in 0.until(mFocusDiyList.size)) {
                    mFocusDiyList[i].anim = false
                }
                mFocusAdapter.notifyDataSetChanged()

                mFocusImageIndex = 1

                val images: MutableList<String> =
                    mFocusTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        mFocusImageIndex)
                })
            }
        })

        mFocusAdapter.setOnThreeClickListener(object : SaloonFocusAdapter.OnThreeClickListener {
            override fun onThreeClick(v: View?, position: Int) {

                for (i in 0.until(mFocusDiyList.size)) {
                    mFocusDiyList[i].anim = false
                }
                mFocusAdapter.notifyDataSetChanged()

                mFocusImageIndex = 2

                val images: MutableList<String> =
                    mFocusTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        mFocusImageIndex)
                })
            }
        })

        mFocusAdapter.setOnFourClickListener(object : SaloonFocusAdapter.OnFourClickListener {
            override fun onFourClick(v: View?, position: Int) {

                for (i in 0.until(mFocusDiyList.size)) {
                    mFocusDiyList[i].anim = false
                }
                mFocusAdapter.notifyDataSetChanged()

                mFocusImageIndex = 3

                val images: MutableList<String> =
                    mFocusTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        mFocusImageIndex)
                })
            }
        })

        mFocusAdapter.setOnFiveClickListener(object : SaloonFocusAdapter.OnFiveClickListener {
            override fun onFiveClick(v: View?, position: Int) {

                for (i in 0.until(mFocusDiyList.size)) {
                    mFocusDiyList[i].anim = false
                }
                mFocusAdapter.notifyDataSetChanged()

                mFocusImageIndex = 4

                val images: MutableList<String> =
                    mFocusTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        mFocusImageIndex)
                })
            }
        })


        mFocusAdapter.setOnSixClickListener(object : SaloonFocusAdapter.OnSixClickListener {
            override fun onSixClick(v: View?, position: Int) {

                for (i in 0.until(mFocusDiyList.size)) {
                    mFocusDiyList[i].anim = false
                }
                mFocusAdapter.notifyDataSetChanged()

                mFocusImageIndex = 5

                val images: MutableList<String> =
                    mFocusTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        mFocusImageIndex)
                })
            }
        })

        mFocusAdapter.setOnSevenClickListener(object : SaloonFocusAdapter.OnSevenClickListener {
            override fun onSevenClick(v: View?, position: Int) {

                for (i in 0.until(mFocusDiyList.size)) {
                    mFocusDiyList[i].anim = false
                }
                mFocusAdapter.notifyDataSetChanged()

                mFocusImageIndex = 6

                val images: MutableList<String> =
                    mFocusTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        mFocusImageIndex)
                })
            }
        })

        mFocusAdapter.setOnEightClickListener(object : SaloonFocusAdapter.OnEightClickListener {
            override fun onEightClick(v: View?, position: Int) {

                for (i in 0.until(mFocusDiyList.size)) {
                    mFocusDiyList[i].anim = false
                }
                mFocusAdapter.notifyDataSetChanged()

                mFocusImageIndex = 7

                val images: MutableList<String> =
                    mFocusTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        mFocusImageIndex)
                })

            }
        })

        mFocusAdapter.setOnNineClickListener(object : SaloonFocusAdapter.OnNineClickListener {
            override fun onNineClick(v: View?, position: Int) {

                for (i in 0.until(mFocusDiyList.size)) {
                    mFocusDiyList[i].anim = false
                }
                mFocusAdapter.notifyDataSetChanged()

                mFocusImageIndex = 8

                val images: MutableList<String> =
                    mFocusTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    if (images[i].contains(" ")) {
                        images[i] = images[i].replace(" ", "")
                    }
                }

                startActivity(context?.let {
                    ImagePreviewActivity.getIntent(it,
                        images,
                        mFocusImageIndex)
                })

            }
        })

        mFocusAdapter.setOnLikeClickListener(object : SaloonFocusAdapter.OnLikeClickListener {
            override fun onLikeClick(v: View?, position: Int) {

                // 点赞， 此时需要验证是否上传头像
                if (SPStaticUtils.getString(Constant.ME_AVATAR,
                        "") != "" || SPStaticUtils.getString(Constant.ME_AVATAR_AUDIT, "") != ""
                ) {

                    mFocusLikePosition = position

                    if (!mFocusDiyList[position].like) {
                        // 点赞
                        if (mFocusTrendList[position].user_id != SPStaticUtils.getString(Constant.USER_ID,
                                "13")
                        ) {
                            mFocusDiyList[position].anim = true

                            AnimalUtils.getAnimal(v as ImageView)

                            doLikeClick(mFocusTrendList[position].id,
                                mFocusTrendList[position].user_id,
                                SPStaticUtils.getString(Constant.USER_ID, "13"))
                        } else {
                            ToastUtils.showShort("不能给自己点赞")
                        }

                    } else {
                        // 取消赞
                        doLikeCancelClick(mFocusTrendList[position].id,
                            mFocusTrendList[position].user_id,
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
        doFocusLikeClickPresent.doFocusLikeClick(map)
    }

    // 取消点赞
    private fun doLikeCancelClick(trendId: Int, hostUid: String, guestUid: String) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.TRENDS_ID] = trendId.toString()
        map[Contents.HOST_UID] = hostUid.toString()
        map[Contents.GUEST_UID] = guestUid.toString()
        doFocusLikeCancelPresent.doFocusLikeCancel(map)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                0 -> {
                    mode = "first"
                    max = 3
                    min = 2
                    getTrendFocus(mode, max, min)
                }
            }
        }
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoFocusLikeClickSuccess(likeClickBean: LikeClickBean?) {
        // 点赞
        if (likeClickBean != null) {
            if (likeClickBean.code == 200) {
                mFocusDiyList[mFocusLikePosition].like = true
                mFocusDiyList[mFocusLikePosition].likeCount++
                mFocusAdapter.notifyDataSetChanged()
            } else {
                ToastUtils.showShort(likeClickBean.msg)
            }
        }
    }

    override fun onDoFocusLikeClickError() {

    }

    override fun onDoFocusLikeCancelSuccess(likeCancelBean: LikeCancelBean?) {
        // 取消赞
        if (likeCancelBean != null) {
            if (likeCancelBean.code == 200) {
                mFocusDiyList[mFocusLikePosition].like = false
                mFocusDiyList[mFocusLikePosition].likeCount--
                mFocusAdapter.notifyDataSetChanged()
            } else {
                ToastUtils.showShort(likeCancelBean.msg)
            }
        }
    }

    override fun onFocusLikeCancelError() {

    }

    override fun onGetTrendFocusSuccess(trendFocusBean: TrendFocusBean?) {

        srl_dynamic_focus_refresh.finishRefresh(true)
        srl_dynamic_focus_refresh.finishLoadMore(true)

        if (trendFocusBean != null) {
            if (trendFocusBean.code == 200) {

                if (trendFocusBean.data.list.isNotEmpty()) {

                    ll_dynamic_focus_empty.visibility = View.GONE

                    val mIdList: MutableList<Int> = arrayListOf()

                    val mFocusUserIdList: MutableList<String> = arrayListOf()

                    if (mode == "first") {
                        mFocusTrendList.clear()
                        mFocusDiyList.clear()
                    }

                    for (i in 0.until(trendFocusBean.data.list.size)) {
                        mFocusTrendList.add(trendFocusBean.data.list[i])
                        mIdList.add(trendFocusBean.data.list[i].id)
                        mFocusUserIdList.add(trendFocusBean.data.list[i].user_id.toString())

                        val focus = true
                        val like = trendFocusBean.data.list[i].guest_uid != null

                        mFocusDiyList.add(LikeBean(trendFocusBean.data.list[i].id,
                            focus, like, trendFocusBean.data.list[i].like_count))

                    }

                    ImHelper.updateFriendInfo(mFocusUserIdList)

                    max = Collections.max(mIdList)
                    min = Collections.min(mIdList)

                    mFocusAdapter.notifyDataSetChanged()

                } else {

                    if (mode == "first") {
                        mFocusTrendList.clear()
                        mFocusDiyList.clear()

                        ll_dynamic_focus_empty.visibility = View.VISIBLE

                    }

                    mFocusAdapter.notifyDataSetChanged()

                }
            } else {
                ToastUtils.showShort(trendFocusBean.msg)
            }
        }


    }

    override fun onGetTrendFocusError() {
        srl_dynamic_focus_refresh.finishRefresh(false)
        srl_dynamic_focus_refresh.finishLoadMore(false)
    }

    override fun onItemClick(v: View?, position: Int) {

        for (i in 0.until(mFocusDiyList.size)) {
            mFocusDiyList[i].anim = false
        }
        mFocusAdapter.notifyDataSetChanged()

        val intent = Intent(context, DynamicOtherShowActivity::class.java)
        intent.putExtra("trendId", mFocusTrendList[position].id)
        intent.putExtra("usersId", mFocusTrendList[position].user_id.toInt())
        intent.putExtra("mode", 1)
        startActivityForResult(intent, 0)
    }

    override fun onItemTextClick(v: View?, position: Int) {

        for (i in 0.until(mFocusDiyList.size)) {
            mFocusDiyList[i].anim = false
        }
        mFocusAdapter.notifyDataSetChanged()

        val intent = Intent(context, DynamicOtherShowActivity::class.java)
        intent.putExtra("trendId", mFocusTrendList[position].id)
        intent.putExtra("usersId", mFocusTrendList[position].user_id.toInt())
        intent.putExtra("mode", 1)
        startActivityForResult(intent, 0)
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

    override fun onDestroy() {
        super.onDestroy()

        getTrendFocusPresent.unregisterCallback(this)
        doFocusLikeClickPresent.unregisterCallback(this)
        doFocusLikeCancelPresent.unregisterCallback(this)

    }

}