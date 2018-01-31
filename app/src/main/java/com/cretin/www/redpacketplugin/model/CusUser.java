package com.cretin.www.redpacketplugin.model;

import cn.bmob.v3.BmobUser;

public class CusUser extends BmobUser {
    //邀请码
    private String inviteCode;

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}