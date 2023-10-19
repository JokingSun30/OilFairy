package com.jokingsun.oilfairy.widget.helper;

import android.os.Handler;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.orhanobut.logger.Logger;


public abstract class HandlerRecheckHelper implements LifecycleObserver {
    public static String TAG = "HandlerRecheckHelper->";

    private Handler handler;
    private Runnable runnable;

    public HandlerRecheckHelper(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    /**
     * 目標檢查任務
     */
    public abstract void targetMission();

    /**
     * 循環檢查條件
     */
    public abstract boolean recheckCondition();

    /**
     * 循環檢查時間
     */
    public abstract int recheckCycleTime();

    /**
     * 開始檢查
     */
    public void startRecheck() {
        if (handler == null) {
            handler = new Handler();
        }

        if (runnable == null) {
            runnable = () -> {
                if (recheckCondition()) {
                    handler.postDelayed(runnable, recheckCycleTime());

                } else {
                    Logger.d(TAG + "符合條件，執行目標任務");
                    targetMission();
                    confirmEndRecheck();
                }
            };
        }

        handler.post(runnable);
    }

    /**
     * 確認移除檢查任務
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void confirmEndRecheck() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            Logger.d(TAG + "確認移除任務");
        }
    }
}
