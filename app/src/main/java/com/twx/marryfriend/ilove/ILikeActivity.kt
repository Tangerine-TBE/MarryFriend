package com.twx.marryfriend.ilove

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gyf.immersionbar.ImmersionBar
import com.twx.marryfriend.R
import kotlinx.android.synthetic.main.activity_ilike.*

class ILikeActivity:AppCompatActivity(R.layout.activity_ilike) {
    private val fragments by lazy {
        arrayOf(LikePeopleFragment(),DislikePeopleFragment(),SuperLikePeopleFragment())
    }
    private val titles by lazy {
        arrayOf("喜欢的人","无感的人","超级喜欢的人")
    }
    private val likeViewModel by lazy {
        ViewModelProvider(this).get(LikeViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .statusBarColorInt(Color.TRANSPARENT)
            .statusBarDarkFont(true)
            .init()
        initListener()
        ilikeViewPager.adapter=object :FragmentStateAdapter(this){
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }
        TabLayoutMediator(ilikeTabLayout,ilikeViewPager,object : TabLayoutMediator.TabConfigurationStrategy {
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                val itemView=LayoutInflater.from(ilikeTabLayout.context).inflate(R.layout.item_ilike_tab,ilikeTabLayout,false)
                tab.customView=itemView
                itemView.findViewById<TextView>(R.id.ilikeTabName).text = titles[position]
            }
        }).attach()
    }

    private fun initListener(){
        goBack.setOnClickListener {
            finish()
        }
        likeViewModel.addSuperLikeChangeListener{
            val index=fragments.indexOfFirst {
                it is SuperLikePeopleFragment
            }
            if (index>=0){
                ilikeViewPager.currentItem=index
            }
        }
    }
}