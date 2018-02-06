package com.cretin.www.redpacketplugin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cretin.www.redpacketplugin.R;
import com.cretin.www.redpacketplugin.model.CusUser;
import com.cretin.www.redpacketplugin.model.UserInfoModel;
import com.cretin.www.redpacketplugin.utils.CircleImageView;
import com.cretin.www.redpacketplugin.utils.CommonUtils;
import com.cretin.www.redpacketplugin.utils.KV;
import com.cretin.www.redpacketplugin.utils.LocalStorageKeys;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class VipInfoActivity extends BaseActivity {

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
    @BindView( R.id.iv_avatar )
    CircleImageView ivAvatar;
    @BindView( R.id.tv_username )
    TextView tvUsername;
    @BindView( R.id.iv_level )
    ImageView ivLevel;
    @BindView( R.id.tv_user_des )
    TextView tvUserDes;
    @BindView( R.id.tv_main_des )
    TextView tvMainDes;
    @BindView( R.id.tv_xufei )
    TextView tvXufei;
    @BindView( R.id.tv_lishi )
    TextView tvLishi;
    private CusUser cusUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_info);
        ButterKnife.bind(this);

        lineDivider.setVisibility(View.GONE);
        tvTitleInfo.setText("用户VIP");

        cusUser = KV.get(LocalStorageKeys.USER_INFO);
        if ( cusUser != null ) {
            showDialog();
            BmobQuery<CusUser> query = new BmobQuery<CusUser>();
            query.include("userInfoModel");
            query.getObject(cusUser.getObjectId(), new QueryListener<CusUser>() {
                @Override
                public void done(CusUser object, BmobException e) {
                    if ( e == null ) {
                        KV.put(LocalStorageKeys.USER_INFO, object);
                        cusUser = object;
                        showData(object);
                    } else {
                        showToast("加载超时,请稍后再试");
                    }
                    stopDialog();
                }
            });
        }
    }

    private void showData(CusUser user) {
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
                tvUserDes.setText("尊敬的普通会员");
                ivLevel.setImageResource(R.mipmap.vip_gray);
            } else if ( userInfoModel.getVipLevel() == 1 ) {
                //周卡
                tvUserDes.setText("尊敬的周卡会员");
                ivLevel.setImageResource(R.mipmap.vip_week);
            } else if ( userInfoModel.getVipLevel() == 2 ) {
                //月卡
                tvUserDes.setText("尊敬的月卡会员");
                ivLevel.setImageResource(R.mipmap.vip_month);
            } else if ( userInfoModel.getVipLevel() == 3 ) {
                //年卡
                tvUserDes.setText("高贵的年费会员");
                ivLevel.setImageResource(R.mipmap.vip_supper);
            }

            long nowTime = new Date(System.currentTimeMillis()).getTime();
            if ( nowTime > endlineTime ) {
                //已过期
                tvMainDes.setTextColor(getResources().getColor(R.color.red_ef0000));
                tvMainDes.setText("尊敬的会员，您的会员已于" + endlineTimeStr + "过期，如需继续尊享自动抢红包的服务，请立即续费会员");
            } else {
                //未过期 计算时间
                //计算还剩多少天
                long tempTime = endlineTime - nowTime;
                double d = tempTime / 3600 / 1000;
                int day = ( int ) (d / 24);
                int hour = ( int ) (d % 24);
                int minute = ( int ) (tempTime / 1000 / 60 % 60);
                tvMainDes.setTextColor(getResources().getColor(R.color.green_status));
                tvMainDes.setText(tvUserDes.getText().toString() + "，您的会员有效期还剩下" + (day < 10 ? ("0" + day) : day) + "天" + (hour < 10 ? ("0" + hour) : hour) + "小时" +
                        (minute < 10 ? ("0" + minute) : minute) + "分钟" + "，如需增加时长，可续费会员");
            }
        }
    }


    @OnClick( {R.id.tv_back, R.id.tv_xufei, R.id.tv_lishi} )
    public void onViewClicked(View view) {
        switch ( view.getId() ) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_lishi:
                startActivity(new Intent(this, PayHistoryActivity.class));
                break;
            case R.id.tv_xufei:
                startActivity(new Intent(this, VipActivity.class));
                break;
        }
    }
}
