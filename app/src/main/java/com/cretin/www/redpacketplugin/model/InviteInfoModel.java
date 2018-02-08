package com.cretin.www.redpacketplugin.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/2/8.
 */

public class InviteInfoModel extends BmobObject {
    //攻略内容
    private String strategy;
    //下载二维码图片路径
    private String downErWeiMa;
    //apk的下载地址
    private String apkDownUrl;
    //微信分享的地址
    private String shareUrl;
    //微信分享的标题
    private String shareTitle;
    //微信分享的内容
    private String shareContent;
    //微信分享的图片路径
    private String shareImgUrl;

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getDownErWeiMa() {
        return downErWeiMa;
    }

    public void setDownErWeiMa(String downErWeiMa) {
        this.downErWeiMa = downErWeiMa;
    }

    public String getApkDownUrl() {
        return apkDownUrl;
    }

    public void setApkDownUrl(String apkDownUrl) {
        this.apkDownUrl = apkDownUrl;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareContent() {
        return shareContent;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public String getShareImgUrl() {
        return shareImgUrl;
    }

    public void setShareImgUrl(String shareImgUrl) {
        this.shareImgUrl = shareImgUrl;
    }
}
