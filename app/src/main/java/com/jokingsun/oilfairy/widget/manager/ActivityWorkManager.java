package com.jokingsun.oilfairy.widget.manager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * 判定程序是為 前景還是背景
 * <p>
 * 1.process 就是程序，是 linux 的概念。
 * <p>
 * 2.一般一個 app擁有一個 uid，執行在一個程序裡，如果 app中給 service等定義不同的 uid，
 * 那 Service 就執行在另外一個程序裡，也就是說 uid 就相當於程序的 id 一樣，
 * 一個 uid 就代表一個程序；也可以幾個 app 定義一個 uid，那他們就執行在一個程序裡了。
 * <p>
 * 3.task 是 android系統的一個 activity 的棧，包含多個 app 的 activity，
 * 通過 ActivityManager可以獲取棧中的 activity 資訊，從而判斷 activity 對應應用的狀態。
 *
 * @author cfd058
 */
public class ActivityWorkManager {

    private final int CUSTOM_ACTIVITY_FOREGROUND_CODE = 100;
    private final int CUSTOM_ACTIVITY_BACKGROUND_CODE = 125;
    private final int CUSTOM_IMPORTANCE_SERVICE_CODE = 300;

    private Context context;
    private ActivityManager.RunningAppProcessInfo runningAppProcessInfo;

    public ActivityWorkManager(Context context) {
        this.context = context;
        this.init();
    }

    private void init() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager packageManager = context.getPackageManager();

        //getRecentTasks() 獲取最近開啟的task，手機檢視最近開啟的應用可以用這個實現。
        //getRunningAppProcess() 獲取app執行中的process。
        //list是系統當前執行程序的集合，importance屬性==100 表示這個程序在前臺，其他數字表示在後臺，
        // 所以通過importance和processName判斷應用是否在前臺

        List<ActivityManager.RunningAppProcessInfo> list = manager.getRunningAppProcesses();
        StringBuilder apps = new StringBuilder();

        for (ActivityManager.RunningAppProcessInfo info : list) {
            runningAppProcessInfo = info;
            apps.append(info.processName).append("\n").append(info.importance);
            Logger.d("App 執行續狀態偵測" + info.importance);
        }
    }

    /**
     * 客製程序是否在前景
     */
    public boolean isProcessForeground() {
        return runningAppProcessInfo.importance == CUSTOM_ACTIVITY_FOREGROUND_CODE;
    }

    /**
     * 客製程序是否在背景
     */
    public boolean isProcessBackGround() {
        return runningAppProcessInfo.importance == CUSTOM_ACTIVITY_BACKGROUND_CODE;
    }

    /**
     * 是否有服務在背景運行
     */
    public boolean isImportanceService() {
        return runningAppProcessInfo.importance == CUSTOM_IMPORTANCE_SERVICE_CODE;
    }

    /** 獲取目前 APP 運行狀態*/
    public int getRecentImportance() {
        return runningAppProcessInfo.importance;
    }

}
