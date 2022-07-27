package com.twx.marryfriend.message

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.message.ImMessageManager
import com.message.ImUserManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageFragment : Fragment(R.layout.fragment_message) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

/*        createAccount.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    ImUserManager.createAccount(UserInfo.getUserId(), "123456")
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        toast("创建用户失败,${e.message}")
                    }
                }
            }
        }
        login.setOnClickListener {
            ImUserManager.login(UserInfo.getUserId(),"123456")
        }
        sendMsg.setOnClickListener {
            val id=editText1.text.toString()
            val content=editText2.text.toString()
            ImMessageManager.sendTextMsg(id,content)
        }*/
    }
}