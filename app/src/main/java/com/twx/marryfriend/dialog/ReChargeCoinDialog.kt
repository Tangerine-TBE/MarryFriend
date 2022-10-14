package com.twx.marryfriend.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.twx.marryfriend.BuildConfig
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.coin.CoinViewModel
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.dialog_recharge_coin.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * 充值金币对话框
 */
class ReChargeCoinDialog(private val fragmentActivity: FragmentActivity):Dialog(fragmentActivity) {
    private val coinViewModel by lazy {
        ViewModelProvider(fragmentActivity).get(CoinViewModel::class.java)
    }
    private var level=1
    private var money="0.01"
        get() {
            if (BuildConfig.DEBUG){
                return "0.01"
            }else{
                return field
            }
        }
    private val loadingDialog by lazy {
        LoadingDialogManager.createLoadingDialog()
            .create(fragmentActivity)
            .setCancelable(false)
    }

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_recharge_coin)
        myHead.setImageResource(UserInfo.getReversedDefHeadImage())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog_close.setOnClickListener {
            dismiss()
        }
        tv_coin_pay.setOnClickListener {
            fragmentActivity.lifecycleScope.launch {
                try {
                    coinViewModel.startAliPay(level,money,fragmentActivity)
                    toast(fragmentActivity,"充值成功")
                }catch (e:Exception){
                    toast(fragmentActivity,e.message?:"支付失败")
                }
                dismiss()
            }
        }
        fragmentActivity.lifecycleScope.launch {
            loadingDialog.show()
            val a1=async {
                try {
                    coinViewModel.getCoin()
                }catch (e:Exception){
                    toast(context,"获取金币失败")
                    0
                }
            }
            val a2=async {
                coinViewModel.getCoinPrice()
            }

            coinTotalText.text=a1.await().toString()

            a2.await().forEachIndexed { index, coinPriceData ->
                when(index){
                    0->{
                        tv_coin_one_coin.text=coinPriceData.describe
                        tv_coin_one_price.text="￥${coinPriceData.now_price}"
                        ll_coin_one.setOnClickListener {
                            ll_coin_one.isSelected=true
                            ll_coin_two.isSelected=false
                            ll_coin_three.isSelected=false
                            money=coinPriceData.now_price
                            level=coinPriceData.level
                        }
                        ll_coin_one.performClick()
                    }
                    1->{
                        tv_coin_two_coin.text=coinPriceData.describe
                        tv_coin_two_price.text="￥${coinPriceData.now_price}"
                        ll_coin_two.setOnClickListener {
                            ll_coin_one.isSelected=false
                            ll_coin_two.isSelected=true
                            ll_coin_three.isSelected=false
                            money=coinPriceData.now_price
                            level=coinPriceData.level
                        }
                    }
                    2->{
                        tv_coin_three_coin.text=coinPriceData.describe
                        tv_coin_three_price.text="￥${coinPriceData.now_price}"
                        ll_coin_three.setOnClickListener {
                            ll_coin_one.isSelected=false
                            ll_coin_two.isSelected=false
                            ll_coin_three.isSelected=true
                            money=coinPriceData.now_price
                            level=coinPriceData.level
                        }
                    }
                }
            }
            loadingDialog.dismiss()
        }
    }

    fun show(head:String?){
        super.show()
        Glide.with(myHead)
            .load(head)
            .placeholder(UserInfo.getReversedDefHeadImage())
            .error(UserInfo.getReversedDefHeadImage())
            .into(myHead)
    }
}