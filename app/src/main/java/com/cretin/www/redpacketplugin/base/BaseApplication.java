package com.cretin.www.redpacketplugin.base;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import com.cretin.www.cretinautoupdatelibrary.utils.CretinAutoUpdateUtils;
import com.cretin.www.redpacketplugin.R;
import com.orhanobut.hawk.Hawk;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by cretin on 2018/1/29.
 */

public class BaseApplication extends Application {
    private static Handler handler;
    private static BaseApplication baseApplication;

    public static BaseApplication getBaseApplication() {
        return baseApplication;
    }

    {
        /**
         * <Wechat
         AppId="wx883da595fd515e72"
         AppSecret="c6799828efd645ac42c7077b2f12f2f5"/>

         <QQ
         AppId="1106291770"
         AppKey="WxnMR18KE5bcMp1c"/>
         */
//        PlatformConfig.setWeixin("wx883da595fd515e72", "39d83912590945dc306147347cf7cb8b");
        PlatformConfig.setWeixin("wx7220be2a6b8a9211", "a461b319deea08594e5ca5cd1d95cb20");
//        PlatformConfig.setQQZone("1106291770", "WxnMR18KE5bcMp1c");
        Config.DEBUG = true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
        handler = new Handler();

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

        // 使用推送服务时的初始化操作
        BmobInstallationManager.getInstance().initialize(new InstallationListener<BmobInstallation>() {
            @Override
            public void done(BmobInstallation bmobInstallation, BmobException e) {
                if ( e == null ) {
                    Log.e("HHHHHHH", bmobInstallation.getObjectId() + "-" + bmobInstallation.getInstallationId());
                } else {
                    Log.e("HHHHHHH", e.getMessage());
                }
            }
        });
        // 启动推送服务
        BmobPush.startWork(this);

        //友盟统计
        UMConfigure.init(this, "5a7c0c1bf43e48616e0001ee"
                , "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_DUM_NORMAL);

        //友盟分享
        UMShareAPI.get(this);

        //初始化Hawk
        initHawk();

        CretinAutoUpdateUtils.Builder builder = new CretinAutoUpdateUtils.Builder()
                .setIgnoreThisVersion(true)
                .setShowType(CretinAutoUpdateUtils.Builder.TYPE_DIALOG_WITH_BACK_DOWN)
                .setIconRes(R.mipmap.hongbao)
                .showLog(true)
                .setRequestMethod(CretinAutoUpdateUtils.Builder.METHOD_GET)
                .build();
        CretinAutoUpdateUtils.init(builder);
    }

    //手动配置Hawk
    private void initHawk() {
//        Hawk.init(this)
//                .setEncryptionMethod(HawkBuilder.EncryptionMethod.MEDIUM)
//                .setStorage(HawkBuilder.newSqliteStorage(this))
//                .setLogLevel(LogLevel.FULL)
//                .build();

        Hawk.init(this).build();
    }

    public static Handler getHandler() {
        return handler;
    }
}
