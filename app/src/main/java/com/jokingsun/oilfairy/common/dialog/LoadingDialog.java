package com.jokingsun.oilfairy.common.dialog;

import com.jokingsun.oilfairy.BR;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.BaseCustomDialog;
import com.jokingsun.oilfairy.common.constant.AppConstant;
import com.jokingsun.oilfairy.databinding.DialogLoadingBinding;

public class LoadingDialog extends BaseCustomDialog<DialogLoadingBinding> {

    private LoadingDialog() {
    }

    public static LoadingDialog getInstance() {
        return new LoadingDialog();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_loading;
    }

    @Override
    protected void setBindingVariable() {
        binding.setVariable(BR.loadingDialog, this);
    }

    @Override
    public int getResStyle() {
        return AppConstant.USE_ANDROID_DIALOG_DEFAULT_THEME;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}