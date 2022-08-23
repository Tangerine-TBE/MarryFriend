package com.twx.marryfriend.mutual

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.gyf.immersionbar.ImmersionBar
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.activity_mutual_like_dialog.*

class MutualLikeDialogActivity:AppCompatActivity(R.layout.activity_mutual_like_dialog) {
    companion object{
        private const val HEAD_IMAGE_KEY="head_image_k"
        fun getIntent(context: Context,taHead:String):Intent{
            val intent=Intent(context,MutualLikeDialogActivity::class.java)
            intent.putExtra(HEAD_IMAGE_KEY,taHead)
            return intent
        }
    }

    private val imageHead by lazy {
        intent.getStringExtra(HEAD_IMAGE_KEY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .statusBarColor(android.R.color.transparent)
            .init()
        Glide.with(myHead).load(UserInfo.getHeadPortrait()).into(myHead)
        Glide.with(taHead).load(imageHead).into(taHead)
        openVip.setOnClickListener {
            toast("开通vip")
        }
        closeMutual.setOnClickListener {
            finish()
        }
        mutualLikeView1.post {
            ScaleAnimation(1f,5f,1f,5f,
                mutualLikeView1.width/2f,mutualLikeView1.height/2f).also {
                it.duration=2000
//                it.repeatCount=Animation.INFINITE
                it.repeatMode= Animation.RESTART
                it.interpolator= LinearInterpolator()
                mutualLikeView1.startAnimation(it)
            }
            ScaleAnimation(1.5f,10f,1.5f,10f,mutualLikeView1.width/2f,mutualLikeView1.height/2f).also {
                it.duration=2000
//                it.repeatCount=Animation.INFINITE
                it.repeatMode= Animation.RESTART
                it.interpolator= LinearInterpolator()
                mutualLikeView2.startAnimation(it)
            }
            ScaleAnimation(2f,15f,2f,15f,mutualLikeView1.width/2f,mutualLikeView1.height/2f).also {
                it.duration=1000
//                it.repeatCount=Animation.INFINITE
                it.repeatMode= Animation.RESTART
                it.interpolator= LinearInterpolator()
                mutualLikeView3.startAnimation(it)
                it.setAnimationListener(object : Animation.AnimationListener{
                    override fun onAnimationStart(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        mutualLikeView1.visibility= View.GONE
                        mutualLikeView2.visibility= View.GONE
                        mutualLikeView3.visibility= View.GONE
                    }

                    override fun onAnimationRepeat(animation: Animation?) {

                    }
                })
            }
            val width=resources.displayMetrics.widthPixels
            AnimationSet(true).also {
                it.addAnimation(TranslateAnimation(-width/2f,0f,0f,0f))
                it.duration=500

                it.interpolator= LinearInterpolator()
                myHeadView.startAnimation(it)
            }
            AnimationSet(true).also {
                it.addAnimation(TranslateAnimation(width/2f,0f,0f,0f))
//                it.addAnimation(ScaleAnimation(0f,1f,0f,1f,taHeadView.width/2f,taHeadView.height/2f))
                it.duration=500
                it.interpolator= LinearInterpolator()
                taHeadView.startAnimation(it)
            }
        }
    }
}