package com.jokingsun.oilfairy.ui.fun.station;

import androidx.lifecycle.ViewModelProvider;

import com.jokingsun.oilfairy.BR;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.base.BaseFragment;
import com.jokingsun.oilfairy.databinding.FragmentFindGasStationBinding;

public class FindGasStation extends BaseFragment<FragmentFindGasStationBinding, FindGasStationViewModel> {

    public static FindGasStation getInstance() {
        return new FindGasStation();
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
        return new int[]{BR.findGasStationViewModel, BR.findGasStation};
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_find_gas_station;
    }

    @Override
    public FindGasStationViewModel getViewModel() {
        if (viewModel == null) {
            viewModel = new ViewModelProvider(this, getFactory()).get(FindGasStationViewModel.class);
        }
        return viewModel;
    }
}
