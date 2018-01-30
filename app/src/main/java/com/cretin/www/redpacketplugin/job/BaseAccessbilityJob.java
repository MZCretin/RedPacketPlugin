package com.cretin.www.redpacketplugin.job;

import android.content.Context;

import com.cretin.www.redpacketplugin.config.Config;
import com.cretin.www.redpacketplugin.services.PackageAccessibilityService;


public abstract class BaseAccessbilityJob implements AccessbilityJob {

    private PackageAccessibilityService service;

    @Override
    public void onCreateJob(PackageAccessibilityService service) {
        this.service = service;
    }

    public Context getContext() {
        return service.getApplicationContext();
    }

    public Config getConfig() {
        return service.getConfig();
    }

    public PackageAccessibilityService getService() {
        return service;
    }
}
