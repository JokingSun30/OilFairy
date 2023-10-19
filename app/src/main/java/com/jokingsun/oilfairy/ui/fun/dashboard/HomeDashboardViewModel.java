package com.jokingsun.oilfairy.ui.fun.dashboard;

import com.jokingsun.oilfairy.base.BaseViewModel;
import com.jokingsun.oilfairy.data.local.livedata.BaseLiveDataModel;
import com.jokingsun.oilfairy.data.remote.ApiHelper;

public class HomeDashboardViewModel extends BaseViewModel<HomeDashboard, BaseLiveDataModel> {

    public HomeDashboardViewModel(ApiHelper apiHelper) {
        super(apiHelper);
    }

    @Override
    protected void setWhenNetWorkRework() {

    }

    @Override
    protected void attachRepository(HomeDashboard navigator) {

    }
}
