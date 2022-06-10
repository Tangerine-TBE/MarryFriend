package com.twx.marryfriend.mine

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.twx.marryfriend.R
import com.twx.marryfriend.mine.user.UserActivity
import com.twx.marryfriend.mine.verify.VerifyActivity
import kotlinx.android.synthetic.main.fragment_mine.*

class MineFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_mine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initPresent()
        initEvent()
    }

    private fun initView() {

    }

    private fun initData() {

    }

    private fun initPresent() {

    }

    private fun initEvent() {

        rl_mine_user.setOnClickListener {
            startActivity(Intent(context, UserActivity::class.java))
        }

        ll_mine_set_verify.setOnClickListener {
            startActivity(Intent(context, VerifyActivity::class.java))
        }

    }

}