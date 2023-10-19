package com.jokingsun.oilfairy.widget.helper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jokingsun.oilfairy.common.constant.AppConstant;

import java.util.Objects;

/**
 * @author cfd058
 */
public class NetWorkStatusHelper implements LifecycleObserver {

    private final Activity activity;
    private final NetWorkReworkListener netWorkReworkListener;
    private BroadcastReceiver isNetWorkAliveBroadCast;
    /**
     * 判斷廣播接收者是否已註冊(避免重複註冊)
     */
    private boolean isReceiverRegister = false;

    public NetWorkStatusHelper(Activity activity, NetWorkReworkListener netWorkReworkListener) {
        this.activity = activity;
        this.netWorkReworkListener = netWorkReworkListener;
    }

    /**
     * 當 Activity/Fragment 執行 ON_CREATE 方法時，會被調用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void enableNetWorkReceiver() {

        if (!isReceiverRegister) {
//            Logger.d("enableNetWorkReceiver Has Register");
            isNetWorkAliveBroadCast = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (Objects.equals(action, AppConstant.ACTION_NET_WORK_REWORK)) {
                        netWorkReworkListener.rework();
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(AppConstant.ACTION_NET_WORK_REWORK);
            activity.registerReceiver(isNetWorkAliveBroadCast, intentFilter);
            isReceiverRegister = true;
        }
    }

    /**
     * 當 Activity/Fragment 執行 ON_DESTROY 方法時，會被調用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void releaseNetWorkReceiver() {
//        Logger.d("releaseNetWorkReceiver");
        if (isReceiverRegister) {
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(isNetWorkAliveBroadCast);
            isReceiverRegister = false;
        }
    }

    public interface NetWorkReworkListener {
        /**
         * 網路重新連線工作
         */
        void rework();
    }

}
