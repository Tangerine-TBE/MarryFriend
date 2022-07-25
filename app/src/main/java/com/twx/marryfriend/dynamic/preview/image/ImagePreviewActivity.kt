package com.twx.marryfriend.dynamic.preview.image

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.dynamic.preview.image.adapter.ImagePreviewAdapter
import com.twx.marryfriend.friend.FriendInfoActivity
import kotlinx.android.synthetic.main.activity_image_preview.*
import java.io.Serializable
import java.util.*

class ImagePreviewActivity : MainBaseViewActivity() {

    private var images: List<String> = arrayListOf()

    private var index = 0

    private lateinit var adapter: ImagePreviewAdapter


    companion object {
        private const val LIST = "imageList"
        private const val INDEX = "imageIndex"
        fun getIntent(context: Context, list: MutableList<String>, index: Int): Intent {
            val intent = Intent(context, ImagePreviewActivity::class.java)
            intent.putExtra(LIST, list as Serializable)
            intent.putExtra(INDEX, index)
            return intent
        }
    }

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