package com.cretin.www.redpacketplugin.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.cretin.www.redpacketplugin.services.PackageAccessibilityService;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cretin on 2018/2/1.
 */

public class CommonUtils {
    /**
     * 获取邀请码
     *
     * @return
     */
    public static String getInviteCode() {
        return Math.abs((System.currentTimeMillis() + "").hashCode()) + "";
    }

    /**
     * 检测是否打开
     *
     * @param mContext
     * @return
     */
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + PackageAccessibilityService.class.getCanonicalName();
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch ( Settings.SettingNotFoundException e ) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if ( accessibilityEnabled == 1 ) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if ( settingValue != null ) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while ( splitter.hasNext() ) {
                    String accessabilityService = splitter.next();

                    if ( accessabilityService.equalsIgnoreCase(service) ) {
                        return true;
                    }
                }
            }
        } else {
        }

        return accessibilityFound;
    }

    /**
     * 设置手机号隐藏中间四位数
     *
     * @return
     */
    public static String formatPhoneStr(String phone) {
        return phone.replace(phone.substring(3, 7), "****");
    }

    /**
     * 时间字符串转long
     *
     * @param time
     * @return
     */
    public static long timeStrFormatToLong(String time) {
        //2018-02-01 14:38:45
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(time).getTime();
        } catch ( ParseException e ) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 时间long转字符串
     *
     * @param time
     * @return
     */
    public static String timeLongFormatToStr(long time) {
        //2018-02-01 14:38:45
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd HH:mm:ss");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 保留指定位小数
     *
     * @param aim
     * @param releaseNum
     * @return
     */
    public static String formatNum(double aim, int releaseNum) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(releaseNum);
        return nf.format(aim);
    }

    /**
     * 保留指定位小数
     *
     * @param aim
     * @param releaseNum
     * @return
     */
    public static String formatNum(float aim, int releaseNum) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(releaseNum);
        return nf.format(aim);
    }
}
