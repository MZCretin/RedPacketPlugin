package com.cretin.www.redpacketplugin.model;

import cn.bmob.v3.BmobUser;

public class CusUser extends BmobUser {
    //邀请码
    private String inviteCode;

    private UserInfoModel userInfoModel;

    public UserInfoModel getUserInfoModel() {
        return userInfoModel;
    }

    public void setUserInfoModel(UserInfoModel userInfoModel) {
        this.userInfoModel = userInfoModel;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}