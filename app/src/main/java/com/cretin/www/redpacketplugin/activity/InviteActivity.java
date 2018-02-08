package com.cretin.www.redpacketplugin.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cretin.www.redpacketplugin.R;
import com.cretin.www.redpacketplugin.model.CusUser;
import com.cretin.www.redpacketplugin.model.InviteInfoModel;
import com.cretin.www.redpacketplugin.utils.KV;
import com.cretin.www.redpacketplugin.utils.LocalStorageKeys;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class InviteActivity extends BaseActivity {
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
    @BindView( R.id.tv_yaoqingma )
    TextView tvYaoqingma;
    @BindView( R.id.tv_copy_yqm )
    TextView tvCopyYqm;
    @BindView( R.id.iv_erweima )
    ImageView ivErweima;
    @BindView( R.id.tv_copy_link )
    TextView tvCopyLink;
    @BindView( R.id.tv_weixin )
    TextView tvWeixin;
    @BindView( R.id.tv_weixin_circle )
    TextView tvWeixinCircle;
    @BindView( R.id.tv_strategy )
    TextView tvStrategy;
    private CusUser cusUser;
    private ClipboardManager cmb;
    private InviteInfoModel inviteInfoModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.bind(this);

        tvTitleInfo.setText("邀请好友");

        cusUser = KV.get(LocalStorageKeys.USER_INFO);
        if ( cusUser != null ) {
            tvYaoqingma.setText(cusUser.getInviteCode());
        }

        showDialog();
        addData();
    }

    private void addData() {
        BmobQuery<InviteInfoModel> query = new BmobQuery<InviteInfoModel>();
        query.setLimit(50);
        query.findObjects(new FindListener<InviteInfoModel>() {
            @Override
            public void done(List<InviteInfoModel> object, BmobException e) {
                stopDialog();
                if ( e == null ) {
                    if ( !object.isEmpty() ) {
                        inviteInfoModel = object.get(0);
                        showData();
                    }
                } else {
                    showToast("请求超时，请稍后再试!");
                }
            }
        });

    }

    //显示数据
    private void showData() {
        //显示攻略
        if ( !TextUtils.isEmpty(inviteInfoModel.getStrategy()) ) {
            tvStrategy.setText(inviteInfoModel.getStrategy());
        }
        //显示下载二维码
        if ( !TextUtils.isEmpty(inviteInfoModel.getDownErWeiMa()) ) {
            Picasso.with(this).load(inviteInfoModel.getDownErWeiMa()).into(ivErweima);
        }
    }

    @OnClick( {R.id.tv_back, R.id.tv_copy_yqm, R.id.tv_copy_link, R.id.tv_weixin, R.id.tv_weixin_circle} )
    public void onViewClicked(View view) {
        switch ( view.getId() ) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_copy_yqm:
                if ( cusUser != null ) {
                    cmb = ( ClipboardManager ) getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(cusUser.getInviteCode());
                    showToast("邀请码已复制");
                }
                break;
            case R.id.tv_copy_link:
                if ( inviteInfoModel != null ) {
                    cmb = ( ClipboardManager ) getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(inviteInfoModel.getApkDownUrl());
                    showToast("下载链接已复制");
                }
                break;
            case R.id.tv_weixin:
                if ( inviteInfoModel != null ) {
                    share(1);
                }
                break;
            case R.id.tv_weixin_circle:
                if ( inviteInfoModel != null ) {
                    share(2);
                }
                break;
        }
    }

    private void share(int type) {
        ShareAction shareAction = new ShareAction(this)
                .withText(inviteInfoModel.getShareContent());//分享内容
        UMWeb web = new UMWeb(inviteInfoModel.getShareUrl());
        web.setTitle(inviteInfoModel.getShareTitle());//标题
        if ( TextUtils.isEmpty(inviteInfoModel.getShareImgUrl()) ) {
            UMImage image = new UMImage(this, R.mipmap.hongbao);//网络图片
            web.setThumb(image);  //缩略图
        } else {
            UMImage image = new UMImage(this, inviteInfoModel.getShareImgUrl());//网络图片
            web.setThumb(image);  //缩略图
        }
        web.setDescription(inviteInfoModel.getShareContent());//描述
        shareAction.withMedia(web);
        switch ( type ) {
            case 1:
                shareAction.setPlatform(SHARE_MEDIA.WEIXIN);//传入平台
                break;
            case 2:
                shareAction.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE);//传入平台
                break;
        }
        shareAction.setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                showToast("分享成功");
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                showToast("分享失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                showToast("取消分享");
            }
        })//回调监听器
                .share();
    }
}
