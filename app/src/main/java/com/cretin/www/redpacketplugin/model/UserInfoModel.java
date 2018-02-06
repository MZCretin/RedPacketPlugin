package com.cretin.www.redpacketplugin.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/1/31.
 */

public class UserInfoModel extends BmobObject{
    //剩余天数
    private int leftDays;
    //邀请的用户
    private List<CusUser> inviteList;
    //被邀请的用户
    private CusUser invitedUser;
    //已抢红包数
    private int packageNums;
    //红包金额总数
    private double allMoney;
    //用户vip等级 0 普通用户 1 周卡  2 月卡  3 终生免费
    private int vipLevel;

    //设置通知的状态 0 关闭 1 开启
    private int tongzhiState;
    //提示音和震动状态 0 关闭 1 开启
    private int tishiyinState;
    //模式选择 0 未选择 1 自动抢 单聊 2 自动抢 群聊 3 自动抢 all 4 通知 all 5 通知 单聊 6 通知 群聊
    private int modeState;
    //模式描述
    private String modeStateValue;

    public String getModeStateValue() {
        return modeStateValue;
    }

    public void setModeStateValue(String modeStateValue) {
        this.modeStateValue = modeStateValue;
    }

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

    public int getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(int vipLevel) {
        this.vipLevel = vipLevel;
    }

    public int getLeftDays() {
        return leftDays;
    }

    public void setLeftDays(int leftDays) {
        this.leftDays = leftDays;
    }

    public List<CusUser> getInviteList() {
        return inviteList;
    }

    public void setInviteList(List<CusUser> inviteList) {
        this.inviteList = inviteList;
    }

    public CusUser getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(CusUser invitedUser) {
        this.invitedUser = invitedUser;
    }

    public int getPackageNums() {
        return packageNums;
    }

    public void setPackageNums(int packageNums) {
        this.packageNums = packageNums;
    }

    public double getAllMoney() {
        return allMoney;
    }

    public void setAllMoney(double allMoney) {
        this.allMoney = allMoney;
    }
}
