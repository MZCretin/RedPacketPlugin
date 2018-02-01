package com.cretin.www.redpacketplugin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.cretin.www.redpacketplugin.utils.CustomProgressDialog;

public abstract class BaseActivity extends AppCompatActivity {
    private CustomProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
    }

    /**
     * 显示加载对话框
     *
     * @param msg
     */
    public void showDialog(String msg) {
        if ( dialog == null ) {
            dialog = CustomProgressDialog.createDialog(this);
        }
        if ( msg != null && !msg.equals("") ) {
            dialog.setMessage(msg);
        }
        dialog.show();
    }

    /**
     * 通用对话框
     */
    public void showDialog() {
        showDialog("加载中...");
    }


    /**
     * 关闭对话框
     */
    public void stopDialog() {
        if ( dialog != null && dialog.isShowing() ) {
            dialog.dismiss();
        }
    }

    /**
     * 弹出toast
     *
     * @param msg
     */
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
