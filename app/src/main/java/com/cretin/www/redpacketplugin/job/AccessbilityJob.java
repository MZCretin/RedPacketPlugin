package com.cretin.www.redpacketplugin.job;

import android.app.Notification;
import android.view.accessibility.AccessibilityEvent;

import com.cretin.www.redpacketplugin.services.PackageAccessibilityService;

public interface AccessbilityJob {
    String getTargetPackageName();
    void onCreateJob(PackageAccessibilityService service);
    void onReceiveJob(AccessibilityEvent event);
    void onStopJob();
    void onNotificationPosted(Notification notification);
    boolean isEnable();
}
