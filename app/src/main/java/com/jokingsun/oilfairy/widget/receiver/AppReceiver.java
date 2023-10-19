package com.jokingsun.oilfairy.widget.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.jokingsun.oilfairy.data.remote.SimpleCallback;
import com.orhanobut.logger.Logger;

/**
 * @author cfd058
 */
public class AppReceiver implements LifecycleObserver {

    /**
     * BroadCast Action
     */
    public final static String ACTION_TYPE_LINK_POST = "LINK_POST";
    public final static String ACTION_TYPE_OPEN_PAGE = "OPEN_PAGE";
    public final static String ACTION_TYPE_SET_CURRENT_PAGE = "CURRENT_PAGE";

    public static final String ACTION_TYPE_CURRENT_PAGE_INDEX = "CURRENT_PAGE_INDEX";
    private final SimpleCallback<Intent> callback;
    private final String actionType;
    private final Context context;
    /**
     * 判斷廣播接收者是否已註冊(避免重複註冊)
     */
    private boolean isReceiverRegister = false;
    private BroadcastReceiver broadcastReceiver;


    public AppReceiver(Context context, String actionType, SimpleCallback<Intent> callback) {
        super();
        this.callback = callback;
        this.context = context;
        this.actionType = actionType;
    }

    /**
     * 解析 Fcm Message ,並包裝送出 type and link broadCast
     */
    public static void linkPost(Context context, String type, String link, String routeSign) {
        Intent intent = new Intent(ACTION_TYPE_LINK_POST);
        intent.putExtra("type", type);
        intent.putExtra("link", link);
        intent.putExtra("routeSign", routeSign);
        context.sendBroadcast(intent);
    }

    /**
     * 指定跳轉頁面 BroadCast
     */
    public static void openPage(Context context, int pageIndex) {
        Intent intent = new Intent(ACTION_TYPE_OPEN_PAGE);
        intent.putExtra("pageIndex", pageIndex);
        context.sendBroadcast(intent);
    }

    /**
     * 指定跳轉頁面 BroadCast
     */
    public static void setMainTagCurrentPage(Context context, int pageIndex) {
        Intent intent = new Intent(ACTION_TYPE_SET_CURRENT_PAGE);
        intent.putExtra(ACTION_TYPE_CURRENT_PAGE_INDEX, pageIndex);
        context.sendBroadcast(intent);
    }


    //--------------------------- Custom BroadCast ----------------------------------------------//

    /**
     * 當 Activity/Fragment 執行 ON_CREATE 方法時，會被調用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void register() {

        if (!isReceiverRegister) {
            if (broadcastReceiver == null) {
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        try {
                            AppReceiver.this.callback.onCallback(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
            }
            isReceiverRegister = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(actionType);
            context.registerReceiver(broadcastReceiver, intentFilter);
            Logger.d("AppReceiver Already Register ");
        }
    }

    /**
     * 當 Activity/Fragment 執行 ON_DESTROY 方法時，會被調用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void unRegister() {
        if (isReceiverRegister) {
            context.unregisterReceiver(broadcastReceiver);
            isReceiverRegister = false;
           Logger.d("AppReceiver UnRegister Finished");
        }
    }
}
