package com.cretin.www.redpacketplugin.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cretin.www.redpacketplugin.R;


public class AlertDialog extends Dialog implements
        View.OnClickListener {
    private View mView;
    private TextView mTitle;
    public TextView mContent;
    public TextView mBtnCancel;
    private TextView mBtnConfirm;
    private String title;
    private String message;
    private LinearLayout ll_left;

    public AlertDialog(Context context) {
        super(context);
    }

    public AlertDialog(Context context, String title, String message) {
        super(context);
        this.title = title;
        this.message = message;
    }

    //隐藏取消
    public void hideLeft() {
        if ( ll_left != null ) {
            ll_left.setVisibility(View.GONE);
            mBtnConfirm.setBackgroundDrawable(mContent.getResources().getDrawable(R.drawable.dialog_item_bg_selector_left_right_bottom));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.common_dialog);
        mTitle = ( TextView ) findViewById(R.id.title);
        mContent = ( TextView ) findViewById(R.id.dialog_content);
        mBtnCancel = ( TextView ) findViewById(R.id.button_cancel);
        mBtnConfirm = ( TextView ) findViewById(R.id.button_confirm);
        ll_left = ( LinearLayout ) findViewById(R.id.ll_left);
        mBtnCancel.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
    }

    public void setContentView(View mView) {
        setContentView(mView);
    }

    public void setTitle(String title) {
        if ( mTitle != null ) {
            mTitle.setText(title);
        }
    }

    public void hideCancel() {
        if ( mBtnCancel != null )
            mBtnCancel.setVisibility(View.GONE);
    }

    public void setRightButton(String button) {
        if ( mBtnConfirm != null ) {
            mBtnConfirm.setText(button);
        }
    }

    public void setLeftButton(String button) {
        if ( mBtnCancel != null ) {
            mBtnCancel.setText(button);
        }
    }

    public void setMessage(String message) {
        if ( mContent != null ) {
            mContent.setText(message);
        }
    }

    OnPositiveClickListener mClickListener;

    public interface OnPositiveClickListener {
        void onPositiveClickListener(View v);

    }

    public interface OnNegativeClickListener {
        void onNegativeClickListener(View v);
    }

    private OnNegativeClickListener mNegativeListener;

    public AlertDialog setOnNegativeListener(
            OnNegativeClickListener mNegativeListener) {
        this.mNegativeListener = mNegativeListener;
        return this;
    }

    public AlertDialog setOnClickListener(OnPositiveClickListener mClickListener) {
        this.mClickListener = mClickListener;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ) {
            case R.id.button_cancel:
                if ( mNegativeListener != null ) {
                    mNegativeListener.onNegativeClickListener(v);
                }
                this.dismiss();
                break;
            case R.id.button_confirm:
                if ( mClickListener != null ) {
                    this.dismiss();
                    mClickListener.onPositiveClickListener(v);
                }
                break;
            default:
                break;
        }
    }
}
