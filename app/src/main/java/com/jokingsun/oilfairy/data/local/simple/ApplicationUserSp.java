package com.jokingsun.oilfairy.data.local.simple;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.jokingsun.oilfairy.BuildConfig;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ApplicationUserSp {
    private static final String LOGIN = "LOGIN";

    /**
     * Access Token
     */
    private static final String TOKEN = "TOKEN";

    private static WeakReference<Application> applicationWeakReference;
    private static EncryptedPreferences sharedPreferences;

    public ApplicationUserSp() {
    }

    public static void init(Application application) {
        applicationWeakReference = new WeakReference<>(application);
        sharedPreferences = new EncryptedPreferences.Builder(application)
                .withEncryptionPassword(BuildConfig.APP_KEY).build();
    }

    private static Context getContext() {
        return applicationWeakReference.get();
    }

    public static void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public static String randUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static void login(String token) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(LOGIN, true).apply();
            sharedPreferences.edit().putString(TOKEN, token).apply();
        }

    }

    public static void logOut() {
        sharedPreferences.edit().putBoolean(LOGIN, false).apply();
        sharedPreferences.edit().putString(TOKEN, "").apply();
    }

    public static String getToken() {
        return sharedPreferences.getString(TOKEN, "");
    }

    public static boolean isLogin() {
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public static boolean putString(String key, String value) {
        return sharedPreferences.edit().putString(key, value).commit();
    }

    public static boolean putBoolean(String key, boolean value) {
        return sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public static boolean putInt(String key, int value) {
        return sharedPreferences.edit().putInt(key, value).commit();
    }

    public static boolean putLong(String key, long value) {
        return sharedPreferences.edit().putLong(key, value).commit();
    }

    public static boolean putFloat(String key, float value) {
        return sharedPreferences.edit().putFloat(key, value).commit();
    }

    public static String getString(String key, String value) {
        return sharedPreferences.getString(key, value);
    }

    public static String getString(String key) {
        return getString(key, "");
    }

    public static boolean getBoolean(String key, boolean value) {
        return sharedPreferences.getBoolean(key, value);
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static int getInt(String key, int value) {
        return sharedPreferences.getInt(key, value);
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static long getLong(String key, long value) {
        return sharedPreferences.getLong(key, value);
    }

    public static long getLong(String key) {
        return getLong(key, 0L);
    }

    public static float getFloat(String key, float value) {
        return sharedPreferences.getFloat(key, value);
    }

    public static float getFloat(String key) {
        return getFloat(key, 0.0F);
    }

    public static void putList(String key, ArrayList<String> strings) {
        String[] array = (String[]) strings.toArray(new String[strings.size()]);
        sharedPreferences.edit().putString(key, TextUtils.join("‚‗‚", array)).apply();
    }

    public static ArrayList<String> getList(String key) {
        String[] split = TextUtils.split(sharedPreferences.getString(key, ""), "‚‗‚");
        ArrayList<String> strings = new ArrayList<>();
        if (split.length > 0) {
            strings.addAll(new ArrayList(Arrays.asList(split)));
        }

        return strings;
    }

    public static boolean putHashMapData(String key, HashMap<String, String> map) {
        JSONArray jsonArray = new JSONArray();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();

        JSONObject object = new JSONObject();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            try {
                object.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        jsonArray.put(object);

        return sharedPreferences.edit().putString(key, jsonArray.toString()).commit();
    }

    public static HashMap<String, String> getHashMapData(String key) {

        HashMap<String, String> map = new HashMap<>();

        String result = sharedPreferences.getString(key, "");
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                JSONArray names = itemObject.names();
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        String value = itemObject.getString(name);
                        map.put(name, value);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map;
    }

}
