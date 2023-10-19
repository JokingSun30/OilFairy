package com.jokingsun.oilfairy.ui.fun.station;

import com.jokingsun.oilfairy.base.BaseViewModel;
import com.jokingsun.oilfairy.data.local.livedata.BaseLiveDataModel;
import com.jokingsun.oilfairy.data.remote.ApiHelper;

public class FindGasStationViewModel extends BaseViewModel<FindGasStation, BaseLiveDataModel> {

    public FindGasStationViewModel(ApiHelper apiHelper) {
        super(apiHelper);
    }

    @Override
    protected void setWhenNetWorkRework() {

    }

    @Override
    protected void attachRepository(FindGasStation navigator) {

    }
}
