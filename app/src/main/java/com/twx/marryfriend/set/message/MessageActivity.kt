package com.twx.marryfriend.set.message

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPStaticUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.set.MessageSwitchBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.set.adapter.MessageAdapter
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : MainBaseViewActivity() {

    private var mTopList: MutableList<MessageSwitchBean> = arrayListOf()
    private var mBottomList: MutableList<MessageSwitchBean> = arrayListOf()

    private lateinit var adapter1: MessageAdapter
    private lateinit var adapter2: MessageAdapter

    override fun getLayoutView(): Int = R.layout.activity_message

    override fun initView() {
        super.initView()

        mTopList.add(MessageSwitchBean("资料审核通知",
            "(头像,声音,实名认证,动态相册,生活照)",
            SPStaticUtils.getBoolean(Constant.DATA_REVIEW_TIP, false),
            true))
        mTopList.add(MessageSwitchBean("TA刚刚喜欢了你",
            "",
            SPStaticUtils.getBoolean(Constant.TA_LIKE_NOW_TIP, false),
            false))
        mTopList.add(MessageSwitchBean("TA评论了你的动态",
            "",
            SPStaticUtils.getBoolean(Constant.TA_COMMENT_TIP, false),
            false))
        mTopList.add(MessageSwitchBean("TA点赞了你的动态",
            "",
            SPStaticUtils.getBoolean(Constant.TA_DIANZAN_TIP, false),
            false))
        mTopList.add(MessageSwitchBean("TA刚看了你的资料",
            "",
            SPStaticUtils.getBoolean(Constant.TA_LOOK_NOW_TIP, false),
            false))
        mTopList.add(MessageSwitchBean("你喜欢的TA上线了",
            "",
            SPStaticUtils.getBoolean(Constant.LIKE_TA_ONLINE_TIP, false),
            false))
        mTopList.add(MessageSwitchBean("和你相互喜欢的Ta上线了",
            "",
            SPStaticUtils.getBoolean(Constant.LIKE_ALL_ONLINE_TIP, false),
            false))

        mBottomList.add(MessageSwitchBean("互相喜欢",
            "(相互喜欢配对成功后提醒)",
            SPStaticUtils.getBoolean(Constant.LIKE_ALL_TIP, false),
            true))
        mBottomList.add(MessageSwitchBean("聊天消息",
            "",
            SPStaticUtils.getBoolean(Constant.MESSAGE_TIP, false),
            false))
        mBottomList.add(MessageSwitchBean("打招呼",
            "(收到超级喜欢、超级消息提醒)",
            SPStaticUtils.getBoolean(Constant.GREET_TIP, false),
            true))


        adapter1 = MessageAdapter(mTopList)
        adapter2 = MessageAdapter(mBottomList)

        rv_message_container_top.layoutManager = LinearLayoutManager(this)
        rv_message_container_top.adapter = adapter1

        rv_message_container_bottom.layoutManager = LinearLayoutManager(this)
        rv_message_container_bottom.adapter = adapter2

        adapter1.notifyDataSetChanged()
        adapter2.notifyDataSetChanged()

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_message_finish.setOnClickListener {
            finish()
        }

        adapter1.setOnItemClickListener(object : MessageAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                when (position) {
                    0 -> {
                        mTopList[0].switch = !mTopList[0].switch
                        SPStaticUtils.put(Constant.DATA_REVIEW_TIP, mTopList[0].switch)
                        adapter1.notifyDataSetChanged()
                    }
                    1 -> {
                        mTopList[1].switch = !mTopList[1].switch
                        SPStaticUtils.put(Constant.TA_LIKE_NOW_TIP, mTopList[1].switch)
                        adapter1.notifyDataSetChanged()
                    }
                    2 -> {
                        mTopList[2].switch = !mTopList[2].switch
                        SPStaticUtils.put(Constant.TA_COMMENT_TIP, mTopList[2].switch)
                        adapter1.notifyDataSetChanged()
                    }
                    3 -> {
                        mTopList[3].switch = !mTopList[3].switch
                        SPStaticUtils.put(Constant.TA_DIANZAN_TIP, mTopList[3].switch)
                        adapter1.notifyDataSetChanged()
                    }
                    4 -> {
                        mTopList[4].switch = !mTopList[4].switch
                        SPStaticUtils.put(Constant.TA_LOOK_NOW_TIP, mTopList[4].switch)
                        adapter1.notifyDataSetChanged()
                    }
                    5 -> {
                        mTopList[5].switch = !mTopList[5].switch
                        SPStaticUtils.put(Constant.LIKE_TA_ONLINE_TIP, mTopList[5].switch)
                        adapter1.notifyDataSetChanged()
                    }
                    6 -> {
                        mTopList[6].switch = !mTopList[6].switch
                        SPStaticUtils.put(Constant.LIKE_ALL_ONLINE_TIP, mTopList[6].switch)
                        adapter1.notifyDataSetChanged()
                    }
                }
            }
        })

        adapter2.setOnItemClickListener(object : MessageAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                when (position) {
                    0 -> {
                        mBottomList[0].switch = !mBottomList[0].switch
                        SPStaticUtils.put(Constant.LIKE_ALL_TIP, mBottomList[0].switch)
                        adapter2.notifyDataSetChanged()
                    }
                    1 -> {
                        mBottomList[1].switch = !mBottomList[1].switch
                        SPStaticUtils.put(Constant.MESSAGE_TIP, mBottomList[1].switch)
                        adapter2.notifyDataSetChanged()
                    }
                    2 -> {
                        mBottomList[2].switch = !mBottomList[2].switch
                        SPStaticUtils.put(Constant.GREET_TIP, mBottomList[2].switch)
                        adapter2.notifyDataSetChanged()
                    }
                }
            }
        })

    }

}