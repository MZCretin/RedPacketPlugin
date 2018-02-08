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
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.cretin.www.redpacketplugin.config.eventbus.NotifyDataNotPrepare;
import com.cretin.www.redpacketplugin.config.eventbus.NotifyDataNotPreparedSuccess;
import com.cretin.www.redpacketplugin.model.CusUser;
import com.cretin.www.redpacketplugin.model.RedPackageInfoModel;
import com.cretin.www.redpacketplugin.model.UserInfoModel;
import com.cretin.www.redpacketplugin.model.WeixinNodeModel;
import com.cretin.www.redpacketplugin.services.PackageAccessibilityService;
import com.cretin.www.redpacketplugin.utils.AccessibilityHelper;
import com.cretin.www.redpacketplugin.utils.CommonUtils;
import com.cretin.www.redpacketplugin.utils.KV;
import com.cretin.www.redpacketplugin.utils.LocalStorageKeys;
import com.cretin.www.redpacketplugin.utils.NotifyHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WechatAccessbilityJob extends BaseAccessbilityJob {

    private static final String TAG = "WechatAccessbilityJob";

    private String CLASS_NAME_RECEIVE_UI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    private String CLASS_NAME_DETAIL_UI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
    private String VIEW_ID_RECEIVE_BTN_OPEN = "com.tencent.mm:id/c2i";
    private String VIEW_ID_RECEIVE_TV_SHOWMANLE = "com.tencent.mm:id/c2h";
    private String VIEW_ID_RECEIVE_CLOSE = "com.tencent.mm:id/c07";
    private String VIEW_ID_DETAIL_MONEY = "com.tencent.mm:id/byw";
    private String VIEW_ID_DETAIL_USERNAME = "com.tencent.mm:id/bys";
    private String VIEW_ID_DETAIL_TIME_TO_QIANG = "com.tencent.mm:id/bzb";
    private String VIEW_ID_DETAIL_PINSHOUQI = "com.tencent.mm:id/byt";
    private String VIEW_ID_DETAIL_CLOSE = "com.tencent.mm:id/hp";
    private String VIEW_ID_CHATTING_TV_ADD = "com.tencent.mm:id/aak";
    private String VIEW_ID_HOME_LV_ITEM = "com.tencent.mm:id/apr";
    private String VIEW_ID_HOME_LV_ITEM_CONTENT = "com.tencent.mm:id/apv";
    private String VIEW_ID_HOME_LV_ITEM_NUMBER = "com.tencent.mm:id/iu";
    private String VIEW_ID_CHATTING_TV_TITLE = "com.tencent.mm:id/ha";
    private String VIEW_ID_HOME_LV_ITEM_LABEL_WXHB = "com.tencent.mm:id/aec";
    private String VIEW_ID_CHATTING_TV_BACK = "com.tencent.mm:id/h_";
    private String FLAG = "UN_PREPARE";

    /**
     * 微信的包名
     */
    private String WECHAT_PACKAGENAME = "com.tencent.mm";

    /**
     * 红包消息的关键字
     */
    private String HONGBAO_TEXT_KEY = "[微信红包]";
    private String TEXT_SHOUMANLE = "手慢了，红包派完了";
    private String TEXT_LINGQUHONGBAO = "领取红包";
    private String TEXT_LV_ITEM_TIPS = "微信红包";

    private PackageInfo mWechatPackageInfo = null;
    private Handler mHandler = null;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新安装包信息
            updatePackageInfo();
            setMainInfo();
        }
    };

    @Override
    public void onCreateJob(PackageAccessibilityService service) {
        super.onCreateJob(service);
        EventBus.getDefault().register(this);
        updatePackageInfo();

        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");

        getContext().registerReceiver(broadcastReceiver, filter);

        setMainInfo();
    }

    @Override
    public void onStopJob() {
        EventBus.getDefault().unregister(this);
        try {
            getContext().unregisterReceiver(broadcastReceiver);
        } catch ( Exception e ) {
        }
    }

    @Subscribe
    public void notifyDataNotPreparedSuccess(NotifyDataNotPreparedSuccess event) {
        //未准备就绪
        setMainInfo();
        Log.e("HHHHHHHHHHH", "6666666666666");
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
    public void onReceiveJob(AccessibilityEvent event, CusUser cusUser) {
        if ( !"PREPARE".equals(FLAG) ) {
            //未准备就绪
            EventBus.getDefault().post(new NotifyDataNotPrepare());
        } else {
            //数据已加载
            handleHongBao(event, cusUser);
        }
    }

    //设置参数
    private void setMainInfo() {
        WeixinNodeModel weixinNodeModel =
                KV.get(LocalStorageKeys.WEIXINNODEMODEL_SINGLE);
        if ( weixinNodeModel != null ) {
            CLASS_NAME_RECEIVE_UI = weixinNodeModel.getCL_NAME_RV_UI();
            CLASS_NAME_DETAIL_UI = weixinNodeModel.getCL_NAME_EL_UI();
            VIEW_ID_RECEIVE_BTN_OPEN = weixinNodeModel.getID_RV_BTN_OPEN();
            VIEW_ID_RECEIVE_TV_SHOWMANLE = weixinNodeModel.getID_RV_TV_SHOWMANLE();
            VIEW_ID_RECEIVE_CLOSE = weixinNodeModel.getID_RV_CLOSE();
            VIEW_ID_DETAIL_MONEY = weixinNodeModel.getID_EL_MONEY();
            VIEW_ID_DETAIL_USERNAME = weixinNodeModel.getID_EL_USERNAME();
            VIEW_ID_DETAIL_TIME_TO_QIANG = weixinNodeModel.getID_EL_TIME_TO_QIANG();
            VIEW_ID_DETAIL_PINSHOUQI = weixinNodeModel.getID_EL_PINSHOUQI();
            VIEW_ID_DETAIL_CLOSE = weixinNodeModel.getID_EL_CLOSE();
            VIEW_ID_CHATTING_TV_ADD = weixinNodeModel.getID_CHATTING_TV_ADD();
            VIEW_ID_HOME_LV_ITEM = weixinNodeModel.getID_HO_LV_IM();
            VIEW_ID_HOME_LV_ITEM_CONTENT = weixinNodeModel.getID_HO_LV_IM_CONTENT();
            VIEW_ID_HOME_LV_ITEM_NUMBER = weixinNodeModel.getID_HO_LV_IM_NUMBER();
            VIEW_ID_CHATTING_TV_TITLE = weixinNodeModel.getID_CHATTING_TV_TITLE();
            VIEW_ID_HOME_LV_ITEM_LABEL_WXHB = weixinNodeModel.getID_HO_LV_IM_LB_WXHB();
            VIEW_ID_CHATTING_TV_BACK = weixinNodeModel.getID_CHATTING_TV_BACK();
            String commonInfo = weixinNodeModel.getCOMMEN_TEXT_INFO();
            String split = weixinNodeModel.getSTR_SPLIT();
            String[] splits = commonInfo.split(split);
//            com.tencent.mm [微信红包] 手慢了，红包派完了 领取红包 微信红包
            if ( splits != null && splits.length == 5 ) {
                WECHAT_PACKAGENAME = splits[0];
                HONGBAO_TEXT_KEY = splits[1];
                TEXT_SHOUMANLE = splits[2];
                TEXT_LINGQUHONGBAO = splits[3];
                TEXT_LV_ITEM_TIPS = splits[4];
            }
            FLAG = "PREPARE";
        } else {
            FLAG = "UN_PREPARE";
        }
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
//        有通知栏通知先不提醒
//        NotifyHelper.playEffect(getContext(), getConfig());
    }

    /**
     * 处理通知
     *
     * @param event
     */
    private void doNotification(AccessibilityEvent event) {
        Parcelable data = event.getParcelableData();
        if ( data == null || !(data instanceof Notification) ) {
            return;
        }
        List<CharSequence> texts = event.getText();
        if ( !texts.isEmpty() ) {
            String text = String.valueOf(texts.get(0));
            notificationEvent(text, ( Notification ) data);
        }
    }

    /**
     * 收到聊天里的红包
     *
     * @param event
     */
    @TargetApi( Build.VERSION_CODES.JELLY_BEAN_MR2 )
    private void handleHongBao(AccessibilityEvent event, CusUser cusUser) {
        if ( event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED ) {
            if ( cusUser != null ) {
                UserInfoModel userInfoModel = cusUser.getUserInfoModel();
                if ( userInfoModel != null ) {
                    if ( userInfoModel.getTongzhiState() != 0 ) {
                        doNotification(event);
                    }
                }
            } else {
                doNotification(event);
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

            if ( CLASS_NAME_RECEIVE_UI.equals(className) ) {
                //如果是手动 就不帮忙关掉
                if ( cusUser != null ) {
                    UserInfoModel userInfoModel = cusUser.getUserInfoModel();
                    if ( userInfoModel != null ) {
                        if ( userInfoModel.getModeState() == 4 ) {
                            //不自动
                            return;
                        }
                    }
                } else {
                    Toast.makeText(getService(), "获取配置信息错误,使用默认配置", Toast.LENGTH_SHORT).show();
                }
                //点中了红包 有两种操作 一种是点开红包  一种是手慢了
                /**
                 * 一种是点开红包
                 */
                //获取开按钮

                List<AccessibilityNodeInfo> kaiNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_RECEIVE_BTN_OPEN);
                //获取 手慢了 提示语句的控件

                List<AccessibilityNodeInfo> slowNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_RECEIVE_TV_SHOWMANLE);
                //获取关闭按钮

                List<AccessibilityNodeInfo> closeNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_RECEIVE_CLOSE);
                if ( !kaiNodes.isEmpty() ) {
                    //获取到开按钮 点击此按钮
                    NotifyHelper.playEffect(getContext(), getConfig());
                    AccessibilityHelper.performClick(kaiNodes.get(0));
                } else {
                    if ( !slowNodes.isEmpty() && !closeNodes.isEmpty() )
                        //手慢了 提示语句的控件 关闭对话框
                        AccessibilityHelper.performClick(closeNodes.get(0));
                }
            } else if ( CLASS_NAME_DETAIL_UI.equals(className) ) {
                //拆完红包后看详细的纪录界面 这里提取下数据后退出就好
                RedPackageInfoModel indo = new RedPackageInfoModel();
                indo.setWeixinVersion(getWechatVersion());
                indo.setPackageTime(CommonUtils.timeLongFormatToStr(new Date(System.currentTimeMillis()).getTime()));
                //获取金额关闭按钮

                List<AccessibilityNodeInfo> moneyNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_DETAIL_MONEY);
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
                        nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_DETAIL_USERNAME);
                if ( !userNodes.isEmpty() ) {
                    String username = userNodes.get(0).getText().toString();
                    indo.setOrigin(username);
                }
                //获取多长时间被抢完的控件  有这个控件代表是群红包

                List<AccessibilityNodeInfo> timeNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_DETAIL_TIME_TO_QIANG);
                //红包类型 0 私包 1 群红包 普通红包 2 群红包 拼手气
                if ( !timeNodes.isEmpty() ) {
                    //群红包
                    //获取拼手气的图标 有 代表是平手气

                    List<AccessibilityNodeInfo> pinNodes =
                            nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_DETAIL_PINSHOUQI);
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

                //如果是手动 就不帮忙关掉
                if ( cusUser != null ) {
                    UserInfoModel userInfoModel = cusUser.getUserInfoModel();
                    if ( userInfoModel != null ) {
                        if ( userInfoModel.getModeState() == 4 ) {
                            //不自动
                            return;
                        }
                    }
                } else {
                    Toast.makeText(getService(), "获取配置信息错误,使用默认配置", Toast.LENGTH_SHORT).show();
                }
                //获取关闭按钮

                List<AccessibilityNodeInfo> closeNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_DETAIL_CLOSE);
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
                        nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_RECEIVE_CLOSE);
                //获取 手慢了 提示语句的控件

                List<AccessibilityNodeInfo> slowNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_RECEIVE_TV_SHOWMANLE);
                if ( !slowNodes.isEmpty() && TEXT_SHOUMANLE.equals(slowNodes.get(0).getText().toString()) ) {
                    if ( !closeNodes.isEmpty() ) {
                        //关掉
                        AccessibilityHelper.performClick(closeNodes.get(0));
                        return;
                    }
                }

                //处理其他情况
                //获取聊天页面的按钮 如果有则代表是聊天页面

                List<AccessibilityNodeInfo> chatNodes =
                        nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_CHATTING_TV_ADD);
                if ( chatNodes.isEmpty() ) {
                    Log.e("MMMMMM", "不在聊天页面 不好说在哪儿");
                    //不在聊天页面 不好说在哪儿
                    //获取首页的listview 的 item 的 列表

                    List<AccessibilityNodeInfo> listItemNodes =
                            nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_HOME_LV_ITEM);
                    if ( listItemNodes.isEmpty() ) {
                        //反正不是在首页 不理会
                        return;
                    } else {
                        //在首页

                        List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_HOME_LV_ITEM_CONTENT);
                        if ( nodes != null ) {
                            for ( AccessibilityNodeInfo node :
                                    nodes ) {
                                if ( node.getText().toString().contains(HONGBAO_TEXT_KEY) ) {
                                    //还要判断是否有未读消息
                                    AccessibilityNodeInfo parent = node.getParent();
                                    if ( parent != null ) {

                                        List<AccessibilityNodeInfo> numsNodes =
                                                parent.findAccessibilityNodeInfosByViewId(VIEW_ID_HOME_LV_ITEM_NUMBER);
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
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(TEXT_LINGQUHONGBAO);
                    if ( list == null )
                        return;
                    // 模式选择 0 未选择 1 自动抢 单聊 2 自动抢 群聊 3 自动抢 all 4 仅打开红包页面
                    int modeState = 4;
                    if ( cusUser != null ) {
                        UserInfoModel userInfoModel = cusUser.getUserInfoModel();
                        if ( userInfoModel != null ) {
                            modeState = userInfoModel.getModeState();
                        }
                    } else {
                        Toast.makeText(getService(), "获取配置信息错误,使用默认配置", Toast.LENGTH_SHORT).show();
                    }

                    //当前聊天类型 0私聊 1群聊 2 未知
                    int chatType = 2;
                    //判断是私聊还是群聊天
                    //获取标题的视图

                    List<AccessibilityNodeInfo> titleNodes =
                            nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_CHATTING_TV_TITLE);
                    if ( titleNodes.isEmpty() ) {
                        //判断不了
                        chatType = 2;
                    } else {
                        String title = titleNodes.get(0).getText().toString();
                        if ( !TextUtils.isEmpty(title) ) {
                            if ( title.contains("(") ) {
                                int indexLeft = title.lastIndexOf("(");
                                String end = title.substring(indexLeft);
                                end = end.substring(1, end.length() - 1);
                                try {
                                    Integer.parseInt(end);
                                    chatType = 1;
                                } catch ( Exception e ) {
                                    //私聊
                                    chatType = 0;
                                }
                            } else {
                                chatType = 0;
                            }
                        }
                    }
                    //0 未选择 1 自动抢 单聊 2 自动抢 群聊 3 自动抢 all 4 仅打开红包页面
                    if ( list.isEmpty() ) {
                        //没有 直接返回
                        if ( modeState != 4 ) {
                            //找到聊天页面的返回按钮

                            List<AccessibilityNodeInfo> backNodes =
                                    nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_CHATTING_TV_BACK);
                            if ( !backNodes.isEmpty() ) {
                                Log.e("MMMMMM", "没有 直接返回   关闭了-----");
                                AccessibilityHelper.performClick(backNodes.get(0));
                            }
                        }
                    } else {
                        if ( modeState == 1 ) {
                            if ( chatType == 0 || chatType == 2 ) {
                                // 放行
                            } else {
                                chatWindowClose(nodeInfo);
                                return;
                            }
                        } else if ( modeState == 2 ) {
                            if ( chatType == 1 || chatType == 2 ) {
                                // 放行
                            } else {
                                chatWindowClose(nodeInfo);
                                return;
                            }
                        } else if ( modeState == 3 ) {
                            //自动抢 all 放行
                        } else {
                            //仅打开红包页面
                            return;
                        }

                        //有 但是要检查是不是红包
                        for ( int i = list.size() - 1; i >= 0; i-- ) {
                            AccessibilityNodeInfo node = list.get(i);
                            AccessibilityNodeInfo parent = node.getParent();
                            if ( parent != null ) {

                                List<AccessibilityNodeInfo> wxhbNodes =
                                        parent.findAccessibilityNodeInfosByViewId(VIEW_ID_HOME_LV_ITEM_LABEL_WXHB);
                                if ( !wxhbNodes.isEmpty() ) {
                                    if ( TEXT_LV_ITEM_TIPS.equals(wxhbNodes.get(0).getText()) ) {
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

    //    /关掉聊天页面
    @RequiresApi( api = Build.VERSION_CODES.JELLY_BEAN_MR2 )
    private void chatWindowClose(AccessibilityNodeInfo nodeInfo) {

        List<AccessibilityNodeInfo> backNodes =
                nodeInfo.findAccessibilityNodeInfosByViewId(VIEW_ID_CHATTING_TV_BACK);
        if ( !backNodes.isEmpty() ) {
            Log.e("MMMMMM", "没有 直接返回   关闭了-----");
            AccessibilityHelper.performClick(backNodes.get(0));
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
    public String getWechatVersion() {
        if ( mWechatPackageInfo == null ) {
            updatePackageInfo();
            if ( mWechatPackageInfo == null ) {
                return "位置版本";
            }
        }
        return mWechatPackageInfo.versionName;
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
