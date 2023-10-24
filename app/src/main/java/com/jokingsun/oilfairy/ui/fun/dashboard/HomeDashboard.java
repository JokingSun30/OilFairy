package com.jokingsun.oilfairy.ui.fun.dashboard;

import androidx.lifecycle.ViewModelProvider;

import com.jokingsun.oilfairy.BR;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.BaseFragment;
import com.jokingsun.oilfairy.databinding.FragmentHomeDashboardBinding;
import com.jokingsun.oilfairy.ui.fun.console.ConsoleCenter;

public class HomeDashboard extends BaseFragment<FragmentHomeDashboardBinding, HomeDashboardViewModel> {

    public static HomeDashboard getInstance() {
        return new HomeDashboard();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initial() {

    }

    @Override
    protected void initToolBar() {
        hideToolbar();
    }

    @Override
    protected void initSettingHaveVisible() {

    }

    @Override
    protected void onBackPressed() {

    }

    @Override
    public int[] getBindingVariable() {
        return new int[]{BR.homeDashboardViewModel, BR.homeDashboard};
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home_dashboard;
    }

    @Override
    public HomeDashboardViewModel getViewModel() {
        if (viewModel == null) {
            viewModel = new ViewModelProvider(this, getFactory()).get(HomeDashboardViewModel.class);
        }
        return viewModel;
    }

    public void jumpToConsoleCenter() {
        AddFragment(new ConsoleCenter());
    }
}
