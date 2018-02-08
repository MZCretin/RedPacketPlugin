package com.cretin.www.redpacketplugin.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/2/7.
 */

public class VersionUpdateModel extends BmobObject {
    private int versionCode = 0;
    private int isForceUpdate = 0;
    private int preBaselineCode = 0;
    private String versionName = "";
    private String downurl = "";
    private String updateLog = "";
    private String size = "";
    private String hasAffectCodes = "";

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getIsForceUpdate() {
        return isForceUpdate;
    }

    public void setIsForceUpdate(int isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }

    public int getPreBaselineCode() {
        return preBaselineCode;
    }

    public void setPreBaselineCode(int preBaselineCode) {
        this.preBaselineCode = preBaselineCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownurl() {
        return downurl;
    }

    public void setDownurl(String downurl) {
        this.downurl = downurl;
    }

    public String getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getHasAffectCodes() {
        return hasAffectCodes;
    }

    public void setHasAffectCodes(String hasAffectCodes) {
        this.hasAffectCodes = hasAffectCodes;
    }
}
