package com.jokingsun.oilfairy.ui.controller;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;
import static com.jokingsun.oilfairy.utils.StringUtil.analyticsOilPrice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.jokingsun.oilfairy.BR;
import com.jokingsun.oilfairy.BuildConfig;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.BaseActivity;
import com.jokingsun.oilfairy.base.callback.iToolbarCallback;
import com.jokingsun.oilfairy.common.adapter.CustomPagerAdapter;
import com.jokingsun.oilfairy.common.constant.AppConstant;
import com.jokingsun.oilfairy.common.custom.CustomViewPager;
import com.jokingsun.oilfairy.common.dialog.LoadingDialog;
import com.jokingsun.oilfairy.data.local.simple.ApplicationUserSp;
import com.jokingsun.oilfairy.data.remote.SimpleCallback;
import com.jokingsun.oilfairy.data.remote.model.response.TestSheetModel;
import com.jokingsun.oilfairy.databinding.ActivityMainBinding;
import com.jokingsun.oilfairy.ui.fun.center.PersonalCenter;
import com.jokingsun.oilfairy.ui.fun.dashboard.HomeDashboard;
import com.jokingsun.oilfairy.ui.fun.station.FindGasStation;
import com.jokingsun.oilfairy.utils.SharedPreferencesUtil;
import com.jokingsun.oilfairy.utils.StringUtil;
import com.jokingsun.oilfairy.widget.manager.UpdateManager;
import com.jokingsun.oilfairy.widget.receiver.AppReceiver;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * @author cfd058
 */
public class MainActivity extends BaseActivity<ActivityMainBinding, MainActivityViewModel>
        implements iToolbarCallback {

    private UpdateManager updateManager;
    private FirebaseAnalytics firebaseAnalytics;
    private FirebaseRemoteConfig remoteConfig;
    private PowerManager.WakeLock wakeLock;
    private CustomPagerAdapter pagerAdapter;

    //private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle the splash screen transition.
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        this.setContainer(R.id.container);
        super.onCreate(savedInstanceState);
        binding.progressbar.setVisibility(View.VISIBLE);
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    protected void initView() {
        Toolbar toolbar = binding.ilToolbar.toolbar;
        this.setSupportActionBar(toolbar);

        updateStatusBarColor(this, R.color.black, false);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        hideToolbar();
    }

    @Override
    protected void initial() {
        //取得遠程版本號
        getRemoteConfig();

        //建構底部導覽
        setBottomNavAndPager();

        //取得 Device Token
        getViewModel().getDeviceTokenAndLogin();

        //初始化廣播接收者
        initReceiver();

        //更新APP with in-app-update
        updateApp();

        //接收推播資訊
        receiveFcmMessage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        countOpenApp();
        loadInterstitialAd();

        if (updateManager != null) {
            updateManager.continueUpdate();
        }
    }

    @Override
    public int getBindingVariable() {
        return BR.mainActivityViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public MainActivityViewModel getViewModel() {
        if (viewModel == null) {
            viewModel = new ViewModelProvider(this, getFactory()).get(MainActivityViewModel.class);
        }
        return viewModel;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        analysisDeepLink(true, intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //----------------------------- init & First Setting-------------------------------------//

    /**
     * 設置底部導覽與 ViewPager
     */
    private void setBottomNavAndPager() {
        pagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(new HomeDashboard());
        pagerAdapter.addFragment(new FindGasStation());
        pagerAdapter.addFragment(new PersonalCenter());

        //setAdapter
        binding.viewpager.setAdapter(pagerAdapter);
        //禁止左右滑動
        binding.viewpager.setPagingEnabled(false);

        binding.navigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_oil_price));
        binding.navigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_gas_station));
        binding.navigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_personal_center));
        binding.navigation.show(1, true);

        binding.navigation.setOnClickMenuListener(model -> {
            binding.viewpager.setCurrentItem(model.getId() - 1);

            Fragment fragment = pagerAdapter.getItem(model.getId() - 1);

            return null;
        });

        binding.viewpager.setCurrentItem(0);
    }

    /**
     * 設置相關系統設定
     */
    private void setSystemSetting() {
        getWindow().addFlags(
//                //螢幕保持開啟
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                //螢幕保持開啟，當在螢幕上鎖時
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        //螢幕上鎖時，啟用螢幕開啟
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                        //禁止錄影/截圖
//                        | WindowManager.LayoutParams.FLAG_SECURE
        );

    }

    /**
     * 更新 App
     */
    private void updateApp() {
        if (ApplicationUserSp.getInt(AppConstant.SYSTEM_OPEN_APP_COUNTS) % 10 == 0) {
            return;
        }

        // Initialize the Update Manager with the Activity and the Update Mode
        updateManager = UpdateManager.Builder(this).mode(IMMEDIATE);
        updateManager.start();

        //ApplicationUserSp.putBoolean(String.valueOf(BuildConfig.VERSION_CODE + 1), true);

        updateManager.getAvailableVersionCode(new UpdateManager.onVersionCheckListener() {
            @Override
            public void onReceiveVersionCode(final int code) {
                if (SharedPreferencesUtil.getSharedPreferencesValueWithBoolean(
                        SharedPreferencesUtil.SHARE_PREFERENCES_NAME_APP_CONFIG, AppConstant.SP_KEY_IS_ENABLE_TESTER)) {
                    showToast("version code:" + code);
                }
            }
        });
    }

    @SuppressLint({"InvalidWakeLockTag", "WakelockTimeout"})
    private void setAppWeakWhenWork() {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            powerManager.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                            | PowerManager.ON_AFTER_RELEASE,
                    "PostLocationService");

        }
        if (wakeLock != null) {
            wakeLock.acquire();
        }

    }

    /**
     * 啟動深度連結
     */
    private void analysisDeepLink(boolean newTask, Intent newIntent) {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(newTask ? newIntent : getIntent())
                .addOnSuccessListener(this, getViewModel())
                .addOnFailureListener(this, getViewModel());
    }

    /**
     * 接收 FireBase 推播訊息
     */
    public void receiveFcmMessage() {
        if (ApplicationUserSp.isLogin()) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String link = bundle.getString("link");
                String type = bundle.getString("type");
                String routeSign = bundle.getString("routeSign");

                Log.d("TAG", "解析 TYPE" + "\n" + type + "\n" + link + "\n" + routeSign);

                if (type != null) {
                    Handler handler = new Handler();
                    handler.postDelayed(() ->
                            AppReceiver.linkPost(this, type, link, routeSign), 1000);
                }
            }
        }
    }

    private void countOpenApp() {
        //累計 App 開啟數量
        int nextOpenCounts = ApplicationUserSp.getInt(AppConstant.SYSTEM_OPEN_APP_COUNTS) + 1;
        ApplicationUserSp.putInt(AppConstant.SYSTEM_OPEN_APP_COUNTS, nextOpenCounts);
    }

    /**
     * 下載全屏廣告
     */
    public void loadInterstitialAd() {
        if (ApplicationUserSp.getInt(AppConstant.SYSTEM_OPEN_APP_COUNTS) % 10 == 0) {
//            AdRequest adRequest = new AdRequest.Builder().build();
//            InterstitialAd.load(this, BuildConfig.AD_INTERSTITIAL_ID, adRequest,
//                    new InterstitialAdLoadCallback() {
//                        @Override
//                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                            // The mInterstitialAd reference will be null until
//                            // an ad is loaded.
//                            MainActivity.this.interstitialAd = interstitialAd;
//
//                            interstitialAd.setFullScreenContentCallback(
//                                    new FullScreenContentCallback() {
//                                        @Override
//                                        public void onAdDismissedFullScreenContent() {
//                                            // Called when fullscreen content is dismissed.
//                                            // Make sure to set your reference to null so you don't
//                                            // show it a second time.
//                                            MainActivity.this.interstitialAd = null;
//                                            Log.d("TAG", "The ad was dismissed.");
//                                        }
//
//                                        @Override
//                                        public void onAdFailedToShowFullScreenContent(AdError adError) {
//                                            // Called when fullscreen content failed to show.
//                                            // Make sure to set your reference to null so you don't
//                                            // show it a second time.
//                                            MainActivity.this.interstitialAd = null;
//                                            Log.d("TAG", "The ad failed to show.");
//                                        }
//
//                                        @Override
//                                        public void onAdShowedFullScreenContent() {
//                                            // Called when fullscreen content is shown.
//                                            Log.d("TAG", "The ad was shown.");
//                                        }
//                                    });
//
//                            if (MainActivity.this.interstitialAd != null) {
//                                MainActivity.this.interstitialAd.show(MainActivity.this);
//                            }
//                        }
//
//                        @Override
//                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                            // Handle the error
//                            Log.i("MainActivity", loadAdError.getMessage());
//                            interstitialAd = null;
//
//                            @SuppressLint("DefaultLocale") String error =
//                                    String.format(
//                                            "domain: %s, code: %d, message: %s",
//                                            loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
//                        }
//                    });
        } else {
            Log.d("loadInterstitialAd", "App 累計開啟次數：" + ApplicationUserSp.getInt(AppConstant.SYSTEM_OPEN_APP_COUNTS));
        }
    }

    //-------------------------------------- Broad & Firebase ---------------------------------//
    private void initReceiver() {

        SimpleCallback<Intent> appReceiverCallback = new SimpleCallback<Intent>() {
            @Override
            public void onCallback(Intent intent) {
                if (intent.getAction() != null) {
                    switch (intent.getAction()) {
                        case AppReceiver.ACTION_TYPE_STOP_LOADING:
                            binding.progressbar.setVisibility(View.GONE);
                            break;
                    }
                }
            }
        };

        getLifecycle().addObserver(new AppReceiver(this, AppReceiver.ACTION_TYPE_STOP_LOADING, appReceiverCallback));
    }

    /**
     * Firebase RemoteConfig
     */
    private void getRemoteConfig() {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();

        remoteConfig.setConfigSettingsAsync(configSettings);

        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        //成功獲取 Firebase 遠端參數
//                        try {
//                            String remoteDomainName = remoteConfig.getString("AGLocalDataVersionCode");
//                            ResRemoteConfigInfo remoteConfigInfo = new Gson().fromJson(remoteDomainName,
//                                    ResRemoteConfigInfo.class);
//
//                            ApplicationUserSp.putString(AppConstant.SYSTEM_REMOTE_VERSION_CONFIG, remoteDomainName);
//
//                            Logger.d("Remote Config Result: " + new Gson().toJson(remoteConfigInfo));
//
//                        } catch (Exception e) {
//                            Logger.d("Remote Config Result Error" + e.getMessage());
//                        }


                    } else {
                        Logger.d("Fetch failed");
                    }
                });
    }

    /**
     * ---------------------------------- About Toolbar Setting ----------------------------------
     */
    @Override
    public Toolbar getToolbar() {
        return binding.ilToolbar.toolbar;
    }

    /**
     * 標題顯示(Image)
     */
    @Override
    public void setTitleImage(int resId) {
        binding.ilToolbar.tvTitle.setVisibility(View.GONE);
        binding.ilToolbar.ivTitle.setVisibility(View.VISIBLE);
        binding.ilToolbar.ivTitle.setImageResource(resId);
    }

    /**
     * 標題顯示(Text)
     */
    @Override
    public void setTitleByResource(int titleId, int textColor) {
        binding.ilToolbar.tvTitle.setVisibility(View.VISIBLE);
        binding.ilToolbar.ivTitle.setVisibility(View.GONE);
        binding.ilToolbar.tvTitle.setText(titleId);
        binding.ilToolbar.tvTitle.setTextColor(textColor);
    }

    /**
     * 標題顯示(Text)
     */
    @Override
    public void setTitle(CharSequence title, int textColor) {
        binding.ilToolbar.tvTitle.setVisibility(View.VISIBLE);
        binding.ilToolbar.ivTitle.setVisibility(View.GONE);
        binding.ilToolbar.tvTitle.setText(title);
        binding.ilToolbar.tvTitle.setTextColor(textColor);
    }

    /**
     * 標題顯示(Image , Text)
     */
    @Override
    public void setTitleAndImage(int resId, int titleId, int textColor) {
        binding.ilToolbar.tvTitle.setVisibility(View.VISIBLE);
        binding.ilToolbar.tvTitle.setText(titleId);
        binding.ilToolbar.tvTitle.setTextColor(textColor);
        binding.ilToolbar.ivTitle.setVisibility(View.VISIBLE);
        binding.ilToolbar.ivTitle.setImageResource(resId);
    }

    /**
     * 設置左邊按鈕(預設系統返回)
     */
    @Override
    public void setLeftBackButton(int resId, View.OnClickListener onClickListener) {
        binding.ilToolbar.leftIcon.setVisibility(View.VISIBLE);
        binding.ilToolbar.leftIcon.setImageResource(resId);
        binding.ilToolbar.leftIcon.setOnClickListener(v -> {
            onClickListener.onClick(binding.ilToolbar.leftIcon);
        });
    }

    /**
     * 設置右邊按鈕(預設系統返回)
     */
    @Override
    public void setRightBackButton(int resId, View.OnClickListener onClickListener) {
        binding.ilToolbar.rightIcon.setVisibility(View.VISIBLE);
        binding.ilToolbar.rightIcon.setImageResource(resId);
        binding.ilToolbar.rightIcon.setOnClickListener(v -> {
            onClickListener.onClick(binding.ilToolbar.rightIcon);
        });
    }

    /**
     * 設置背景色樣
     */
    @Override
    public void setToolBarBackGroundColor(int backBackGroundColor) {
        binding.ilToolbar.toolbar.setBackgroundColor(backBackGroundColor);
    }

    /**
     * 清除左側設定(保留預設 icon)
     */
    @Override
    public void clearLeftContent() {
        binding.ilToolbar.leftIcon.setVisibility(View.GONE);
        binding.ilToolbar.leftIcon.setOnClickListener(null);
        binding.ilToolbar.leftInnerContent.removeAllViews();
    }

    /**
     * 清除右側設定(保留預設 icon)
     */
    @Override
    public void clearRightContent() {
        binding.ilToolbar.rightIcon.setVisibility(View.GONE);
        binding.ilToolbar.rightIcon.setOnClickListener(null);
        binding.ilToolbar.rightInnerContent.removeAllViews();
    }

    /**
     * 清除左右側設定(保留預設 icon)
     */
    @Override
    public void clearLeftAndRightContent() {
        binding.ilToolbar.leftIcon.setVisibility(View.GONE);
        binding.ilToolbar.rightIcon.setVisibility(View.GONE);
        binding.ilToolbar.rightInnerContent.removeAllViews();
        binding.ilToolbar.leftInnerContent.removeAllViews();
    }

    @Override
    public ViewGroup getLeftInnerContent() {
        return binding.ilToolbar.leftInnerContent;
    }

    @Override
    public ViewGroup getRightInnerContent() {
        return binding.ilToolbar.rightInnerContent;
    }

    @Override
    public void showToolbar() {
        binding.appBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideToolbar() {
        binding.appBar.setVisibility(View.GONE);
    }

}

