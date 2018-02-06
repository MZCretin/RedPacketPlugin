package com.cretin.www.redpacketplugin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cretin.www.redpacketplugin.R;
import com.cretin.www.redpacketplugin.config.eventbus.NotifyOnActivityStop;
import com.cretin.www.redpacketplugin.model.CusUser;
import com.cretin.www.redpacketplugin.model.RedPackageInfoModel;
import com.cretin.www.redpacketplugin.model.UserInfoModel;
import com.cretin.www.redpacketplugin.utils.AlertDialog;
import com.cretin.www.redpacketplugin.utils.CircleImageView;
import com.cretin.www.redpacketplugin.utils.CommonUtils;
import com.cretin.www.redpacketplugin.utils.KV;
import com.cretin.www.redpacketplugin.utils.LocalStorageKeys;

import org.greenrobot.eventbus.EventBus;

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
    public static final String TAG = "MainActivity";
    @BindView( R.id.swip_refresh )
    SwipeRefreshLayout swipRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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
    }

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
                    stopDialog();
                    swipRefresh.setRefreshing(false);
                }
            });
        }

        //同步本地数据到服务器
        final List<RedPackageInfoModel> list = KV.get(LocalStorageKeys.RED_PACKAGE_LIST);
        if ( list != null && !list.isEmpty() ) {
            List<BmobObject> datas = new ArrayList<>();
            for ( RedPackageInfoModel red :
                    list ) {
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
                            UserInfoModel userInfoModel = cusUser.getUserInfoModel();
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
                                                            cusUser.setUserInfoModel(object);
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
            long endlineTime = CommonUtils.timeStrFormatToLong(createdAt) + leftDay * 24 * 60 * 60 * 1000;
            //获取截止时间字符串
            String endlineTimeStr = CommonUtils.timeLongFormatToStr(endlineTime);
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
                                            cusUser.setUserInfoModel(object);
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

    @OnClick( {R.id.iv_level, R.id.tv_user_des, R.id.tv_open, R.id.ll_vip, R.id.ll_mode, R.id.ll_shuoming} )
    public void onViewClicked(View view) {
        switch ( view.getId() ) {
            case R.id.iv_level:
                break;
            case R.id.tv_user_des:
                break;
            case R.id.tv_open:
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
            case R.id.ll_mode:
                startActivity(new Intent(this, SelectModeActivity.class));
                break;
            case R.id.ll_shuoming:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getServiceState(false);
    }

    //获取当前服务状态
    private void getServiceState(boolean flag) {
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

    private long lastBackTime;

    //在需要监听的activity中重写onKeyDown()。
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0 ) {
            long currentTime = System.currentTimeMillis();
            if ( currentTime - lastBackTime > 1 * 1000 ) {
                lastBackTime = currentTime;
                showToast("再按一次退出程序");
            } else {
                NotifyOnActivityStop stop = new NotifyOnActivityStop();
                EventBus.getDefault().post(stop);
                MainActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
