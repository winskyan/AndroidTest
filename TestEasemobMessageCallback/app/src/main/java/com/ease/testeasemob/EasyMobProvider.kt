package com.ease.testeasemob

import android.content.Context
import com.hyphenate.EMCallBack
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMOptions
import com.hyphenate.push.EMPushConfig
import com.hyphenate.push.EMPushHelper
import com.hyphenate.push.EMPushType
import com.hyphenate.push.PushListener
import com.hyphenate.util.EMLog

class EasyMobProvider : EMMessageListener {
    private var mChatter: String? = null
    private var mOnChatterMessageChangedListener: (() -> Unit)? = null
    private var mInitialized: MutableMap<String, Boolean>? = null

    fun init(ctx: Context) {
        val options = EMOptions()

        // 默认添加好友时，是不需要验证的，改成需要验证
//        options.acceptInvitationAlways = false
        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.autoTransferMessageAttachments = false
        // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(false)
        /**
         * NOTE:你需要设置自己申请的账号来使用三方推送功能，详见集成文档
         */
        // TODO：push支持
//        val builder = EMPushConfig.Builder(applicationContext)
//        builder.enableVivoPush() // 需要在AndroidManifest.xml中配置appId和appKey
//            .enableMeiZuPush("134952", "f00e7e8499a549e09731a60a4da399e3")
//            .enableMiPush("2882303761517426801", "5381742660801")
//            .enableOppoPush(
//                "0bb597c5e9234f3ab9f821adbeceecdb",
//                "cd93056d03e1418eaa6c3faf10fd7537"
//            )
//            .enableHWPush() // 需要在AndroidManifest.xml中配置appId
//            .enableFCM("921300338324")
//        options.pushConfig = builder.build()
        //初始化
        EMClient.getInstance().init(ctx, options)
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(BuildConfig.DEBUG)

        initPush()

        EMClient.getInstance().chatManager().addMessageListener(this)
    }

    /**
     * 登录聊天服务器
     *
     * @param username String 用户名
     * @param token String
     */
    fun login(username: String, token: String) {
        EMClient.getInstance().login(username, token, object : EMCallBack {
            //回调
            override fun onSuccess() {
                EMClient.getInstance().chatManager().loadAllConversations()
                EMLog.d("测试", "登录聊天服务器成功！")
            }

            override fun onProgress(progress: Int, status: String) {}
            override fun onError(code: Int, message: String) {
                EMLog.d("测试", "登录聊天服务器失败 $message")
            }
        })
    }

    /**
     * 初始化push
     */
    private fun initPush() {
        EMPushHelper.getInstance().setPushListener(object : PushListener() {
            override fun onError(pushType: EMPushType, errorCode: Long) {
                // TODO: 返回的errorCode仅9xx为环信内部错误，可从EMError中查询，其他错误请根据pushType去相应第三方推送网站查询。
                EMLog.e("PushClient", "Push client occur a error: $pushType - $errorCode")
            }

            override fun isSupportPush(
                pushType: EMPushType,
                pushConfig: EMPushConfig
            ): Boolean {
                // 由外部实现代码判断设备是否支持FCM推送
//                    if (pushType == EMPushType.FCM) {
//                        LogUtils.d(
//                            "FCM",
//                            "GooglePlayServiceCode:" + GoogleApiAvailabilityLight.getInstance()
//                                .isGooglePlayServicesAvailable(context)
//                        )
//                        return demoModel.isUseFCM() && GoogleApiAvailabilityLight.getInstance()
//                            .isGooglePlayServicesAvailable(context) === ConnectionResult.SUCCESS
//                    }
                return super.isSupportPush(pushType, pushConfig)
            }
        })
    }

    /**
     * 发送文本消息
     *
     * @param receiverId String
     * @param text String
     */
    fun sendTextMessage(receiverId: String, text: String) {
        val message = EMMessage.createTxtSendMessage(text, receiverId)
        message.setMessageStatusCallback(object : EMCallBack {
            override fun onSuccess() {
                EMLog.e("测试", "=====onSuccess=====")
                mOnChatterMessageChangedListener?.invoke()
            }

            override fun onError(code: Int, error: String?) {
                EMLog.e("测试", "=====onError=====$error")
                mOnChatterMessageChangedListener?.invoke()
            }

            override fun onProgress(progress: Int, status: String?) {
                EMLog.e("测试", "=====onProgress=====")
                mOnChatterMessageChangedListener?.invoke()
            }

        })
        EMLog.e("测试", "sendTextMessage start send.   text=$text")
        EMClient.getInstance().chatManager().sendMessage(message)
        EMLog.e("测试", "sendTextMessage end send")
    }


    fun setOnMessageChangedListener(chatter: String, listener: () -> Unit) {
        mChatter = chatter
        mOnChatterMessageChangedListener = listener
    }

    fun release() {
        if (EMClient.getInstance().isLoggedInBefore)
            EMClient.getInstance().logout(true)
    }

    //-----------------------消息回调-----------------
    override fun onMessageReceived(messages: MutableList<EMMessage>?) {
        EMLog.d("EasyMobProvider", "onMessageReceived:${messages?.size}")
    }

    override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {
        EMLog.d("EasyMobProvider", "onCmdMessageReceived:${messages?.size}")
    }

    override fun onMessageRead(messages: MutableList<EMMessage>?) {
        EMLog.d("EasyMobProvider", "onMessageRead:${messages?.size}")
    }

    override fun onMessageDelivered(messages: MutableList<EMMessage>?) {
        EMLog.d("EasyMobProvider", "onMessageDelivered:${messages?.size}")
    }

    override fun onMessageRecalled(messages: MutableList<EMMessage>?) {
        EMLog.d("EasyMobProvider", "onMessageRecalled:${messages?.size}")
    }

    override fun onMessageChanged(message: EMMessage?, change: Any?) {
        EMLog.d("EasyMobProvider", "onMessageChanged:${message}")
    }

    //-----------------------end 消息回调-----------------
}