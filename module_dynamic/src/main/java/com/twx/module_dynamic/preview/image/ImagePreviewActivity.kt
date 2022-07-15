package com.twx.module_dynamic.preview.image

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ToastUtils
import com.twx.module_base.base.MainBaseViewActivity
import com.twx.module_dynamic.R
import com.twx.module_dynamic.preview.image.adapter.ImagePreviewAdapter
import kotlinx.android.synthetic.main.activity_image_preview.*
import java.util.*

class ImagePreviewActivity : MainBaseViewActivity() {

    private var images: List<String> = arrayListOf()

    private var index = 0

    private lateinit var adapter: ImagePreviewAdapter

    override fun getLayoutView(): Int = R.layout.activity_image_preview

    override fun initView() {
        super.initView()
        vp_image_preview_pager.offscreenPageLimit = 3
    }

    override fun initLoadData() {
        super.initLoadData()

        images = intent.getSerializableExtra("imageList") as List<String>
        index = intent.getIntExtra("imageIndex", 0)

        tv_image_preview_indicator.text = 1.toString() + "/" + images.size

        adapter = ImagePreviewAdapter(images)
        vp_image_preview_pager.adapter = adapter

        tv_image_preview_indicator.visibility = View.VISIBLE

        vp_image_preview_pager.registerOnPageChangeCallback(mPageChangeCallback)
        if (index < images.size) {
            vp_image_preview_pager.setCurrentItem(index, false)
        }
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_image_preview_finish.setOnClickListener {
            finish()
        }

        adapter.setOnItemClickListener(object : ImagePreviewAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                if (rl_image_preview_menu.visibility == View.GONE) {
                    rl_image_preview_menu.visibility = View.VISIBLE
                } else {
                    rl_image_preview_menu.visibility = View.GONE
                }
            }
        })
    }

    /**
     * ViewPager2 页面改变监听器
     */
    private val mPageChangeCallback: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {
            @Suppress("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tv_image_preview_indicator.text = (position + 1).toString() + "/" + images.size
            }
        }

}