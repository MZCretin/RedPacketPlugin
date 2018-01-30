package com.cretin.www.redpacketplugin.base;

import android.app.Application;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

/**
 * Created by cretin on 2018/1/29.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initHawk();
    }

    //手动配置Hawk
    private void initHawk() {
        Hawk.init(this).setEncryption(new NoEncryption()).build();
    }
}
