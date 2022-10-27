package com.twx.marryfriend.set.message

import android.util.Log
import android.view.View
import android.widget.Switch
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.hyphenate.easeim.section.me.activity.MessageReceiveSetActivity
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.set.MessageSwitchBean
import com.twx.marryfriend.bean.vip.GetPushSetBean
import com.twx.marryfriend.bean.vip.UpdatePushSetBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.net.callback.vip.IDoUpdatePushSetCallback
import com.twx.marryfriend.net.callback.vip.IGetPushSetCallback
import com.twx.marryfriend.net.impl.vip.doUpdatePushSetPresentImpl
import com.twx.marryfriend.net.impl.vip.getPushSetPresentImpl
import com.twx.marryfriend.net.present.vip.IDoUpdatePushSetPresent
import com.twx.marryfriend.set.adapter.MessageAdapter
import com.twx.marryfriend.set.adapter.MessageTwoAdapter
import kotlinx.android.synthetic.main.activity_message.*
import java.util.*

class MessageActivity : MainBaseViewActivity(), IGetPushSetCallback, IDoUpdatePushSetCallback,
    MessageAdapter.OnItemClickListener, MessageTwoAdapter.OnTwoItemClickListener {

    private var data = true   // 资料审核通知
    private var love = true   // TA刚刚喜欢了你
    private var comment = true   // TA评论了你的动态
    private var like = true   // TA点赞了你的动态
    private var view = true   // TA刚看了你的资料
    private var online = true   // 你喜欢的TA上线了
    private var love2Online = true   // 和你相互喜欢的TA上线了
    private var love2 = true   // 互相喜欢
    private var gift = true    // 收到礼物通知


    private var mTopList: MutableList<MessageSwitchBean> = arrayListOf()
    private var mBottomList: MutableList<MessageSwitchBean> = arrayListOf()

    private lateinit var adapter1: MessageAdapter
    private lateinit var adapter2: MessageTwoAdapter

    private lateinit var getPushSetPresent: getPushSetPresentImpl
    private lateinit var doUpdatePushSetPresent: doUpdatePushSetPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_message

    override fun initView() {
        super.initView()

        getPushSetPresent = getPushSetPresentImpl.getsInstance()
        getPushSetPresent.registerCallback(this)

        doUpdatePushSetPresent = doUpdatePushSetPresentImpl.getsInstance()
        doUpdatePushSetPresent.registerCallback(this)

        data = SPStaticUtils.getBoolean(Constant.DATA_REVIEW_TIP, false)
        love = SPStaticUtils.getBoolean(Constant.TA_LIKE_NOW_TIP, false)
        comment = SPStaticUtils.getBoolean(Constant.TA_COMMENT_TIP, false)
        like = SPStaticUtils.getBoolean(Constant.TA_DIANZAN_TIP, false)
        view = SPStaticUtils.getBoolean(Constant.TA_LOOK_NOW_TIP, false)
        online = SPStaticUtils.getBoolean(Constant.LIKE_TA_ONLINE_TIP, false)
        love2Online = SPStaticUtils.getBoolean(Constant.LIKE_ALL_ONLINE_TIP, false)
        love2 = SPStaticUtils.getBoolean(Constant.LIKE_ALL_TIP, false)
        gift = SPStaticUtils.getBoolean(Constant.GET_GIFT, false)


        mTopList.add(MessageSwitchBean("资料审核通知", "(头像,声音,实名认证,动态相册,生活照)", data, true))
        mTopList.add(MessageSwitchBean("TA刚刚喜欢了你", "", love, false))
        mTopList.add(MessageSwitchBean("TA评论了你的动态", "", comment, false))
        mTopList.add(MessageSwitchBean("TA点赞了你的动态", "", like, false))
        mTopList.add(MessageSwitchBean("TA刚看了你的资料", "", view, false))
        mTopList.add(MessageSwitchBean("你喜欢的TA上线了", "", online, false))
        mTopList.add(MessageSwitchBean("和你相互喜欢的Ta上线了", "", love2Online, false))

        mBottomList.add(MessageSwitchBean("互相喜欢", "(相互喜欢配对成功后提醒)", love2, true))
        mBottomList.add(MessageSwitchBean("收到礼物", "", gift, false))


        adapter1 = MessageAdapter(mTopList)
        adapter2 = MessageTwoAdapter(mBottomList)

        adapter1.setOnItemClickListener(this)
        adapter2.setOnItemClickListener(this)

        rv_message_container_top.layoutManager = LinearLayoutManager(this)
        rv_message_container_top.adapter = adapter1

        rv_message_container_bottom.layoutManager = LinearLayoutManager(this)
        rv_message_container_bottom.adapter = adapter2

        adapter1.notifyDataSetChanged()
        adapter2.notifyDataSetChanged()


    }

    override fun initLoadData() {
        super.initLoadData()

        getPushSet()

    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_message_finish.setOnClickListener {
            finish()
            doUpdatePushSet()
        }

    }

    // 获取推送设置
    private fun getPushSet() {

        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getPushSetPresent.getPushSet(map)

    }

    // 修改推送设置
    private fun doUpdatePushSet() {

        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.SHENHE_TONGZHI] = if (data) {
            "1"
        } else {
            "0"
        }
        map[Contents.TA_GANG_XIHUAN_NI] = if (love) {
            "1"
        } else {
            "0"
        }
        map[Contents.PINGLUN_DONGTAI] = if (comment) {
            "1"
        } else {
            "0"
        }
        map[Contents.DIANZAN_DONGTAI] = if (like) {
            "1"
        } else {
            "0"
        }
        map[Contents.KANLE_NIDE_ZILIAO] = if (view) {
            "1"
        } else {
            "0"
        }
        map[Contents.NIXIHUANDE_SHANGXIAN] = if (online) {
            "1"
        } else {
            "0"
        }
        map[Contents.XIANGHU_XIHUAN_SHANGXIAN] = if (love2Online) {
            "1"
        } else {
            "0"
        }
        map[Contents.DIANJI_XIANGHU_XIHUAN] = if (love2) {
            "1"
        } else {
            "0"
        }
        map[Contents.SHOUDAO_LIWU_TONGZHI] = if (gift) {
            "1"
        } else {
            "0"
        }

        doUpdatePushSetPresent.doUpdatePushSet(map)


    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetPushSetSuccess(getPushSetBean: GetPushSetBean?) {
        if (getPushSetBean != null) {
            if (getPushSetBean.code == 200) {

                data = getPushSetBean.data.shenhe_tongzhi == 1
                love = getPushSetBean.data.ta_gang_xihuan_ni == 1
                comment = getPushSetBean.data.pinglun_dongtai == 1
                like = getPushSetBean.data.dianzan_dongtai == 1
                view = getPushSetBean.data.kanle_nide_ziliao == 1
                online = getPushSetBean.data.nixihuande_shangxian == 1
                love2Online = getPushSetBean.data.xianghu_xihuan_shangxian == 1
                love2 = getPushSetBean.data.dianji_xianghu_xihuan == 1
                gift = getPushSetBean.data.shoudao_liwu_tongzhi == 1


                mTopList[0].switch = data
                mTopList[1].switch = love
                mTopList[2].switch = comment
                mTopList[3].switch = like
                mTopList[4].switch = view
                mTopList[5].switch = online
                mTopList[6].switch = love2Online

                mBottomList[0].switch = love2
                mBottomList[1].switch = gift

                SPStaticUtils.put(Constant.DATA_REVIEW_TIP, data)
                SPStaticUtils.put(Constant.TA_LIKE_NOW_TIP, love)
                SPStaticUtils.put(Constant.TA_COMMENT_TIP, comment)
                SPStaticUtils.put(Constant.TA_DIANZAN_TIP, like)
                SPStaticUtils.put(Constant.TA_LOOK_NOW_TIP, view)
                SPStaticUtils.put(Constant.LIKE_TA_ONLINE_TIP, online)
                SPStaticUtils.put(Constant.LIKE_ALL_ONLINE_TIP, love2Online)
                SPStaticUtils.put(Constant.LIKE_ALL_TIP, love2)
                SPStaticUtils.put(Constant.GET_GIFT, gift)

                adapter1.notifyDataSetChanged()
                adapter2.notifyDataSetChanged()

            }
        }

    }

    override fun onGetPushSetError() {
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    override fun onDoUpdatePushSetSuccess(updatePushSetBean: UpdatePushSetBean?) {
        if (updatePushSetBean != null) {
            if (updatePushSetBean.code == 200) {
                ToastUtils.showShort("修改成功")
                doUpdatePushSetPresent.unregisterCallback(this)
            }
        }
    }

    override fun onDoUpdatePushSetError() {
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    override fun onDestroy() {
        super.onDestroy()
        getPushSetPresent.unregisterCallback(this)
        doUpdatePushSetPresent.unregisterCallback(this)
    }

    override fun onItemClick(v: View?, position: Int) {

    }

    override fun onItemSwitchClick(v: View?, position: Int) {

        when (position) {
            0 -> {
                data = !data
                SPStaticUtils.put(Constant.DATA_REVIEW_TIP, data)
                Log.i("guo", "data :$data")
            }
            1 -> {
                love = !love
                SPStaticUtils.put(Constant.TA_LIKE_NOW_TIP, love)
                Log.i("guo", "love :$love")
            }
            2 -> {
                comment = !comment
                SPStaticUtils.put(Constant.TA_COMMENT_TIP, comment)
                Log.i("guo", "comment :$comment")
            }
            3 -> {
                like = !like
                SPStaticUtils.put(Constant.TA_DIANZAN_TIP, like)
                Log.i("guo", "like :$like")
            }
            4 -> {
                view = !view
                SPStaticUtils.put(Constant.TA_LOOK_NOW_TIP, view)
                Log.i("guo", "view :$view")
            }
            5 -> {
                online = !online
                SPStaticUtils.put(Constant.LIKE_TA_ONLINE_TIP, online)
                Log.i("guo", "online :$online")
            }
            6 -> {
                love2Online = !love2Online
                SPStaticUtils.put(Constant.LIKE_ALL_ONLINE_TIP, love2Online)
                Log.i("guo", "love2Online :$love2Online")
            }
        }

    }

    override fun onTwoItemClick(v: View?, position: Int) {

    }

    override fun onTwoItemSwitchClick(v: View?, position: Int) {
        when (position) {
            0 -> {
                love2 = !love2
                SPStaticUtils.put(Constant.LIKE_ALL_TIP, love2)
                Log.i("guo", "love2 :$love2")
            }
            1 -> {
                gift = !gift
                SPStaticUtils.put(Constant.GET_GIFT, gift)
                Log.i("guo", "gift :$gift")
            }
        }
    }


}