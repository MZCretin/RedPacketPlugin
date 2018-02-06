package com.cretin.www.redpacketplugin.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/2/5.
 */

public class PayTypeModel extends BmobObject {
    //支付类型 1 支付宝 2 微信
    private int payType;
    //支付类型Value
    private String payTypeValue;
    //付款码图片路径路径
    private String payPicUrl;
    //套餐类型 1 周卡 2 月卡 3 年费会员
    private int comboType;
    //套餐类型Value
    private String comboTypeValue;
    //金额 和 描述
    private String moneyDes;
    //支付下标 1 支付宝 周卡 2 支付宝 月卡 3 支付宝 年费 4 微信 周卡 5 微信 月卡 6 微信 年费
    private int payIndex;

    public int getPayIndex() {
        return payIndex;
    }

    public void setPayIndex(int payIndex) {
        this.payIndex = payIndex;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getPayTypeValue() {
        return payTypeValue;
    }

    public void setPayTypeValue(String payTypeValue) {
        this.payTypeValue = payTypeValue;
    }

    public String getPayPicUrl() {
        return payPicUrl;
    }

    public void setPayPicUrl(String payPicUrl) {
        this.payPicUrl = payPicUrl;
    }

    public int getComboType() {
        return comboType;
    }

    public void setComboType(int comboType) {
        this.comboType = comboType;
    }

    public String getComboTypeValue() {
        return comboTypeValue;
    }

    public void setComboTypeValue(String comboTypeValue) {
        this.comboTypeValue = comboTypeValue;
    }

    public String getMoneyDes() {
        return moneyDes;
    }

    public void setMoneyDes(String moneyDes) {
        this.moneyDes = moneyDes;
    }
}
