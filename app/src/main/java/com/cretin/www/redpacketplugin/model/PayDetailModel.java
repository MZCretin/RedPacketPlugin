package com.cretin.www.redpacketplugin.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/2/5.
 */

public class PayDetailModel extends BmobObject {
    //付款码
    private String payCode;
    //付款类型
    private PayTypeModel payTypeModel;
    //天数
    private int dayNums;
    //显示的金额
    private String money;
    //支付类型描述
    private String payTypeDes;
    //状态 1 未处理   2 已处理   3 付款码错误
    private int state;
    //状态描述
    private String stateValue;
    //提示
    private String message;
    //所属用户id
    private String authirUserId;

    public String getAuthirUserId() {
        return authirUserId;
    }

    public void setAuthirUserId(String authirUserId) {
        this.authirUserId = authirUserId;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public PayTypeModel getPayTypeModel() {
        return payTypeModel;
    }

    public void setPayTypeModel(PayTypeModel payTypeModel) {
        this.payTypeModel = payTypeModel;
    }

    public int getDayNums() {
        return dayNums;
    }

    public void setDayNums(int dayNums) {
        this.dayNums = dayNums;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getPayTypeDes() {
        return payTypeDes;
    }

    public void setPayTypeDes(String payTypeDes) {
        this.payTypeDes = payTypeDes;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateValue() {
        return stateValue;
    }

    public void setStateValue(String stateValue) {
        this.stateValue = stateValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
