package com.twx.module_dynamic.show.others

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.twx.module_base.base.BaseApplication
import com.twx.module_base.base.MainBaseViewActivity
import com.twx.module_base.constant.Constant
import com.twx.module_base.constant.Contents
import com.twx.module_dynamic.R
import com.twx.module_dynamic.bean.CheckTrendBean
import com.twx.module_dynamic.bean.DeleteTrendBean
import com.twx.module_dynamic.net.callback.IDoCheckTrendCallback
import com.twx.module_dynamic.net.callback.IDoDeleteTrendCallback
import com.twx.module_dynamic.net.impl.doCheckTrendPresentImpl
import com.twx.module_dynamic.net.impl.doDeleteTrendPresentImpl
import com.twx.module_dynamic.show.mine.DynamicMineLikeActivity
import com.twx.module_dynamic.show.mine.DynamicMineShowActivity
import kotlinx.android.synthetic.main.activity_dynamic_mine_show.*
import kotlinx.android.synthetic.main.activity_dynamic_other_show.*
import java.util.*

class DynamicOtherShowActivity : MainBaseViewActivity(), IDoCheckTrendCallback {

    private var trendId = 0

    private var userId = ""

    private var sex = 0

    private var eduList: MutableList<String> = arrayListOf()

    private lateinit var doCheckTrendPresent: doCheckTrendPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_dynamic_other_show

    override fun initView() {
        super.initView()

        trendId = intent.getIntExtra("trendId", 0)
        userId = intent.getStringExtra("userId").toString()

        doCheckTrendPresent = doCheckTrendPresentImpl.getsInstance()
        doCheckTrendPresent.registerCallback(this)

        sex = intent.getIntExtra("sex", 1)

        when (sex) {
            1 -> riv_dynamic_other_show_heard_name.text = "他的动态"
            2 -> riv_dynamic_other_show_heard_name.text = "她的动态"
        }

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

    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_dynamic_other_show_finish.setOnClickListener {
            finish()
        }

        iv_dynamic_other_show_heard_edit.setOnClickListener {
            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(DynamicEditDialog(this))
                .show()
        }

        iv_dynamic_other_show_tip_hide.setOnClickListener {
            iv_dynamic_other_show_tip.visibility = View.GONE
            SPStaticUtils.put(Constant.HIDE_REPORT_TIP, true)
        }

        rl_dynamic_other_show_like.setOnClickListener {
            val intent = Intent(this, DynamicMineLikeActivity::class.java)
            startActivity(intent)
        }

    }


    // 获取具体动态
    private fun getTrendsList() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.ID] = trendId.toString()
        map[Contents.USER_ID] = userId.toString()
        doCheckTrendPresent.doCheckTrend(map)
    }


    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoCheckTrendSuccess(checkTrendBean: CheckTrendBean) {

        val info = checkTrendBean.data.list[0]
        val image = checkTrendBean.data.imgs

        Glide.with(applicationContext).load(info.headface).into(riv_dynamic_other_show_avatar)
        riv_dynamic_other_show_heard_name.text = info.nick

        Glide.with(applicationContext).load(info.headface).into(riv_dynamic_other_show_avatar)
        tv_dynamic_other_show_name.text = info.nick

        val year = (TimeUtils.getValueByCalendarField(TimeUtils.getNowDate(),
            Calendar.YEAR) - info.age).toString().substring(2, 4)
        val city = info.work_city_str
        val edu = eduList[info.education]

        val job =  if (info.industry_str == ""){
            "${info.industry_str}"
        }else{
            " ${info.industry_str}/${info.occupation_str}"
        }
        tv_dynamic_other_show_info.text = "${year}年  $city  $edu  $job"


        if (TimeUtils.isToday(info.create_time)) {
            tv_dynamic_other_show_time.text = "${
                TimeUtils.getValueByCalendarField(info.create_time,
                    Calendar.HOUR_OF_DAY)
            }: " +
                    "${
                        TimeUtils.getValueByCalendarField(info.create_time,
                            Calendar.MINUTE)
                    }"
        } else {

            val day =
                TimeUtils.getValueByCalendarField(info.create_time, Calendar.DAY_OF_YEAR)
            val nowDay =
                TimeUtils.getValueByCalendarField(TimeUtils.getNowDate(), Calendar.DAY_OF_YEAR)

            if (day - nowDay == -1) {
                // 是昨天
                tv_dynamic_other_show_time.text = "昨天" +
                        "${
                            TimeUtils.getValueByCalendarField(info.create_time,
                                Calendar.HOUR_OF_DAY)
                        }: " +
                        "${
                            TimeUtils.getValueByCalendarField(info.create_time,
                                Calendar.MINUTE)
                        }"

            } else {
                tv_dynamic_other_show_time.text =
                    "${
                        TimeUtils.getValueByCalendarField(info.create_time, Calendar.YEAR)
                    }年" + "${
                        TimeUtils.getValueByCalendarField(info.create_time, Calendar.MONTH)
                    }月" +
                            "${
                                TimeUtils.getValueByCalendarField(info.create_time,
                                    Calendar.DAY_OF_MONTH)
                            }日" +
                            "${
                                TimeUtils.getValueByCalendarField(info.create_time,
                                    Calendar.HOUR_OF_DAY)
                            }: " +
                            "${
                                TimeUtils.getValueByCalendarField(info.create_time, Calendar.MINUTE)
                            }"
            }
        }

        tv_dynamic_other_show_location.text = info.position

        tv_dynamic_other_show_like.text = info.like_count.toString()


        tv_dynamic_other_show_text.text = info.text_content


        when (info.trends_type) {
            1 -> {
                // 图片
                if (info.text_content != "") {
                    tv_dynamic_other_show_text.text = info.text_content
                } else {
                    tv_dynamic_other_show_text.visibility = View.GONE
                }

                val list = info.image_url.split(",")

                when (list.size) {
                    1 -> {
                        iv_dynamic_other_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[0]).into(iv_dynamic_other_show_one)
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
                        iv_dynamic_other_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[0]).into(iv_dynamic_other_show_one)
                        iv_dynamic_other_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[1]).into(iv_dynamic_other_show_two)
                        iv_dynamic_other_show_three.visibility = View.INVISIBLE
                        iv_dynamic_other_show_four.visibility = View.GONE
                        iv_dynamic_other_show_five.visibility = View.GONE
                        iv_dynamic_other_show_six.visibility = View.GONE
                        iv_dynamic_other_show_seven.visibility = View.GONE
                        iv_dynamic_other_show_eight.visibility = View.GONE
                        iv_dynamic_other_show_nine.visibility = View.GONE
                    }
                    3 -> {
                        iv_dynamic_other_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[0]).into(iv_dynamic_other_show_one)
                        iv_dynamic_other_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[1]).into(iv_dynamic_other_show_two)
                        iv_dynamic_other_show_three.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[2])
                            .into(iv_dynamic_other_show_three)
                        iv_dynamic_other_show_four.visibility = View.GONE
                        iv_dynamic_other_show_five.visibility = View.GONE
                        iv_dynamic_other_show_six.visibility = View.GONE
                        iv_dynamic_other_show_seven.visibility = View.GONE
                        iv_dynamic_other_show_eight.visibility = View.GONE
                        iv_dynamic_other_show_nine.visibility = View.GONE
                    }
                    4 -> {
                        iv_dynamic_other_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[0]).into(iv_dynamic_other_show_one)
                        iv_dynamic_other_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[1]).into(iv_dynamic_other_show_two)
                        iv_dynamic_other_show_three.visibility = View.INVISIBLE
                        iv_dynamic_other_show_four.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[2])
                            .into(iv_dynamic_other_show_four)
                        iv_dynamic_other_show_five.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[3])
                            .into(iv_dynamic_other_show_five)
                        iv_dynamic_other_show_six.visibility = View.INVISIBLE
                        iv_dynamic_other_show_seven.visibility = View.GONE
                        iv_dynamic_other_show_eight.visibility = View.GONE
                        iv_dynamic_other_show_nine.visibility = View.GONE
                    }
                    5 -> {
                        iv_dynamic_other_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[0]).into(iv_dynamic_other_show_one)
                        iv_dynamic_other_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[1]).into(iv_dynamic_other_show_two)
                        iv_dynamic_other_show_three.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[2])
                            .into(iv_dynamic_other_show_three)
                        iv_dynamic_other_show_four.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[3])
                            .into(iv_dynamic_other_show_four)
                        iv_dynamic_other_show_five.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[4])
                            .into(iv_dynamic_other_show_five)
                        iv_dynamic_other_show_six.visibility = View.INVISIBLE
                        iv_dynamic_other_show_seven.visibility = View.GONE
                        iv_dynamic_other_show_eight.visibility = View.GONE
                        iv_dynamic_other_show_nine.visibility = View.GONE
                    }
                    6 -> {
                        iv_dynamic_other_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[0]).into(iv_dynamic_other_show_one)
                        iv_dynamic_other_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[1]).into(iv_dynamic_other_show_two)
                        iv_dynamic_other_show_three.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[2])
                            .into(iv_dynamic_other_show_three)
                        iv_dynamic_other_show_four.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[3])
                            .into(iv_dynamic_other_show_four)
                        iv_dynamic_other_show_five.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[4])
                            .into(iv_dynamic_other_show_five)
                        iv_dynamic_other_show_six.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[5]).into(iv_dynamic_other_show_six)
                        iv_dynamic_other_show_seven.visibility = View.GONE
                        iv_dynamic_other_show_eight.visibility = View.GONE
                        iv_dynamic_other_show_nine.visibility = View.GONE
                    }
                    7 -> {
                        iv_dynamic_other_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[0]).into(iv_dynamic_other_show_one)
                        iv_dynamic_other_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[1]).into(iv_dynamic_other_show_two)
                        iv_dynamic_other_show_three.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[2])
                            .into(iv_dynamic_other_show_three)
                        iv_dynamic_other_show_four.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[3])
                            .into(iv_dynamic_other_show_four)
                        iv_dynamic_other_show_five.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[4])
                            .into(iv_dynamic_other_show_five)
                        iv_dynamic_other_show_six.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[5]).into(iv_dynamic_other_show_six)
                        iv_dynamic_other_show_seven.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[6])
                            .into(iv_dynamic_other_show_seven)
                        iv_dynamic_other_show_eight.visibility = View.INVISIBLE
                        iv_dynamic_other_show_nine.visibility = View.INVISIBLE
                    }
                    8 -> {
                        iv_dynamic_other_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[0]).into(iv_dynamic_other_show_one)
                        iv_dynamic_other_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[1]).into(iv_dynamic_other_show_two)
                        iv_dynamic_other_show_three.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[2])
                            .into(iv_dynamic_other_show_three)
                        iv_dynamic_other_show_four.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[3])
                            .into(iv_dynamic_other_show_four)
                        iv_dynamic_other_show_five.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[4])
                            .into(iv_dynamic_other_show_five)
                        iv_dynamic_other_show_six.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[5]).into(iv_dynamic_other_show_six)
                        iv_dynamic_other_show_seven.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[6])
                            .into(iv_dynamic_other_show_seven)
                        iv_dynamic_other_show_eight.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[7])
                            .into(iv_dynamic_other_show_eight)
                        iv_dynamic_other_show_nine.visibility = View.INVISIBLE
                    }
                    9 -> {
                        iv_dynamic_other_show_one.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[0]).into(iv_dynamic_other_show_one)
                        iv_dynamic_other_show_two.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[1]).into(iv_dynamic_other_show_two)
                        iv_dynamic_other_show_three.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[2])
                            .into(iv_dynamic_other_show_three)
                        iv_dynamic_other_show_four.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[3])
                            .into(iv_dynamic_other_show_four)
                        iv_dynamic_other_show_five.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[4])
                            .into(iv_dynamic_other_show_five)
                        iv_dynamic_other_show_six.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[5]).into(iv_dynamic_other_show_six)
                        iv_dynamic_other_show_seven.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[6])
                            .into(iv_dynamic_other_show_seven)
                        iv_dynamic_other_show_eight.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[7])
                            .into(iv_dynamic_other_show_eight)
                        iv_dynamic_other_show_nine.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(list[8])
                            .into(iv_dynamic_other_show_nine)
                    }
                }

            }
            2 -> {
                // 视频
                if (info.text_content != "") {
                    tv_dynamic_other_show_text.text = info.text_content
                } else {
                    tv_dynamic_other_show_text.visibility = View.GONE
                }

                iv_dynamic_other_show_one.visibility = View.VISIBLE
                Glide.with(applicationContext).load(info.video_url)
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
            3 -> {
                // 文字
                tv_dynamic_other_show_text.text = info.text_content
                iv_dynamic_other_show_one.visibility = View.GONE
                iv_dynamic_other_show_two.visibility = View.GONE
                iv_dynamic_other_show_three.visibility = View.GONE
                iv_dynamic_other_show_four.visibility = View.GONE
                iv_dynamic_other_show_five.visibility = View.GONE
                iv_dynamic_other_show_six.visibility = View.GONE
                iv_dynamic_other_show_seven.visibility = View.GONE
                iv_dynamic_other_show_eight.visibility = View.GONE
                iv_dynamic_other_show_nine.visibility = View.GONE
            }
        }

        when (checkTrendBean.data.imgs.size) {
            0 -> {
                ll_dynamic_other_show_like.visibility = View.GONE
            }
            1 -> {
                iv_dynamic_other_show_like1.visibility = View.VISIBLE
                Glide.with(applicationContext).load(image[0].image_url)
                    .into(iv_dynamic_other_show_like1)

                iv_dynamic_other_show_like2.visibility = View.GONE

                iv_dynamic_other_show_like3.visibility = View.GONE
            }
            2 -> {
                iv_dynamic_other_show_like1.visibility = View.VISIBLE
                Glide.with(applicationContext).load(image[0].image_url)
                    .into(iv_dynamic_other_show_like1)

                iv_dynamic_other_show_like2.visibility = View.VISIBLE
                Glide.with(applicationContext).load(image[1].image_url)
                    .into(iv_dynamic_other_show_like2)

                iv_dynamic_other_show_like3.visibility = View.GONE
            }
            3 -> {
                iv_dynamic_other_show_like1.visibility = View.VISIBLE
                Glide.with(applicationContext).load(image[0].image_url)
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

    override fun onDoCheckTrendError() {

    }

    inner class DynamicEditDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_dynamic_other_edit

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_dialog_dynamic_other_edit_close)
            val report = findViewById<TextView>(R.id.tv_dialog_dynamic_other_edit_report)
            val cancel = findViewById<TextView>(R.id.tv_dialog_dynamic_other_edit_cancel)

            close.setOnClickListener {
                dismiss()
            }

            report.setOnClickListener {
                ToastUtils.showShort("举报该动态")
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

}