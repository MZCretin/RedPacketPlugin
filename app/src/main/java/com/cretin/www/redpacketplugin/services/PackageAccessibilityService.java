package com.cretin.www.redpacketplugin.services;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.cretin.www.redpacketplugin.config.Config;
import com.cretin.www.redpacketplugin.config.eventbus.EventBusMsg;
import com.cretin.www.redpacketplugin.config.eventbus.NotifyOnActivityStop;
import com.cretin.www.redpacketplugin.job.AccessbilityJob;
import com.cretin.www.redpacketplugin.job.WechatAccessbilityJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;


/**
 * Created by cretin on 2018/1/29.
 */

public class PackageAccessibilityService extends AccessibilityService {
    public static final String TAG = "HHHHHHHH";

    //当前任务列表 目前只有微信红包
    private static ArrayList<AccessbilityJob> mJobs = new ArrayList<>();

    //保存当前服务的实例
    private static PackageAccessibilityService service;

    public Config getConfig() {
        return Config.getConfig(this);
    }

    @Override
    public void onCreate() {
        Log.e(TAG,"onCreate");
        super.onCreate();
        EventBus.getDefault().register(this);
        WechatAccessbilityJob wechatJob = new WechatAccessbilityJob();
        wechatJob.onCreateJob(this);
        mJobs.add(wechatJob);
    }

    /**
     * 系统成功连接到辅助功能服务时调用，可以执行执行任何一次性设置步骤，包括连接到用户反馈系统服务，如音频管理器或设备振动器。还可以在此调用
     */
    @Override
    protected void onServiceConnected() {
        Log.e(TAG,"onServiceConnected");
        super.onServiceConnected();
        service = this;
        EventBusMsg msg = new EventBusMsg();
        msg.setType(EventBusMsg.ACCESSIBILITY_CONNECTED);
        EventBus.getDefault().post(msg);
    }

    /**
     * 当系统检测到与Accessibility服务指定的事件过滤参数匹配的AccessibilityEvent时调用。这是必须实现的方法，通常需要在该方法中根据AccessibilityEvent作出判断并执行一些处理。
     *
     * @param event
     */
    @TargetApi( Build.VERSION_CODES.JELLY_BEAN_MR2 )
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String eventTypeName = "";
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                eventTypeName = "TYPE_VIEW_CLICKED";
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                eventTypeName = "TYPE_VIEW_FOCUSED";
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                eventTypeName = "TYPE_VIEW_LONG_CLICKED";
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                eventTypeName = "TYPE_VIEW_SELECTED";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                eventTypeName = "TYPE_VIEW_TEXT_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                eventTypeName = "TYPE_WINDOW_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                eventTypeName = "TYPE_NOTIFICATION_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                eventTypeName = "TYPE_TOUCH_EXPLORATION_GESTURE_END";
                break;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                eventTypeName = "TYPE_ANNOUNCEMENT";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                eventTypeName = "TYPE_TOUCH_EXPLORATION_GESTURE_START";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                eventTypeName = "TYPE_VIEW_HOVER_ENTER";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                eventTypeName = "TYPE_VIEW_HOVER_EXIT";
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                eventTypeName = "TYPE_VIEW_SCROLLED";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                eventTypeName = "TYPE_VIEW_TEXT_SELECTION_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                eventTypeName = "TYPE_WINDOW_CONTENT_CHANGED";
                break;
        }
        Log.e(TAG, "eventType:" + eventType);
        Log.e(TAG, "eventTypeName:" + eventTypeName);



        Log.e(TAG,"onAccessibilityEvent");
        String pkn = String.valueOf(event.getPackageName());
        for (AccessbilityJob job : mJobs) {
            if(pkn.equals(job.getTargetPackageName()) && job.isEnable()) {
                job.onReceiveJob(event);
                break;
            }
        }
    }

    /**
     * 当系统想要中断服务提供的反馈时调用，通常是响应用户操作，如将焦点移动到其他控件。
     */
    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
        Toast.makeText(this, "中断抢红包服务", Toast.LENGTH_SHORT).show();
    }

    /**
     * 当系统即将关闭辅助功能服务时调用，可以执行任何一次性关机程序，包括取消分配用户反馈系统服务，例如音频管理器或设备振动器。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Log.e(TAG,"onDestory");
        service = null;
        EventBusMsg msg = new EventBusMsg();
        msg.setType(EventBusMsg.ACCESSIBILITY_DISCONNECTED);
        EventBus.getDefault().post(msg);
        //关闭Job
        if ( !mJobs.isEmpty() ) {
            for ( AccessbilityJob job :
                    mJobs ) {
                job.onStopJob();
            }
        }
    }

    @TargetApi( Build.VERSION_CODES.N )
    @Subscribe
    public void onActivityStop(NotifyOnActivityStop event){
        service.disableSelf();
    }



//    @TargetApi( Build.VERSION_CODES.JELLY_BEAN )
//    public static boolean isRunning() {
//        if ( service == null ) {
//            return false;
//        }
//        AccessibilityManager accessibilityManager = ( AccessibilityManager ) service.getSystemService(Context.ACCESSIBILITY_SERVICE);
//        AccessibilityServiceInfo info = service.getServiceInfo();
//        if ( info == null ) {
//            return false;
//        }
//        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
//        Iterator<AccessibilityServiceInfo> iterator = list.iterator();
//
//        boolean isConnect = false;
//        while ( iterator.hasNext() ) {
//            AccessibilityServiceInfo i = iterator.next();
//            if ( i.getId().equals(info.getId()) ) {
//                isConnect = true;
//                break;
//            }
//        }
//        if ( !isConnect ) {
//            return false;
//        }
//        return true;
//    }
}
