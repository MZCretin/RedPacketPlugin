package com.cretin.www.redpacketplugin.model;

import android.support.annotation.NonNull;

/**
 * Created by cretin on 2018/2/8.
 */

public class SleepTimeModel implements Comparable {
    //时间类型 1 秒 2 分钟
    private int type;
    private int time;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        SleepTimeModel o1 = ( SleepTimeModel ) o;
        if ( o1.getTime() < getTime() ) {
            return 1;
        } else {
            return -1;
        }
    }
}
