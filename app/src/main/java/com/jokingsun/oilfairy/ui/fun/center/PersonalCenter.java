package com.jokingsun.oilfairy.ui.fun.center;

import androidx.lifecycle.ViewModelProvider;

import com.jokingsun.oilfairy.BR;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.BaseFragment;
import com.jokingsun.oilfairy.databinding.FragmentPersonalCenterBinding;

public class PersonalCenter extends BaseFragment<FragmentPersonalCenterBinding, PersonalCenterViewModel> {

    public static PersonalCenter getInstance() {
        return new PersonalCenter();
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
        return new int[]{BR.personalCenterViewModel, BR.personalCenter};
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_personal_center;
    }

    @Override
    public PersonalCenterViewModel getViewModel() {
        if (viewModel == null) {
            viewModel = new ViewModelProvider(this, getFactory()).get(PersonalCenterViewModel.class);
        }
        return viewModel;
    }
}
