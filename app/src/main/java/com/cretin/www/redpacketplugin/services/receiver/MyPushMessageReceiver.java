package com.cretin.www.redpacketplugin.services.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cretin.www.redpacketplugin.config.eventbus.NotifyForceExit;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.push.PushConstants;

public class MyPushMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ( intent.getAction().equals(PushConstants.ACTION_MESSAGE) ) {
            String msg = intent.getStringExtra("msg");
            if ( !TextUtils.isEmpty(msg) && msg.contains("MESSAGE_YIDI_LOGIN") ) {
                EventBus.getDefault().post(new NotifyForceExit());
            }
        }
    }
}