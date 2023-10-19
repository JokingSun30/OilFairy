package com.jokingsun.oilfairy.common.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.jokingsun.oilfairy.BR;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.BaseCustomDialog;
import com.jokingsun.oilfairy.common.constant.AppConstant;
import com.jokingsun.oilfairy.databinding.DialogToastBinding;

/**
 * @author cfd058
 */
public class ToastDialog extends BaseCustomDialog<DialogToastBinding> {

    private final String content;
    private final BaseCustomDialog.DialogCallback callback;
    private View view;

    private ToastDialog(String content, DialogCallback callback) {
        this.content = content;
        this.callback = callback;
    }

    public static ToastDialog getInstance(String content, DialogCallback callback) {
        return new ToastDialog(content, callback);
    }


    @Override
    protected int getResStyle() {
        return AppConstant.USE_ANDROID_DIALOG_DEFAULT_THEME;
    }

    @SuppressLint("SetTextI18n")
    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = super.onCreateView(inflater, container, savedInstanceState);

        binding.tvContent.setText("金色圖標");

        this.lockSystemBack = true;
        return this.view;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_toast;
    }

    @Override
    protected void setBindingVariable() {
        binding.setVariable(BR.toastDialog, this);
    }
}