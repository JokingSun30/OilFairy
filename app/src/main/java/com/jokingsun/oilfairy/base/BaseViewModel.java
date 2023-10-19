package com.jokingsun.oilfairy.base;

import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.jokingsun.oilfairy.base.callback.MyTouchListener;
import com.jokingsun.oilfairy.data.remote.ApiHelper;
import com.jokingsun.oilfairy.utils.GeneralUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cfd058
 */
public abstract class BaseViewModel<N, M> extends ViewModel {

    /**
     * 保存 MyTouchListener 接口的列表
     */
    private final List<MyTouchListener> myTouchListeners = new ArrayList<>();
    public M dataModel;
    protected ApiHelper apiHelper;
    private WeakReference<N> navigator;
    protected final Gson gson;

    public BaseViewModel(ApiHelper apiHelper) {
        this.apiHelper = apiHelper;
        this.gson = new Gson();
    }

    /**
     * 當網路異常恢復後，執行恢復資料操作
     */
    protected abstract void setWhenNetWorkRework();

    /**
     * 打印 Log 訊息
     */
    protected void printLog(String message) {
        GeneralUtil.printLog(this.getClass().getSimpleName(), message);
    }

    /**
     * ------------------------------ About WeakReference ----------------------------------------
     */
    public N getNavigator() {
        return navigator.get();
    }

    public void setNavigator(N navigator) {
        this.navigator = new WeakReference<>(navigator);
        this.attachRepository(navigator);
    }

    /**
     * 建構需要使用的 Repository 可以複數
     *
     * @param navigator (傳入 Weak 供 Repo 調用)
     */
    protected abstract void attachRepository(N navigator);


    /**
     * ------------------------------- About OnTouchEvent ---------------------------------------
     * 以下方法為 Activity 所有 ， fragment 必須取得 Activity 的 ViewModel 才可註冊觸摸事件
     * ex: mainViewModel = new ViewModelProvider(getActivity(), factory).get(MainViewModel.class);
     */

    public void handleOnTouchEvent(MotionEvent ev) {
        for (MyTouchListener listener : myTouchListeners) {
            listener.onTouchEvent(ev);
        }
    }

    /**
     * 提供给 Fragment 通过方法来注册 Activity 的触摸事件的方法
     *
     * @param listener
     */
    public void registerMyTouchListener(MyTouchListener listener) {
        myTouchListeners.add(listener);
    }

    /**
     * 提供给 Fragment 通过 getActivity() 方法来取消注册自己的触摸事件的方法
     *
     * @param listener
     */
    public void unRegisterMyTouchListener(MyTouchListener listener) {
        myTouchListeners.remove(listener);
    }

    /**
     * 根据 EditText 所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            // 点击EditText的事件，忽略它。
            return !(event.getX() > left) || !(event.getX() < right)
                    || !(event.getY() > top) || !(event.getY() < bottom);
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

}
