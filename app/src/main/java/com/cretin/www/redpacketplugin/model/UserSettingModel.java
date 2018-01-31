package com.cretin.www.redpacketplugin.model;

/**
 * Created by cretin on 2018/1/31.
 */

public class UserSettingModel {
    //设置通知的状态 0 关闭 1 开启
    private int tongzhiState;
    //提示音和震动状态 0 关闭 1 开启
    private int tishiyinState;
    //模式选择 0 未选择 1 自动抢 单聊 2 自动抢 群聊 3 自动抢 all 4 通知 all 5 通知 单聊 6 通知 群聊
    private int  modeState;

    public int getTongzhiState() {
        return tongzhiState;
    }

    public void setTongzhiState(int tongzhiState) {
        this.tongzhiState = tongzhiState;
    }

    public int getTishiyinState() {
        return tishiyinState;
    }

    public void setTishiyinState(int tishiyinState) {
        this.tishiyinState = tishiyinState;
    }

    public int getModeState() {
        return modeState;
    }

    public void setModeState(int modeState) {
        this.modeState = modeState;
    }
}
