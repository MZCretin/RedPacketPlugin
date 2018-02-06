package com.cretin.www.redpacketplugin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cretin.www.redpacketplugin.R;
import com.cretin.www.redpacketplugin.model.PayTypeModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class VipActivity extends BaseActivity {

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
    @BindView( R.id.tv01 )
    TextView tv01;
    @BindView( R.id.tv01_des )
    TextView tv01Des;
    @BindView( R.id.iv_01 )
    ImageView iv01;
    @BindView( R.id.ll_01 )
    LinearLayout ll01;
    @BindView( R.id.tv02 )
    TextView tv02;
    @BindView( R.id.tv02_des )
    TextView tv02Des;
    @BindView( R.id.iv_02 )
    ImageView iv02;
    @BindView( R.id.ll_02 )
    LinearLayout ll02;
    @BindView( R.id.tv03 )
    TextView tv03;
    @BindView( R.id.tv03_des )
    TextView tv03Des;
    @BindView( R.id.iv_03 )
    ImageView iv03;
    @BindView( R.id.ll_03 )
    LinearLayout ll03;
    @BindView( R.id.iv_zhifubao )
    ImageView ivZhifubao;
    @BindView( R.id.ll_zhifubao )
    LinearLayout llZhifubao;
    @BindView( R.id.iv_weixin )
    ImageView ivWeixin;
    @BindView( R.id.ll_weixin )
    LinearLayout llWeixin;
    @BindView( R.id.tv_open )
    TextView tvOpen;
    //支付选择的模式
    private int currZhifuIndex = 0;
    //套餐
    private int currTaocan = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        ButterKnife.bind(this);

        tvTitleInfo.setText("充值会员");

        getData();
    }

    //获取数据
    private void getData() {
        showDialog();
        BmobQuery<PayTypeModel> query = new BmobQuery<PayTypeModel>();
        query.findObjects(new FindListener<PayTypeModel>() {
            @Override
            public void done(List<PayTypeModel> object, BmobException e) {
                stopDialog();
                if ( e == null ) {
                    if ( object.size() == 6 ) {
                        for ( PayTypeModel payTypeModel : object ) {
                            if ( payTypeModel.getPayType() == 1 ) {
                                if ( payTypeModel.getComboType() == 1 ) {
                                    //周卡
                                    tv01.setText(payTypeModel.getComboTypeValue());
                                    tv01Des.setText(payTypeModel.getMoneyDes());
                                } else if ( payTypeModel.getComboType() == 2 ) {
                                    tv02.setText(payTypeModel.getComboTypeValue());
                                    tv02Des.setText(payTypeModel.getMoneyDes());
                                } else if ( payTypeModel.getComboType() == 3 ) {
                                    tv03.setText(payTypeModel.getComboTypeValue());
                                    tv03Des.setText(payTypeModel.getMoneyDes());
                                }
                            }
                        }
                    } else {
                        showToast("数据获取失败");
                    }
                } else {
                    showToast("请求超时,请稍后再试!");
                }
            }
        });
    }

    @OnClick( {R.id.tv_back, R.id.ll_01, R.id.ll_02, R.id.ll_03, R.id.ll_zhifubao, R.id.ll_weixin, R.id.tv_open} )
    public void onViewClicked(View view) {
        switch ( view.getId() ) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.ll_01:
                selectTaocan(1);
                break;
            case R.id.ll_02:
                selectTaocan(2);
                break;
            case R.id.ll_03:
                selectTaocan(3);
                break;
            case R.id.ll_zhifubao:
                selectZhifu(1);
                break;
            case R.id.ll_weixin:
                selectZhifu(2);
                break;
            case R.id.tv_open:
                doZhifu();
                break;
        }
    }

    private void doZhifu() {
        if ( currTaocan == 0 ) {
            showToast("请选择套餐");
            return;
        }
        if ( currZhifuIndex == 0 ) {
            showToast("请选择支付方式");
            return;
        }
        //根据所选择的按钮选择出PayIndex
        int payIndex = 0;
        // 1 支付宝 周卡 2 支付宝 月卡 3 支付宝 年费 4 微信 周卡 5 微信 月卡 6 微信 年费
        if ( currZhifuIndex == 1 ) {
            //支付宝
            if ( currTaocan == 1 ) {
                //周卡
                payIndex = 1;
            } else if ( currTaocan == 2 ) {
                //月卡
                payIndex = 2;
            } else {
                //年费
                payIndex = 3;
            }
        } else {
            //微信
            if ( currTaocan == 1 ) {
                //周卡
                payIndex = 4;
            } else if ( currTaocan == 2 ) {
                //月卡
                payIndex = 5;
            } else {
                //年费
                payIndex = 6;
            }
        }
        if ( payIndex == 0 ) {
            showToast("请求超时,请稍后再试");
            return;
        }
        Intent intent = new Intent(this, PayActivity.class);
        intent.putExtra("payIndex", payIndex);
        startActivity(intent);
    }

    //选择支付方式
    private void selectZhifu(int i) {
        currZhifuIndex = i;
        ivZhifubao.setImageResource(R.mipmap.select);
        ivWeixin.setImageResource(R.mipmap.select);
        if ( i == 1 ) {
            ivZhifubao.setImageResource(R.mipmap.select_done);
        } else {
            ivWeixin.setImageResource(R.mipmap.select_done);
        }
    }

    //选择套餐
    private void selectTaocan(int i) {
        currTaocan = i;
        iv01.setImageResource(R.mipmap.select);
        iv02.setImageResource(R.mipmap.select);
        iv03.setImageResource(R.mipmap.select);
        if ( i == 1 ) {
            iv01.setImageResource(R.mipmap.select_done);
        } else if ( i == 2 ) {
            iv02.setImageResource(R.mipmap.select_done);
        } else {
            iv03.setImageResource(R.mipmap.select_done);
        }
    }
}
