package com.cretin.www.redpacketplugin.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/2/6.
 */

public class WeixinNodeModel extends BmobObject{
    //默认配置
    private String CL_NAME_RV_UI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    private String CL_NAME_EL_UI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
    private String ID_RV_BTN_OPEN = "com.tencent.mm:id/c2i";
    private String ID_RV_TV_SHOWMANLE = "com.tencent.mm:id/c2h";
    private String ID_RV_CLOSE = "com.tencent.mm:id/c07";
    private String ID_EL_MONEY = "com.tencent.mm:id/byw";
    private String ID_EL_USERNAME = "com.tencent.mm:id/bys";
    private String ID_EL_TIME_TO_QIANG = "com.tencent.mm:id/bzb";
    private String ID_EL_PINSHOUQI = "com.tencent.mm:id/byt";
    private String ID_EL_CLOSE = "com.tencent.mm:id/hp";
    private String ID_CHATTING_TV_ADD = "com.tencent.mm:id/aak";
    private String ID_HO_LV_IM = "com.tencent.mm:id/apr";
    private String ID_HO_LV_IM_CONTENT = "com.tencent.mm:id/apv";
    private String ID_HO_LV_IM_NUMBER = "com.tencent.mm:id/iu";
    private String ID_CHATTING_TV_TITLE = "com.tencent.mm:id/ha";
    private String ID_HO_LV_IM_LB_WXHB = "com.tencent.mm:id/aec";
    private String ID_CHATTING_TV_BACK = "com.tencent.mm:id/h_";
    private int versionCode;
    private String STR_SPLIT = "";

    public String getSTR_SPLIT() {
        return STR_SPLIT;
    }

    public void setSTR_SPLIT(String STR_SPLIT) {
        this.STR_SPLIT = STR_SPLIT;
    }

    /**
     * 红包消息的关键字
     */
    private String COMMEN_TEXT_INFO = "com.tencent.mm [微信红包] 手慢了，红包派完了 领取红包 微信红包";

    public String getCOMMEN_TEXT_INFO() {
        return COMMEN_TEXT_INFO;
    }

    public void setCOMMEN_TEXT_INFO(String COMMEN_TEXT_INFO) {
        this.COMMEN_TEXT_INFO = COMMEN_TEXT_INFO;
    }

    public String getCL_NAME_RV_UI() {
        return CL_NAME_RV_UI;
    }

    public void setCL_NAME_RV_UI(String CL_NAME_RV_UI) {
        this.CL_NAME_RV_UI = CL_NAME_RV_UI;
    }

    public String getCL_NAME_EL_UI() {
        return CL_NAME_EL_UI;
    }

    public void setCL_NAME_EL_UI(String CL_NAME_EL_UI) {
        this.CL_NAME_EL_UI = CL_NAME_EL_UI;
    }

    public String getID_RV_BTN_OPEN() {
        return ID_RV_BTN_OPEN;
    }

    public void setID_RV_BTN_OPEN(String ID_RV_BTN_OPEN) {
        this.ID_RV_BTN_OPEN = ID_RV_BTN_OPEN;
    }

    public String getID_RV_TV_SHOWMANLE() {
        return ID_RV_TV_SHOWMANLE;
    }

    public void setID_RV_TV_SHOWMANLE(String ID_RV_TV_SHOWMANLE) {
        this.ID_RV_TV_SHOWMANLE = ID_RV_TV_SHOWMANLE;
    }

    public String getID_RV_CLOSE() {
        return ID_RV_CLOSE;
    }

    public void setID_RV_CLOSE(String ID_RV_CLOSE) {
        this.ID_RV_CLOSE = ID_RV_CLOSE;
    }

    public String getID_EL_MONEY() {
        return ID_EL_MONEY;
    }

    public void setID_EL_MONEY(String ID_EL_MONEY) {
        this.ID_EL_MONEY = ID_EL_MONEY;
    }

    public String getID_EL_USERNAME() {
        return ID_EL_USERNAME;
    }

    public void setID_EL_USERNAME(String ID_EL_USERNAME) {
        this.ID_EL_USERNAME = ID_EL_USERNAME;
    }

    public String getID_EL_TIME_TO_QIANG() {
        return ID_EL_TIME_TO_QIANG;
    }

    public void setID_EL_TIME_TO_QIANG(String ID_EL_TIME_TO_QIANG) {
        this.ID_EL_TIME_TO_QIANG = ID_EL_TIME_TO_QIANG;
    }

    public String getID_EL_PINSHOUQI() {
        return ID_EL_PINSHOUQI;
    }

    public void setID_EL_PINSHOUQI(String ID_EL_PINSHOUQI) {
        this.ID_EL_PINSHOUQI = ID_EL_PINSHOUQI;
    }

    public String getID_EL_CLOSE() {
        return ID_EL_CLOSE;
    }

    public void setID_EL_CLOSE(String ID_EL_CLOSE) {
        this.ID_EL_CLOSE = ID_EL_CLOSE;
    }

    public String getID_CHATTING_TV_ADD() {
        return ID_CHATTING_TV_ADD;
    }

    public void setID_CHATTING_TV_ADD(String ID_CHATTING_TV_ADD) {
        this.ID_CHATTING_TV_ADD = ID_CHATTING_TV_ADD;
    }

    public String getID_HO_LV_IM() {
        return ID_HO_LV_IM;
    }

    public void setID_HO_LV_IM(String ID_HO_LV_IM) {
        this.ID_HO_LV_IM = ID_HO_LV_IM;
    }

    public String getID_HO_LV_IM_CONTENT() {
        return ID_HO_LV_IM_CONTENT;
    }

    public void setID_HO_LV_IM_CONTENT(String ID_HO_LV_IM_CONTENT) {
        this.ID_HO_LV_IM_CONTENT = ID_HO_LV_IM_CONTENT;
    }

    public String getID_HO_LV_IM_NUMBER() {
        return ID_HO_LV_IM_NUMBER;
    }

    public void setID_HO_LV_IM_NUMBER(String ID_HO_LV_IM_NUMBER) {
        this.ID_HO_LV_IM_NUMBER = ID_HO_LV_IM_NUMBER;
    }

    public String getID_CHATTING_TV_TITLE() {
        return ID_CHATTING_TV_TITLE;
    }

    public void setID_CHATTING_TV_TITLE(String ID_CHATTING_TV_TITLE) {
        this.ID_CHATTING_TV_TITLE = ID_CHATTING_TV_TITLE;
    }

    public String getID_HO_LV_IM_LB_WXHB() {
        return ID_HO_LV_IM_LB_WXHB;
    }

    public void setID_HO_LV_IM_LB_WXHB(String ID_HO_LV_IM_LB_WXHB) {
        this.ID_HO_LV_IM_LB_WXHB = ID_HO_LV_IM_LB_WXHB;
    }

    public String getID_CHATTING_TV_BACK() {
        return ID_CHATTING_TV_BACK;
    }

    public void setID_CHATTING_TV_BACK(String ID_CHATTING_TV_BACK) {
        this.ID_CHATTING_TV_BACK = ID_CHATTING_TV_BACK;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}
