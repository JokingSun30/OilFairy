package com.jokingsun.oilfairy.base;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jokingsun.oilfairy.common.constant.AppConstant;
import com.orhanobut.logger.Logger;

/**
 * @author cfd058
 */
public abstract class BaseCustomDialog<T extends ViewDataBinding> extends DialogFragment {

    protected T binding;
    protected boolean lockSystemBack = false;
    private SystemBackListener systemBackListener;

    protected abstract int getResStyle();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getDialog() != null) {
            getDialog().setOnShowListener(null);
            getDialog().setOnCancelListener(null);
            getDialog().setOnDismissListener(null);
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog;
        if (getResStyle() != AppConstant.USE_ANDROID_DIALOG_DEFAULT_THEME) {
            dialog = new Dialog(getContext(), getResStyle());
        } else {
            dialog = super.onCreateDialog(savedInstanceState);
        }

        Window window = dialog.getWindow();
        if (window != null) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            removeDefaultDialogPadding(window);
        }

        return dialog;
    }

    /**
     * 去掉默認的 dialog padding
     * @param window
     */
    public void removeDefaultDialogPadding(Window window ) {
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
    }

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        catchSystemBackEvent();
        setBindingVariable();
    }

    /**
     * 設置布局 LAYOUT
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 指定 Binding Variable
     */
    protected abstract void setBindingVariable();

    @Override
    public void show(FragmentManager manager, String tag) {
        if (!this.isAdded() && !this.isVisible() && !this.isRemoving()) {
            try {
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                fragmentTransaction.remove(this).commit();
                super.show(manager, tag);
            } catch (Exception var4) {
                var4.printStackTrace();
            }
        }
    }

    public void cancel() {
        if (this.isAdded()) {
            this.dismiss();
        }
    }

    /**
     * 該 DIALOG 攔截系統返回鍵，並提供回調處理介面
     * 如果僅需要鎖定系統返回，則設定 lockSystemBack = true;
     * 若需要另外處理攔截後的客製邏輯，請實作 SystemBackListener 並設置
     */
    protected void catchSystemBackEvent() {
        this.getDialog().setOnKeyListener((v, keyCode, event) -> {
            //攔截到的系統返回事件
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (lockSystemBack) {
                    Logger.d("攔截到系統返回鍵" + "需求鎖定");
                    return true;
                }

                if (systemBackListener != null) {
                    Logger.d("攔截到系統返回鍵" + "需求鎖定並處理");
                    systemBackListener.handleBack();
                    return true;
                }
            }
            return false;
        });
    }

    public interface DialogCallback {
        /**
         * On click positive.
         */
        void onClickPositive();

        /**
         * On click negative.
         */
        void onClickNegative();
    }

    public interface SystemBackListener {
        /**
         * 處理系統返回事件
         */
        void handleBack();
    }

    public void setSystemBackListener(SystemBackListener systemBackListener) {
        this.systemBackListener = systemBackListener;
    }

}

