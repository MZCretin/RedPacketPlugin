package com.cretin.www.redpacketplugin.job;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.cretin.www.redpacketplugin.config.Config;
import com.cretin.www.redpacketplugin.model.RedPackageInfoModel;
import com.cretin.www.redpacketplugin.services.PackageAccessibilityService;
import com.cretin.www.redpacketplugin.utils.AccessibilityHelper;
import com.cretin.www.redpacketplugin.utils.CommonUtils;
import com.cretin.www.redpacketplugin.utils.KV;
import com.cretin.www.redpacketplugin.utils.LocalStorageKeys;
import com.cretin.www.redpacketplugin.utils.NotifyHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WechatAccessbilityJob extends BaseAccessbilityJob {

    private static final String TAG = "WechatAccessbilityJob";

    /**
     * 微信的包名
     */
    public static final String WECHAT_PACKAGENAME = "com.tencent.mm";

    /**
     * 红包消息的关键字
     */
    private static final String HONGBAO_TEXT_KEY = "[微信红包]";

    private PackageInfo mWechatPackageInfo = null;
    private Handler mHandler = null;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新安装包信息
            updatePackageInfo();
        }
    };

    @Override
    public void onCreateJob(PackageAccessibilityService service) {
        super.onCreateJob(service);

        updatePackageInfo();

        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");

        getContext().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onStopJob() {
        try {
            getContext().unregisterReceiver(broadcastReceiver);
        } catch ( Exception e ) {
        }
    }

    @TargetApi( Build.VERSION_CODES.JELLY_BEAN_MR2 )
    @Override
    public void onNotificationPosted(Notification notification) {
        String text = String.valueOf(notification.tickerText);
        notificationEvent(text, notification);
    }

    @Override
    public boolean isEnable() {
        return getConfig().isEnableWechat();
    }

    @Override
    public String getTargetPackageName() {
        return WECHAT_PACKAGENAME;
    }

    @Override
    public void onReceiveJob(AccessibilityEvent event) {
        handleHongBao(event);
    }

    /**
     * 通知栏事件
     */
    private void notificationEvent(String ticker, Notification nf) {
        String text = ticker;
        int index = text.indexOf(":");
        if ( index != -1 ) {
            text = text.substring(index + 1);
        }
        text = text.trim();
        if ( text.contains(HONGBAO_TEXT_KEY) ) { //红包消息
            newHongBaoNotification(nf);
        }
    }

    /**
     * 打开通知栏消息
     */
    @TargetApi( Build.VERSION_CODES.JELLY_BEAN )
    private void newHongBaoNotification(Notification notification) {
        //以下是精华，将微信的通知栏消息打开
        PendingIntent pendingIntent = notification.contentIntent;
        boolean lock = NotifyHelper.isLockScreen(getContext());

        if ( !lock ) {
            NotifyHelper.send(pendingIntent);
        } else {
            NotifyHelper.showNotify(getContext(), String.valueOf(notification.tickerText), pendingIntent);
        }

        if ( lock || getConfig().getWechatMode() != Config.WX_MODE_0 ) {
            NotifyHelper.playEffect(getContext(), getConfig());
        }
    }


    /**
     * 收到聊天里的红包
     *
     * @param event
     */
    @TargetApi( Build.VERSION_CODES.JELLY_BEAN_MR2 )
    private void handleHongBao(AccessibilityEvent event) {
        if ( event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED ) {
            Parcelable data = event.getParcelableData();
            if ( data == null || !(data instanceof Notification) ) {
                return;
            }
            List<CharSequence> texts = event.getText();
            if ( !texts.isEmpty() ) {
                String text = String.valueOf(texts.get(0));
                notificationEvent(text, ( Notification ) data);
            }
        } else {
            AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();
            if ( nodeInfo == null ) {
                //如果一直为空 八成是服务断了
                Log.w(TAG, "rootWindow为空");
                nodeInfo = event.getSource();
                if ( nodeInfo == null ) {
                    Log.w(TAG, "rootWindow为空 +1");
                    return;
                }
            }
            /**
             * 说明 点击红包后的操作和红包详情都交给className来判断
             */
            String className = event.getClassName().toString();
            Log.e("HHHHHHH", "className   " + className);
            if ( "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(className) ) {
                //点中了红包 有两种操作 一种是点开红包  一种是手慢了
                /**
                 * 一种是点开红包
                 */
                //获取开按钮
                List<AccessibilityNodeInfo> kaiNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c2i");
                //获取 手慢了 提示语句的控件
                List<AccessibilityNodeInfo> slowNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c2h");
                //获取关闭按钮
                List<AccessibilityNodeInfo> closeNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c07");
                if ( !kaiNodes.isEmpty() ) {
                    //获取到开按钮 点击此按钮
                    NotifyHelper.playEffect(getContext(), getConfig());
                    AccessibilityHelper.performClick(kaiNodes.get(0));
                } else {
                    if ( !slowNodes.isEmpty() && !closeNodes.isEmpty() )
                        //手慢了 提示语句的控件 关闭对话框
                        AccessibilityHelper.performClick(closeNodes.get(0));
                }
            } else if ( "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(className) ) {
                //拆完红包后看详细的纪录界面 这里提取下数据后退出就好
                RedPackageInfoModel indo = new RedPackageInfoModel();
                indo.setPackageTime(CommonUtils.timeLongFormatToStr(new Date(System.currentTimeMillis()).getTime()));
                //获取金额关闭按钮
                List<AccessibilityNodeInfo> moneyNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/byw");
                if ( !moneyNodes.isEmpty() ) {
                    String money = moneyNodes.get(0).getText().toString();
                    try {
                        double moneyDouble = Double.parseDouble(money);
                        indo.setMoney(moneyDouble);
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
                //获取发红包的用户名
                List<AccessibilityNodeInfo> userNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bys");
                if ( !userNodes.isEmpty() ) {
                    String username = userNodes.get(0).getText().toString();
                    indo.setOrigin(username);
                }
                //获取多长时间被抢完的控件  有这个控件代表是群红包
                List<AccessibilityNodeInfo> timeNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bzb");
                //红包类型 0 私包 1 群红包 普通红包 2 群红包 拼手气
                if ( !timeNodes.isEmpty() ) {
                    //群红包
                    //获取拼手气的图标 有 代表是平手气
                    List<AccessibilityNodeInfo> pinNodes =
                            nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/byt");
                    if ( pinNodes.isEmpty() ) {
                        //普通
                        indo.setType(1);
                    } else {
                        //拼手气
                        indo.setType(2);
                    }
                } else {
                    //个人红包
                    indo.setType(0);
                }

                //保存数据到hawk
                List<RedPackageInfoModel> list = KV.get(LocalStorageKeys.RED_PACKAGE_LIST);
                if ( list == null ) {
                    list = new ArrayList<>();
                }
                list.add(indo);
                KV.put(LocalStorageKeys.RED_PACKAGE_LIST, list);

                //获取关闭按钮
                List<AccessibilityNodeInfo> closeNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/hp");
                if ( !closeNodes.isEmpty() ) {
                    //关掉
                    AccessibilityHelper.performClick(closeNodes.get(0));
                    return;
                } else {
                    AccessibilityHelper.performBack(getService());
                }
            } else {
                /**
                 * 有一种情况 就是点开了红包准备抢的时候  红包被人抢了 现在会由开红包 去到 手慢了  但是不会触发
                 * com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI 所以要做额外处理
                 */
                //获取关闭按钮
                List<AccessibilityNodeInfo> closeNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c07");
                //获取 手慢了 提示语句的控件
                List<AccessibilityNodeInfo> slowNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c2h");
                if ( !slowNodes.isEmpty() && "手慢了，红包派完了".equals(slowNodes.get(0).getText().toString()) ) {
                    if ( !closeNodes.isEmpty() ) {
                        //关掉
                        AccessibilityHelper.performClick(closeNodes.get(0));
                        return;
                    }
                }

                //处理其他情况
                //获取聊天页面的按钮 如果有则代表是聊天页面
                List<AccessibilityNodeInfo> chatNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aak");
                if ( chatNodes.isEmpty() ) {
                    Log.e("MMMMMM", "不在聊天页面 不好说在哪儿");
                    //不在聊天页面 不好说在哪儿
                    //获取首页的listview 的 item 的 列表
                    List<AccessibilityNodeInfo> listItemNodes =
                            nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/apr");
                    if ( listItemNodes.isEmpty() ) {
                        //反正不是在首页 不理会
                        return;
                    } else {
                        //在首页
                        List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/apv");
                        if ( nodes != null ) {
                            for ( AccessibilityNodeInfo node :
                                    nodes ) {
                                if ( node.getText().toString().contains("[微信红包]") ) {
                                    //还要判断是否有未读消息
                                    AccessibilityNodeInfo parent = node.getParent();
                                    if ( parent != null ) {
                                        List<AccessibilityNodeInfo> numsNodes =
                                                parent.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/iu");
                                        if ( !numsNodes.isEmpty() ) {
                                            CharSequence text = numsNodes.get(0).getText();
                                            if ( text != null ) {
                                                if ( Integer.parseInt(text.toString()) != 0 ) {
                                                    //此时才能跳转
                                                    AccessibilityHelper.performClick(parent);
                                                }
                                            }
                                        }
                                    }
                                    return;
                                }
                            }
                        }
                    }
                } else {
                    Log.e("MMMMMM", "在聊天页面");
                    //在聊天页面
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");
                    if ( list == null )
                        return;
                    if ( list.isEmpty() ) {
                        //没有 直接返回
                        //找到聊天页面的返回按钮
                        List<AccessibilityNodeInfo> backNodes =
                                nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/h_");
                        if ( !backNodes.isEmpty() ) {
                            Log.e("MMMMMM", "没有 直接返回   关闭了-----");
                            AccessibilityHelper.performClick(backNodes.get(0));
                        }
                    } else {
                        //有 但是要检查是不是红包
                        for ( int i = list.size() - 1; i >= 0; i-- ) {
                            AccessibilityNodeInfo node = list.get(i);
                            AccessibilityNodeInfo parent = node.getParent();
                            if ( parent != null ) {
                                List<AccessibilityNodeInfo> wxhbNodes =
                                        parent.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aec");
                                if ( !wxhbNodes.isEmpty() ) {
                                    if ( "微信红包".equals(wxhbNodes.get(0).getText()) ) {
                                        //是的 没错  领取红包
                                        AccessibilityHelper.performClick(node);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Handler getHandler() {
        if ( mHandler == null ) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    /**
     * 获取微信的版本
     */
    private int getWechatVersion() {
        if ( mWechatPackageInfo == null ) {
            return 0;
        }
        return mWechatPackageInfo.versionCode;
    }

    /**
     * 更新微信包信息
     */
    private void updatePackageInfo() {
        try {
            mWechatPackageInfo = getContext().getPackageManager().getPackageInfo(WECHAT_PACKAGENAME, 0);
        } catch ( PackageManager.NameNotFoundException e ) {
            e.printStackTrace();
        }
    }
}
