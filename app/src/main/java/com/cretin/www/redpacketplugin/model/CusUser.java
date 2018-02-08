package com.cretin.www.redpacketplugin.model;

import cn.bmob.v3.BmobUser;

public class CusUser extends BmobUser {
    //邀请码
    private String inviteCode;
    //installationId 当前登录的用户
    private String installationId;
    //被邀请的用户id
    private String invitedUserId;

    public String getInvitedUserId() {
        return invitedUserId;
    }

    public void setInvitedUserId(String invitedUserId) {
        this.invitedUserId = invitedUserId;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

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