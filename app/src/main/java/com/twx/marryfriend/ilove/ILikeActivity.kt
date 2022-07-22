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

//https://lanhuapp.com/web/#/item/project/detailDetach?pid=0f172b45-d776-4080-a03e-618374ed56e4&image_id=765fbf73-31d9-473f-870c-33178d1822c3&tid=5173cb5f-00ad-4a38-b103-b616ccec0e12&project_id=0f172b45-d776-4080-a03e-618374ed56e4&fromEditor=true&type=image
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