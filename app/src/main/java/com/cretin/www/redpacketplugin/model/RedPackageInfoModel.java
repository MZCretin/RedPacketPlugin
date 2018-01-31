package com.cretin.www.redpacketplugin.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/1/31.
 */

public class RedPackageInfoModel extends BmobObject{
    //红包金额
    private double money;
    //红包来源
    private String origin;
    //红包类型 0 私包 1 群红包
    private int type;

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
