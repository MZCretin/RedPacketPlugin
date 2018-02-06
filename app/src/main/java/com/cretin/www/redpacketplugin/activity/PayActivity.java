package com.cretin.www.redpacketplugin.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cretin.www.redpacketplugin.R;
import com.cretin.www.redpacketplugin.model.PayDetailModel;
import com.cretin.www.redpacketplugin.model.PayTypeModel;
import com.cretin.www.redpacketplugin.utils.FileUtil;
import com.cretin.www.redpacketplugin.utils.PermissionsChecker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class PayActivity extends BaseActivity {

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
    @BindView( R.id.iv_pic )
    ImageView ivPic;
    @BindView( R.id.et_dingdan )
    EditText etDingdan;
    @BindView( R.id.tv_commit )
    TextView tvCommit;
    @BindView( R.id.tv_taocan )
    TextView tvTaocan;
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int REQUEST_CODE = 2; // 请求码
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private int payIndex = 0;
    private PayTypeModel mPayTypeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        mPermissionsChecker = new PermissionsChecker(this);
        ButterKnife.bind(this);

        payIndex = getIntent().getIntExtra("payIndex", 0);

        tvTitleInfo.setText("支付");

        getData();
    }

    //获取数据
    private void getData() {
        showDialog();
        BmobQuery<PayTypeModel> query = new BmobQuery<PayTypeModel>();
        query.addWhereEqualTo("payIndex", payIndex);
        query.findObjects(new FindListener<PayTypeModel>() {
            @Override
            public void done(List<PayTypeModel> object, BmobException e) {
                stopDialog();
                if ( e == null ) {
                    if ( object.isEmpty() ) {
                        showToast("请求超时,请稍后再试");
                    } else {
                        showData(object.get(0));
                    }
                } else {
                    showToast("请求超时,请稍后再试!");
                }
            }
        });
    }

    //展示数据
    private void showData(final PayTypeModel payTypeModel) {
        mPayTypeModel = payTypeModel;
        Picasso.with(this).load(payTypeModel.getPayPicUrl()).into(ivPic);

        tvTaocan.setText(payTypeModel.getComboTypeValue());

        tvRight.setText("保存付款码");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mPermissionsChecker.lacksPermissions(PERMISSIONS) ) {
                    startPermissionsActivity();
                } else {
                    showDialog("正在下载...");
                    download(payTypeModel.getPayPicUrl());
                }
            }
        });
    }

    private void startPermissionsActivity() {
        Intent intent = new Intent(this, PermissionsActivity.class);
        intent.putExtra(PermissionsActivity.EXTRA_PERMISSIONS, PERMISSIONS);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED ) {
            showToast("授权失败");
        }
        if ( requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_GRANTED ) {
            showToast("授权成功");
            showDialog("正在下载...");
            download("http://jokesimg.cretinzp.com/common/red_plugin/zhifu/weixin_28_pic.jpg");
        }
    }

    @OnClick( {R.id.tv_back, R.id.tv_commit} )
    public void onViewClicked(View view) {
        switch ( view.getId() ) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_commit:
                commit();
                break;
        }
    }

    //提交
    private void commit() {
        String payCode = etDingdan.getText().toString().trim();
        if ( mPayTypeModel.getPayType() == 1 ) {
            //支付宝
            if(payCode.length() != 28){
                showToast("支付宝订单号长度为28位,请检查");
                return;
            }
        } else {
            //微信
            if(payCode.length() !=36){
                showToast("微信订单号长度为36位,请检查");
                return;
            }
        }
        if ( TextUtils.isEmpty(payCode) ) {
            showToast("请输入订单号");
            return;
        }
        if ( mPayTypeModel == null ) {
            getData();
            showToast("请求异常,请稍后再试");
            return;
        }
        showDialog();
        PayDetailModel payDetailModel = new PayDetailModel();
        payDetailModel.setPayTypeModel(mPayTypeModel);
        int dayNums = 0;
        String money = "0";
        switch ( mPayTypeModel.getComboType() ) {
            case 1:
                dayNums = 7;
                money = "7";
                break;
            case 2:
                dayNums = 30;
                money = "28";
                break;
            case 3:
                dayNums = 365;
                money = "98";
                break;
        }
        payDetailModel.setDayNums(dayNums);
        payDetailModel.setMessage("正在审核中");
        payDetailModel.setMoney(money);
        payDetailModel.setPayCode(payCode);
        payDetailModel.setState(1);
        payDetailModel.setStateValue("审核中");
        payDetailModel.setPayTypeDes(mPayTypeModel.getPayTypeValue());
        payDetailModel.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                stopDialog();
                if ( e == null ) {
                    showToast("提交成功,请等待审核");
                    finish();
                } else {
                    showToast("请求超时,请稍后再试！");
                }
            }
        });
    }

    private void download(String url) {
        //Target
        Target target = new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                String imageName = System.currentTimeMillis() + ".jpg";

                File dcimFile = FileUtil
                        .getDCIMFile(FileUtil.PATH_PHOTOGRAPH, imageName);

                FileOutputStream ostream = null;
                try {
                    ostream = new FileOutputStream(dcimFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                    ostream.close();
                } catch ( Exception e ) {
                    e.printStackTrace();
                }

                // 最后通知图库更新
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(new File(dcimFile.getPath()))));
                stopDialog();
                Toast.makeText(PayActivity.this, "图片下载至:" + dcimFile, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                stopDialog();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        //Picasso下载
        Picasso.with(this).load(url).into(target);
    }
}
