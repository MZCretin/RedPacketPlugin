package com.cretin.www.redpacketplugin.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cretin.www.redpacketplugin.R;
import com.cretin.www.redpacketplugin.base.BaseApplication;
import com.cretin.www.redpacketplugin.config.eventbus.NotifyDataNotPrepare;
import com.cretin.www.redpacketplugin.config.eventbus.NotifyDataNotPreparedSuccess;
import com.cretin.www.redpacketplugin.config.eventbus.NotifyForceExit;
import com.cretin.www.redpacketplugin.config.eventbus.NotifyOnActivityStop;
import com.cretin.www.redpacketplugin.config.eventbus.NotifySetSllepTime;
import com.cretin.www.redpacketplugin.config.eventbus.NotifyVipHasDied;
import com.cretin.www.redpacketplugin.model.CusUser;
import com.cretin.www.redpacketplugin.model.RedPackageInfoModel;
import com.cretin.www.redpacketplugin.model.UserInfoModel;
import com.cretin.www.redpacketplugin.model.WeixinNodeModel;
import com.cretin.www.redpacketplugin.utils.AlertDialog;
import com.cretin.www.redpacketplugin.utils.CircleImageView;
import com.cretin.www.redpacketplugin.utils.CommonUtils;
import com.cretin.www.redpacketplugin.utils.KV;
import com.cretin.www.redpacketplugin.utils.LocalStorageKeys;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends BaseActivity {

    @BindView( R.id.iv_avatar )
    CircleImageView ivAvatar;
    @BindView( R.id.tv_username )
    TextView tvUsername;
    @BindView( R.id.iv_level )
    ImageView ivLevel;
    @BindView( R.id.tv_user_des )
    TextView tvUserDes;
    @BindView( R.id.tv_leijinums )
    TextView tvLeijinums;
    @BindView( R.id.tv_leijimoney )
    TextView tvLeijimoney;
    @BindView( R.id.tv_state )
    TextView tvState;
    @BindView( R.id.tv_open )
    TextView tvOpen;
    @BindView( R.id.tv_xiumian_shiijan )
    TextView tvXiuMianShiJian;
    @BindView( R.id.tv_vip_des )
    TextView tvVipDes;
    @BindView( R.id.ll_vip )
    LinearLayout llVip;
    @BindView( R.id.tb_notification )
    ToggleButton tbNotification;
    @BindView( R.id.tb_yinxiao )
    ToggleButton tbYinxiao;
    @BindView( R.id.tv_mode_des )
    TextView tvModeDes;
    @BindView( R.id.ll_mode )
    LinearLayout llMode;
    @BindView( R.id.ll_shuoming )
    LinearLayout llShuoming;
    @BindView( R.id.ll_log )
    LinearLayout llLog;
    @BindView( R.id.ll_yaoqing )
    LinearLayout llYaoqing;
    @BindView( R.id.ll_shijian )
    LinearLayout llShijian;
    public static final String TAG = "MainActivity";
    @BindView( R.id.swip_refresh )
    SwipeRefreshLayout swipRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        cusUser = KV.get(LocalStorageKeys.USER_INFO);

        swipRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                addData();
            }
        });

        showDialog();
        addData();
        //设置切换监听
        tbNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setInfo();
            }
        });
        tbYinxiao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setInfo();
            }
        });

        //处理ToggleButton显示不正常
        if ( Build.VERSION.SDK_INT <= 19 ) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            tbNotification.setLayoutParams(params);
            tbYinxiao.setLayoutParams(params);
        }

        findSystemSleepTime();

//        VersionUpdateModel model = new VersionUpdateModel();
//        model.setDownurl("http://jokesimg.cretinzp.com/apk/app-release_131_jiagu_sign.apk");
//        model.setSize("15688259");
//        model.setHasAffectCodes("1|2|3|4|5|6|7|8|9|10");
//        model.setUpdateLog("最新版v1.3.1\n1、新增评论回复功能，沟通更尽兴\n2、新增评论点赞功能，速速点赞\n3、优化N多你可能都不知道的细节\n4、为了更好的体验，推荐更新");
//        model.setIsForceUpdate(0);
//        model.setVersionCode(11);
//        model.setVersionName("v1.3.1");
//        model.setPreBaselineCode(10);
//        model.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                showToast(s);
//            }
//        });

//        UserExplainModel model1 = new UserExplainModel();
//        model1.setDesc("sss");
//        model1.setPicUrl("sss");
//        model1.setSetp(1);
//        model1.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//
//            }
//        });


        //v6.6.2 1240
//        WeixinNodeModel weixinNodeModel = new WeixinNodeModel();
//        weixinNodeModel.setCL_NAME_RV_UI("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI");
//        weixinNodeModel.setCL_NAME_EL_UI("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI");
//        weixinNodeModel.setID_RV_BTN_OPEN("com.tencent.mm:id/c4j");//
//        weixinNodeModel.setID_RV_TV_SHOWMANLE("com.tencent.mm:id/c4i");//
//        weixinNodeModel.setID_RV_CLOSE("com.tencent.mm:id/c28");//
//        weixinNodeModel.setID_EL_MONEY("com.tencent.mm:id/c0w");//
//        weixinNodeModel.setID_EL_USERNAME("com.tencent.mm:id/c0s");//
//        weixinNodeModel.setID_EL_TIME_TO_QIANG("com.tencent.mm:id/c1b");//
//        weixinNodeModel.setID_EL_PINSHOUQI("com.tencent.mm:id/c0t");//
//        weixinNodeModel.setID_EL_CLOSE("com.tencent.mm:id/hy");//
//        weixinNodeModel.setID_CHATTING_TV_ADD("com.tencent.mm:id/aag");//
//        weixinNodeModel.setID_HO_LV_IM("com.tencent.mm:id/app");//
//        weixinNodeModel.setID_HO_LV_IM_CONTENT("com.tencent.mm:id/apt");//
//        weixinNodeModel.setID_HO_LV_IM_NUMBER("com.tencent.mm:id/j4");//
//        weixinNodeModel.setID_CHATTING_TV_TITLE("com.tencent.mm:id/hj");//
//        weixinNodeModel.setID_HO_LV_IM_LB_WXHB("com.tencent.mm:id/aea");//
//        weixinNodeModel.setID_CHATTING_TV_BACK("com.tencent.mm:id/hi");//
//        weixinNodeModel.setCOMMEN_TEXT_INFO("com.tencent.mm [微信红包] 手慢了，红包派完了 领取红包 微信红包");
//        weixinNodeModel.setVersionCode(1240);
//        weixinNodeModel.setSTR_SPLIT(" ");
//        weixinNodeModel.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                showToast(s);
//            }
//        });

        //v6.6.1 1220
//        WeixinNodeModel weixinNodeModel = new WeixinNodeModel();
//        weixinNodeModel.setCL_NAME_RV_UI("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI");
//        weixinNodeModel.setCL_NAME_EL_UI("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI");
//        weixinNodeModel.setID_RV_BTN_OPEN("com.tencent.mm:id/c2i");
//        weixinNodeModel.setID_RV_TV_SHOWMANLE("com.tencent.mm:id/c2h");
//        weixinNodeModel.setID_RV_CLOSE("com.tencent.mm:id/c07");
//        weixinNodeModel.setID_EL_MONEY("com.tencent.mm:id/byw");
//        weixinNodeModel.setID_EL_USERNAME("com.tencent.mm:id/bys");
//        weixinNodeModel.setID_EL_TIME_TO_QIANG("com.tencent.mm:id/bzb");
//        weixinNodeModel.setID_EL_PINSHOUQI("com.tencent.mm:id/byt");
//        weixinNodeModel.setID_EL_CLOSE("com.tencent.mm:id/hp");
//        weixinNodeModel.setID_CHATTING_TV_ADD("com.tencent.mm:id/aak");
//        weixinNodeModel.setID_HO_LV_IM("com.tencent.mm:id/apr");
//        weixinNodeModel.setID_HO_LV_IM_CONTENT("com.tencent.mm:id/apv");
//        weixinNodeModel.setID_HO_LV_IM_NUMBER("com.tencent.mm:id/iu");
//        weixinNodeModel.setID_CHATTING_TV_TITLE("com.tencent.mm:id/ha");
//        weixinNodeModel.setID_HO_LV_IM_LB_WXHB("com.tencent.mm:id/aec");
//        weixinNodeModel.setID_CHATTING_TV_BACK("com.tencent.mm:id/h_");
//        weixinNodeModel.setCOMMEN_TEXT_INFO("com.tencent.mm [微信红包] 手慢了，红包派完了 领取红包 微信红包");
//        weixinNodeModel.setVersionCode(1220);
//        weixinNodeModel.setSTR_SPLIT(" ");
//        weixinNodeModel.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                showToast(s);
//            }
//        });

        //v6.6.0 1200
        //        WeixinNodeModel weixinNodeModel = new WeixinNodeModel();
//        weixinNodeModel.setCL_NAME_RV_UI("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI");
//        weixinNodeModel.setCL_NAME_EL_UI("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI");
//        weixinNodeModel.setID_RV_BTN_OPEN("com.tencent.mm:id/c22");
//        weixinNodeModel.setID_RV_TV_SHOWMANLE("com.tencent.mm:id/c21");
//        weixinNodeModel.setID_RV_CLOSE("com.tencent.mm:id/bzq");
//        weixinNodeModel.setID_EL_MONEY("com.tencent.mm:id/byf");
//        weixinNodeModel.setID_EL_USERNAME("com.tencent.mm:id/byb");
//        weixinNodeModel.setID_EL_TIME_TO_QIANG("com.tencent.mm:id/byv");
//        weixinNodeModel.setID_EL_PINSHOUQI("com.tencent.mm:id/byc");
//        weixinNodeModel.setID_EL_CLOSE("com.tencent.mm:id/hp");
//        weixinNodeModel.setID_CHATTING_TV_ADD("com.tencent.mm:id/aa4");
//        weixinNodeModel.setID_HO_LV_IM("com.tencent.mm:id/apb");
//        weixinNodeModel.setID_HO_LV_IM_CONTENT("com.tencent.mm:id/apf");
//        weixinNodeModel.setID_HO_LV_IM_NUMBER("com.tencent.mm:id/iu");
//        weixinNodeModel.setID_CHATTING_TV_TITLE("com.tencent.mm:id/ha");
//        weixinNodeModel.setID_HO_LV_IM_LB_WXHB("com.tencent.mm:id/adw");
//        weixinNodeModel.setID_CHATTING_TV_BACK("com.tencent.mm:id/h_");
//        weixinNodeModel.setCOMMEN_TEXT_INFO("com.tencent.mm [微信红包] 手慢了，红包派完了 领取红包 微信红包");
//        weixinNodeModel.setVersionCode(1220);
//        weixinNodeModel.setSTR_SPLIT(" ");
//        weixinNodeModel.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                showToast(s);
//            }
//        });

        //v6.5.23 1180
//        WeixinNodeModel weixinNodeModel = new WeixinNodeModel();
//        weixinNodeModel.setCL_NAME_RV_UI("com.tencent.mm.plugin.luckymoney.ui.En_fba4b94f");
//        weixinNodeModel.setCL_NAME_EL_UI("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI");
//        weixinNodeModel.setID_RV_BTN_OPEN("com.tencent.mm:id/bx4");//
//        weixinNodeModel.setID_RV_TV_SHOWMANLE("com.tencent.mm:id/bx3");//
//        weixinNodeModel.setID_RV_CLOSE("com.tencent.mm:id/bus");//
//        weixinNodeModel.setID_EL_MONEY("com.tencent.mm:id/btg");//
//        weixinNodeModel.setID_EL_USERNAME("com.tencent.mm:id/btc");//
//        weixinNodeModel.setID_EL_TIME_TO_QIANG("com.tencent.mm:id/btx");//
//        weixinNodeModel.setID_EL_PINSHOUQI("com.tencent.mm:id/btd");//
//        weixinNodeModel.setID_EL_CLOSE("com.tencent.mm:id/hj");//
//        weixinNodeModel.setID_CHATTING_TV_ADD("com.tencent.mm:id/aa6");//
//        weixinNodeModel.setID_HO_LV_IM("com.tencent.mm:id/aoh");//
//        weixinNodeModel.setID_HO_LV_IM_CONTENT("com.tencent.mm:id/aol");//
//        weixinNodeModel.setID_HO_LV_IM_NUMBER("com.tencent.mm:id/io");//
//        weixinNodeModel.setID_CHATTING_TV_TITLE("com.tencent.mm:id/h5");//
//        weixinNodeModel.setID_HO_LV_IM_LB_WXHB("com.tencent.mm:id/ade");//
//        weixinNodeModel.setID_CHATTING_TV_BACK("com.tencent.mm:id/h4");//
//        weixinNodeModel.setCOMMEN_TEXT_INFO("com.tencent.mm [微信红包] 手慢了，红包派完了 领取红包 微信红包");
//        weixinNodeModel.setVersionCode(1180);
//        weixinNodeModel.setSTR_SPLIT(" ");
//        weixinNodeModel.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                showToast(s);
//            }
//        });

        //v6.5.22 1160
//        WeixinNodeModel weixinNodeModel = new WeixinNodeModel();
//        weixinNodeModel.setCL_NAME_RV_UI("com.tencent.mm.plugin.luckymoney.ui.En_fba4b94f");
//        weixinNodeModel.setCL_NAME_EL_UI("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI");
//        weixinNodeModel.setID_RV_BTN_OPEN("com.tencent.mm:id/bwn");//1
//        weixinNodeModel.setID_RV_TV_SHOWMANLE("com.tencent.mm:id/bwm");//1
//        weixinNodeModel.setID_RV_CLOSE("com.tencent.mm:id/bub");//1
//        weixinNodeModel.setID_EL_MONEY("com.tencent.mm:id/bsz");//1
//        weixinNodeModel.setID_EL_USERNAME("com.tencent.mm:id/bsv");//1
//        weixinNodeModel.setID_EL_TIME_TO_QIANG("com.tencent.mm:id/bte");//1
//        weixinNodeModel.setID_EL_PINSHOUQI("com.tencent.mm:id/bsw");//1
//        weixinNodeModel.setID_EL_CLOSE("com.tencent.mm:id/hj");//1
//        weixinNodeModel.setID_CHATTING_TV_ADD("com.tencent.mm:id/aa6");//1
//        weixinNodeModel.setID_HO_LV_IM("com.tencent.mm:id/aoh");//1
//        weixinNodeModel.setID_HO_LV_IM_CONTENT("com.tencent.mm:id/aol");//1
//        weixinNodeModel.setID_HO_LV_IM_NUMBER("com.tencent.mm:id/io");//1
//        weixinNodeModel.setID_CHATTING_TV_TITLE("com.tencent.mm:id/h5");//1
//        weixinNodeModel.setID_HO_LV_IM_LB_WXHB("com.tencent.mm:id/ade");//1
//        weixinNodeModel.setID_CHATTING_TV_BACK("com.tencent.mm:id/h4");//1
//        weixinNodeModel.setCOMMEN_TEXT_INFO("com.tencent.mm [微信红包] 手慢了，红包派完了 领取红包 微信红包");
//        weixinNodeModel.setVersionCode(1160);
//        weixinNodeModel.setSTR_SPLIT(" ");
//        weixinNodeModel.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                showToast(s);
//            }
//        });

        //v6.5.19 1140
//        WeixinNodeModel weixinNodeModel = new WeixinNodeModel();
//        weixinNodeModel.setCL_NAME_RV_UI("com.tencent.mm.plugin.luckymoney.ui.En_fba4b94f");
//        weixinNodeModel.setCL_NAME_EL_UI("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI");
//        weixinNodeModel.setID_RV_BTN_OPEN("com.tencent.mm:id/bv8");//12
//        weixinNodeModel.setID_RV_TV_SHOWMANLE("com.tencent.mm:id/bv7");//12
//        weixinNodeModel.setID_RV_CLOSE("com.tencent.mm:id/bsv");//12
//        weixinNodeModel.setID_EL_MONEY("com.tencent.mm:id/brj");//12
//        weixinNodeModel.setID_EL_USERNAME("com.tencent.mm:id/brf");//12
//        weixinNodeModel.setID_EL_TIME_TO_QIANG("com.tencent.mm:id/brz");//12
//        weixinNodeModel.setID_EL_PINSHOUQI("com.tencent.mm:id/brg");//12
//        weixinNodeModel.setID_EL_CLOSE("com.tencent.mm:id/hg");//12
//        weixinNodeModel.setID_CHATTING_TV_ADD("com.tencent.mm:id/a9t");//12
//        weixinNodeModel.setID_HO_LV_IM("com.tencent.mm:id/an5");//12
//        weixinNodeModel.setID_HO_LV_IM_CONTENT("com.tencent.mm:id/an9");//12
//        weixinNodeModel.setID_HO_LV_IM_NUMBER("com.tencent.mm:id/il");//12
//        weixinNodeModel.setID_CHATTING_TV_TITLE("com.tencent.mm:id/h2");//12
//        weixinNodeModel.setID_HO_LV_IM_LB_WXHB("com.tencent.mm:id/ac2");//12
//        weixinNodeModel.setID_CHATTING_TV_BACK("com.tencent.mm:id/h1");//12
//        weixinNodeModel.setCOMMEN_TEXT_INFO("com.tencent.mm [微信红包] 手慢了，红包派完了 领取红包 微信红包");
//        weixinNodeModel.setVersionCode(1140);
//        weixinNodeModel.setSTR_SPLIT(" ");
//        weixinNodeModel.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                showToast(s);
//            }
//        });

//        //v6.5.16 1120
//        WeixinNodeModel weixinNodeModel = new WeixinNodeModel();
//        weixinNodeModel.setCL_NAME_RV_UI("com.tencent.mm.plugin.luckymoney.ui.En_fba4b94f");
//        weixinNodeModel.setCL_NAME_EL_UI("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI");
//        weixinNodeModel.setID_RV_BTN_OPEN("com.tencent.mm:id/brt");//123
//        weixinNodeModel.setID_RV_TV_SHOWMANLE("com.tencent.mm:id/brs");//123
//        weixinNodeModel.setID_RV_CLOSE("com.tencent.mm:id/bph");//123
//        weixinNodeModel.setID_EL_MONEY("com.tencent.mm:id/bo6");//123
//        weixinNodeModel.setID_EL_USERNAME("com.tencent.mm:id/bo2");//123
//        weixinNodeModel.setID_EL_TIME_TO_QIANG("com.tencent.mm:id/bol");//123
//        weixinNodeModel.setID_EL_PINSHOUQI("com.tencent.mm:id/bo3");//123
//        weixinNodeModel.setID_EL_CLOSE("com.tencent.mm:id/hg");//123
//        weixinNodeModel.setID_CHATTING_TV_ADD("com.tencent.mm:id/a76");//123
//        weixinNodeModel.setID_HO_LV_IM("com.tencent.mm:id/ajz");//123
//        weixinNodeModel.setID_HO_LV_IM_CONTENT("com.tencent.mm:id/ak3");//123
//        weixinNodeModel.setID_HO_LV_IM_NUMBER("com.tencent.mm:id/il");//123
//        weixinNodeModel.setID_CHATTING_TV_TITLE("com.tencent.mm:id/h2");//123
//        weixinNodeModel.setID_HO_LV_IM_LB_WXHB("com.tencent.mm:id/a_c");//123
//        weixinNodeModel.setID_CHATTING_TV_BACK("com.tencent.mm:id/h1");//123
//        weixinNodeModel.setCOMMEN_TEXT_INFO("com.tencent.mm [微信红包] 手慢了，红包派完了 领取红包 微信红包");
//        weixinNodeModel.setVersionCode(1120);
//        weixinNodeModel.setSTR_SPLIT(" ");
//        weixinNodeModel.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                showToast(s);
//            }
//        });

        //v6.5.13 1100
//        WeixinNodeModel weixinNodeModel = new WeixinNodeModel();
//        weixinNodeModel.setCL_NAME_RV_UI("com.tencent.mm.plugin.luckymoney.ui.En_fba4b94f");
//        weixinNodeModel.setCL_NAME_EL_UI("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI");
//        weixinNodeModel.setID_RV_BTN_OPEN("com.tencent.mm:id/bp6");//1234
//        weixinNodeModel.setID_RV_TV_SHOWMANLE("com.tencent.mm:id/bp5");//1234
//        weixinNodeModel.setID_RV_CLOSE("com.tencent.mm:id/bmu");//1234
//        weixinNodeModel.setID_EL_MONEY("com.tencent.mm:id/bli");//1234
//        weixinNodeModel.setID_EL_USERNAME("com.tencent.mm:id/ble");//1234
//        weixinNodeModel.setID_EL_TIME_TO_QIANG("com.tencent.mm:id/bly");//1234
//        weixinNodeModel.setID_EL_PINSHOUQI("com.tencent.mm:id/blf");//1234
//        weixinNodeModel.setID_EL_CLOSE("com.tencent.mm:id/hd");//1234
//        weixinNodeModel.setID_CHATTING_TV_ADD("com.tencent.mm:id/a6l");//1234
//        weixinNodeModel.setID_HO_LV_IM("com.tencent.mm:id/aja");//1234
//        weixinNodeModel.setID_HO_LV_IM_CONTENT("com.tencent.mm:id/aje");//1234
//        weixinNodeModel.setID_HO_LV_IM_NUMBER("com.tencent.mm:id/ie");//1234
//        weixinNodeModel.setID_CHATTING_TV_TITLE("com.tencent.mm:id/gz");//1234
//        weixinNodeModel.setID_HO_LV_IM_LB_WXHB("com.tencent.mm:id/a9r");//1234
//        weixinNodeModel.setID_CHATTING_TV_BACK("com.tencent.mm:id/gy");//1234
//        weixinNodeModel.setCOMMEN_TEXT_INFO("com.tencent.mm [微信红包] 手慢了，红包派完了 领取红包 微信红包");
//        weixinNodeModel.setVersionCode(1100);
//        weixinNodeModel.setSTR_SPLIT(" ");
//        weixinNodeModel.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                showToast(s);
//            }
//        });

        //v6.5.10 1080
//        WeixinNodeModel weixinNodeModel = new WeixinNodeModel();
//        weixinNodeModel.setCL_NAME_RV_UI("com.tencent.mm.plugin.luckymoney.ui.En_fba4b94f");
//        weixinNodeModel.setCL_NAME_EL_UI("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI");
//        weixinNodeModel.setID_RV_BTN_OPEN("com.tencent.mm:id/bnr");//12345
//        weixinNodeModel.setID_RV_TV_SHOWMANLE("com.tencent.mm:id/bnq");//12345
//        weixinNodeModel.setID_RV_CLOSE("com.tencent.mm:id/blh");//12345
//        weixinNodeModel.setID_EL_MONEY("com.tencent.mm:id/bk6");//12345
//        weixinNodeModel.setID_EL_USERNAME("com.tencent.mm:id/bk2");//12345
//        weixinNodeModel.setID_EL_TIME_TO_QIANG("com.tencent.mm:id/bkl");//12345
//        weixinNodeModel.setID_EL_PINSHOUQI("com.tencent.mm:id/bk3");//12345
//        weixinNodeModel.setID_EL_CLOSE("com.tencent.mm:id/h7");//12345
//        weixinNodeModel.setID_CHATTING_TV_ADD("com.tencent.mm:id/a5j");//12345
//        weixinNodeModel.setID_HO_LV_IM("com.tencent.mm:id/aie");//12345
//        weixinNodeModel.setID_HO_LV_IM_CONTENT("com.tencent.mm:id/aii");//12345
//        weixinNodeModel.setID_HO_LV_IM_NUMBER("com.tencent.mm:id/i8");//12345
//        weixinNodeModel.setID_CHATTING_TV_TITLE("com.tencent.mm:id/gs");//12345
//        weixinNodeModel.setID_HO_LV_IM_LB_WXHB("com.tencent.mm:id/a8i");//12345
//        weixinNodeModel.setID_CHATTING_TV_BACK("com.tencent.mm:id/gr");//12345
//        weixinNodeModel.setCOMMEN_TEXT_INFO("com.tencent.mm [微信红包] 手慢了，红包派完了 领取红包 微信红包");
//        weixinNodeModel.setVersionCode(1080);
//        weixinNodeModel.setSTR_SPLIT(" ");
//        weixinNodeModel.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                showToast(s);
//            }
//        });
    }

    //获取配置文件
    private void getPeizhi() {
        final int versionCode = getVersion();
        if ( versionCode == -1 ) {
            showToast("未检测到微信的安装,请先安装微信");
            return;
        }
        BmobQuery<WeixinNodeModel> query = new BmobQuery<WeixinNodeModel>();
        query.setLimit(500);
        query.findObjects(new FindListener<WeixinNodeModel>() {
            @Override
            public void done(List<WeixinNodeModel> object, BmobException e) {
                if ( e == null ) {
                    if ( object.isEmpty() ) {
                        showToast("暂无配置文件可加载");
                    } else {
                        KV.put(LocalStorageKeys.WEIXINNODEMODEL_LIST, object);
                        boolean flag = false;
                        for ( WeixinNodeModel weixinNodeModel :
                                object ) {
                            if ( versionCode == weixinNodeModel.getVersionCode() ) {
                                flag = true;
                                KV.put(LocalStorageKeys.WEIXINNODEMODEL_SINGLE, weixinNodeModel);
                                EventBus.getDefault().post(new NotifyDataNotPreparedSuccess());
                            }
                        }
                        if ( !flag ) {
                            showCusDialog("您的微信版本太低了，目前最低适配到微信2017年08月21日发布的V6.5.13版本，请更新您的微信", false);
                            KV.put(LocalStorageKeys.WEIXINNODEMODEL_SINGLE, null);
                        }
                    }
                } else {
                    if ( retryNums < 3 ) {
                        //重新加载一次
                        retryNums++;
                        getPeizhi();
                    }
                }
            }
        });
    }

    private int retryNums;

    private void addData() {
        if ( cusUser != null ) {
            BmobQuery<CusUser> query = new BmobQuery<>();
            query.include("userInfoModel");
            query.getObject(cusUser.getObjectId(), new QueryListener<CusUser>() {
                @Override
                public void done(CusUser object, BmobException e) {
                    if ( e == null ) {
                        KV.put(LocalStorageKeys.USER_INFO, object);
                        showData(object);
                    } else {
                        showToast("加载超时,请稍后再试");
                    }
                    //同步数据要放到这里进行
                    enquene();
                    stopDialog();
                    swipRefresh.setRefreshing(false);
                }
            });

            //如果配置文件没有加载成功 则重新加载
            getPeizhi();
        }
    }

    //同步本地数据
    private void enquene() {
        //同步本地数据到服务器
        final List<RedPackageInfoModel> list = KV.get(LocalStorageKeys.RED_PACKAGE_LIST);
        if ( list != null && !list.isEmpty() ) {
            List<BmobObject> datas = new ArrayList<>();
            for ( RedPackageInfoModel red :
                    list ) {
                red.setAuthorUserId(cusUser.getObjectId());
                red.setAuthorPhone(cusUser.getUsername());
                datas.add(red);
            }
            final double[] allMoney = {0};
            //第二种方式：v3.5.0开始提供
            new BmobBatch().insertBatch(datas).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(final List<BatchResult> o, BmobException e) {
                    if ( e == null ) {
                        int num = 0;
                        for ( int i = 0; i < o.size(); i++ ) {
                            BatchResult result = o.get(i);
                            BmobException ex = result.getError();
                            if ( ex == null ) {
                                num++;
                                allMoney[0] += list.get(i).getMoney();
                            }
                        }
                        //提交数据到
                        if ( cusUser != null ) {
                            final UserInfoModel userInfoModel = cusUser.getUserInfoModel();
                            if ( userInfoModel != null ) {
                                //获取单条数据
                                BmobQuery<UserInfoModel> query = new BmobQuery<UserInfoModel>();
                                final int finalNum = num;
                                query.getObject(userInfoModel.getObjectId(), new QueryListener<UserInfoModel>() {

                                    @Override
                                    public void done(final UserInfoModel object, BmobException e) {
                                        if ( e == null ) {
                                            object.setAllMoney(object.getAllMoney() + allMoney[0]);
                                            object.setPackageNums(object.getPackageNums() + finalNum);
                                            object.update(object.getObjectId(), new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if ( e == null ) {
                                                        KV.put(LocalStorageKeys.RED_PACKAGE_LIST, null);
                                                        CusUser cusUser = KV.get(LocalStorageKeys.USER_INFO);
                                                        if ( cusUser != null ) {
                                                            UserInfoModel userInfoModel1 = cusUser.getUserInfoModel();
                                                            userInfoModel1.setAllMoney(object.getAllMoney() + allMoney[0]);
                                                            userInfoModel1.setPackageNums(object.getPackageNums() + finalNum);
                                                            cusUser.setUserInfoModel(userInfoModel1);
                                                            KV.put(LocalStorageKeys.USER_INFO, cusUser);
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                        }
                                    }
                                });
                            }
                        } else {
                            stopDialog();
                            showToast("请求超时，请稍后再试");
                        }
                    }
                }
            });
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */

    public int getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo("com.tencent.mm", 0);
            int versionCode = info.versionCode;
            return versionCode;
        } catch ( Exception e ) {
            e.printStackTrace();
            return -1;
        }
    }

    private CusUser cusUser;
    private int firstIn;

    //显示数据
    private void showData(CusUser user) {
        //设置用户名
        tvUsername.setText(CommonUtils.formatPhoneStr(user.getUsername()));
        //设置试用期到期时间
        UserInfoModel userInfoModel = user.getUserInfoModel();
        if ( userInfoModel != null ) {
            int leftDay = userInfoModel.getLeftDays();
            String createdAt = user.getCreatedAt();
//            2018-02-01 14:38:45
            //计算截止时间
            String endlineTimeStr = CommonUtils.plusDay(leftDay, createdAt);
            //0 普通用户 1 周卡  2 月卡  3 终生免费
            if ( userInfoModel.getVipLevel() == 0 ) {
                //普通用户
                tvUserDes.setText("尊敬的普通用户 您的试用期截止于\n" + endlineTimeStr);
                tvVipDes.setText("您是普通会员，有效期截止于" + endlineTimeStr);
                ivLevel.setImageResource(R.mipmap.vip_gray);
            } else if ( userInfoModel.getVipLevel() == 1 ) {
                //周卡
                tvUserDes.setText("尊敬的周卡VIP用户 您的有效时间截止于\n" + endlineTimeStr);
                tvVipDes.setText("您是周卡VIP用户，有效期截止于" + endlineTimeStr);
                ivLevel.setImageResource(R.mipmap.vip_week);
            } else if ( userInfoModel.getVipLevel() == 2 ) {
                //月卡
                tvUserDes.setText("尊敬的月卡VIP用户 您的有效时间截止于\n" + endlineTimeStr);
                tvVipDes.setText("您是月卡VIP用户，有效期截止于" + endlineTimeStr);
                ivLevel.setImageResource(R.mipmap.vip_month);
            } else if ( userInfoModel.getVipLevel() == 3 ) {
                //月卡
                tvUserDes.setText("高贵的终生免费VIP用户\n您好！我们将终生为您服务");
                tvVipDes.setText("您是终生免费VIP用户，永久有效");
                ivLevel.setImageResource(R.mipmap.vip_supper);
            }

            getServiceState(false);

            //累计抢到红包数
            String msg = userInfoModel.getPackageNums() + "个";
            SpannableString msp = new SpannableString(msg);
            //设置字体大小（相对值,单位：像素） 参数表示为默认字体大小的多少倍
            msp.setSpan(new RelativeSizeSpan(0.5f), msg.length() - 1, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //0.5f表示默认字体大小的一半
            tvLeijinums.setText(msp);
            //累计抢到红包金额
            String msg1 = CommonUtils.formatNum(userInfoModel.getAllMoney(), 2) + "元";
            SpannableString msp1 = new SpannableString(msg1);
            //设置字体大小（相对值,单位：像素） 参数表示为默认字体大小的多少倍
            msp1.setSpan(new RelativeSizeSpan(0.5f), msg1.length() - 1, msg1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //0.5f表示默认字体大小的一半
            tvLeijimoney.setText(msp1);
            //设置通知和提示音状态
            tbYinxiao.setChecked(userInfoModel.getTishiyinState() == 0 ? false : true);
            tbNotification.setChecked(userInfoModel.getTongzhiState() == 0 ? false : true);
            //设置模式
            tvModeDes.setText(userInfoModel.getModeStateValue());
        }
        firstIn++;
    }

    /**
     * 开启设置
     *
     * @param flag
     */
    public void openAccessibilityServiceSettings(boolean flag) {
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            if ( !flag )
                showToast("点击 电点微信红包插件 开启服务");
            else
                showToast("手动关闭 电点微信红包插件 服务");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    //设置数据
    private void setInfo() {
        if ( firstIn == 0 )
            return;
        showDialog("设置中...");
        if ( cusUser != null ) {
            UserInfoModel userInfoModel = cusUser.getUserInfoModel();
            if ( userInfoModel != null ) {
                //获取单条数据
                BmobQuery<UserInfoModel> query = new BmobQuery<UserInfoModel>();
                query.getObject(userInfoModel.getObjectId(), new QueryListener<UserInfoModel>() {

                    @Override
                    public void done(final UserInfoModel object, BmobException e) {
                        if ( e == null ) {
                            object.setTongzhiState(tbNotification.isChecked() ? 1 : 0);
                            object.setTishiyinState(tbYinxiao.isChecked() ? 1 : 0);
                            object.update(object.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    stopDialog();
                                    if ( e == null ) {
                                        showToast("设置成功");
                                        CusUser cusUser = KV.get(LocalStorageKeys.USER_INFO);
                                        if ( cusUser != null ) {
                                            UserInfoModel userInfoModel1 = cusUser.getUserInfoModel();
                                            userInfoModel1.setTongzhiState(tbNotification.isChecked() ? 1 : 0);
                                            userInfoModel1.setTishiyinState(tbYinxiao.isChecked() ? 1 : 0);
                                            cusUser.setUserInfoModel(userInfoModel1);
                                            KV.put(LocalStorageKeys.USER_INFO, cusUser);
                                        }
                                    } else {
                                        showToast("设置失败");
                                        tbNotification.setChecked(!tbNotification.isChecked());
                                        tbYinxiao.setChecked(!tbYinxiao.isChecked());
                                    }
                                }
                            });
                        } else {
                            Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
            }
        } else {
            stopDialog();
            showToast("请求超时，请稍后再试");
        }
    }

    @OnClick( {R.id.iv_level, R.id.tv_user_des, R.id.tv_open, R.id.ll_vip, R.id.ll_shijian, R.id.ll_mode, R.id.ll_yaoqing, R.id.ll_shuoming, R.id.ll_log} )
    public void onViewClicked(View view) {
        switch ( view.getId() ) {
            case R.id.iv_level:
                break;
            case R.id.tv_user_des:
                break;
            case R.id.tv_open:
                if ( ifVipDied() ) {
                    tvOpen.setText("VIP已经过期,请续费");
                    if ( dialog != null ) {
                        if ( !dialog.isShowing() )
                            showCusDialog("VIP已过期，请续费", true);
                    } else {
                        showCusDialog("VIP已过期，请续费", true);
                    }
                    return;
                }
                if ( "开启自动抢红包".equals(tvOpen.getText().toString()) ) {
                    openAccessibilityServiceSettings(false);
                } else {
                    final AlertDialog dialog = new AlertDialog(this);
                    dialog.setOnClickListener(new AlertDialog.OnPositiveClickListener() {
                        @Override
                        public void onPositiveClickListener(View v) {
                            //关闭服务
                            EventBus.getDefault().post(new NotifyOnActivityStop());
                            getServiceState(true);
                        }
                    });
                    dialog.show();
                    dialog.setMessage("关闭后将不能再自动抢红包了，确定关闭服务吗？");
                }
                break;
            case R.id.ll_vip:
                startActivity(new Intent(this, VipInfoActivity.class));
                break;
            case R.id.ll_shijian:
                startActivity(new Intent(this, SetSleepTimeActivity.class));
                break;
            case R.id.ll_mode:
                startActivity(new Intent(this, SelectModeActivity.class));
                break;
            case R.id.ll_log:
                startActivity(new Intent(this, RedPackageLogActivity.class));
                break;
            case R.id.ll_yaoqing:
                startActivity(new Intent(this, InviteActivity.class));
                break;
            case R.id.ll_shuoming:
                startActivity(new Intent(this, UserExplainActivity.class));
                break;
        }
    }

    private AlertDialog dialog;

    private void showCusDialog(String msg, boolean show) {
        if ( dialog == null )
            dialog = new AlertDialog(this);
        dialog.setOnClickListener(new AlertDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClickListener(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setMessage(msg);
        if ( show ) {
            dialog.setLeftButton("去续费");
            dialog.setOnNegativeListener(new AlertDialog.OnNegativeClickListener() {
                @Override
                public void onNegativeClickListener(View v) {
                    startActivity(new Intent(MainActivity.this, VipInfoActivity.class));
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( firstIn == 0 )
            return;
        getServiceState(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //Vip是否过期
    private boolean ifVipDied() {
        CusUser user = KV.get(LocalStorageKeys.USER_INFO);
        if ( user != null ) {
            UserInfoModel userInfoModel = user.getUserInfoModel();
            if ( userInfoModel != null ) {
                int leftDay = userInfoModel.getLeftDays();
                if ( leftDay == 0 )
                    return false;
                String createdAt = user.getCreatedAt();
                //计算截止时间
                String endlineTimeStr = CommonUtils.plusDay(leftDay, createdAt);
                if ( CommonUtils.isBeforeToday(endlineTimeStr) ) {
                    //已过期
                    return true;
                } else {
                    //未过期 放行
                    return false;
                }
            }
        }
        return true;
    }

    //获取当前服务状态
    private void getServiceState(boolean flag) {
        if ( !flag ) {
            if ( ifVipDied() ) {
                tvOpen.setText("VIP已经过期,请续费");
                tvState.setText("续费后才能继续尊享特权");
                if ( dialog != null ) {
                    if ( !dialog.isShowing() )
                        showCusDialog("VIP已过期，请续费", true);
                } else {
                    showCusDialog("VIP已过期，请续费", true);
                }
                return;
            }
        }
        if ( CommonUtils.isAccessibilitySettingsOn(getApplicationContext()) ) {
            //服务开启了
            tvOpen.setText("关闭自动抢红包");
            tvState.setText("正在持续为您抢红包中...");
            tvOpen.setBackgroundResource(R.drawable.bg_red_button_round100);
            if ( flag ) {
                //是点击按钮之后来的  如果没有退出成功 就必须要过去直接点击
                openAccessibilityServiceSettings(flag);
            }
        } else {
            //服务没有打开
            tvOpen.setText("开启自动抢红包");
            tvState.setText("点击按钮开启极速抢红包之旅");
            tvOpen.setBackgroundResource(R.drawable.bg_button_round100);
        }
    }

    //在需要监听的activity中重写onKeyDown()。
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0 ) {
            final AlertDialog dialog = new AlertDialog(this);
            dialog.setOnClickListener(new AlertDialog.OnPositiveClickListener() {
                @Override
                public void onPositiveClickListener(View v) {
                    dialog.dismiss();
                    NotifyOnActivityStop stop = new NotifyOnActivityStop();
                    KV.put(LocalStorageKeys.QUANJU_LOGIN_STATE, false);
                    EventBus.getDefault().post(stop);
                    MainActivity.this.finish();
                }
            });
            dialog.show();
            dialog.setMessage("是否退出？退出将不能继续自动抢红包");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Subscribe
    public void notifyVipHasDied(NotifyVipHasDied event) {
        //Vip已经过期
        tvOpen.setText("VIP已经过期,请续费");
    }

    @Subscribe
    public void notifyDataNotPrepare(NotifyDataNotPrepare event) {
        //数据未正常加载
        getPeizhi();
    }

    @Subscribe
    public void notifyForceExit(NotifyForceExit event) {
        //清除登录状态
        KV.put(LocalStorageKeys.QUANJU_LOGIN_STATE, false);
        //退回登录页面
        Intent intent = new Intent(BaseApplication.getBaseApplication(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("type", "showdialog");
        startActivity(intent);
    }
    @Subscribe
    public void notifySetSllepTime(NotifySetSllepTime event) {
        findSystemSleepTime();
    }


    //获取系统的休眠时间
    private void findSystemSleepTime() {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            // 判断是否有WRITE_SETTINGS权限
            if ( Settings.System.canWrite(this) ) {
                setSleepTime();
            }
        } else {
            setSleepTime();
        }
    }

    //设置休眠时间到页面上
    private void setSleepTime() {
        int time = getScreenOffTime();
        if ( time / 1000 % 60 != 0 ) {
            //秒
            tvXiuMianShiJian.setText("设置休眠时间(" + (time / 1000) + "秒后休眠)");
        } else {
            //分
            tvXiuMianShiJian.setText("设置休眠时间(" + (time / 1000 / 60) + "分钟后休眠)");
        }
    }

    //获取系统休眠时间
    private int getScreenOffTime() {
        int screenOffTime = 0;
        try {
            screenOffTime = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch ( Exception localException ) {

        }
        return screenOffTime;
    }
}
