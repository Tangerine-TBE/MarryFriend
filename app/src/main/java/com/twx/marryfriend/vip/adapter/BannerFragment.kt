package com.twx.marryfriend.vip.adapter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.dynamic.saloon.tip.like.LikeFragment
import kotlinx.android.synthetic.main.fragment_banner.*

class BannerFragment : Fragment() {

    private lateinit var mContext: Context

    private var gifPath = 0

    fun newInstance(context: Context, gifPath: Int): BannerFragment {
        val fragment = BannerFragment()
        fragment.mContext = context
        fragment.gifPath = gifPath
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_banner, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initPresent()
        initEvent()
    }

    private fun initView() {
        Glide.with(mContext)
            .load(gifPath)
            .into(iv_banner_pic);
    }

    private fun initData() {

    }

    private fun initPresent() {

    }

    private fun initEvent() {

    }

}