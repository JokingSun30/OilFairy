package com.jokingsun.oilfairy.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.jokingsun.oilfairy.MobileApplication;

import java.util.Map;

/**
 * @author cfd058
 */
public class SharedPreferencesUtil {
    public final static String SHARE_PREFERENCES_NAME_APP_CONFIG = "CFD_VOXY_APP_CONFIG";
    public final static String SHARE_PREFERENCES_NAME_AD_CONFIG = "CFD_VOXY_AD_CONFIG";
    public final static String SHARE_PREFERENCES_NAME_FCM = "CFD_VOXY_FCM";


    public static void setSharedPreferencesName(String SHARE_PREFERENCES_NAME) {
        SHARE_PREFERENCES_NAME = SHARE_PREFERENCES_NAME;
    }

    public static void setSharedPreferences(String sharePreferencesName, Map<String, String> sharedPreferencesMap) {
        SharedPreferences.Editor edit = MobileApplication.getContext().getSharedPreferences(sharePreferencesName, MODE_PRIVATE).edit();
        for (Map.Entry<String, String> entry : sharedPreferencesMap.entrySet()) {
            edit.putString(entry.getKey(), entry.getValue());
        }

        edit.commit();
    }

    public static void setSharedPreferences(String sharePreferencesName, String sharedPreferencesKey, String sharedPreferencesValue) {
        SharedPreferences.Editor edit = MobileApplication.getContext().getSharedPreferences(sharePreferencesName, MODE_PRIVATE).edit();

        edit.putString(sharedPreferencesKey, sharedPreferencesValue);

        edit.commit();
    }

    public static void setSharedPreferences(String sharePreferencesName, String sharedPreferencesKey, int sharedPreferencesValue) {
        SharedPreferences.Editor edit = MobileApplication.getContext().getSharedPreferences(sharePreferencesName, MODE_PRIVATE).edit();

        edit.putInt(sharedPreferencesKey, sharedPreferencesValue);

        edit.commit();
    }

    public static void setSharedPreferences(String sharePreferencesName, String sharedPreferencesKey, boolean sharedPreferencesValue) {
        SharedPreferences.Editor edit = MobileApplication.getContext().getSharedPreferences(sharePreferencesName, MODE_PRIVATE).edit();

        edit.putBoolean(sharedPreferencesKey, sharedPreferencesValue);

        edit.commit();
    }

    public static void updateSharedPreferences(String sharePreferencesName, Map<String, String> sharedPreferencesMap) {
        SharedPreferences.Editor edit = MobileApplication.getContext().getSharedPreferences(sharePreferencesName, MODE_PRIVATE).edit();
        for (Map.Entry<String, String> entry : sharedPreferencesMap.entrySet()) {
            edit.putString(entry.getKey(), entry.getValue());
        }
        edit.apply();
    }

    public static void updateSharedPreferences(String sharePreferencesName, String sharedPreferencesKey, String sharedPreferencesValue) {
        SharedPreferences.Editor edit = MobileApplication.getContext().getSharedPreferences(sharePreferencesName, MODE_PRIVATE).edit();
        edit.putString(sharedPreferencesKey, sharedPreferencesValue);

        edit.apply();
    }

    public static void updateSharedPreferences(String sharePreferencesName, String sharedPreferencesKey, int sharedPreferencesValue) {
        SharedPreferences.Editor edit = MobileApplication.getContext().getSharedPreferences(sharePreferencesName, MODE_PRIVATE).edit();
        edit.putInt(sharedPreferencesKey, sharedPreferencesValue);

        edit.apply();
    }

    public static void clearSharedPreferences(String sharePreferencesName) {
        SharedPreferences.Editor edit = MobileApplication.getContext().getSharedPreferences(sharePreferencesName, MODE_PRIVATE).edit();
        edit.clear();

        edit.commit();
    }


    public static void clearAllSharedPreferences() {
        Context context = MobileApplication.getContext();

        SharedPreferences.Editor edit = context.getSharedPreferences(SHARE_PREFERENCES_NAME_APP_CONFIG, MODE_PRIVATE).edit();
        edit.clear();
        edit.commit();

        edit = context.getSharedPreferences(SHARE_PREFERENCES_NAME_AD_CONFIG, MODE_PRIVATE).edit();
        edit.clear();
        edit.commit();

        edit = context.getSharedPreferences(SHARE_PREFERENCES_NAME_FCM, MODE_PRIVATE).edit();
        edit.clear();
        edit.commit();
    }

    public static String getSharedPreferencesValue(String sharePreferencesName, String sharedPreferencesKey) {
        SharedPreferences spref = MobileApplication.getContext().getSharedPreferences(sharePreferencesName, MODE_PRIVATE);

        return spref.getString(sharedPreferencesKey, null);
    }

    public static int getSharedPreferencesValueWithInt(String sharePreferencesName, String sharedPreferencesKey) {
        SharedPreferences spref = MobileApplication.getContext().getSharedPreferences(sharePreferencesName, MODE_PRIVATE);

        return spref.getInt(sharedPreferencesKey, 0);
    }

    public static boolean getSharedPreferencesValueWithBoolean(String sharePreferencesName, String sharedPreferencesKey) {
        SharedPreferences spref = MobileApplication.getContext().getSharedPreferences(sharePreferencesName, MODE_PRIVATE);

        return spref.getBoolean(sharedPreferencesKey, false);
    }
}
