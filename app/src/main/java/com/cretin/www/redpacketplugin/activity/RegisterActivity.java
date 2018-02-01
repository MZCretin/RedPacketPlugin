package com.cretin.www.redpacketplugin.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cretin.www.redpacketplugin.R;
import com.cretin.www.redpacketplugin.base.BaseApplication;
import com.cretin.www.redpacketplugin.model.CusUser;
import com.cretin.www.redpacketplugin.model.UserInfoModel;
import com.cretin.www.redpacketplugin.utils.CommonUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private com.cretin.www.clearedittext.view.ClearEditText mEd_username;
    private com.cretin.www.clearedittext.view.ClearEditText mEd_password;
    private com.cretin.www.clearedittext.view.ClearEditText mEd_password_confirm;
    private com.cretin.www.clearedittext.view.ClearEditText mEd_code;
    private TextView mTv_get_code;
    private com.cretin.www.clearedittext.view.ClearEditText mEd_invite_code;
    private LinearLayout mLl_main_title;
    private TextView mTv_back;
    private TextView mTv_title_info;
    private TextView mTv_right;
    private ImageView mIv_right;
    private View mLine_divider;
    private TextView mTv_back_login;
    private TextView mTv_register;
    private Handler handlerCode = BaseApplication.getHandler();
    private int countDown = 60;// 倒计时秒数
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if ( countDown != 1 ) {
                countDown--;
                mTv_get_code.setText("还剩" + countDown + "S");
                mTv_get_code.setEnabled(false);
                handlerCode.postDelayed(this, 1000);
            } else if ( countDown == 1 ) {
                countDown = 60;
                mTv_get_code.setEnabled(true);
                mTv_get_code.setText("重新发送");
            } else if ( countDown <= 0 ) {
                countDown = 60;
                mTv_get_code.setEnabled(true);
                mTv_get_code.setText("获取验证码");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bindViews();
        initEvent();
    }

    private void initEvent() {
        mTv_title_info.setText("新用户注册");
        mTv_get_code.setOnClickListener(this);
        mTv_back_login.setOnClickListener(this);
        mTv_register.setOnClickListener(this);
        mTv_back.setOnClickListener(this);
    }

    private void bindViews() {
        mEd_username = ( com.cretin.www.clearedittext.view.ClearEditText ) findViewById(R.id.ed_username);
        mEd_password = ( com.cretin.www.clearedittext.view.ClearEditText ) findViewById(R.id.ed_password);
        mEd_password_confirm = ( com.cretin.www.clearedittext.view.ClearEditText ) findViewById(R.id.ed_password_confirm);
        mEd_code = ( com.cretin.www.clearedittext.view.ClearEditText ) findViewById(R.id.ed_code);
        mTv_get_code = ( TextView ) findViewById(R.id.tv_get_code);
        mEd_invite_code = ( com.cretin.www.clearedittext.view.ClearEditText ) findViewById(R.id.ed_invite_code);
        mTv_back_login = ( TextView ) findViewById(R.id.tv_back_login);
        mTv_register = ( TextView ) findViewById(R.id.tv_register);
        mLl_main_title = ( LinearLayout ) findViewById(R.id.ll_main_title);
        mTv_back = ( TextView ) findViewById(R.id.tv_back);
        mTv_title_info = ( TextView ) findViewById(R.id.tv_title_info);
        mTv_right = ( TextView ) findViewById(R.id.tv_right);
        mIv_right = ( ImageView ) findViewById(R.id.iv_right);
        mLine_divider = ( View ) findViewById(R.id.line_divider);
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_back_login:
                finish();
                break;
            case R.id.tv_register:
                register();
                break;
            case R.id.tv_get_code:
                getCode();
                break;
        }
    }

    //获取验证码
    private void getCode() {
        final String phone = mEd_username.getText().toString().trim();
        if ( TextUtils.isEmpty(phone) ) {
            showToast("手机号不能为空");
            return;
        }
        showDialog("发送验证码...");
        BmobQuery<CusUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", phone);
        query.findObjects(new FindListener<CusUser>() {
            @Override
            public void done(List<CusUser> object, BmobException e) {
                if ( e == null ) {
                    if ( object.size() == 0 ) {
                        //获取验证码
                        BmobSMS.requestSMSCode(phone, "RedPackage", new QueryListener<Integer>() {
                            @Override
                            public void done(Integer smsId, BmobException ex) {
                                stopDialog();
                                if ( ex == null ) {
                                    //验证码发送成功
                                    showToast("验证码发送成功");
                                    handlerCode.postDelayed(runnable, 1000);
                                } else {
                                    if ( ex.getErrorCode() == 10010 ) {
                                        showToast("验证码发送太快,请稍后再试");
                                    } else
                                        showToast(ex.getMessage());
                                }
                            }
                        });
                    } else {
                        stopDialog();
                        showToast("该手机号已经被注册,请直接登录");
                    }
                } else {
                    stopDialog();
                }
            }
        });
    }

    //注册操作
    private void register() {
        final String phone = mEd_username.getText().toString().trim();
        final String password = mEd_password.getText().toString();
        String password1 = mEd_password_confirm.getText().toString();
        final String code = mEd_code.getText().toString();
        String inviteCode = mEd_invite_code.getText().toString();
        if ( TextUtils.isEmpty(phone) ) {
            showToast("手机号不能为空");
            return;
        }
        if ( TextUtils.isEmpty(password) ) {
            showToast("密码不能为空");
            return;
        }
        if ( !password.equals(password1) ) {
            showToast("两次密码输入不一致");
        }
        if ( TextUtils.isEmpty(code) ) {
            showToast("验证码不能为空");
            return;
        }
        showDialog("正在注册...");

        if ( !TextUtils.isEmpty(inviteCode) ) {
            //检查邀请码
            BmobQuery<CusUser> query = new BmobQuery<>();
            query.addWhereEqualTo("inviteCode", inviteCode);
            query.findObjects(new FindListener<CusUser>() {
                @Override
                public void done(List<CusUser> object, BmobException e) {
                    if ( e == null ) {
                        if ( object.isEmpty() ) {
                            stopDialog();
                            showToast("该邀请码不存在，请检查后重试");
                        } else {
                            //可以去注册
                            final UserInfoModel userInfoModel = new UserInfoModel();
                            userInfoModel.setInvitedUser(object.get(0));
                            userInfoModel.setAllMoney(0);
                            userInfoModel.setInviteList(null);
                            //默认一天时间
                            userInfoModel.setLeftDays(1);
                            userInfoModel.setPackageNums(0);
                            userInfoModel.setModeState(0);
                            userInfoModel.setModeStateValue("当前未选择");
                            userInfoModel.setVipLevel(0);
                            userInfoModel.save(new SaveListener<String>() {

                                @Override
                                public void done(String objectId, BmobException e) {
                                    if ( e == null ) {
                                        doRegister(phone, password, code, userInfoModel);
                                    } else {
                                        showToast("注册失败,稍后再试");
                                    }
                                }
                            });
                        }
                    } else {
                        stopDialog();
                    }
                }
            });
            return;
        }

        //没有被人邀请
        final UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setAllMoney(0);
        userInfoModel.setInviteList(null);
        //默认一天时间
        userInfoModel.setLeftDays(1);
        userInfoModel.setModeState(0);
        userInfoModel.setModeStateValue("当前未选择");
        userInfoModel.setPackageNums(0);
        userInfoModel.setVipLevel(0);
        userInfoModel.save(new SaveListener<String>() {

            @Override
            public void done(String objectId, BmobException e) {
                if ( e == null ) {
                    doRegister(phone, password, code, userInfoModel);
                } else {
                    showToast("注册失败,稍后再试");
                }
            }
        });
    }

    private void doRegister(String phone, String password, String code, UserInfoModel userInfoModel) {
        //用手机号进行注册
        CusUser user = new CusUser();
        user.setMobilePhoneNumber(phone);//设置手机号码（必填）
        user.setPassword(password);
        //设置邀请码
        user.setInviteCode(CommonUtils.getInviteCode());
        if ( userInfoModel != null )
            user.setUserInfoModel(userInfoModel);
        //设置用户密码
        user.signOrLogin(code, new SaveListener<CusUser>() {

            @Override
            public void done(CusUser user, BmobException e) {
                stopDialog();
                if ( e == null ) {
                    showToast("注册成功");
                    handlerCode.removeCallbacks(runnable);
                    finish();
                } else {
                    showToast("注册失败,稍后再试");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerCode.removeCallbacks(runnable);
    }
}
