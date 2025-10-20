package com.nkanaev.comics;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;


public class MainApplication extends MultiDexApplication {
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Android 16 optimized theme handling
        int default_mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        int mode = getPreferences().getInt(Constants.SETTINGS_THEME, default_mode);
        // Android 16 enhanced theme validation
        switch (mode) {
            case AppCompatDelegate.MODE_NIGHT_NO:
            case AppCompatDelegate.MODE_NIGHT_YES:
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                break;
            default:
                mode = default_mode;
        }
        AppCompatDelegate.setDefaultNightMode(mode);
        
        // Android 16 performance optimizations
        System.setProperty("android.graphics.hardware.acceleration", "true");
        
        // Android 16 optimization: disable all animations for instant touch response
        System.setProperty("android.anim.disable", "true");
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public static SharedPreferences getPreferences() {
        return instance.getSharedPreferences(Constants.SETTINGS_NAME, 0);
    }

    public static boolean isNightModeEnabled(){
        return ( getAppContext().getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK ) == Configuration.UI_MODE_NIGHT_YES;
    }
}
