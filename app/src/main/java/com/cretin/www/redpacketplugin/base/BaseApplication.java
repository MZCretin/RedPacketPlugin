package com.cretin.www.redpacketplugin.base;

import android.app.Application;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

/**
 * Created by cretin on 2018/1/29.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //初始化Hawk
        initHawk();

        BmobConfig config = new BmobConfig.Builder(this)
                //设置appkey
                .setApplicationId("fdfdfed16ace6e7de64cb91955b9f10b")
                //请求超时时间（单位为秒）：默认15s
                .setConnectTimeout(30)
                //文件分片上传时每片的大小（单位字节），默认512*1024
                .setUploadBlockSize(1024 * 1024)
                //文件的过期时间(单位为秒)：默认1800s
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);
    }

    //手动配置Hawk
    private void initHawk() {
        Hawk.init(this).setEncryption(new NoEncryption()).build();
    }
}
