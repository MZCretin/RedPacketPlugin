package com.cretin.www.redpacketplugin.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cretin.www.redpacketplugin.R;
import com.cretin.www.redpacketplugin.config.eventbus.NotifySetSllepTime;
import com.cretin.www.redpacketplugin.model.SleepTimeModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetSleepTimeActivity extends BaseActivity {

    @BindView( R.id.tv_back )
    TextView tvBack;
    @BindView( R.id.tv_title_info )
    TextView tvTitleInfo;
    @BindView( R.id.tv_right )
    TextView tvRight;
    @BindView( R.id.iv_right )
    ImageView ivRight;
    @BindView( R.id.line_divider )
    View lineDivider;
    @BindView( R.id.ll_main_title )
    LinearLayout llMainTitle;
    @BindView( R.id.recyclerview )
    RecyclerView recyclerview;
    private List<SleepTimeModel> list;
    private Integer[] time = {30 * 1000, 60 * 1000, 5 * 60 * 1000, 10 * 60 * 1000, 20 * 60 * 1000, 30 * 60 * 1000};
    private Integer[] type = {1, 2, 2, 2, 2, 2};
    private Adapter adapter;
    //系统锁屏时间
    private int screenOffTime;
    private static final int REQUEST_CODE = 2; // 请求码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_sleep_time);
        ButterKnife.bind(this);
        tvTitleInfo.setText("设置休眠时间");

        initData();

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            // 判断是否有WRITE_SETTINGS权限
            if ( !Settings.System.canWrite(this) ) {
                // 申请WRITE_SETTINGS权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                addData();
            }
        } else {
            addData();
        }
    }

    /**
     * 获取锁屏时间
     *
     * @return
     */
    private int getScreenOffTime() {
        int screenOffTime = 0;
        try {
            screenOffTime = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch ( Exception localException ) {

        }
        return screenOffTime;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        list = new ArrayList<>();
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(list);
        adapter.setNotDoAnimationCount(1);
        recyclerview.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                setTime(list.get(position).getTime());
                EventBus.getDefault().post(new NotifySetSllepTime());
            }
        });
    }

    //    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if ( requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED ) {
//            showToast("授权失败");
//        }
//        if ( requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_GRANTED ) {
//            showToast("授权成功");
//            addData();
//        }
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == REQUEST_CODE ) {
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                // 判断是否有WRITE_SETTINGS权限
                if ( Settings.System.canWrite(this) ) {
                    addData();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addData() {
        screenOffTime = getScreenOffTime();
        list.clear();
        for ( int i = 0; i < time.length; i++ ) {
            SleepTimeModel model = new SleepTimeModel();
            model.setTime(time[i]);
            model.setType(type[i]);
            list.add(model);
        }

        if ( screenOffTime != 0 ) {
            boolean has = false;
            for ( int t :
                    time ) {
                if ( screenOffTime == t ) {
                    has = true;
                }
            }
            if ( !has ) {
                SleepTimeModel model = new SleepTimeModel();
                if ( screenOffTime / 1000 % 60 != 0 ) {
                    //秒
                    model.setType(1);
                    model.setTime(screenOffTime);
                } else {
                    //分
                    model.setType(2);
                    model.setTime(screenOffTime);
                }
                list.add(model);
            }
        }

        Collections.sort(list);
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置时间
     *
     * @param time
     */
    private void setTime(int time) {
        try {
            Settings.System.putInt(getContentResolver(),
                    android.provider.Settings.System.SCREEN_OFF_TIMEOUT, time);
            screenOffTime = getScreenOffTime();
            adapter.notifyDataSetChanged();
        } catch ( Exception e ) {

        }
    }

    @OnClick( R.id.tv_back )
    public void onViewClicked() {
        finish();
    }

    class Adapter extends BaseQuickAdapter<SleepTimeModel, BaseViewHolder> {

        public Adapter(@Nullable List<SleepTimeModel> data) {
            super(R.layout.item_recycler_set_sleep_time, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, SleepTimeModel item) {
            if ( item.getType() == 1 ) {
                //秒
                helper.setText(R.id.tv_title, (item.getTime() / 1000) + "秒后休眠");
            } else {
                //分钟
                helper.setText(R.id.tv_title, (item.getTime() / 1000 / 60) + "分钟后休眠");
            }
            if ( item.getTime() == screenOffTime ) {
                helper.setImageResource(R.id.iv_select, R.mipmap.select_done);
            } else {
                helper.setImageResource(R.id.iv_select, R.mipmap.select);
            }
        }
    }
}
