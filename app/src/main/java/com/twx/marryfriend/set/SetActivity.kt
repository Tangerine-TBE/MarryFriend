package com.twx.marryfriend.set

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.set.SetDataBean
import com.twx.marryfriend.bean.set.SetSwitchBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.search.SearchParamActivity
import com.twx.marryfriend.set.adapter.FeedbackAdapter
import com.twx.marryfriend.set.adapter.SetAdapter
import com.twx.marryfriend.set.adapter.SetDataAdapter
import com.twx.marryfriend.set.adapter.SetSwitchAdapter
import com.twx.marryfriend.set.feedback.FeedbackActivity
import com.twx.marryfriend.set.message.MessageActivity
import com.twx.marryfriend.set.safe.SafeActivity
import kotlinx.android.synthetic.main.activity_set.*

class SetActivity : MainBaseViewActivity() {

    override fun getLayoutView(): Int = R.layout.activity_set

    private var mSwitchList: MutableList<SetSwitchBean> = arrayListOf()
    private var mDataList: MutableList<SetDataBean> = arrayListOf()

    private lateinit var adapter1: SetAdapter
    private lateinit var adapter2: SetSwitchAdapter
    private lateinit var adapter3: SetAdapter
    private lateinit var adapter4: SetDataAdapter
    private lateinit var adapter5: SetAdapter

    override fun initView() {
        super.initView()



        mSwitchList.add(SetSwitchBean("隐藏在线状态",
            R.drawable.ic_set_state_hide,
            SPStaticUtils.getBoolean(Constant.HIDE_STATE, false)))
        mSwitchList.add(SetSwitchBean("隐藏会员标识",
            R.drawable.ic_set_vip_hide,
            SPStaticUtils.getBoolean(Constant.HIDE_VIP, false)))

        mDataList.add(SetDataBean("数据与缓存", R.drawable.ic_set_data, "30MB"))

        adapter1 = SetAdapter(DataProvider.FirstSetData)
        adapter2 = SetSwitchAdapter(mSwitchList)
        adapter3 = SetAdapter(DataProvider.SecondSetData)
        adapter4 = SetDataAdapter(mDataList)
        adapter5 = SetAdapter(DataProvider.ThirdSetData)

        rv_set_container_1.layoutManager = LinearLayoutManager(this)
        rv_set_container_1.adapter = adapter1
        rv_set_container_2.layoutManager = LinearLayoutManager(this)
        rv_set_container_2.adapter = adapter2
        rv_set_container_3.layoutManager = LinearLayoutManager(this)
        rv_set_container_3.adapter = adapter3
        rv_set_container_4.layoutManager = LinearLayoutManager(this)
        rv_set_container_4.adapter = adapter4
        rv_set_container_5.layoutManager = LinearLayoutManager(this)
        rv_set_container_5.adapter = adapter5

        adapter1.notifyDataSetChanged()
        adapter2.notifyDataSetChanged()
        adapter3.notifyDataSetChanged()
        adapter4.notifyDataSetChanged()
        adapter5.notifyDataSetChanged()
    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_set_finish.setOnClickListener {
            finish()
        }

        adapter1.setOnItemClickListener(object : SetAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                when (position) {
                    0 -> {
                        val intent = Intent(this@SetActivity, SearchParamActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        })

        adapter2.setOnItemClickListener(object : SetSwitchAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                when (position) {
                    0 -> {
                        ToastUtils.showShort("000")
                        mSwitchList[0].switch = !mSwitchList[0].switch
                        SPStaticUtils.put(Constant.HIDE_STATE, mSwitchList[0].switch)
                        adapter2.notifyDataSetChanged()
                    }
                    1 -> {
                        ToastUtils.showShort("111")
                        mSwitchList[1].switch = !mSwitchList[1].switch
                        SPStaticUtils.put(Constant.HIDE_VIP, mSwitchList[1].switch)
                        adapter2.notifyDataSetChanged()
                    }
                }
            }
        })

        adapter3.setOnItemClickListener(object : SetAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                when (position) {
                    0 -> {
                        ToastUtils.showShort("消息通知")
                        startActivity(Intent(this@SetActivity, MessageActivity::class.java))
                    }
                    1 -> {
                        ToastUtils.showShort("帮助反馈")
                        startActivity(Intent(this@SetActivity, FeedbackActivity::class.java))
                    }
                    2 -> {
                        ToastUtils.showShort("账户与安全")
                        startActivity(Intent(this@SetActivity, SafeActivity::class.java))
                    }
                    3 -> {
                        ToastUtils.showShort("恢复购买")
                    }
                }
            }
        })

        adapter4.setOnItemClickListener(object : SetDataAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                ToastUtils.showShort("数据与缓存")
            }
        })

        adapter5.setOnItemClickListener(object : SetAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                when (position) {
                    0 -> {
                        ToastUtils.showShort("关于佳偶婚恋交友")
                    }
                    1 -> {
                        ToastUtils.showShort("黑名单")
                    }
                    2 -> {
                        ToastUtils.showShort("好评")
                    }
                }
            }
        })

    }

}