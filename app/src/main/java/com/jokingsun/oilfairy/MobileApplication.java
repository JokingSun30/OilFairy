package com.jokingsun.oilfairy;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.jokingsun.oilfairy.data.local.simple.ApplicationUserSp;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.Map;
import java.util.Objects;

public class MobileApplication extends Application {

    /* Change this to {@code false} when you want to use the downloadable Emoji font. */
    /**
     * true :本地綑綁加載   false : 初始到 google 下載
     */
    private static final boolean USE_BUNDLED_EMOJI = false;

    private static MobileApplication instance;

    public MobileApplication() {
        instance = this;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //使用者偏好設定存取庫
        ApplicationUserSp.init(this);

        //AdMob init
        //MobileAds.initialize(this);

        //Emoji
        initEmojiCompat();

        //AppsFlyer
        //initAppsFlyerSetting();

        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority,
                                      @Nullable @org.jetbrains.annotations.Nullable String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

    /**
     * 初始化表情符號字符
     */
    private void initEmojiCompat() {
        final EmojiCompat.Config config;
        if (USE_BUNDLED_EMOJI) {
//            // Use the bundled font for EmojiCompat
//            config = new BundledEmojiCompatConfig(getApplicationContext());
        } else {
            // Use a downloadable font for EmojiCompat
            final FontRequest fontRequest = new FontRequest(
                    "com.google.android.gms.fonts",
                    "com.google.android.gms",
                    "Noto Color Emoji Compat",
                    R.array.com_google_android_gms_fonts_certs);
            config = new FontRequestEmojiCompatConfig(getApplicationContext(), fontRequest);
        }

        config.setReplaceAll(true)
                .registerInitCallback(new EmojiCompat.InitCallback() {
                    @Override
                    public void onInitialized() {
                        Log.i("TAG", "EmojiCompat initialized");
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        Logger.e("TAG", "EmojiCompat initialization failed", throwable);
                    }
                });

        EmojiCompat.init(config);
    }

    /**
     * 初始化 AppsFlyer 設定
     */
    private void initAppsFlyerSetting() {
        //String afDevKey = BuildConfig.AF_DEV_KEY;

        AppsFlyerLib appsflyer = AppsFlyerLib.getInstance();
        appsflyer.setMinTimeBetweenSessions(0);
        appsflyer.setDebugLog(true);

        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionDataMap) {
                for (String attrName : conversionDataMap.keySet())
                    Log.d("AppsFlyer", "Conversion attribute: " + attrName + " = " + conversionDataMap.get(attrName));
                String status = Objects.requireNonNull(conversionDataMap.get("af_status")).toString();

                if (status.equals("Non-organic")) {
                    if (Objects.requireNonNull(conversionDataMap.get("is_first_launch")).toString().equals("true")) {
                        Log.d("AppsFlyer", "Conversion: First Launch");
                    } else {
                        Log.d("AppsFlyer", "Conversion: Not First Launch");
                    }
                } else {
                    Log.d("AppsFlyer", "Conversion: This is an organic install.");
                }

                Map<String, Object> conversionData = conversionDataMap;
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                Log.d("AppsFlyer", "error getting conversion data: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
                Log.d("AppsFlyer", "onAppOpenAttribution: This is fake call.");
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                Log.d("AppsFlyer", "error onAttributionFailure : " + errorMessage);
            }
        };

//        appsflyer.init(afDevKey, conversionListener, this);
//        appsflyer.start(this);
    }
}
