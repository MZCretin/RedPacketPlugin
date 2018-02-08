package com.cretin.www.redpacketplugin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cretin.www.clearedittext.view.ClearEditText;
import com.cretin.www.cretinautoupdatelibrary.interfaces.ForceExitCallBack;
import com.cretin.www.cretinautoupdatelibrary.model.UpdateEntity;
import com.cretin.www.cretinautoupdatelibrary.utils.CretinAutoUpdateUtils;
import com.cretin.www.redpacketplugin.R;
import com.cretin.www.redpacketplugin.config.eventbus.NotifyOnActivityStop;
import com.cretin.www.redpacketplugin.model.CusUser;
import com.cretin.www.redpacketplugin.model.VersionUpdateModel;
import com.cretin.www.redpacketplugin.utils.AlertDialog;
import com.cretin.www.redpacketplugin.utils.KV;
import com.cretin.www.redpacketplugin.utils.LocalStorageKeys;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class LoginActivity extends BaseActivity {
    @BindView( R.id.ed_username )
    ClearEditText edUsername;
    @BindView( R.id.ed_password )
    ClearEditText edPassword;
    @BindView( R.id.tv_register )
    TextView tvRegister;
    @BindView( R.id.tv_get_password )
    TextView tvGetPassword;
    @BindView( R.id.tv_login )
    TextView tvLogin;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            edPassword.requestFocus();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        String type = getIntent().getStringExtra("type");
        //显示被强挤下线
        if ( !TextUtils.isEmpty(type) ) {
            //关闭服务 不能关就算了 不提示
            EventBus.getDefault().post(new NotifyOnActivityStop());
            final AlertDialog dialog = new AlertDialog(this);
            dialog.setOnClickListener(new AlertDialog.OnPositiveClickListener() {
                @Override
                public void onPositiveClickListener(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            dialog.setMessage("您的账号在异地登录，可能密码已经泄露，请尽快修改密码，修改密码可以通过找回密码重置");
            dialog.setLeftButton("重置密码");
            dialog.setOnNegativeListener(new AlertDialog.OnNegativeClickListener() {
                @Override
                public void onNegativeClickListener(View v) {
                    startActivity(new Intent(LoginActivity.this, ResetPswActivity.class));
                }
            });
        }

        //检查有没有数据 有就显示
        String info = KV.get(LocalStorageKeys.USER_LOGIN_INFO);
        if ( !TextUtils.isEmpty(info) ) {
            String phone = info.split("#")[0];
            String psw = info.split("#")[1];
            edUsername.setText(phone);
            edPassword.setText(psw);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(1);
                }
            }, 100);
        }

        checkUpdate();
    }

    //检查是否有更新
    private void checkUpdate() {
        BmobQuery<VersionUpdateModel> query = new BmobQuery<VersionUpdateModel>();
        query.setLimit(50);
        query.findObjects(new FindListener<VersionUpdateModel>() {
            @Override
            public void done(List<VersionUpdateModel> object, BmobException e) {
                if ( e == null ) {
                    if ( !object.isEmpty() ) {
                        VersionUpdateModel versionUpdateModel = object.get(0);
                        UpdateEntity data = new UpdateEntity();
                        data.setUpdateLog(versionUpdateModel.getUpdateLog());
                        data.setDownurl(versionUpdateModel.getDownurl());
                        data.setHasAffectCodes(versionUpdateModel.getHasAffectCodes());
                        data.setIsForceUpdate(versionUpdateModel.getIsForceUpdate());
                        data.setPreBaselineCode(versionUpdateModel.getPreBaselineCode());
                        data.setSize(versionUpdateModel.getSize());
                        data.setVersionCode(versionUpdateModel.getVersionCode());
                        data.setVersionName(versionUpdateModel.getVersionName());
                        CretinAutoUpdateUtils.getInstance(LoginActivity.this).check(data, new ForceExitCallBack() {
                            @Override
                            public void exit() {
                                LoginActivity.this.finish();
                            }
                        }, true);
                    }
                }
            }
        });
    }

    @OnClick( {R.id.tv_register, R.id.tv_get_password, R.id.tv_login} )
    public void onViewClicked(View view) {
        switch ( view.getId() ) {
            case R.id.tv_register:
                //去注册
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_get_password:
                //重置密码
                startActivity(new Intent(this, ResetPswActivity.class));
                break;
            case R.id.tv_login:
                login();
                break;
        }
    }

    //登录
    private void login() {
        final String phone = edUsername.getText().toString().trim();
        final String password = edPassword.getText().toString();
        if ( TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) ) {
            showToast("用户名或密码不能为空！");
            return;
        }
        showDialog("正在登录...");
        CusUser bu2 = new CusUser();
        bu2.setUsername(phone);
        bu2.setPassword(password);
        bu2.login(new SaveListener<CusUser>() {
                      @Override
                      public void done(final CusUser bmobUser, BmobException e) {
                          if ( e == null ) {
                              //更新数据
                              String installationId = BmobInstallationManager.getInstallationId();
                              if ( !TextUtils.isEmpty(installationId) && installationId.equals(bmobUser.getInstallationId()) ) {
                                  //上次登录也是自己 放行
                              } else {
                                  //上次登录不是我自己 推送让其退出登录
                                  tuisong(bmobUser.getInstallationId());
                              }
                              //设置唯一标示
                              bmobUser.setInstallationId(installationId);
                              bmobUser.update(new UpdateListener() {
                                  @Override
                                  public void done(BmobException e) {
                                      if ( e == null ) {
                                          showToast("登录成功");
                                          //更新成功
                                          CusUser cusUser = KV.get(LocalStorageKeys.USER_INFO);
                                          if ( cusUser != null ) {
                                              bmobUser.setUserInfoModel(cusUser.getUserInfoModel());
                                          }
                                          KV.put(LocalStorageKeys.USER_INFO, bmobUser);
                                          //保存账户和密码
                                          String info = phone + "#" + password;
                                          KV.put(LocalStorageKeys.USER_LOGIN_INFO, info);
                                          KV.put(LocalStorageKeys.QUANJU_LOGIN_STATE, true);
                                          startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                          stopDialog();
                                          finish();
                                      } else {
                                          stopDialog();
                                          //更新失败
                                          showToast("登录失败，请稍后重试");
                                      }
                                  }
                              });
                          } else {
                              stopDialog();
                              if ( e.getErrorCode() == 101 ) {
                                  showToast("用户名或密码错误");
                              } else
                                  showToast("网络异常");
                          }

                      }
                  }
        );
    }

    private int retryTimes;

    /**
     * 推送
     *
     * @param installationId
     */
    private void tuisong(final String installationId) {
        if ( !TextUtils.isEmpty(installationId) ) {
            BmobPushManager bmobPushManager = new BmobPushManager();
            BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
            query.addWhereEqualTo("installationId", installationId);
            bmobPushManager.setQuery(query);
            bmobPushManager.pushMessage("MESSAGE_YIDI_LOGIN", new PushListener() {
                @Override
                public void done(BmobException e) {
                    if ( e == null ) {
                    } else {
                        if ( retryTimes < 3 ) {
                            retryTimes++;
                            tuisong(installationId);
                        }
                    }
                }
            });
        }
    }
}
