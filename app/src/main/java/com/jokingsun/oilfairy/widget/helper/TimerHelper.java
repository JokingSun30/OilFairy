package com.jokingsun.oilfairy.widget.helper;

import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.orhanobut.logger.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author cfd058
 */
public class TimerHelper implements LifecycleObserver {

    private static TimerHelper timerHelper;
    private final Activity activity;
    private final TimerListener timerListener;
    private final int timeCount;
    private Timer timer;
    private int timeTmp;
    private TimerTask timerTask;
    private boolean isTimeOut = false;
    private TimerMonitor timerMonitor;

    public TimerHelper(Activity activity, int timeCount, TimerListener timerListener) {
        this.timeCount = timeCount;
        this.activity = activity;
        this.timerListener = timerListener;
        this.timeTmp = timeCount;
    }

    public static TimerHelper getInstance(Activity activity, int timeCount, TimerListener timerListener) {
//        if (timerHelper == null) {
//            synchronized (TimerHelper.class) {
//                if (timerHelper == null) {
//                    timerHelper = new TimerHelper(activity, timeCount, timerListener);
//                }
//            }
//        }
        return new TimerHelper(activity, timeCount, timerListener);
    }

    /**
     * 倒數計時
     */
    public void startTimeCountDown() {
        Logger.d("倒數計時啟動");
        try {
            timer = new Timer();
            timerListener.beforeCountDown();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    activity.runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            try {
                                timeTmp--;

                                if (timeTmp < 1) {
                                    isTimeOut = true;
                                    timeTmp = timeCount;
                                    timerListener.timeOut();
                                    cancelTimer();
                                } else {
                                    isTimeOut = false;
                                    timerListener.timeCountDowning(timeTmp);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            timer.schedule(timerTask, 0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 計時
     */
    public void startTimeCount() {
        Logger.d("Timer Start");
        try {
            timer = new Timer();
            timerListener.beforeCountDown();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    activity.runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            try {
                                timeTmp++;
                                timerListener.timeCountDowning(timeTmp);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            timer.schedule(timerTask, 0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isTimeOut() {
        return isTimeOut;
    }

    /**
     * 關閉 dialog 前，需釋放 Timer 資源 及 關閉 timer thread
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void cancelTimer() {
        if (timer != null && timerTask != null) {
            Logger.d("Timer close");
            isTimeOut = true;
            timerTask.cancel();
            timer.cancel();
            if (timerMonitor != null) {
                timerMonitor.onCancelTimeSuccess();
            }
        } else {
            if (timerMonitor != null) {
                timerMonitor.onCancelTimeFailure();
            }
        }
    }

    public void setTimerMonitor(TimerMonitor timerMonitor) {
        this.timerMonitor = timerMonitor;
    }

    /**
     * 監聽時間計數動態
     */
    public interface TimerListener {

        /**
         * 計數前
         */
        void beforeCountDown();

        /**
         * 時間到
         */
        void timeOut();

        /**
         * 計數中
         *
         * @param timeCount
         */
        void timeCountDowning(int timeCount);
    }

    /**
     * 監聽時間計數器的資訊
     */
    public interface TimerMonitor {

        void onCancelTimeSuccess();

        void onCancelTimeFailure();

    }
}
