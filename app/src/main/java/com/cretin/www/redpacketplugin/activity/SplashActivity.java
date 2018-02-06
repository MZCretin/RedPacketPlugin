package com.cretin.www.redpacketplugin.activity;

import android.content.Intent;
import android.os.Bundle;

import com.cretin.www.redpacketplugin.R;
import com.cretin.www.redpacketplugin.base.BaseApplication;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        BaseApplication.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
