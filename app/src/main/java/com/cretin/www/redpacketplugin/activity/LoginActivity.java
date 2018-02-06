package com.cretin.www.redpacketplugin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cretin.www.clearedittext.view.ClearEditText;
import com.cretin.www.redpacketplugin.R;
import com.cretin.www.redpacketplugin.model.CusUser;
import com.cretin.www.redpacketplugin.utils.KV;
import com.cretin.www.redpacketplugin.utils.LocalStorageKeys;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

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
        bu2.add("include","userInfoModel");
        bu2.login(new SaveListener<CusUser>() {
                      @Override
                      public void done(CusUser bmobUser, BmobException e) {
                          stopDialog();
                          if ( e == null ) {
                              showToast("登录成功");
                              //保存账户和密码
                              String info = phone + "#" + password;
                              KV.put(LocalStorageKeys.USER_LOGIN_INFO, info);
                              startActivity(new Intent(LoginActivity.this, MainActivity.class));
                              finish();
                          } else {
                              if ( e.getErrorCode() == 101 ) {
                                  showToast("用户名或密码错误");
                              } else
                                  showToast("网络异常");
                          }
                      }
                  }
        );
    }


}
