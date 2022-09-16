package com.twx.marryfriend.mine.focus.other

import android.content.Context
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
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.dynamic.CancelFocusBean
import com.twx.marryfriend.bean.mine.MeFocusWhoBean
import com.twx.marryfriend.bean.mine.MeFocusWhoList
import com.twx.marryfriend.bean.mine.WhoFocusMeList
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.message.ChatActivity
import com.twx.marryfriend.mine.focus.mine.FocusMineAdapter
import com.twx.marryfriend.mine.focus.mine.FocusMineFragment
import com.twx.marryfriend.net.callback.dynamic.IDoCancelFocusCallback
import com.twx.marryfriend.net.callback.mine.IGetMeFocusWhoCallback
import com.twx.marryfriend.net.impl.dynamic.doCancelFocusPresentImpl
import com.twx.marryfriend.net.impl.mine.getMeFocusWhoPresentImpl
import com.twx.marryfriend.net.impl.mine.getWhoFocusMePresentImpl
import kotlinx.android.synthetic.main.fragment_focus_mine.*
import kotlinx.android.synthetic.main.fragment_focus_other.*
import java.util.*

/**
 * 我关注谁界面
 */
class FocusOtherFragment : Fragment(), IGetMeFocusWhoCallback,
    FocusOtherAdapter.OnItemClickListener {

    private var currentPaper = 1

    private lateinit var mContext: Context

    private var mList: MutableList<MeFocusWhoList> = arrayListOf()

    private lateinit var adapter: FocusOtherAdapter

    private lateinit var getMeFocusWhoPresent: getMeFocusWhoPresentImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_focus_other, container, false)
    }

    fun newInstance(context: Context): FocusOtherFragment {
        val fragment = FocusOtherFragment()
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

        getMeFocusWhoPresent = getMeFocusWhoPresentImpl.getsInstance()
        getMeFocusWhoPresent.registerCallback(this)

        adapter = FocusOtherAdapter(mList)
        adapter.setOnItemClickListener(this)

        rv_focus_other_container.layoutManager = LinearLayoutManager(mContext)
        rv_focus_other_container.adapter = adapter

        sfl_focus_other_refresh.setRefreshHeader(ClassicsHeader(mContext))
        sfl_focus_other_refresh.setRefreshFooter(ClassicsFooter(mContext))

        sfl_focus_other_refresh.autoRefresh()
    }

    private fun initData() {

    }

    private fun initPresent() {

    }

    private fun initEvent() {
        sfl_focus_other_refresh.setOnRefreshListener {
            // 刷新数据
            currentPaper = 1
            getFocusOtherData(currentPaper)
            sfl_focus_other_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        sfl_focus_other_refresh.setOnLoadMoreListener {
            getFocusOtherData(currentPaper)
            sfl_focus_other_refresh.finishLoadMore(2000);//传入false表示刷新失败
        }

    }

    private fun getFocusOtherData(page: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getMeFocusWhoPresent.getMeFocusWho(map, page)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetMeFocusWhoSuccess(meFocusWhoBean: MeFocusWhoBean?) {
        if (meFocusWhoBean != null) {
            if (meFocusWhoBean.data.list.isNotEmpty()) {

                ll_focus_other_empty?.visibility = View.GONE

                if (currentPaper == 1) {
                    mList.clear()
                }
                currentPaper++
                for (i in 0.until(meFocusWhoBean.data.list.size)) {
                    mList.add(meFocusWhoBean.data.list[i])
                }
                adapter.notifyDataSetChanged()
            }
        }

        if (sfl_focus_other_refresh != null) {
            sfl_focus_other_refresh.finishRefresh(true)
            sfl_focus_other_refresh.finishLoadMore(true)
        }
    }

    override fun onGetMeFocusWhoCodeError() {
        if (sfl_focus_other_refresh != null) {
            sfl_focus_other_refresh.finishRefresh(false)
            sfl_focus_other_refresh.finishLoadMore(false)
        }
    }

    override fun onItemClick(v: View?, position: Int) {
        startActivity(context?.let { FriendInfoActivity.getIntent(it, mList[position].guest_uid) })
    }

    override fun onMoreClick(v: View?, position: Int) {

        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(context?.let { CancelDialog(it, position) })
            .show()

    }

    override fun onChatClick(v: View?, position: Int) {
        val identity = mList[position].identity_status == 1
        startActivity(context?.let {
            ChatActivity.getIntent(
                it,
                mList[position].guest_uid.toString(),
                mList[position].nick,
                mList[position].image_url,
                identity)
        })
    }


    inner class CancelDialog(context: Context, val position: Int) : FullScreenPopupView(context),
        IDoCancelFocusCallback {

        private var isCancel = true

        private lateinit var doCancelFocusPresent: doCancelFocusPresentImpl

        override fun getImplLayoutId(): Int = R.layout.dialog_focus_delete

        override fun onCreate() {
            super.onCreate()

            doCancelFocusPresent = doCancelFocusPresentImpl.getsInstance()
            doCancelFocusPresent.registerCallback(this)

            findViewById<ImageView>(R.id.iv_focus_delete_close).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_focus_delete_act).setOnClickListener {
                if (position < mList.size) {
                    val map: MutableMap<String, String> = TreeMap()
                    map[Contents.HOST_UID] = mList[position].host_uid.toString()
                    map[Contents.GUEST_UID] = mList[position].guest_uid.toString()
                    doCancelFocusPresent.doCancelFocusOther(map)
                }
            }

            findViewById<TextView>(R.id.tv_focus_delete_cancel).setOnClickListener {
                dismiss()
            }

        }

        override fun onDismiss() {
            super.onDismiss()
        }

        override fun onLoading() {

        }

        override fun onError() {

        }

        override fun onDoCancelFocusSuccess(cancelFocusBean: CancelFocusBean?) {
            if (isCancel) {
                isCancel = false
                if (cancelFocusBean != null) {
                    if (cancelFocusBean.code == 200) {
                        dismiss()
                        ToastUtils.showShort("取消关注成功")
                        mList.removeAt(position)
                        adapter.notifyDataSetChanged()
                    } else {
                        ToastUtils.showShort(cancelFocusBean.msg)
                    }
                }
            }
        }

        override fun onDoCancelFocusError() {
            ToastUtils.showShort("取消关注失败，请稍后再试")
        }

    }


    override fun onDestroy() {
        super.onDestroy()

        getMeFocusWhoPresent.unregisterCallback(this)

    }

}