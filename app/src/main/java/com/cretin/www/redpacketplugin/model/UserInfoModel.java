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
