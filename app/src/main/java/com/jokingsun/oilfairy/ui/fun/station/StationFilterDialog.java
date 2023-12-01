package com.jokingsun.oilfairy.ui.fun.station;

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
import com.jokingsun.oilfairy.common.dialog.ToastDialog;
import com.jokingsun.oilfairy.databinding.DialogToastBinding;

public class StationFilterDialog extends BaseCustomDialog<DialogToastBinding> {

    private StationFilterDialog() {
    }


    @Override
    protected int getResStyle() {
        return AppConstant.USE_ANDROID_DIALOG_DEFAULT_THEME;
    }

    @SuppressLint("SetTextI18n")
    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        this.lockSystemBack = true;
        return view;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.layout_station_filter_dialog;
    }

    @Override
    protected void setBindingVariable() {
        binding.setVariable(BR.stationFilterDialog, this);
    }
}