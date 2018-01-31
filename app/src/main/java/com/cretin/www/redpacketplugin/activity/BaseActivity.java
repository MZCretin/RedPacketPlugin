package com.cretin.www.redpacketplugin.activity;

import android.support.v7.app.AppCompatActivity;

import com.cretin.www.redpacketplugin.utils.CustomProgressDialog;

public abstract class BaseActivity extends AppCompatActivity {
    private CustomProgressDialog dialog;

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
     * 关闭对话框
     */
    public void stopDialog() {
        if ( dialog != null && dialog.isShowing() ) {
            dialog.dismiss();
        }
    }
}
