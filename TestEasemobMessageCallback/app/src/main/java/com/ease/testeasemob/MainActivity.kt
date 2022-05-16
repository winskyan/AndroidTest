package com.ease.testeasemob

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation

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

        easyMobProvider.setOnMessageChangedListener("user2222") {
            EMClient.getInstance().chatManager().loadAllConversations()
            val conversion = EMClient.getInstance().chatManager()
                .getConversation("user2222", EMConversation.EMConversationType.Chat)
            val allMessages = conversion.allMessages
            var messageContent = "";
            for (value in allMessages) {
                messageContent += value.body.toString()
            }
            runOnUiThread { tv.text = messageContent }

        }
    }
}