package com.ease.testeasemob

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMOptions
import com.hyphenate.easeui.EaseIM
import com.hyphenate.util.EMLog

class MainActivity : AppCompatActivity() {
    private lateinit var easyMobProvider: EasyMobProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        easyMobProvider = EasyMobProvider()
        easyMobProvider.init(this.applicationContext)
        easyMobProvider.login("liu", "1")
    }


    override fun onResume() {
        super.onResume()
        val tv = findViewById<TextView>(R.id.test_btn)
        tv.setOnClickListener(View.OnClickListener {
            easyMobProvider.sendTextMessage("user2222", "test send message from user1111")
        })


//        EMClient.getInstance().loginWithToken("liu", "1", object : EMCallBack {
//            override fun onSuccess() {
//                EMLog.e("测试", "=====login success=====")
//            }
//
//            override fun onError(code: Int, error: String?) {
//                EMLog.e("测试", "=====login onError=====$error")
//            }
//
//            override fun onProgress(progress: Int, status: String?) {
//                EMLog.e("测试", "=====onProgress onProgress=====$status")
//            }
//        })
    }
}